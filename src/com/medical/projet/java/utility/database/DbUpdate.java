package com.medical.projet.java.utility.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import com.medical.projet.java.utility.AppSecurity;

// TODO: Auto-generated Javadoc
/**
 * The Class DbUpdate.
 */
public class DbUpdate {

    /** The conn. */
    private static Connection conn;

    /**
     * Instantiates a new db update.
     */
    // Private constructor to prevent instantiation
    private DbUpdate() {}

    public static void update(String tableName, String column, Object value, String checkColumn, String checkValue) throws SQLException {
        String sanitizedTableName = AppSecurity.sanitize(tableName);
        String sanitizedColumn = AppSecurity.sanitize(column);
        String sanitizedValue = AppSecurity.sanitize(value.toString());
        String sanitizedCheckColumn = AppSecurity.sanitize(checkColumn);
        String sanitizedCheckValue = AppSecurity.sanitize(checkValue);

        System.out.println(sanitizedValue);

        conn =  DbConnect.sharedConnection();

        String call = "{call updateData(?, ?, ?, ?, ?)}";

        try (CallableStatement callableStatement = conn.prepareCall(call)) {

            callableStatement.setString(1, sanitizedTableName);
            callableStatement.setString(2, sanitizedColumn);
            callableStatement.setString(3, sanitizedValue);
            callableStatement.setString(4, sanitizedCheckColumn);
            callableStatement.setString(5, sanitizedCheckValue);

            // Execute the stored procedure
            callableStatement.execute();

            System.out.println("column : " + sanitizedColumn + " has been updated with value : "+ sanitizedValue + " based on the column : " + sanitizedCheckColumn + " with value : " + sanitizedCheckValue);

        } catch (SQLException e) {
            throw e;
        }

    }

}
