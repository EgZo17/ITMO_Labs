package com.labwork.server.commands;

import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.LabCollection;

import java.time.LocalDate;

/**
 * Команда добавления элемента в коллекцию.
 */

public class Add implements Command {

    @Override
    public Response execute(Request request) {
        LabCollection collection = LabCollection.getInstance();
        Object arg = request.getArgument();

        if (arg instanceof LabWork) {
            LabWork labWork = (LabWork) arg;

            int newId = collection.getNextId();
            labWork.setId(newId);

            labWork.setCreationDate(LocalDate.now());

            collection.addElement(labWork);
            
            return new Response(true, "Element successfully added with ID: " + newId);
        } else {
            return new Response(false, "Error: invalid argument type (expected LabWork)");
        }
    }
}
