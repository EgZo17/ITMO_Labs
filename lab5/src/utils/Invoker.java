package utils;

import java.util.HashMap;
import java.util.Map;

public class Invoker {
    private Map<String, Command> commands = new HashMap<>();
    private boolean isRunning = true;

    public Invoker() {}

    public void registerCommand(String name, Command command) {
        commands.put(name, command);
    }

    public boolean executeCommand(String commandName, String[] parameters) {
        Command command = commands.get(commandName);
        if (command == null) {
                System.out.println("Error: Unsupported command. Enter \"help\" to see more.");
                return false;
            }
        command.execute(parameters);
        return true;
    }

    public void stop() {
        isRunning = false;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
}
