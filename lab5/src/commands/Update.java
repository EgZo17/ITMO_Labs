package commands;

import utils.Command;
import utils.ElementInputManager;
import java.util.Scanner;
import collection.LabCollection;
import data.LabWork;

public class Update implements Command {
    private int id;
    private LabCollection labCollection;
    private Scanner scanner;

    public Update(int id, LabCollection labCollection, Scanner scanner) {
        this.id = id;
        this.labCollection = labCollection;
        this.scanner = scanner;
    }

    @Override
    public void execute() {
        LabWork targetLabWork = labCollection.getElementById(id);
        if (targetLabWork == null) {
            System.out.println("Item with such ID does not exist, try again.\n");
            return;
        }
        LabWork newLabWork = ElementInputManager.readElement(scanner, labCollection);
        targetLabWork.setName(newLabWork.getName());
        targetLabWork.setCoordinates(newLabWork.getCoordinates());
        targetLabWork.setMinimalPoint(newLabWork.getMinimalPoint());
        targetLabWork.setDifficulty(newLabWork.getDifficulty());
        targetLabWork.setAuthor(newLabWork.getAuthor());
        labCollection.initializeSorting();
        System.out.println(String.format("\nItem with ID %s has been updated successfully.\n", id));
    }
}
