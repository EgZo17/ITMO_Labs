package commands;

import utils.Command;
import utils.ElementInputManager;
import java.util.Scanner;
import collection.LabCollection;
import data.LabWork;

public class Add implements Command {
    private LabCollection labCollection;
    private Scanner scanner;

    public Add(LabCollection labCollection, Scanner scanner) {
        this.labCollection = labCollection;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        LabWork newLabWork = ElementInputManager.readElement(scanner, labCollection);
        labCollection.addElement(newLabWork);
        System.out.println("\nNew item has been added successfully.\n");
    }
}
