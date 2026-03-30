package commands;

import utils.Command;
import collection.LabCollection;

public class RemoveAt implements Command {
    private LabCollection labCollection;
    private int index;

    public RemoveAt(LabCollection labCollection, int index) {
        this.labCollection = labCollection;
        this.index = index;
    }

    @Override
    public void execute() {
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
}
