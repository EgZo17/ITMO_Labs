package com.labwork.common.protocol;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Класс-контейнер для передачи результата выполнения команды от сервера к клиенту.
 */

public class Response implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean success;
    private final String message;
    private final LinkedList<?> data;

    /**
     * Конструктор для команд, не возвращающих данные (например, add, clear, remove_by_id).
     *
     * @param success статус выполнения (true - успешно, false - ошибка)
     * @param message текст для вывода пользователю
     */
    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
    }

    /**
     * Конструктор для команд, возвращающих коллекцию объектов (например, show, filter_by_difficulty).
     *
     * @param success статус выполнения
     * @param message текст для вывода пользователю
     * @param data    отсортированная коллекция объектов (LabWork или Person)
     */
    public Response(boolean success, String message, LinkedList<?> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public LinkedList<?> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", dataCount=" + (data != null ? data.size() : 0) +
                '}';
    }
}
