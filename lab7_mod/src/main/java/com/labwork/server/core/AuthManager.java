package com.labwork.server.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labwork.common.protocol.Request;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class AuthManager {
    private static final Logger logger = LoggerFactory.getLogger(AuthManager.class);

    /**
     * Хэширование пароля SHA-512
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-512 not available", e);
        }
    }

    /**
     * Регистрация пользователя
     * @return true если успех, false если пользователь занят
     */
    public boolean register(String username, String password) {
        String hash = hashPassword(password);
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.USERNAME, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, hash);
            stmt.executeUpdate();
            logger.info("User registered: {}", username);
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            logger.warn("Registration failed: User {} already exists", username);
            return false; // Пользователь уже есть
        } catch (SQLException e) {
            logger.error("DB Error during registration", e);
            return false;
        }
    }

    /**
     * Проверка логина и пароля
     * @return ID пользователя, если успешно; -1 если неверные данные
     */
    public int login(String username, String password) {
        String hash = hashPassword(password);
        String sql = "SELECT id FROM users WHERE username = ? AND password_hash = ?";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.USERNAME, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, hash);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            logger.error("DB Error during login", e);
        }
        return -1;
    }

    /**
     * Проверяет логин/пароль из запроса.
     * @return >0  — ID пользователя (успех)
     *         -1  — неверный логин или пароль
     *         -2  — логин/пароль не переданы в запросе
     */
    public int validateAndGetUserId(Request request) {
        if (request.getLogin() == null || request.getPassword() == null) {
            return -2;
        }
        return login(request.getLogin(), request.getPassword());
    }
}
