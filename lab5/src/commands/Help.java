package com.labwork.commands;

import com.labwork.utils.Command;
import com.labwork.enums.CommandDescription;

/**
 * Команда для вывода справки по всем доступным командам.
 */

public class Help implements Command {
    public Help() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        System.out.println("=== Available Commands ===");
        for (CommandDescription cmd : CommandDescription.values()) {
            System.out.printf("  %-42s - %s%n", 
                cmd.name().toLowerCase() + " " + cmd.getSignature(), 
                cmd.getDescription());
        }
    }
}
