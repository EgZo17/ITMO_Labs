package commands;

import utils.Command;
import enums.CommandDescription;

public class Help implements Command {
    public Help() {}

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
        System.out.println("=== Available Commands ===");
        for (CommandDescription cmd : CommandDescription.values()) {
            System.out.printf("  %-42s - %s%n", 
                cmd.name().toLowerCase() + " " + cmd.getSignature(), 
                cmd.getDescription());
        }
    }
}
