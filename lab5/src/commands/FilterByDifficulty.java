package com.labwork.commands;

import com.labwork.collection.LabCollection;
import com.labwork.data.LabWork;
import com.labwork.utils.Command;
import com.labwork.enums.Difficulty;

public class FilterByDifficulty implements Command {
    private LabCollection labCollection = LabCollection.getInstance();
    private String difficultyName;

    public FilterByDifficulty() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        Difficulty difficulty;
        try {
            difficulty = Difficulty.valueOf(difficultyName);
        } catch (IllegalArgumentException e) {
            System.out.println("There's no such difficulty, try again.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        String stopLine = "~".repeat(50);
        boolean labFound = false;
        sb.append(stopLine);
        for (LabWork labWork : labCollection.getCollection()) {
            if (labWork.getDifficulty() == null) {
                continue;
            }
            if (labWork.getDifficulty().equals(difficulty)) {
                sb.append(labWork);
                labFound = true;
            }
        }
        if (!labFound) {
            sb.append("\nNothing is here yet!\n");
        }
        sb.append(stopLine);
        System.out.println(sb.toString());
    }

    @Override
    public boolean validate(String[] parameters) {
        if (parameters.length != 1) {
            System.out.println("Error: Invalid command signature. Enter \"help\" to see more.");
            return false;
        }
        try {
            this.difficultyName = (String) parameters[0];
        } catch (Exception e) {
            System.out.println("Error: Invalid command signature. Enter \"help\" to see more.");
            return false;
        }
        return true;
    }
}
