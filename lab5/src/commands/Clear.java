package com.labwork.commands;

import com.labwork.utils.Command;
import com.labwork.collection.LabCollection;

/**
 * Команда для очистки всей коллекции.
 */

public class Clear implements Command {
    private LabCollection labCollection = LabCollection.getInstance();

    public Clear() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        labCollection.clear();
        System.out.println("Collection is cleared.");
    }
}
