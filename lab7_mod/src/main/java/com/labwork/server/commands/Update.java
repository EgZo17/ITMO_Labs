package com.labwork.server.commands;

import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.AuthManager;
import com.labwork.server.core.DatabaseManager;
import com.labwork.server.core.LabCollection;
import java.util.List;

/**
 * Команда обновления элемента по ID.
 * Аргумент: Object[] {Integer id, LabWork newData}
 */
public class Update implements Command {
    private final DatabaseManager dbManager;
    private final AuthManager authManager;

    public Update(DatabaseManager dbManager, AuthManager authManager) {
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
            if (!(arg instanceof Object[])) {
                return new Response(false, "Error: update requires [Integer id, LabWork newData]");
            }

            Object[] args = (Object[]) arg;
            if (args.length != 2 || !(args[0] instanceof Integer) || !(args[1] instanceof LabWork)) {
                return new Response(false, "Error: invalid argument format for update");
            }

            int id = (Integer) args[0];
            LabWork newData = (LabWork) args[1];
            newData.setId(id);

            LabCollection collection = LabCollection.getInstance();
            List<LabWork> list = collection.getCollection();
            
            LabWork existing = null;
            for (LabWork w : list) {
                if (w.getId() == id) {
                    existing = w;
                    break;
                }
            }

            if (existing == null) {
                return new Response(false, "No element with ID " + id + " found");
            }

            if (!java.util.Objects.equals(existing.getOwnerId(), userId)) {
                return new Response(false, "Access denied: object belongs to another user");
            }

            if (dbManager.update(newData)) {
                
                existing.setName(newData.getName());
                existing.setCoordinates(newData.getCoordinates());
                existing.setMinimalPoint(newData.getMinimalPoint());
                existing.setDifficulty(newData.getDifficulty());
                existing.setAuthor(newData.getAuthor());
                
                collection.initializeSorting();
                
                return new Response(true, "Element with ID " + id + " successfully updated");
            } else {
                return new Response(false, "Database update failed or element not found");
            }

        } catch (Exception e) {
            return new Response(false, "Error: " + e.getMessage());
        }
    }
}
