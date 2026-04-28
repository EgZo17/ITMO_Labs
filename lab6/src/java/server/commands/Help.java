package com.labwork.server.commands;

import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;

/**
 * Команда вывода справки по всем доступным командам.
 */

public class Help implements Command {

    @Override
    public Response execute(Request request) {
        StringBuilder helpText = new StringBuilder();
        helpText.append("=== Available Commands ===\n");

        for (CommandDescription cmd : CommandDescription.values()) {
            String cmdName = cmd.name().toLowerCase();
            helpText.append(String.format("  %-42s - %s\n", 
                cmdName + " " + cmd.getSignature(), 
                cmd.getDescription()));
        }
        
        return new Response(true, helpText.toString());
    }
}
