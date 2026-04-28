package com.labwork.server.commands;

import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.LabCollection;

/**
 * Команда удаления элемента по индексу.
 */

public class RemoveAt implements Command {

    @Override
    public Response execute(Request request) {
        LabCollection collection = LabCollection.getInstance();
        Object arg = request.getArgument();

        if (arg instanceof Integer) {
            Integer index = (Integer) arg;
            
            if (index < 0 || index >= collection.getLength()) {
                return new Response(false, "Error: index " + index + " is out of bounds (0-" + (collection.getLength() - 1) + ")");
            }
            
            boolean removed = collection.delElementByIndex(index);
            
            if (removed) {
                return new Response(true, "Element at index " + index + " successfully removed");
            } else {
                return new Response(false, "Failed to remove element at index " + index);
            }
        } else {
            return new Response(false, "Error: invalid argument type (expected Integer index)");
        }
    }
}
