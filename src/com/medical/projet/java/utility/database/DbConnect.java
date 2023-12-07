package com.medical.projet.java.utility.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import com.medical.projet.java.utility.AppSettings;

import javafx.application.Platform;

/**
 * The Class DbConnect.
 */
public class DbConnect {

    /** The conn. */
    private static Connection conn;

    private static boolean timerActive = false;

    /**
     * Instantiates a new db connect.
     */
    private DbConnect() {

        try {
            // Provide the connection URL, username, and password
            String url = AppSettings.INSTANCE.dbUrl;
            String username = AppSettings.INSTANCE.dbUsername;
            String password = AppSettings.INSTANCE.dbPassword;

            // Load the Oracle JDBC driver
            Class.forName("oracle.jdbc.OracleDriver");

            // Establish the database connection
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connection to Oracle Database has been established.");
        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e.getMessage());
            handleConnectionError();
        } catch (ClassNotFoundException e) {
            System.out.println("Class Not Found Exception: " + e.getMessage());
        }

    }

    private void handleConnectionError() {
        final int[] seconds = {30}; // Initial countdown value
        timerActive = true;

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    System.out.println("Connection to database failed. Trying again in " + seconds[0] + " sec.");
                    seconds[0]--;

                    if (seconds[0] < 0) {
                        timer.cancel(); // Stop the timer when the countdown reaches zero
                        timerActive = false;
                        sharedConnection(); // Optionally, trigger another attempt here
                    }
                });
            }
        }, 0, 1000);
    }

    /**
     * Gets the connection.
     *
     * @return the connection
     */
    private static Connection getConnection() {
        if (conn == null && !timerActive) {
            new DbConnect(); // Initialize the connection if it's not already done
        }
        return conn;
    }

    /**
     * Shared connection.
     *
     * @return the connection
     */
    public static Connection sharedConnection() {
        return getConnection();
    }

    /**
     * Close the connection.
     */
    public static void closeConnection() {
        try {
            Connection connection = sharedConnection();
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection to Oracle Database has been closed.");
                conn = null; // Set the connection variable to null after closing
            } else {
                System.out.println("Connection is already closed or null.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
