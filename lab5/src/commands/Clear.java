package commands;

import utils.Command;
import collection.LabCollection;

public class Clear implements Command {
    private LabCollection labCollection;

    public Clear(LabCollection labCollection) {
        this.labCollection = labCollection;
    }

    @Override
    public void execute() {
        labCollection.clear();
        System.out.println("Collection is cleared.\n");
    }
}
