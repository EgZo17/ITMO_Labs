package com.labwork.server.commands;

import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.LabCollection;

import java.util.LinkedList;

/**
 * Команда отображения всех элементов коллекции.
 */

public class Show implements Command {

    @Override
    public Response execute(Request request) {
        LabCollection collection = LabCollection.getInstance();

        if (collection.isEmpty()) {
            return new Response(false, "Collection is empty");
        }

        LinkedList<LabWork> listToSend = new LinkedList<>(collection.getCollection());
        
        listToSend.sort(null);

        return new Response(true, "Collection elements:", listToSend);
    }
}
