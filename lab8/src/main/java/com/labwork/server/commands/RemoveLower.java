package com.labwork.server.commands;

import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.AuthManager;
import com.labwork.server.core.DatabaseManager;
import com.labwork.server.core.LabCollection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Команда удаления всех элементов с minimalPoint меньше заданного.
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
            if (!(arg instanceof Float)) {
                return new Response(false, "Error: expected minimalPoint (Float)");
            }

            Float minimalPoint = (Float) arg;
            LabCollection collection = LabCollection.getInstance();

            List<LabWork> toRemove = collection.getCollection().stream()
                .filter(w -> w.getMinimalPoint() != null && 
                             w.getMinimalPoint() < minimalPoint &&
                             Objects.equals(w.getOwnerId(), userId))
                .collect(Collectors.toList());

            if (toRemove.isEmpty()) {
                return new Response(true, "No elements with minimalPoint < " + minimalPoint);
            }

            int removedCount = 0;
            for (LabWork work : toRemove) {
                if (dbManager.removeById(work.getId())) {
                    collection.delElementById(work.getId());
                    removedCount++;
                }
            }

            return new Response(true, "Removed " + removedCount + " elements with minimalPoint < " + minimalPoint);

        } catch (Exception e) {
            return new Response(false, "Error: " + e.getMessage());
        }
    }
}
