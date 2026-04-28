package com.labwork.server.network;

import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.CommandManager;
import com.labwork.server.core.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Scanner;

/**
 * Главный класс серверного приложения.
 * Использует java.io потоки.
 * Протокол: [4 байта длина][сериализованные данные]
 */

public class ServerApp {
    private static final Logger logger = LoggerFactory.getLogger(ServerApp.class);
    private static final int PORT = 5000;
    
    private final CommandManager commandManager;

    public ServerApp() {
        this.commandManager = new CommandManager();
    }

    public void start() {
        logger.info("Server is starting on port {}", PORT);
        
        FileManager.initialize();
        try {
            FileManager.loadCollection();
            logger.info("Collection loaded successfully from file");
        } catch (JAXBException | IOException e) {
            logger.warn("Failed to load collection: {}", e.getMessage());
            logger.info("Starting with empty collection");
        }

        startServerConsole();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            logger.info("Server is listening for connections on port {}", PORT);
            
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    logger.info("Client connected: {}", clientSocket.getInetAddress().getHostAddress());
                    
                    handleClient(clientSocket);
                    
                    logger.info("Client disconnected. Waiting for new connections...");
                } catch (IOException e) {
                    logger.error("Error handling client connection: {}", e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Failed to start server socket", e);
        } finally {

            logger.info("Server is shutting down completely. Saving collection...");
            try {
                FileManager.saveCollection();
                logger.info("Collection saved successfully");
            } catch (JAXBException | IOException e) {
                logger.error("Failed to save collection", e);
            }
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        InputStream in = clientSocket.getInputStream();
        OutputStream out = clientSocket.getOutputStream();

        while (true) {
            try {
                byte[] lenBytes = new byte[4];
                readFully(in, lenBytes);
                int dataLength = ByteBuffer.wrap(lenBytes).getInt();
                
                byte[] data = new byte[dataLength];
                readFully(in, data);
                
                Request request = (Request) deserialize(data);
                logger.info("Received request: {}", request.getCommandName());
                
                Response response = commandManager.execute(request);
                
                byte[] respData = serialize(response);
                byte[] respLen = ByteBuffer.allocate(4).putInt(respData.length).array();
                out.write(respLen);
                out.write(respData);
                out.flush();
                
                logger.info("Sent response: {}", response.isSuccess() ? "SUCCESS" : "ERROR");
                
            } catch (EOFException e) {
                logger.info("Client disconnected normally.");
                break;
            } catch (ClassNotFoundException e) {
                logger.error("Unknown class received", e);
                break;
            } catch (IOException e) {
                logger.error("IO error: {}", e.getMessage());
                break;
            }
        }
    }

    private void readFully(InputStream in, byte[] buffer) throws IOException {
        int offset = 0;
        while (offset < buffer.length) {
            int read = in.read(buffer, offset, buffer.length - offset);
            if (read == -1) {
                throw new EOFException("Unexpected end of stream");
            }
            offset += read;
        }
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
     * Запускает фоновый поток для чтения команд из консоли сервера.
     * Доступны: save (сохранить), exit (выключить).
     */
    private void startServerConsole() {
        Thread consoleThread = new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("Server console active. Type 'save' to save, 'exit' to quit.");
                
                while (scanner.hasNextLine()) {
                    String input = scanner.nextLine().trim().toLowerCase();
                    
                    switch (input) {
                        case "save":
                            try {
                                FileManager.saveCollection();
                                System.out.println("[Server] Collection saved successfully.");
                            } catch (Exception e) {
                                System.err.println("[Server] Save failed: " + e.getMessage());
                            }
                            break;
                            
                        case "exit":
                            logger.info("Server is shutting down completely. Saving collection...");
                            try {
                                FileManager.saveCollection();
                                logger.info("Collection saved successfully");
                            } catch (JAXBException | IOException e) {
                                logger.error("Failed to save collection", e);
                            }
                            System.exit(0);
                            break;
                            
                        case "":
                            break;
                            
                        default:
                            System.out.println("[Server] Unknown command. Available: save, exit");
                    }
                }
            }
        });
        
        consoleThread.setDaemon(true);
        consoleThread.start();
    }

    public static void main(String[] args) {
        new ServerApp().start();
    }
}
