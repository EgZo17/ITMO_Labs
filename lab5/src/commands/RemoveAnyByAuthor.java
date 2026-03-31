package com.labwork.commands;

import com.labwork.utils.Command;
import com.labwork.collection.LabCollection;
import com.labwork.data.LabWork;
import java.util.ArrayList;
import java.util.Random;

public class RemoveAnyByAuthor implements Command {
    private LabCollection labCollection = LabCollection.getInstance();
    private String authorName;
    private final Random rand = new Random();

    public RemoveAnyByAuthor() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        ArrayList<LabWork> authorWorks = new ArrayList<>();
        for (LabWork labWork : labCollection.getCollection()) {
            if (labWork.getAuthor().getName().equals(authorName)) {
                authorWorks.add(labWork);
            }
        }
        if (authorWorks.isEmpty()) {
            System.out.println("This author has never been mentioned, try again.");
            return;
        }
        LabWork pickedLabWork = authorWorks.get(rand.nextInt(authorWorks.size()));
        labCollection.delElementById(pickedLabWork.getId());
        System.out.println("Item has been deleted successfully.");
    }

    @Override
    public boolean validate(String[] parameters) {
        if (parameters.length != 1) {
            System.out.println("Error: Invalid command signature. Enter \"help\" to see more.");
            return false;
        }
        try {
            this.authorName = (String) parameters[0];
        } catch (Exception e) {
            System.out.println("Error: Invalid command signature. Enter \"help\" to see more.");
            return false;
        }
        return true;
    }
}
