package com.labwork.server.commands;

import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import com.labwork.server.core.AuthManager;

public class Register implements Command {
    private final AuthManager authManager;

    public Register(AuthManager authManager) {
        this.authManager = authManager;
    }

    @Override
    public Response execute(Request request) {
        String login = request.getLogin();
        String password = request.getPassword();

        if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
            return new Response(false, "Login and password are required");
        }

        if (authManager.register(login, password)) {
            return new Response(true, "User successfully registered");
        } else {
            return new Response(false, "User with this login already exists");
        }
    }
}
