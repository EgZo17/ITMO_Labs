package com.labwork.server.commands;

import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.AuthManager;

public class Login implements Command {
    private final AuthManager authManager;

    public Login(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public Response execute(Request request) {
        String login = request.getLogin();
        String password = request.getPassword();

        if (login == null || login.isEmpty()) {
            return new Response(false, "Login is required");
        }

        int userId = authManager.login(login, password);
        if (userId != -1) {
            // В ответе возвращаем ID, чтобы клиент мог его использовать
            return new Response(true, "Login successful. Welcome, " + login);
        } else {
            return new Response(false, "Wrong login or password");
        }
    }
}
