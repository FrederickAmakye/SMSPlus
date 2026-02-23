package com.frederickamakye.smsplus.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Database {

    // Get db url from command line arg -Ddb.url or default to data/student.db
    private static final String URL = System.getProperty("db.url", "jdbc:sqlite:data/students.db");

    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void init() {
        String sql = """
            CREATE TABLE IF NOT EXISTS students (
                student_id TEXT PRIMARY KEY,
                full_name TEXT NOT NULL,
                programme TEXT NOT NULL,
                level INTEGER NOT NULL,
                gpa REAL NOT NULL,
                email TEXT,
                phone TEXT,
                date_added TEXT,
                status TEXT
            );
        """;

        try (Connection conn = connect();
            Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            logger.info("Database initialized.");
        } catch (SQLException e) {
            logger.error("Unable to initialize database.", e);
            throw new RuntimeException("Application start failed: DB initialization failure.", e);
        }
    }
}