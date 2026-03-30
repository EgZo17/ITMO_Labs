package utils;

import collection.LabCollection;
import java.util.Scanner;
import data.*;
import enums.*;
import commands.*;

public class Client {
    public static void run() {
        Scanner scanner = new Scanner(System.in, "cp866");

        Location location1 = new Location(5, 0.5, 0.11);
        Location location2 = new Location(8, 0.1, 1.7);
        Person author1 = new Person("Abraham", 1.74, Color.BLACK, location1);
        Person author2 = new Person("Bozo", 1.87, Color.ORANGE, location2);
        Coordinates coords1 = new Coordinates(17.8, 6);
        Coordinates coords2 = new Coordinates(33.1, 5);
        LabWork labWork1 = new LabWork(2, "В", coords1, 61.6f, Difficulty.HARD, author1);
        LabWork labWork2 = new LabWork(1, "Г", coords2, 61.6f, Difficulty.HOPELESS, author2);
        LabWork labWork3 = new LabWork(36, "Ж", coords2, 60f, Difficulty.TERRIBLE, author1);
        LabWork labWork4 = new LabWork(43, "Б", coords2, 90f, Difficulty.HARD, author2);
        LabWork labWork5 = new LabWork(113, "Я", coords2, 50f, Difficulty.HOPELESS, author1);

        // System.out.println(LabWork1.compareTo(LabWork2));

        // System.out.println(author1);

        LabCollection labCollection = new LabCollection();
        labCollection.addElement(labWork2);
        labCollection.addElement(labWork1);
        labCollection.addElement(labWork5);
        labCollection.addElement(labWork3);
        labCollection.addElement(labWork4);

        // System.out.println(ElementInputManager.readElement(scanner, labCollection));

        Show show = new Show(labCollection);
        show.execute();

        // Add add = new Add(labCollection, scanner);
        // add.execute();

        // show.execute();

        // AddIfMin addIfMin = new AddIfMin(labCollection, scanner);
        // addIfMin.execute();

        // show.execute();

        // RemoveLower removeLower = new RemoveLower(labCollection, scanner);
        // removeLower.execute();

        // show.execute();

        Update update = new Update(2, labCollection, scanner);
        update.execute();

        show.execute();

        // Clear clear = new Clear(labCollection);
        // clear.execute();
        // System.out.println(labCollection);

        // FilterByDifficulty filter1 = new FilterByDifficulty(labCollection, "HOPELESS");
        // filter1.execute();

        // FilterByDifficulty filter2 = new FilterByDifficulty(labCollection, "HARD");
        // filter2.execute();

        // RemoveAt removeAt = new RemoveAt(labCollection, 3);
        // removeAt.execute();
        // System.out.println(labCollection);

        // RemoveAnyByAuthor removeAnyByAuthor = new RemoveAnyByAuthor(labCollection, author2.getName());
        // removeAnyByAuthor.execute();
        // System.out.println(labCollection);

        // RemoveById removeById1 = new RemoveById(labCollection, 113);
        // removeById1.execute();
        // show.execute();

        // RemoveById removeById2 = new RemoveById(labCollection, 1113);
        // removeById2.execute();
        // System.out.println(labCollection);

        // PrintFieldDescendingAuthor printFieldDescendingAuthor = new PrintFieldDescendingAuthor(labCollection);
        // printFieldDescendingAuthor.execute();

        // Exit exit = new Exit();
        // exit.execute();
    }
}
