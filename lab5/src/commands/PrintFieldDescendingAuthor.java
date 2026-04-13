package com.labwork.commands;

import com.labwork.utils.Command;
import java.util.ArrayList;
import java.util.Collections;
import com.labwork.collection.LabCollection;
import com.labwork.data.LabWork;
import com.labwork.data.Person;

/**
 * Команда для вывода всех значений поля author в порядке убывания.
 */

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
        System.out.println(sb.toString());
    }
}
