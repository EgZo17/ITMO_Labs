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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Главный класс клиентского приложения.
 */

public class ClientApp {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    private final Scanner scanner;
    private NetworkClient networkClient;

    private String currentLogin;
    private String currentPassword;

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
        if (currentLogin != null) {
            request.setLogin(currentLogin);
            request.setPassword(currentPassword);
        }

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

    public void handleAuthCommand(String commandType) {
        System.out.print("Enter login: ");
        String login = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Request request = new Request(commandType, null);
        request.setLogin(login);
        request.setPassword(password);

        try {
            networkClient.send(request);
            Response response = networkClient.receive();

            System.out.println(response.getMessage());
            
            if (response.isSuccess() && commandType.equals("login")) {
                this.currentLogin = login;
                this.currentPassword = password;
                System.out.println("Session is active. Data is saved.");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Network error: " + e.getMessage());
        }
    }

    public void executeScript(String filename) {
        executeScriptRecursive(filename, 1, new HashSet<>());
    }

    private void executeScriptRecursive(String filename, int loopCount, Set<String> executedScripts) {
        File scriptFile = new File(filename);
        String absolutePath;
        try { absolutePath = scriptFile.getCanonicalPath(); } catch (IOException e) { System.err.println("Invalid path: " + filename); return; }

        if (executedScripts.contains(absolutePath)) { System.err.println("Circular include: " + filename); return; }
        if (!scriptFile.exists()) { System.err.println("Not found: " + filename); return; }

        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(scriptFile))) {
            String line;
            while ((line = br.readLine()) != null) lines.add(line.trim());
        } catch (IOException e) { System.err.println("Read error: " + e.getMessage()); return; }

        int startIdx = 0;
        int iterations = loopCount;
        if (!lines.isEmpty() && lines.get(0).matches("\\d+")) {
            iterations = Math.min(Integer.parseInt(lines.get(0)), 100);
            startIdx = 1;
        }

        executedScripts.add(absolutePath);
        System.out.println("Executing: " + filename + " (" + iterations + " iterations)");

        for (int iter = 0; iter < iterations; iter++) {
            if (iterations > 1) System.out.println("--- Iteration " + (iter + 1) + " ---");
            
            int i = startIdx;
            while (i < lines.size()) {
                String line = lines.get(i);
                i++;
                
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split(" ", 2);
                String cmd = parts[0];
                String arg = parts.length > 1 ? parts[1].trim() : null;

                // Вложенные скрипты
                if (cmd.equals("execute_script")) {
                    if (arg != null && !arg.isEmpty()) {
                        File nested = new File(scriptFile.getParentFile(), arg);
                        System.out.println("  -> " + nested.getName());
                        executeScriptRecursive(nested.getPath(), 1, executedScripts);
                    }
                    continue;
                }

                Object requestArg = null;
                boolean isBlockCommand = false;

                try {
                    if (cmd.equals("add") || cmd.equals("add_if_min") || cmd.equals("remove_lower")) {
                        isBlockCommand = true;
                        if (i + 11 > lines.size()) throw new RuntimeException("Not enough lines for LabWork data");
                        requestArg = parseLabWork(lines, i);
                    } else if (cmd.equals("update")) {
                        isBlockCommand = true;
                        if (arg == null || arg.isEmpty()) throw new RuntimeException("Missing ID argument");
                        int id = Integer.parseInt(arg);
                        if (i + 11 > lines.size()) throw new RuntimeException("Not enough lines for LabWork data");
                        LabWork lw = parseLabWork(lines, i);
                        lw.setId(id);
                        requestArg = new Object[]{id, lw};
                    } else if (cmd.equals("remove_by_id") || cmd.equals("remove_at")) {
                        if (arg == null || arg.isEmpty()) throw new RuntimeException("Missing argument");
                        requestArg = Integer.parseInt(arg);
                    } else if (cmd.equals("remove_any_by_author")) {
                        requestArg = arg;
                    } else if (cmd.equals("filter_by_difficulty")) {
                        requestArg = Difficulty.valueOf(arg.toUpperCase());
                    } else {
                        requestArg = arg;
                    }
                } catch (Exception e) {
                    System.err.println("Parse error for '" + cmd + "': " + e.getMessage());
                    // если парсинг блока упал, пропускаем все его строки,
                    // чтобы они не выполнились как отдельные команды
                    if (isBlockCommand) i += 11;
                    continue;
                }

                // если всё успешно, тоже пропускаем строки блока
                if (isBlockCommand) i += 11;

                Request req = new Request(cmd, requestArg);
                req.setLogin(currentLogin);
                req.setPassword(currentPassword);

                try {
                    networkClient.send(req);
                    Response resp = networkClient.receive();
                    System.out.println("  " + resp.getMessage());
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Network error: " + e.getMessage());
                    executedScripts.remove(absolutePath);
                    return;
                }
            }
        }
        System.out.println("Done: " + filename);
        executedScripts.remove(absolutePath);
    }

    private LabWork parseLabWork(List<String> lines, int idx) {
        String name = lines.get(idx);
        String cX = lines.get(idx + 1);
        String cY = lines.get(idx + 2);
        String minPt = lines.get(idx + 3);
        String diff = lines.get(idx + 4);
        String pName = lines.get(idx + 5);
        String pHeight = lines.get(idx + 6);
        String pColor = lines.get(idx + 7);
        String lX = lines.get(idx + 8);
        String lY = lines.get(idx + 9);
        String lZ = lines.get(idx + 10);

        Coordinates coords = new Coordinates(Double.parseDouble(cX), Integer.parseInt(cY));
        Float point = minPt.isEmpty() ? null : Float.parseFloat(minPt);
        Difficulty difficulty = diff.isEmpty() ? null : Difficulty.valueOf(diff.toUpperCase());
        Color eyeColor = pColor.isEmpty() ? null : Color.valueOf(pColor.toUpperCase());
        Location location = new Location(Integer.parseInt(lX), Double.parseDouble(lY), Double.parseDouble(lZ));
        Person author = new Person(pName, Double.parseDouble(pHeight), eyeColor, location);

        return new LabWork(0, name, coords, point, difficulty, author);
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
