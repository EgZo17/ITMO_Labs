package com.labwork.commands;

import com.labwork.utils.Command;

public class ExecuteScript implements Command {
    public ExecuteScript() {

    }

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
    }

    @Override
    public boolean validate(String[] parameters) {
        if (parameters.length != 1) {
            System.out.println("Error: Invalid command signature. Enter \"help\" to see more.");
            return false;
        }
        try {
            // TODO
        } catch (Exception e) {
            System.out.println("Error: Invalid command signature. Enter \"help\" to see more.");
            return false;
        }
        return true;
    }
}
