package com.labwork.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import com.labwork.utils.Command;
import com.labwork.utils.GlobalScanner;
import com.labwork.utils.Invoker;

/**
 * Команда для выполнения скрипта из файла. Содержит команды в том же виде, что и при интерактивном вводе.
 * Защищена от рекурсии.
 */

public class ExecuteScript implements Command {
    private final Invoker invoker = Invoker.getInvoker();
    private String filename;
    private Set<String> executingScripts = new HashSet<>();

    public ExecuteScript() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }

        String absPath = new File(filename).getAbsolutePath();
        
        if (executingScripts.contains(absPath)) {
            System.out.println("Error: Recursive script call");
            return;
        }
        
        executingScripts.add(absPath);

        Scanner originalScanner = GlobalScanner.getScanner();
        
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {

            Scanner scriptScanner = new Scanner(bufferedReader);
            
            GlobalScanner.setScanner(scriptScanner);
            
            String line;
            int lineNumber = 0;
            
            while (scriptScanner.hasNextLine()) {
                line = scriptScanner.nextLine().trim();

                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }
                
                lineNumber++;
                System.out.println("[" + lineNumber + "] > " + line);

                String[] parts = line.split("\\s+");
                String[] args;
                String commandName = parts[0].toLowerCase();
                if (parts.length == 1) {
                    args = new String[0];
                } else {
                    args = Arrays.copyOfRange(parts, 1, parts.length);
                }
                
                invoker.executeCommand(commandName, args);
            }
            
            if (lineNumber > 0) {
                System.out.println("Script executed successfully.");
            }
            else {
                System.out.println("Script is empty.");
            }
            
        } catch (FileNotFoundException e) {
            System.out.println("Error: script file not found: " + filename);
        } catch (IOException e) {
            System.out.println("Input-output exception: " + e.getMessage());
        } finally {
            GlobalScanner.setScanner(originalScanner);
            executingScripts.remove(absPath);
        }
    }

    @Override
    public boolean validate(String[] parameters) {
        if (parameters.length != 1) {
            System.out.println("Error: Invalid command signature. Enter \"help\" to see more.");
            return false;
        }
        try {
            this.filename = (String) parameters[0];
        } catch (Exception e) {
            System.out.println("Error: Invalid command signature. Enter \"help\" to see more.");
            return false;
        }
        return true;
    }
}
