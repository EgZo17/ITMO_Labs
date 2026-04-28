package com.labwork.server.commands;

import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.LabCollection;

/**
 * Команда удаления элемента по ID.
 */

public class RemoveById implements Command {

    @Override
    public Response execute(Request request) {
        LabCollection collection = LabCollection.getInstance();
        Object arg = request.getArgument();

        if (arg instanceof Integer) {
            Integer id = (Integer) arg;

            boolean removed = collection.delElementById(id);
            
            if (removed) {
                return new Response(true, "Element with ID " + id + " successfully removed");
            } else {
                return new Response(false, "No element with ID " + id + " found");
            }
        } else {
            return new Response(false, "Error: invalid argument type (expected Integer ID)");
        }
    }
}
