package com.labwork.server.commands;

import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.LabCollection;

/**
 * Команда вывода информации о коллекции.
 */

public class Info implements Command {

    @Override
    public Response execute(Request request) {
        LabCollection collection = LabCollection.getInstance();

        StringBuilder info = new StringBuilder();
        info.append("Collection type: ").append(collection.getClass().getName()).append("\n");
        info.append("Initialization date: ").append(collection.getInitializationDate()).append("\n");
        info.append("Number of elements: ").append(collection.getLength());
        
        return new Response(true, info.toString());
    }
}
