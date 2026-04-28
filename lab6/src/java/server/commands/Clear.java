package com.labwork.server.commands;

import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.LabCollection;

/**
 * Команда очистки коллекции.
 */

public class Clear implements Command {

    @Override
    public Response execute(Request request) {
        LabCollection collection = LabCollection.getInstance();
        
        if (collection.isEmpty()) {
            return new Response(false, "Collection is already empty");
        }
        
        collection.clear();
        return new Response(true, "Collection successfully cleared");
    }
}
