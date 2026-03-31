package commands;

import utils.Command;
import utils.ElementInputManager;
import utils.GlobalScanner;
import java.util.Scanner;
import collection.LabCollection;
import data.LabWork;

public class Update implements Command {
    private int id;
    private LabCollection labCollection = LabCollection.getInstance();
    private Scanner scanner = GlobalScanner.getScanner();

    public Update() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        LabWork targetLabWork = labCollection.getElementById(id);
        if (targetLabWork == null) {
            System.out.println("Item with such ID does not exist, try again.\n");
            return;
        }
        LabWork newLabWork = ElementInputManager.readElement(scanner);
        targetLabWork.setName(newLabWork.getName());
        targetLabWork.setCoordinates(newLabWork.getCoordinates());
        targetLabWork.setMinimalPoint(newLabWork.getMinimalPoint());
        targetLabWork.setDifficulty(newLabWork.getDifficulty());
        targetLabWork.setAuthor(newLabWork.getAuthor());
        labCollection.initializeSorting();
        System.out.println(String.format("\nItem with ID %s has been updated successfully.\n", id));
    }

    @Override
    public boolean validate(String[] parameters) {
        if (parameters.length != 1) {
            System.out.println("Error: Invalid command signature. Enter \"help\" to see more.");
            return false;
        }
        try {
            this.id = Integer.parseInt((String) parameters[0]);
        } catch (Exception e) {
            System.out.println("Error: Invalid command signature. Enter \"help\" to see more.");
            return false;
        }
        return true;
    }
}
