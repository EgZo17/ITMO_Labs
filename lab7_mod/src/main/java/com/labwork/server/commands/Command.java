package com.labwork.server.commands;

import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;

/**
 * Интерфейс для выполнения команд на сервере.
 */

public interface Command {
    /**
     * Выполняет команду.
     *
     * @param request Запрос от клиента (содержит аргументы).
     * @return Ответ для отправки клиенту.
     */
    Response execute(Request request);
}
