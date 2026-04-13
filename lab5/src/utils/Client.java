package com.labwork.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import javax.xml.bind.JAXBException;
import com.labwork.commands.*;

/**
 * Класс клиента, запускаемый главным классом приложения.
 * Инициализирует коллекцию, загружает данные из файла, запускает интерактивный режим.
 */

public class Client {
    private Invoker invoker;
    private Scanner scanner;

    public void run() throws JAXBException, IOException {
        this.invoker = Invoker.getInvoker();
        this.scanner = GlobalScanner.getScanner();

        initializeCommands();
        FileManager.initialize();
        FileManager.loadCollection();

        System.out.println("\n=== LabWork Collection Manager ===");
        System.out.println("Type 'help' for available commands.");
        
        while (invoker.isRunning()) {
            System.out.print("\n> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) continue;
            
            String[] parts = input.split("\\s+");
            String[] parameters;
            String commandName = parts[0].toLowerCase();
            if (parts.length == 1) {
                parameters = new String[0];
            } else {
                parameters = Arrays.copyOfRange(parts, 1, parts.length);
            }
            
            invoker.executeCommand(commandName, parameters);
        }
    }

    private void initializeCommands() {
        invoker.registerCommand("help", new Help());
        invoker.registerCommand("info", new Info());
        invoker.registerCommand("show", new Show());
        invoker.registerCommand("add", new Add());
        invoker.registerCommand("update", new Update());
        invoker.registerCommand("remove_by_id", new RemoveById());
        invoker.registerCommand("clear", new Clear());
        invoker.registerCommand("save", new Save());
        invoker.registerCommand("execute_script", new ExecuteScript());
        invoker.registerCommand("exit", new Exit());
        invoker.registerCommand("remove_at", new RemoveAt());
        invoker.registerCommand("add_if_min", new AddIfMin());
        invoker.registerCommand("remove_lower", new RemoveLower());
        invoker.registerCommand("remove_any_by_author", new RemoveAnyByAuthor());
        invoker.registerCommand("filter_by_difficulty", new FilterByDifficulty());
        invoker.registerCommand("print_field_descending_author", new PrintFieldDescendingAuthor());
    }
}
