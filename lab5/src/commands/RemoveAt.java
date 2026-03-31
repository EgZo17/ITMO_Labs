package commands;

import utils.Command;
import collection.LabCollection;

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
            System.out.println("Index must be a non-negative number, try again.\n");
            return;
        }
        if (!labCollection.delElementByIndex(index)) {
            System.out.println("This index is out of range, try again.\n");
            return;
        }
        System.out.println("Item has been deleted successfully.\n");
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
