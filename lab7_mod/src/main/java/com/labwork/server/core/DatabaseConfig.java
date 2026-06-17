package com.labwork.server.core;

/**
 * Конфигурация подключения к базе данных.
 * Для переключения между локальной и кафедральной БД измени значения констант.
 */

public class DatabaseConfig {
    
    // ===== НАСТРОЙКИ ПОДКЛЮЧЕНИЯ =====

    // Для своей БД:
    /*
    public static final String HOST = "localhost";
    public static final String PORT = "5432";
    public static final String DATABASE = "lab7";
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "z17012007";
    */
    
    // Для сервера:
    public static final String HOST = "pg";
    public static final String PORT = "5432";
    public static final String DATABASE = "studs";
    public static final String USERNAME = "s493049";
    public static final String PASSWORD = "s6N9ezA3WM9r2Fhn";
    
    // JDBC URL
    public static final String JDBC_URL = 
        "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE;
    
    // Приватный конструктор, чтобы нельзя было создать экземпляр
    private DatabaseConfig() {}
}
