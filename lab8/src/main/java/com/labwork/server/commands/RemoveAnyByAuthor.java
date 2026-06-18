package com.labwork.server.commands;

import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.AuthManager;
import com.labwork.server.core.DatabaseManager;
import com.labwork.server.core.LabCollection;
import java.util.Objects;
import java.util.Optional;

/**
 * Команда удаления одного элемента по автору.
 */
public class RemoveAnyByAuthor implements Command {
    private final DatabaseManager dbManager;
    private final AuthManager authManager;

    public RemoveAnyByAuthor(DatabaseManager dbManager, AuthManager authManager) {
        this.dbManager = dbManager;
        this.authManager = authManager;
    }

    @Override
    public Response execute(Request request) {
        int userId = authManager.validateAndGetUserId(request);
        if (userId == -2) return new Response(false, "Authorization is required (log in)");
        if (userId == -1) return new Response(false, "Wrong login or password");

        try {
            Object arg = request.getArgument();
            if (!(arg instanceof String)) {
                return new Response(false, "Error: expected author name (String)");
            }

            String authorName = (String) arg;
            LabCollection collection = LabCollection.getInstance();

            // Ищем первый объект с таким автором, принадлежащий текущему пользователю
            Optional<LabWork> found = collection.getCollection().stream()
                    .filter(w -> w.getAuthor() != null && 
                                 w.getAuthor().getName().equals(authorName) &&
                                 Objects.equals(w.getOwnerId(), userId))
                    .findFirst();

            if (found.isEmpty()) {
                return new Response(false, "No element found with author: " + authorName);
            }

            LabWork targetWork = found.get();

            // Удаляем из БД
            if (dbManager.removeById(targetWork.getId())) {
                collection.delElementById(targetWork.getId());
                return new Response(true, "Element by author " + authorName + " successfully removed");
            } else {
                return new Response(false, "Database error: deletion failed");
            }

        } catch (Exception e) {
            return new Response(false, "Error: " + e.getMessage());
        }
    }
}
