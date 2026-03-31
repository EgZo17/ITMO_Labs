package commands;

import utils.Command;
import java.util.ArrayList;
import java.util.Collections;
import collection.LabCollection;
import data.LabWork;
import data.Person;

public class PrintFieldDescendingAuthor implements Command {
    private LabCollection labCollection = LabCollection.getInstance();

    public PrintFieldDescendingAuthor() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        ArrayList<Person> authors = new ArrayList<>();
        for (LabWork labWork : labCollection.getCollection()) {
            authors.add(labWork.getAuthor());
        }
        System.out.println("Authors in descending order:\n");
        if (authors.isEmpty()) {
            System.out.println("<<No authors here yet>>\n");
            return;
        }
        Collections.sort(authors, Collections.reverseOrder());
        StringBuilder sb = new StringBuilder();
        String stopLine = "-".repeat(50);
        sb.append(stopLine);
        for (Person author : authors) {
            sb.append(author);
        }
        sb.append(stopLine);
        sb.append("\n");
        System.out.println(sb.toString());
    }
}
