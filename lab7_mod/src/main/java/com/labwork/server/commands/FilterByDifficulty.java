package com.labwork.server.commands;

import com.labwork.common.models.Difficulty;
import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.LabCollection;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Команда фильтрации элементов по сложности.
 */

public class FilterByDifficulty implements Command {

    @Override
    public Response execute(Request request) {
        LabCollection collection = LabCollection.getInstance();
        Object arg = request.getArgument();

        if (arg instanceof Difficulty) {
            Difficulty targetDifficulty = (Difficulty) arg;
            
            LinkedList<LabWork> filtered = collection.getCollection().stream()
                    .filter(lw -> targetDifficulty.equals(lw.getDifficulty()))
                    .collect(Collectors.toCollection(LinkedList::new));
            
            if (filtered.isEmpty()) {
                return new Response(false, "No elements with difficulty " + targetDifficulty);
            }
            
            return new Response(true, "Elements with difficulty " + targetDifficulty + ":", filtered);
        } else {
            return new Response(false, "Error: invalid argument type (expected Difficulty)");
        }
    }
}
