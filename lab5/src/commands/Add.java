package com.labwork.commands;

import com.labwork.utils.Command;
import com.labwork.utils.ElementInputManager;
import com.labwork.utils.GlobalScanner;
import java.util.Scanner;
import com.labwork.collection.LabCollection;
import com.labwork.data.LabWork;

public class Add implements Command {
    private LabCollection labCollection = LabCollection.getInstance();

    public Add() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        Scanner scanner = GlobalScanner.getScanner();
        LabWork newLabWork = ElementInputManager.readElement(scanner);
        labCollection.addElement(newLabWork);
        System.out.println("\nNew item has been added successfully.");
    }
}
