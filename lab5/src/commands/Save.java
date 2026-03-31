package com.labwork.commands;

import com.labwork.utils.Command;

public class Save implements Command {
    public Save() {

    }

    @Override
    public void execute(String[] parameters) {
        if (!validate(parameters)) {
            return;
        }
    }
}
