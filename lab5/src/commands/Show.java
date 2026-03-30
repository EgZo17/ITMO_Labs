package commands;

import utils.Command;
import collection.LabCollection;

public class Show implements Command {
    private LabCollection labCollection;

    public Show(LabCollection labCollection) {
        this.labCollection = labCollection;
    }

    @Override
    public void execute() {
        System.out.println(labCollection);
    }
}
