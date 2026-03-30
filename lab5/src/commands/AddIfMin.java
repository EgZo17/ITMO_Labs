package commands;

import utils.Command;
import utils.ElementInputManager;
import java.util.Scanner;
import collection.LabCollection;
import data.LabWork;

public class AddIfMin implements Command {
    private LabCollection labCollection;
    private Scanner scanner;

    public AddIfMin(LabCollection labCollection, Scanner scanner) {
        this.labCollection = labCollection;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        LabWork newLabWork = ElementInputManager.readElement(scanner, labCollection);
        LabWork minLabWork = labCollection.getMinElement();
        if (newLabWork.compareTo(minLabWork) < 0) {
            labCollection.addElement(newLabWork);
            System.out.println("\nNew item has been added successfully.\n");
            return;
        }
        System.out.println("\nNew item has not been added: not less than minimal.\n");
    }
}
