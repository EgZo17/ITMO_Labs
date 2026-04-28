package com.labwork.server.commands;

import com.labwork.common.models.LabWork;
import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.LabCollection;

/**
 * Команда обновления элемента по ID.
 * Аргумент: Object[] {Integer id, LabWork newData}
 */

public class Update implements Command {

    @Override
    public Response execute(Request request) {
        LabCollection collection = LabCollection.getInstance();
        Object arg = request.getArgument();

        if (arg instanceof Object[]) {
            Object[] args = (Object[]) arg;
            
            if (args.length != 2 || !(args[0] instanceof Integer) || !(args[1] instanceof LabWork)) {
                return new Response(false, "Error: update requires [Integer id, LabWork newData]");
            }
            
            Integer id = (Integer) args[0];
            LabWork newData = (LabWork) args[1];
            
            LabWork existing = collection.getElementById(id);
            
            if (existing == null) {
                return new Response(false, "No element with ID " + id + " found");
            }
            
            try {
                existing.setName(newData.getName());
                existing.setCoordinates(newData.getCoordinates());
                existing.setMinimalPoint(newData.getMinimalPoint());
                existing.setDifficulty(newData.getDifficulty());
                existing.setAuthor(newData.getAuthor());
                
                collection.initializeSorting();
                
                return new Response(true, "Element with ID " + existing.getId() + " successfully updated");
                
            } catch (IllegalArgumentException e) {
                return new Response(false, "Error: invalid data - " + e.getMessage());
            }
            
        } else {
            return new Response(false, "Error: invalid argument type for update command");
        }
    }
}
