package com.medical.projet.java.utility.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import com.medical.projet.java.utility.AppSecurity;

// TODO: Auto-generated Javadoc
/**
 * The Class DbDelete.
 */
public class DbDelete {

    /** The conn. */
    private static Connection conn = DbConnect.sharedConnection();

    /**
     * Instantiates a new db delete.
     */
    // Private constructor to prevent instantiation
    private DbDelete() {}

    public static void delete(String tableName, String column, String value) {
        String sanitizedTableName = AppSecurity.sanitize(tableName);
        String sanitizedColumn = AppSecurity.sanitize(column);
        String sanitizedValue = AppSecurity.sanitize(value);

        conn = DbConnect.sharedConnection();

        String call = "{call SetToIsDeleted(?, ?, ?)}";

        try (CallableStatement callableStatement = conn.prepareCall(call)) {

            callableStatement.setString(1, sanitizedTableName);
            callableStatement.setString(2, sanitizedColumn);
            callableStatement.setString(3, sanitizedValue);

            // Execute the stored procedure
            callableStatement.execute();


            System.out.println("Based on value : " + sanitizedValue + ", the row has been set to isDeleted");

        } catch (SQLException e) {
            e.getMessage();
            System.out.println("no row has been found in table : " + sanitizedTableName + " with value : " + sanitizedValue + " in column : " + sanitizedColumn);
        }


    }


}
