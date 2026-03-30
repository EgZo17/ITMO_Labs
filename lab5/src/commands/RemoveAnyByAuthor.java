package commands;

import utils.Command;
import collection.LabCollection;
import data.LabWork;
import java.util.ArrayList;
import java.util.Random;

public class RemoveAnyByAuthor implements Command {
    private LabCollection labCollection;
    private String authorName;
    private final Random rand = new Random();

    public RemoveAnyByAuthor(LabCollection labCollection, String authorName) {
        this.labCollection = labCollection;
        this.authorName = authorName;
    }

    @Override
    public void execute() {
        ArrayList<LabWork> authorWorks = new ArrayList<>();
        for (LabWork labWork : labCollection.getCollection()) {
            if (labWork.getAuthor().getName().equals(authorName)) {
                authorWorks.add(labWork);
            }
        }
        if (authorWorks.isEmpty()) {
            System.out.println("This author has never been mentioned, try again.\n");
            return;
        }
        LabWork pickedLabWork = authorWorks.get(rand.nextInt(authorWorks.size()));
        labCollection.delElementById(pickedLabWork.getId());
        System.out.println("Item has been deleted successfully.\n");
    }
}
