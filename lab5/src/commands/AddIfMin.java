package commands;

import utils.Command;
import utils.ElementInputManager;
import utils.GlobalScanner;
import java.util.Scanner;
import collection.LabCollection;
import data.LabWork;

public class AddIfMin implements Command {
    private LabCollection labCollection = LabCollection.getInstance();
    private Scanner scanner = GlobalScanner.getScanner();

    public AddIfMin() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        LabWork newLabWork = ElementInputManager.readElement(scanner);
        LabWork minLabWork = labCollection.getMinElement();
        if (newLabWork.compareTo(minLabWork) < 0) {
            labCollection.addElement(newLabWork);
            System.out.println("\nNew item has been added successfully.\n");
            return;
        }
        System.out.println("\nNew item has not been added: not less than minimal.\n");
    }
}
