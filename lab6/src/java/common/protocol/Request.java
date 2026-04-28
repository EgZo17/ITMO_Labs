package com.labwork.common.protocol;

import java.io.Serializable;

/**
 * Класс-контейнер для передачи команды от клиента к серверу.
 * Реализует Serializable для сетевой передачи.
 */

public class Request implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String commandName;
    private final Object argument;

    /**
     * Создает новый запрос.
     *
     * @param commandName имя команды (например, "add", "update", "show")
     * @param argument аргумент команды. Может быть:
     *                 - null (для команд без аргументов: show, clear, info)
     *                 - одиночным объектом (LabWork для add, String/Integer для remove_by_id и т.д.)
     *                 - массивом Object[] из двух элементов для команды update: {id, newLabWork}
     */
    public Request(String commandName, Object argument) {
        if (commandName == null || commandName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя команды не может быть пустым");
        }
        this.commandName = commandName;
        this.argument = argument;
    }

    public String getCommandName() {
        return commandName;
    }

    public Object getArgument() {
        return argument;
    }

    @Override
    public String toString() {
        return "Request{" +
                "commandName='" + commandName + '\'' +
                ", argumentType=" + (argument != null ? argument.getClass().getSimpleName() : "null") +
                '}';
    }
}
