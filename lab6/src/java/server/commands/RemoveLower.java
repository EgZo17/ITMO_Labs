package com.labwork.server.commands;

import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.LabCollection;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Команда удаления всех элементов, меньших чем заданный.
 */

public class RemoveLower implements Command {

    @Override
    public Response execute(Request request) {
        LabCollection collection = LabCollection.getInstance();
        Object arg = request.getArgument();

        if (arg instanceof LabWork) {
            LabWork newElement = (LabWork) arg;
            
            long removedCount = collection.getCollection().stream()
                    .filter(existing -> existing.compareTo(newElement) < 0)
                    .count();
            
            if (removedCount == 0) {
                return new Response(false, "No elements are less than the specified one");
            }
            
            LinkedList<LabWork> filtered = collection.getCollection().stream()
                    .filter(existing -> existing.compareTo(newElement) >= 0)
                    .collect(Collectors.toCollection(LinkedList::new));
            
            collection.setCollection(filtered);
            
            return new Response(true, "Successfully removed " + removedCount + " elements that are less than specified");
        } else {
            return new Response(false, "Error: invalid argument type (expected LabWork)");
        }
    }
}
