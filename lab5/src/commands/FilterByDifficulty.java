package commands;

import collection.LabCollection;
import data.LabWork;
import utils.Command;
import enums.Difficulty;

public class FilterByDifficulty implements Command {
    private LabCollection labCollection;
    private String difficultyName;

    public FilterByDifficulty(LabCollection labCollection, String difficultyName) {
        this.labCollection = labCollection;
        this.difficultyName = difficultyName;
    }

    @Override
    public void execute() {
        Difficulty difficulty;
        try {
            difficulty = Difficulty.valueOf(difficultyName);
        } catch (IllegalArgumentException e) {
            System.out.println("There's no such difficulty, try again.\n");
            return;
        }
        StringBuilder sb = new StringBuilder();
        String stopLine = "~".repeat(50);
        boolean labFound = false;
        sb.append(stopLine);
        for (LabWork labWork : labCollection.getCollection()) {
            if (labWork.getDifficulty().equals(difficulty)) {
                sb.append(labWork);
                labFound = true;
            }
        }
        if (!labFound) {
            sb.append("\nNothing is here yet!\n");
        }
        sb.append(stopLine);
        sb.append("\n");
        System.out.println(sb.toString());
    }
}
