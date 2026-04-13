package com.labwork.commands;

import com.labwork.utils.Command;

/**
 * Команда для завершения работы программы без сохранения коллекции.
 */

public class Exit implements Command {
    public Exit() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        System.out.println("Exiting the program...");
        System.exit(0);
    }
}
