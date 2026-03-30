package commands;

import utils.Command;
import collection.LabCollection;

public class RemoveById implements Command {
    private LabCollection labCollection;
    private int id;

    public RemoveById(LabCollection labCollection, int id) {
        this.labCollection = labCollection;
        this.id = id;
    }

    @Override
    public void execute() {
        if (id <= 0) {
            System.out.println("ID must be a positive number, try again.\n");
            return;
        }
        if (!labCollection.delElementById(id)) {
            System.out.println("Item with this ID does not exist, try again.\n");
            return;
        }
        System.out.println("Item has been deleted successfully.\n");
    }
}
