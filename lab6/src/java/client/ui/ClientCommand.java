package com.labwork.client.ui;

import java.util.Arrays;
import com.labwork.common.protocol.Request;

/**
 * Перечисление всех доступных клиентских команд.
 * Реализует логику маршрутизации через полиморфизм констант.
 */

public enum ClientCommand {

    HELP("help") {
        @Override
        public boolean execute(ClientApp app, String args) {
            app.sendRequest(new Request("help", null));
            return true;
        }
    },
    INFO("info") {
        @Override
        public boolean execute(ClientApp app, String args) {
            app.sendRequest(new Request("info", null));
            return true;
        }
    },
    SHOW("show") {
        @Override
        public boolean execute(ClientApp app, String args) {
            app.sendRequest(new Request("show", null));
            return true;
        }
    },
    CLEAR("clear") {
        @Override
        public boolean execute(ClientApp app, String args) {
            app.sendRequest(new Request("clear", null));
            return true;
        }
    },
    REMOVE_BY_ID("remove_by_id") {
        @Override
        public boolean execute(ClientApp app, String args) {
            app.handleIntCommand("remove_by_id", args);
            return true;
        }
    },
    REMOVE_AT("remove_at") {
        @Override
        public boolean execute(ClientApp app, String args) {
            app.handleIntCommand("remove_at", args);
            return true;
        }
    },
    FILTER_BY_DIFFICULTY("filter_by_difficulty") {
        @Override
        public boolean execute(ClientApp app, String args) {
            app.handleEnumCommand("filter_by_difficulty", args);
            return true;
        }
    },
    REMOVE_ANY_BY_AUTHOR("remove_any_by_author") {
        @Override
        public boolean execute(ClientApp app, String args) {
            app.handleAuthorCommand("remove_any_by_author", args);
            return true;
        }
    },
    PRINT_FIELD_DESCENDING_AUTHOR("print_field_descending_author") {
        @Override
        public boolean execute(ClientApp app, String args) {
            app.sendRequest(new Request("print_field_descending_author", null));
            return true;
        }
    },
    ADD("add") {
        @Override
        public boolean execute(ClientApp app, String args) {
            app.handleLabWorkCommand("add");
            return true;
        }
    },
    ADD_IF_MIN("add_if_min") {
        @Override
        public boolean execute(ClientApp app, String args) {
            app.handleLabWorkCommand("add_if_min");
            return true;
        }
    },
    REMOVE_LOWER("remove_lower") {
        @Override
        public boolean execute(ClientApp app, String args) {
            app.handleLabWorkCommand("remove_lower");
            return true;
        }
    },
    UPDATE("update") {
        @Override
        public boolean execute(ClientApp app, String args) {
            app.handleUpdateCommand(args);
            return true;
        }
    },
    EXECUTE_SCRIPT("execute_script") {
        @Override
        public boolean execute(ClientApp app, String args) {
            app.executeScript(args);
            return true;
        }
    },
    EXIT("exit") {
        @Override
        public boolean execute(ClientApp app, String args) {
            System.out.println("Exiting client application...");
            return false;
        }
    };

    private final String commandName;

    ClientCommand(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    /**
     * Находит команду по строке ввода (регистронезависимо).
     */
    public static ClientCommand fromName(String input) {
        if (input == null) return null;
        return Arrays.stream(values())
                .filter(cmd -> cmd.commandName.equalsIgnoreCase(input))
                .findFirst()
                .orElse(null);
    }

    /**
     * Абстрактный метод выполнения.
     * @param app ссылка на приложение (для доступа к сокету и сканеру)
     * @param args аргументы команды
     * @return true - продолжать работу, false - завершить
     */
    public abstract boolean execute(ClientApp app, String args);
}
