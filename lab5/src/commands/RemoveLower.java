package commands;

import utils.Command;
import utils.ElementInputManager;
import utils.GlobalScanner;
import java.util.ArrayList;
import java.util.Scanner;
import collection.LabCollection;
import data.LabWork;

public class RemoveLower implements Command {
    private LabCollection labCollection = LabCollection.getInstance();
    private Scanner scanner = GlobalScanner.getScanner();

    public RemoveLower() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        LabWork thresholdLabWork = ElementInputManager.readElement(scanner);
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
