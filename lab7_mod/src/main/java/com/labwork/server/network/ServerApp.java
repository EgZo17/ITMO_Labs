package com.labwork.server.network;

import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.AuthManager;
import com.labwork.server.core.CommandManager;
import com.labwork.server.core.DatabaseConfig;
import com.labwork.server.core.DatabaseManager;
import com.labwork.server.core.LabCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.*;

/**
 * Главный класс сервера.
 * Реализует многопоточную обработку через 3 пула потоков.
 */

public class ServerApp {
    private static final Logger logger = LoggerFactory.getLogger(ServerApp.class);
    private static final int PORT = 5000;

    private final ExecutorService readPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final ExecutorService processPool = Executors.newCachedThreadPool();
    private final ExecutorService writePool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private DatabaseManager dbManager;
    private CommandManager commandManager;
    private ServerSocket serverSocket;

    public void start() {
        logger.info("Server is starting on port {}", PORT);

        try {
            this.dbManager = new DatabaseManager();
            Connection conn = DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.USERNAME, DatabaseConfig.PASSWORD);
            dbManager.initializeDatabase(conn);
            dbManager.loadCollection();
        }
        catch (SQLException e) {
            logger.error("CRITICAL: Database connection failed. Server stopped.");
            logger.error(e.getMessage());
            return;
        }

        AuthManager authManager = new AuthManager();

        this.commandManager = new CommandManager(dbManager, authManager); 

        startServerConsole();

        try {
            serverSocket = new ServerSocket(PORT);
            logger.info("Server is listening for connections on port {}", PORT);

            while (!serverSocket.isClosed()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    logger.info("Client connected: {}", clientSocket.getInetAddress().getHostAddress());
                    
                    readPool.submit(() -> handleClient(clientSocket));
                } catch (IOException e) {
                    if (!serverSocket.isClosed()) {
                        logger.error("Error accepting client", e);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Failed to start server socket", e);
        } finally {
            shutdown();
        }
    }

    private void handleClient(Socket clientSocket) {
        // try-with-resources автоматически закроет потоки при разрыве соединения
        try (InputStream in = clientSocket.getInputStream();
             OutputStream out = clientSocket.getOutputStream()) {

            while (!clientSocket.isClosed()) {
                // Читаем заголовок (4 байта длины)
                ByteBuffer lengthBuf = ByteBuffer.allocate(4);
                if (!readFully(in, lengthBuf)) break;
                lengthBuf.flip();
                int dataLength = lengthBuf.getInt();

                ByteBuffer dataBuf = ByteBuffer.allocate(dataLength);
                if (!readFully(in, dataBuf)) break;

                // Десериализуем с обработкой ClassNotFoundException
                Request request;
                try {
                    request = (Request) deserialize(dataBuf.array());
                } catch (ClassNotFoundException e) {
                    logger.error("Deserialization failed", e);
                    writePool.submit(() -> sendResponse(clientSocket, new Response(false, "Invalid data format")));
                    continue;
                }

                logger.info("Read request: {}", request.getCommandName());

                processPool.submit(() -> {
                    try {
                        Response response = commandManager.execute(request);
                        writePool.submit(() -> sendResponse(clientSocket, response));
                    } catch (Exception e) {
                        logger.error("Processing error", e);
                        writePool.submit(() -> sendResponse(clientSocket, 
                            new Response(false, "Internal server error")));
                    }
                });
            }
        } catch (IOException e) {
            logger.info("Client disconnected or IO error: {}", e.getMessage());
        }
    }

    private void sendResponse(Socket clientSocket, Response response) {
        if (clientSocket.isClosed()) return;
        
        try {
            OutputStream out = clientSocket.getOutputStream();
            
            byte[] respData = serialize(response);
            ByteBuffer header = ByteBuffer.allocate(4);
            header.putInt(respData.length);

            out.write(header.array());
            out.write(respData);
            out.flush();
            
            logger.info("Sent response: {}", response.isSuccess() ? "SUCCESS" : "ERROR");
        } catch (IOException e) {
            logger.error("Failed to send response", e);
        }
    }

    private boolean readFully(InputStream in, ByteBuffer buf) throws IOException {
        byte[] temp = new byte[buf.remaining()];
        int offset = 0;
        int bytesRead;
        while (offset < temp.length && (bytesRead = in.read(temp, offset, temp.length - offset)) != -1) {
            offset += bytesRead;
        }
        if (offset < temp.length) return false; // Клиент закрыл соединение
        buf.put(temp);
        return true;
    }

    private byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        }
    }

    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        }
    }

    /**
     * Консоль управления сервером.
     */
    private void startServerConsole() {
        Thread consoleThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Server console active. Type 'exit' to quit.");
            try {
                while (scanner.hasNextLine()) {
                    String input = scanner.nextLine().trim().toLowerCase();
                    switch (input) {
                        case "exit":
                            System.out.println("Shutting down server...");
                            try { serverSocket.close(); } catch (IOException ignored) {}
                            return;
                        case "info":
                            System.out.println(" Server Info:");
                            System.out.println("   - Collection Size: " + LabCollection.getInstance().getLength());
                            System.out.println("   - Read Pool Active: " + ((ThreadPoolExecutor)readPool).getActiveCount());
                            System.out.println("   - Process Pool Active: " + ((ThreadPoolExecutor)processPool).getActiveCount());
                            break;
                        default:
                            if (!input.isEmpty()) System.out.println("Unknown command.");
                    }
                }
            } finally {
                scanner.close();
            }
        });
        consoleThread.setDaemon(true);
        consoleThread.start();
    }

    /**
     * Корректное завершение работы.
     */
    private void shutdown() {
        logger.info("Server is shutting down...");
        readPool.shutdown();
        processPool.shutdown();
        writePool.shutdown();
        try {
            if (!readPool.awaitTermination(2, TimeUnit.SECONDS)) readPool.shutdownNow();
            if (!processPool.awaitTermination(2, TimeUnit.SECONDS)) processPool.shutdownNow();
            if (!writePool.awaitTermination(2, TimeUnit.SECONDS)) writePool.shutdownNow();
        } catch (InterruptedException e) {
            readPool.shutdownNow();
            processPool.shutdownNow();
            writePool.shutdownNow();
        }
        logger.info("Server stopped.");
    }

    public static void main(String[] args) {
        new ServerApp().start();
    }
}
