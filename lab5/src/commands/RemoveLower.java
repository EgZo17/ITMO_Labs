package commands;

import utils.Command;
import utils.ElementInputManager;
import java.util.ArrayList;
import java.util.Scanner;
import collection.LabCollection;
import data.LabWork;

public class RemoveLower implements Command {
    private LabCollection labCollection;
    private Scanner scanner;

    public RemoveLower(LabCollection labCollection, Scanner scanner) {
        this.labCollection = labCollection;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        LabWork thresholdLabWork = ElementInputManager.readElement(scanner, labCollection);
        ArrayList<LabWork> toDelete = new ArrayList<>();
        for (LabWork labWork : labCollection.getCollection()) {
            if (labWork.compareTo(thresholdLabWork) < 0) {
                toDelete.add(labWork);
            }
        }
        for (LabWork labWork : toDelete) {
            labCollection.delElement(labWork);
        }
        System.out.println(String.format("\nItems have been deleted: %s\n", toDelete.size()));
    }
}
