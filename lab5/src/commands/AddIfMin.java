package com.labwork.commands;

import com.labwork.utils.Command;
import com.labwork.utils.ElementInputManager;
import com.labwork.utils.GlobalScanner;
import java.util.Scanner;
import com.labwork.collection.LabCollection;
import com.labwork.data.LabWork;

public class AddIfMin implements Command {
    private LabCollection labCollection = LabCollection.getInstance();

    public AddIfMin() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        Scanner scanner = GlobalScanner.getScanner();
        LabWork newLabWork = ElementInputManager.readElement(scanner);
        LabWork minLabWork = labCollection.getMinElement();
        if (newLabWork.compareTo(minLabWork) < 0) {
            labCollection.addElement(newLabWork);
            System.out.println("\nNew item has been added successfully.");
            return;
        }
        System.out.println("\nNew item has not been added: not less than minimal.");
    }
}
