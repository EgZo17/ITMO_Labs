package com.labwork.client.ui;

import com.labwork.client.network.NetworkClient;
import com.labwork.client.utils.GlobalScanner;
import com.labwork.common.models.Color;
import com.labwork.common.models.Coordinates;
import com.labwork.common.models.Difficulty;
import com.labwork.common.models.LabWork;
import com.labwork.common.models.Location;
import com.labwork.common.models.Person;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.client.utils.ElementInputManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Главный класс клиентского приложения.
 */

public class ClientApp {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    private final Scanner scanner;
    private NetworkClient networkClient;

    private static final int MAX_SCRIPT_DEPTH = 5;
    private int scriptDepth = 0;

    public ClientApp() {
        this.scanner = GlobalScanner.getScanner();
        this.networkClient = null;
    }

    /**
     * Пытается подключиться к серверу. При ошибке предлагает повторить.
     */
    private boolean connectToServer() {
        while (true) {
            try {
                System.out.print("Connecting to server at " + HOST + ":" + PORT + "... ");
                networkClient = new NetworkClient(HOST, PORT);
                System.out.println("OK");
                return true;
            } catch (IOException e) {
                System.out.println("FAILED");
                System.out.println("Server is unavailable: " + e.getMessage());
                System.out.print("Retry connection? (y/n): ");
                
                if (!scanner.hasNextLine() || !scanner.nextLine().trim().equalsIgnoreCase("y")) {
                    System.out.println("Exiting client application...");
                    return false;
                }
            }
        }
    }

    public void start() {
        if (!connectToServer()) {
            return;
        }

        System.out.println("Type 'help' to see available commands. Type 'exit' to quit.");
        String line;
        while (true) {
            System.out.print("-> ");
            if (!scanner.hasNextLine()) break;
            line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 2);
            String cmdName = parts[0];
            String args = parts.length > 1 ? parts[1].trim() : null;

            ClientCommand command = ClientCommand.fromName(cmdName);

            if (command != null) {
                boolean shouldContinue = command.execute(this, args);
                if (!shouldContinue) break;
            } else {
                System.out.println("Unknown command: '" + cmdName + "'. Type 'help'.");
            }
        }
        
