package com.fisiocare.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Gerencia o pool de conexões com o PostgreSQL via HikariCP
 */
public class DatabaseConnection {

    // ========= CONFIGURAÇÕES DO BANCO =========
    private static final String HOST     = "localhost";
    private static final String PORT     = "5432";
    private static final String DATABASE = "fisiocare_db";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "Aml11282006$";
    // ==========================================

    private static HikariDataSource dataSource;

    static {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://" + HOST + ":" + PORT + "/" + DATABASE);
            config.setUsername(USER);
            config.setPassword(PASSWORD);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setConnectionTimeout(10_000); // 10 secs
            config.setIdleTimeout(300_000); // 5 min
            config.setMaxLifetime(1_200_000); // 20 min
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            System.err.println("Erro ao configurar pool de conexões: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) throw new SQLException("DataSource não inicializado.");
        return dataSource.getConnection();
    }

    public static boolean testConnection() {
        try (Connection c = getConnection()) {
            return c.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
