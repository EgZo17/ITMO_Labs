package com.labwork.commands;

import com.labwork.utils.Command;
import com.labwork.collection.LabCollection;

/**
 * Команда для удаления элемента по индексу в коллекции.
 */

public class RemoveAt implements Command {
    private LabCollection labCollection = LabCollection.getInstance();
    private int index;

    public RemoveAt() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        if (index < 0) {
            System.out.println("Index must be a non-negative number, try again.");
            return;
        }
        if (!labCollection.delElementByIndex(index)) {
            System.out.println("This index is out of range, try again.");
            return;
        }
        System.out.println("Item has been deleted successfully.");
    }

    @Override
    public boolean validate(String[] parameters) {
        if (parameters.length != 1) {
            System.out.println("Error: Invalid command signature. Enter \"help\" to see more.");
            return false;
        }
        try {
            this.index = Integer.parseInt((String) parameters[0]);
        } catch (Exception e) {
            System.out.println("Error: Invalid command signature. Enter \"help\" to see more.");
            return false;
        }
        return true;
    }
}
