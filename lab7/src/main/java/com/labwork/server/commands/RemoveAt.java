package com.labwork.server.commands;

import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.AuthManager;
import com.labwork.server.core.DatabaseManager;
import com.labwork.server.core.LabCollection;
import java.util.LinkedList;
import java.util.Objects;

/**
 * Команда удаления элемента по индексу в коллекции.
 */
public class RemoveAt implements Command {
    private final DatabaseManager dbManager;
    private final AuthManager authManager;

    public RemoveAt(DatabaseManager dbManager, AuthManager authManager) {
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
            if (!(arg instanceof Integer)) {
                return new Response(false, "Error: expected Integer index");
            }

            int index = (Integer) arg;
            LabCollection collection = LabCollection.getInstance();
            LinkedList<LabWork> list = (LinkedList<LabWork>) collection.getCollection();

            if (index < 0 || index >= list.size()) {
                return new Response(false, "Error: index " + index + " is out of bounds");
            }

            LabWork targetWork = list.get(index);

            if (!Objects.equals(targetWork.getOwnerId(), userId)) {
                return new Response(false, "Access denied: object belongs to another user");
            }

            if (dbManager.removeById(targetWork.getId())) {
                // 5️⃣ Только при успехе удаляем из памяти
                list.remove(index);
                return new Response(true, "Element at index " + index + " removed successfully");
            } else {
                return new Response(false, "Database error: deletion failed");
            }

        } catch (Exception e) {
            return new Response(false, "Error: " + e.getMessage());
        }
    }
}
