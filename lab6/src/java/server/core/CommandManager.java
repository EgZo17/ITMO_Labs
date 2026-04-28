package com.labwork.server.core;

import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Модуль обработки полученных команд (Invoker).
 * Сопоставляет имена команд с их реализациями и запускает выполнение.
 */

public class CommandManager {
    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);
    
    private final Map<String, Command> commands = new HashMap<>();

    public CommandManager() {
        // Регистрация всех доступных команд
        commands.put("help", new Help());
        commands.put("info", new Info());
        commands.put("show", new Show());
        commands.put("add", new Add());
        commands.put("update", new Update());
        commands.put("remove_by_id", new RemoveById());
        commands.put("clear", new Clear());
        commands.put("remove_at", new RemoveAt());
        commands.put("add_if_min", new AddIfMin());
        commands.put("remove_lower", new RemoveLower());
        commands.put("remove_any_by_author", new RemoveAnyByAuthor());
        commands.put("filter_by_difficulty", new FilterByDifficulty());
        commands.put("print_field_descending_author", new PrintFieldDescendingAuthor());
        
        logger.info("CommandManager initialized with {} commands", commands.size());
    }

    /**
     * Выполняет команду на основе полученного запроса.
     *
     * @param request запрос от клиента
     * @return результат выполнения
     */
    public Response execute(Request request) {
        String cmdName = request.getCommandName();
        logger.info("Processing command: {}", cmdName);
        
        Command command = commands.get(cmdName);
        if (command != null) {
            return command.execute(request);
        } else {
            logger.warn("Unknown command received: {}", cmdName);
            return new Response(false, "Unknown command: " + cmdName + ". Type 'help' for available commands.");
        }
    }
}
