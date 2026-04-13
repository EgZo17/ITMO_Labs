package com.labwork.commands;

import com.labwork.utils.Command;
import com.labwork.collection.LabCollection;

/**
 * Команда для удаления элемента из коллекции по его ID.
 */

public class RemoveById implements Command {
    private LabCollection labCollection = LabCollection.getInstance();
    private int id;

    public RemoveById() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        if (id <= 0) {
            System.out.println("ID must be a positive number, try again.");
            return;
        }
        if (!labCollection.delElementById(id)) {
            System.out.println("Item with this ID does not exist, try again.");
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
            this.id = Integer.parseInt((String) parameters[0]);
        } catch (Exception e) {
            System.out.println("Error: Invalid command signature. Enter \"help\" to see more.");
            return false;
        }
        return true;
    }
}
