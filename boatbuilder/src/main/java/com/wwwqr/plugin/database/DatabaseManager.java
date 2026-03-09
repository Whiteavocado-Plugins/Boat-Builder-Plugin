package com.wwwqr.plugin.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.Location;

public class DatabaseManager {
    private final JavaPlugin plugin;
    private static Connection conn;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public static void connect() {
        try {
            if (conn != null && !conn.isClosed()) {
                return;//Already connected
            }

            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/boatbuilder",
                "wwwqr",
                "MarfKas23&"
            );

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConn() {
        try {
            return conn;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void close() {
        try {
            conn.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}