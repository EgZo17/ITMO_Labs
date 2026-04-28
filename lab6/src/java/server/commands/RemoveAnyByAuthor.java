package com.labwork.server.commands;

import com.labwork.common.models.Person;
import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.LabCollection;
import java.util.Optional;

/**
 * Команда удаления одного элемента по автору.
 */

public class RemoveAnyByAuthor implements Command {

    @Override
    public Response execute(Request request) {
        LabCollection collection = LabCollection.getInstance();
        Object arg = request.getArgument();

        if (arg instanceof Person) {
            Person targetAuthor = (Person) arg;
            
            Optional<LabWork> found = collection.getCollection().stream()
                    .filter(lw -> lw.getAuthor().getName().equals(targetAuthor.getName()))
                    .findFirst();
            
            if (found.isPresent()) {
                LabWork toRemove = found.get();
                collection.delElement(toRemove);
                return new Response(true, "Element by author " + targetAuthor.getName() + " successfully removed");
            } else {
                return new Response(false, "No element found with author " + targetAuthor.getName());
            }
        } else {
            return new Response(false, "Error: invalid argument type (expected Person)");
        }
    }
}
