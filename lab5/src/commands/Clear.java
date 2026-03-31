package commands;

import utils.Command;
import collection.LabCollection;

public class Clear implements Command {
    private LabCollection labCollection = LabCollection.getInstance();

    public Clear() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        labCollection.clear();
        System.out.println("Collection is cleared.\n");
    }
}
