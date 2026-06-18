package com.labwork.server.core;

import com.labwork.common.models.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Менеджер работы с PostgreSQL.
 * Потокобезопасен: каждый метод открывает и закрывает своё соединение.
 */
public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    public DatabaseManager() {
        try {
            Class.forName("org.postgresql.Driver");
            logger.info("PostgreSQL JDBC Driver loaded.");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQL driver not found", e);
        }
    }

    /**
     * Загружает коллекцию из БД в LabCollection.getInstance() при старте сервера.
     */
    public void loadCollection() throws SQLException {
        logger.info("Loading collection from database...");
        LabCollection collection = LabCollection.getInstance();

        collection.clear();

        String sql = "SELECT * FROM lab_works ORDER BY id ASC";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.USERNAME, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            Map<Integer, String> userLogins = loadAllUserLogins(conn);
            
            int count = 0;
            while (rs.next()) {
                LabWork work = mapToLabWork(rs);

                if (work.getOwnerId() != null) {
                    String ownerLogin = userLogins.get(work.getOwnerId());
                    work.setOwnerLogin(ownerLogin);
                }
                
                collection.addElement(work);
                count++;
            }
            logger.info("Successfully loaded {} elements from database.", count);
        }
    }

    /**
     * Загружает все логины пользователей из БД (для оптимизации).
     */
    private Map<Integer, String> loadAllUserLogins(Connection conn) throws SQLException {
        Map<Integer, String> userLogins = new HashMap<>();
        String sql = "SELECT id, username FROM users";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                userLogins.put(rs.getInt("id"), rs.getString("username"));
            }
        }
        return userLogins;
    }

    /**
     * Добавляет элемент в БД. Возвращает true при успехе.
     * При успехе обновляет ID в объекте work.
     */
    public boolean add(LabWork work, int ownerId) throws SQLException {
        String sql = "INSERT INTO lab_works (name, coordinates_x, coordinates_y, creation_date, " +
                    "minimal_point, difficulty, person_name, person_height, person_eye_color, " +
                    "location_x, location_y, location_z, owner_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.USERNAME, DatabaseConfig.PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, work.getName());
            stmt.setDouble(2, work.getCoordinates().getX());
            stmt.setInt(3, work.getCoordinates().getY());
            stmt.setDate(4, java.sql.Date.valueOf(work.getCreationDate()));
            stmt.setFloat(5, work.getMinimalPoint());
            stmt.setString(6, work.getDifficulty() != null ? work.getDifficulty().name() : null);
            stmt.setString(7, work.getAuthor().getName());
            stmt.setDouble(8, work.getAuthor().getHeight());
            stmt.setString(9, work.getAuthor().getEyeColor().name());
            stmt.setInt(10, work.getAuthor().getLocation().getX());
            stmt.setDouble(11, work.getAuthor().getLocation().getY());
            stmt.setDouble(12, work.getAuthor().getLocation().getZ());
            stmt.setInt(13, ownerId); 

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    work.setId(rs.getInt(1));

                    String ownerLogin = getUserLoginById(ownerId);
                    work.setOwnerLogin(ownerLogin);
                    
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Обновляет существующий элемент в БД по ID.
     */
    public boolean update(LabWork work) throws SQLException {
        String sql = "UPDATE lab_works SET name=?, coordinates_x=?, coordinates_y=?, " +
                     "creation_date=?, minimal_point=?, difficulty=?, person_name=?, " +
                     "person_height=?, person_eye_color=?, location_x=?, location_y=?, location_z=? " +
                     "WHERE id=?";
        
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.USERNAME, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, work.getName());
            stmt.setDouble(2, work.getCoordinates().getX());
            stmt.setInt(3, work.getCoordinates().getY());
            stmt.setDate(4, java.sql.Date.valueOf(work.getCreationDate()));
            stmt.setFloat(5, work.getMinimalPoint());
            stmt.setString(6, work.getDifficulty() != null ? work.getDifficulty().name() : null);
            stmt.setString(7, work.getAuthor().getName());
            stmt.setDouble(8, work.getAuthor().getHeight());
            stmt.setString(9, work.getAuthor().getEyeColor().name());
            stmt.setInt(10, work.getAuthor().getLocation().getX());
            stmt.setDouble(11, work.getAuthor().getLocation().getY());
            stmt.setDouble(12, work.getAuthor().getLocation().getZ());
            stmt.setInt(13, work.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Удаляет элемент из БД по ID.
     */
    public boolean removeById(int id) throws SQLException {
        String sql = "DELETE FROM lab_works WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.USERNAME, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Полная очистка коллекции в БД.
     */
    public boolean clear() throws SQLException {
        String sql = "DELETE FROM lab_works";
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.USERNAME, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Маппинг ResultSet -> LabWork.
     */
    private LabWork mapToLabWork(ResultSet rs) throws SQLException {
        Coordinates coords = new Coordinates(rs.getDouble("coordinates_x"), rs.getInt("coordinates_y"));
        Location loc = new Location(rs.getInt("location_x"), rs.getDouble("location_y"), rs.getDouble("location_z"));
        Person author = new Person(
            rs.getString("person_name"),
            rs.getDouble("person_height"),
            Color.valueOf(rs.getString("person_eye_color")),
            loc
        );
        
        String diffStr = rs.getString("difficulty");
        Difficulty diff = diffStr != null ? Difficulty.valueOf(diffStr) : null;

        LabWork work = new LabWork(
            rs.getInt("id"),
            rs.getString("name"),
            coords,
            rs.getFloat("minimal_point"),
            diff,
            author
        );
        
        work.setCreationDate(rs.getDate("creation_date").toLocalDate());
        work.setOwnerId(rs.getObject("owner_id", Integer.class)); 
        
        return work;
    }

    /**
     * Создаёт таблицы, если они ещё не существуют.
     */
    public void initializeDatabase(Connection conn) throws SQLException {
        String[] statements = {
            "CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY, " +
                "username VARCHAR(255) UNIQUE NOT NULL, " +
                "password_hash VARCHAR(128) NOT NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")",
            "CREATE TABLE IF NOT EXISTS lab_works (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "coordinates_x DOUBLE PRECISION NOT NULL, " +
                "coordinates_y INTEGER NOT NULL, " +
                "creation_date DATE NOT NULL, " +
                "minimal_point REAL, " +
                "difficulty VARCHAR(50), " +
                "person_name VARCHAR(255) NOT NULL, " +
                "person_height DOUBLE PRECISION, " +
                "person_eye_color VARCHAR(50) NOT NULL, " +
                "location_x INTEGER, " +
                "location_y DOUBLE PRECISION, " +
                "location_z DOUBLE PRECISION, " +
                "owner_id INTEGER REFERENCES users(id) ON DELETE CASCADE" +
            ")",
            "CREATE SEQUENCE IF NOT EXISTS labwork_id_seq START WITH 1",
            "CREATE INDEX IF NOT EXISTS idx_labwork_owner ON lab_works(owner_id)",
            "CREATE INDEX IF NOT EXISTS idx_labwork_name ON lab_works(name)"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                stmt.execute(sql);
            }
            logger.info("Database schema initialized successfully");
        } catch (SQLException e) {
            if (e.getMessage().contains("permission denied")) {
                logger.warn("No permission to create tables. Please run initialization script manually.");
            } else {
                throw e;
            }
        }
    }

    /**
     * Получает логин пользователя по его ID.
     */
    public String getUserLoginById(int userId) {
        String sql = "SELECT username FROM users WHERE id = ?"; // ✅ username, не login
        try (Connection conn = DriverManager.getConnection(DatabaseConfig.JDBC_URL, DatabaseConfig.USERNAME, DatabaseConfig.PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting user login by ID: {}", userId, e);
        }
        return null;
    }
}
