package com.labwork.server.commands;

import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.AuthManager;
import com.labwork.server.core.DatabaseManager;
import com.labwork.server.core.LabCollection;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Команда добавления элемента, если его значение меньше минимального.
 */
public class AddIfMin implements Command {
    private final DatabaseManager dbManager;
    private final AuthManager authManager;

    public AddIfMin(DatabaseManager dbManager, AuthManager authManager) {
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

            LabWork newElement = (LabWork) arg;
            newElement.setCreationDate(LocalDate.now());
            
            LabCollection collection = LabCollection.getInstance();

            Optional<LabWork> minOpt = collection.getCollection().stream()
                    .min(LabWork::compareTo);

            boolean shouldAdd = false;
            if (minOpt.isEmpty()) {
                shouldAdd = true;
            } else {
                LabWork minElement = minOpt.get();
                if (newElement.compareTo(minElement) < 0) {
                    shouldAdd = true;
                }
            }

            if (shouldAdd) {
                if (dbManager.add(newElement, userId)) {
                    newElement.setOwnerId(userId);
                    collection.addElement(newElement);
                    return new Response(true, "Element added successfully. ID: " + newElement.getId());
                } else {
                    return new Response(false, "Database error: insertion failed");
                }
            } else {
                return new Response(false, "Element is not less than minimum. Not added");
            }

        } catch (Exception e) {
            return new Response(false, "Error: " + e.getMessage());
        }
    }
}
