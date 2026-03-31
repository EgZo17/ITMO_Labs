package commands;

import utils.Command;
import collection.LabCollection;

public class Show implements Command {
    private LabCollection labCollection = LabCollection.getInstance();

    public Show() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        System.out.println(labCollection);
    }
}