        try { networkClient.close(); } catch (IOException ignored) {}
    }

    // Вспомогательные методы

    void sendRequest(Request request) {
        try {
            networkClient.send(request);
            Response response = networkClient.receive();
            
            System.out.println(response.getMessage());
            if (response.getData() != null) {
                printDataList(response.getData());
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Network error: " + e.getMessage());
        }
    }

    void handleIntCommand(String cmd, String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println("Error: integer argument required.");
            return;
        }
        try {
            sendRequest(new Request(cmd, Integer.parseInt(arg)));
        } catch (NumberFormatException e) {
            System.out.println("Error: invalid integer format.");
        }
    }

    void handleEnumCommand(String cmd, String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println("Error: difficulty level required.");
            return;
        }
        try {
            sendRequest(new Request(cmd, Difficulty.valueOf(arg.toUpperCase())));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: invalid difficulty. Use: HARD, HOPELESS, TERRIBLE.");
        }
    }

    void handleAuthorCommand(String cmd, String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println("Error: author name required.");
            return;
        }
        Person author = new Person();
        author.setName(arg);
        sendRequest(new Request(cmd, author));
    }

    void handleLabWorkCommand(String cmd) {
        System.out.println("Enter element details:");
        try {
            LabWork labWork = ElementInputManager.readElement(scanner);
            sendRequest(new Request(cmd, labWork));
        } catch (Exception e) {
            System.out.println("Input error: " + e.getMessage());
        }
    }

    void handleUpdateCommand(String arg) {
        if (arg == null || arg.isEmpty()) {
            System.out.println("Error: ID required for update.");
            return;
        }
        try {
            int id = Integer.parseInt(arg);
            System.out.println("Enter updated element details:");
            LabWork labWork = ElementInputManager.readElement(scanner);
            
            Object[] updateArgs = new Object[]{id, labWork};
            sendRequest(new Request("update", updateArgs));
        } catch (NumberFormatException e) {
            System.out.println("Error: invalid ID format.");
        } catch (Exception e) {
            System.out.println("Input error: " + e.getMessage());
        }
    }

    void executeScript(String fileName) {
        if (scriptDepth >= MAX_SCRIPT_DEPTH) {
            System.out.println("[Script] Error: Maximum nesting depth (" + MAX_SCRIPT_DEPTH + ") reached.");
            return;
        }

        scriptDepth++;
        System.out.println("[Script Level " + scriptDepth + "] Starting: " + fileName);

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            int lineNum = 0;
            while ((line = reader.readLine()) != null) {
                lineNum++;
                String rawLine = line.trim();
                if (rawLine.isEmpty() || rawLine.startsWith("#")) continue;

                System.out.println("[" + scriptDepth + ":" + lineNum + "] " + rawLine);

                String[] parts = rawLine.split("\\s+", 2);
                String cmd = parts[0].toLowerCase();
                String args = parts.length > 1 ? parts[1].trim() : null;

                try {
                    switch (cmd) {
                        case "exit" -> 
                            System.out.println("[" + scriptDepth + "] 'exit' ignored inside script.");

                        case "add", "add_if_min", "remove_lower" -> {
                            LabWork work = readLabWorkFromScript(reader);
                            sendRequest(new Request(cmd, work));
                        }

                        case "update" -> {
                            if (args == null || args.isEmpty()) throw new IllegalArgumentException("ID required");
                            int id = Integer.parseInt(args);
                            LabWork work = readLabWorkFromScript(reader);
                            sendRequest(new Request("update", new Object[]{id, work}));
                        }

                        case "execute_script" -> {
                            if (args == null || args.isEmpty()) throw new IllegalArgumentException("Filename required");
                            executeScript(args);
                        }

                        case "help", "info", "show", "clear", "print_field_descending_author" -> 
                            sendRequest(new Request(cmd, null));

                        case "remove_by_id", "remove_at" -> 
                            sendRequest(new Request(cmd, Integer.parseInt(args)));

                        case "filter_by_difficulty" -> 
                            sendRequest(new Request(cmd, Difficulty.valueOf(args.toUpperCase())));

                        case "remove_any_by_author" -> {
                            Person p = new Person();
                            p.setName(args);
                            sendRequest(new Request(cmd, p));
                        }

                        default -> 
                            System.out.println("[" + scriptDepth + "] ️ Unknown command: " + cmd);
                    }
                } catch (Exception e) {
                    System.out.println("[" + scriptDepth + ":" + lineNum + "] ❌ Error: " + e.getMessage());
                }
            }
            System.out.println("[Script Level " + scriptDepth + "] Finished: " + fileName);
        } catch (IOException e) {
            System.out.println("[Script Level " + scriptDepth + "] File error: " + e.getMessage());
        } finally {
            scriptDepth--;
        }
    }

    private LabWork readLabWorkFromScript(BufferedReader reader) throws IOException {
        String name = reader.readLine();
        String cX = reader.readLine();
        String cY = reader.readLine();
        String minPt = reader.readLine();
        String diff = reader.readLine();
        String pName = reader.readLine();
        String pHeight = reader.readLine();
        String pColor = reader.readLine();
        String lX = reader.readLine();
        String lY = reader.readLine();
        String lZ = reader.readLine();

        if (name == null || cX == null || cY == null || minPt == null || diff == null ||
            pName == null || pHeight == null || pColor == null || lX == null || lY == null) {
            throw new IOException("Script error: unexpected end of file while reading LabWork fields");
        }

        Coordinates coords = new Coordinates(
            Double.parseDouble(cX.trim()),
            Integer.parseInt(cY.trim())
        );

        Float point = minPt.trim().isEmpty() ? null : Float.parseFloat(minPt.trim());
        Difficulty difficulty = diff.trim().isEmpty() ? null : Difficulty.valueOf(diff.trim().toUpperCase());

        Color eyeColor = pColor.trim().isEmpty() ? null : Color.valueOf(pColor.trim().toUpperCase());

        Location location = new Location(
            Integer.parseInt(lX.trim()),
            Double.parseDouble(lY.trim()),
            Double.parseDouble(lZ.trim())
        );

        Person author = new Person(
            pName.trim(),
            Double.parseDouble(pHeight.trim()),
            eyeColor,
            location
        );

        // id = 0, т.к. сервер сгенерирует его автоматически
        return new LabWork(0, name.trim(), coords, point, difficulty, author);
    }

    private void printDataList(LinkedList<?> data) {
        for (Object item : data) {
            System.out.println(item);
        }
    }

    public static void main(String[] args) {
        new ClientApp().start();
    }
}
