package com.labwork.server.commands;

import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.LabCollection;
import java.time.LocalDate;

/**
 * Команда добавления элемента, если его значение меньше минимального.
 */

public class AddIfMin implements Command {

    @Override
    public Response execute(Request request) {
        LabCollection collection = LabCollection.getInstance();
        Object arg = request.getArgument();

        if (arg instanceof LabWork) {
            LabWork newElement = (LabWork) arg;
            
            if (collection.isEmpty()) {
                int newId = collection.getNextId();
                newElement.setId(newId);
                newElement.setCreationDate(LocalDate.now());
                collection.addElement(newElement);
                return new Response(true, "Collection was empty. Element added with ID: " + newId);
            }
            
            LabWork minElement = collection.getCollection().stream()
                    .min(LabWork::compareTo)
                    .orElse(null);
            
            if (minElement != null && newElement.compareTo(minElement) < 0) {
                int newId = collection.getNextId();
                newElement.setId(newId);
                newElement.setCreationDate(LocalDate.now());
                collection.addElement(newElement);
                return new Response(true, "Element is less than minimum. Added with ID: " + newId);
            } else {
                return new Response(false, "Element is not less than minimum. Not added");
            }
        } else {
            return new Response(false, "Error: invalid argument type (expected LabWork)");
        }
    }
}
