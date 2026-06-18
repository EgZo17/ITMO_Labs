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
 * Команда очистки коллекции.
 * Удаляет только элементы, принадлежащие текущему авторизованному пользователю.
 */
public class Clear implements Command {
    private final DatabaseManager dbManager;
    private final AuthManager authManager;

    public Clear(DatabaseManager dbManager, AuthManager authManager) {
        this.dbManager = dbManager;
        this.authManager = authManager;
    }

    @Override
    public Response execute(Request request) {
        int userId = authManager.validateAndGetUserId(request);
        if (userId == -2) return new Response(false, "Authorization is required (log in)");
        if (userId == -1) return new Response(false, "Wrong login or password");

        try {
            LabCollection collection = LabCollection.getInstance();

            // Находим элементы текущего пользователя
            List<LabWork> myElements = collection.getCollection().stream()
                    .filter(w -> Objects.equals(w.getOwnerId(), userId))
                    .collect(Collectors.toList());

            if (myElements.isEmpty()) {
                return new Response(false, "No elements owned by you to clear");
            }

            int removedCount = 0;
            boolean allSuccess = true;
            
            for (LabWork work : myElements) {
                if (dbManager.removeById(work.getId())) {
                    collection.delElementById(work.getId());
                    removedCount++;
                } else {
                    allSuccess = false;
                    break;
                }
            }

            if (allSuccess) {
                return new Response(true, "Cleared " + removedCount + " elements");
            } else {
                return new Response(false, "Database error: partial clear failed");
            }

        } catch (Exception e) {
            return new Response(false, "Error: " + e.getMessage());
        }
    }
}
