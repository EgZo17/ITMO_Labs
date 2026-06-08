package com.labwork.server.commands;

import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.AuthManager;
import com.labwork.server.core.DatabaseManager;
import com.labwork.server.core.LabCollection;
import java.util.List;
import java.util.Objects;

/**
 * Команда удаления всех элементов, меньших чем заданный.
 */
public class RemoveLower implements Command {
    private final DatabaseManager dbManager;
    private final AuthManager authManager;

    public RemoveLower(DatabaseManager dbManager, AuthManager authManager) {
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
            if (!(arg instanceof LabWork)) {
                return new Response(false, "Error: expected LabWork argument");
            }

            LabWork compareElement = (LabWork) arg;
            LabCollection collection = LabCollection.getInstance();

            List<LabWork> toRemove = collection.getCollection().stream()
                    .filter(existing -> existing.compareTo(compareElement) < 0)
                    .toList();

            if (toRemove.isEmpty()) {
                return new Response(true, "No elements are less than the specified one");
            }

            for (LabWork work : toRemove) {
                if (!Objects.equals(work.getOwnerId(), userId)) {
                    return new Response(false, "Access denied: cannot remove elements belonging to other users");
                }
            }

            boolean allSuccess = true;
            for (LabWork work : toRemove) {
                if (!dbManager.removeById(work.getId())) {
                    allSuccess = false;
                    break;
                }
            }

            if (allSuccess) {
                collection.getCollection().removeAll(toRemove);
                return new Response(true, "Successfully removed " + toRemove.size() + " elements");
            } else {
                return new Response(false, "Database error: partial removal failed");
            }

        } catch (Exception e) {
            return new Response(false, "Error: " + e.getMessage());
        }
    }
}
