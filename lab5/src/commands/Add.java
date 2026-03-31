package commands;

import utils.Command;
import utils.ElementInputManager;
import utils.GlobalScanner;
import java.util.Scanner;
import collection.LabCollection;
import data.LabWork;

public class Add implements Command {
    private LabCollection labCollection = LabCollection.getInstance();
    private Scanner scanner = GlobalScanner.getScanner();

    public Add() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        LabWork newLabWork = ElementInputManager.readElement(scanner);
        labCollection.addElement(newLabWork);
        System.out.println("\nNew item has been added successfully.\n");
    }
}
