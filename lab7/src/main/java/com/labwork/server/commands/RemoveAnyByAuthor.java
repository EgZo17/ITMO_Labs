package com.labwork.server.commands;

import com.labwork.common.models.LabWork;
import com.labwork.common.models.Person;
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
            if (!(arg instanceof Person)) {
                return new Response(false, "Error: expected Person argument");
            }

            Person targetAuthor = (Person) arg;
            LabCollection collection = LabCollection.getInstance();

            Optional<LabWork> found = collection.getCollection().stream()
                    .filter(w -> w.getAuthor().getName().equals(targetAuthor.getName()))
                    .findFirst();

            if (found.isEmpty()) {
                return new Response(false, "No element found with author: " + targetAuthor.getName());
            }

            LabWork targetWork = found.get();

            if (!Objects.equals(targetWork.getOwnerId(), userId)) {
                return new Response(false, "Access denied: object belongs to another user");
            }

            if (dbManager.removeById(targetWork.getId())) {
                collection.getCollection().removeIf(w -> w.getId() == targetWork.getId());
                return new Response(true, "Element by author " + targetAuthor.getName() + " successfully removed");
            } else {
                return new Response(false, "Database error: deletion failed");
            }

        } catch (Exception e) {
            return new Response(false, "Error: " + e.getMessage());
        }
    }
}
