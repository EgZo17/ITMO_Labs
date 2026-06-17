package com.labwork.server.commands;

import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.AuthManager;
import com.labwork.server.core.DatabaseManager;
import com.labwork.server.core.LabCollection;

import java.time.LocalDate;

public class Add implements Command {
    private final DatabaseManager dbManager;
    private final AuthManager authManager;

    public Add(DatabaseManager dbManager, AuthManager authManager) {
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

            LabWork work = (LabWork) arg;
            work.setCreationDate(LocalDate.now());

            if (dbManager.add(work, userId)) {
                LabCollection.getInstance().addElement(work);
                return new Response(true, "Added. ID: " + work.getId());
            }
            return new Response(false, "Database insert failed");
        } catch (Exception e) {
            return new Response(false, "Error: " + e.getMessage());
        }
    }
}
