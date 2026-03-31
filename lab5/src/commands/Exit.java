package commands;

import utils.Command;

public class Exit implements Command {
    public Exit() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        System.out.println("Exiting the program...");
        System.exit(0);
    }
}
