package com.labwork.server.commands;

import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.AuthManager;
import com.labwork.server.core.DatabaseManager;
import com.labwork.server.core.LabCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Objects;

public class RemoveById implements Command {
    private static final Logger logger = LoggerFactory.getLogger(RemoveById.class);
    private final DatabaseManager dbManager;
    private final AuthManager authManager;

    public RemoveById(DatabaseManager dbManager, AuthManager authManager) {
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
                return new Response(false, "Error: expected Integer ID");
            }
            int id = (Integer) arg;

            LabWork work = LabCollection.getInstance().getElementById(id);
            if (work == null) return new Response(false, "Element not found");
            if (!Objects.equals(work.getOwnerId(), userId)) {
                return new Response(false, "Access denied: object belongs to another user");
            }

            if (dbManager.removeById(id)) {
                boolean removed = LabCollection.getInstance().delElementById(id);
                if (!removed) {
                    logger.warn("Element with ID {} removed from DB but not found in collection", id);
                }
                return new Response(true, "Element with ID " + id + " removed");
            }
            return new Response(false, "Element not found in database");
            
        } catch (SQLException e) {
            logger.error("SQL error in remove_by_id [id={}]", request.getArgument(), e);
            return new Response(false, "Database error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected crash in remove_by_id", e);
            return new Response(false, "Server error: " + e.getClass().getSimpleName());
        }
    }
}
