package com.labwork.server.commands;

import com.labwork.common.models.Person;
import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.LabCollection;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Команда вывода значений поля author в порядке убывания.
 */

public class PrintFieldDescendingAuthor implements Command {

    @Override
    public Response execute(Request request) {
        LabCollection collection = LabCollection.getInstance();
        
        if (collection.isEmpty()) {
            return new Response(false, "Collection is empty");
        }
        
        LinkedList<Person> authors = collection.getCollection().stream()
                .map(LabWork::getAuthor)
                .sorted((p1, p2) -> -p1.compareTo(p2)) // Сортировка по убыванию
                .collect(Collectors.toCollection(LinkedList::new));
        
        return new Response(true, "Authors in descending order:", authors);
    }
}
