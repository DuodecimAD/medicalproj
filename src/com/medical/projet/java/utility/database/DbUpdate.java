package com.medical.projet.java.utility.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

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

        System.out.println(currentLine() + sanitizedValue);

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

            System.out.println(currentLine() + "column : " + sanitizedColumn + " has been updated with value : "+ sanitizedValue + " based on the column : " + sanitizedCheckColumn + " with value : " + sanitizedCheckValue);

        } catch (SQLException e) {
            throw e;
        }

    }
    
    public static void update(String tableName, String action, int specialisteID, List<Integer> values) throws SQLException {
        
        //String ValuesToString = values.stream().map(Object::toString).collect(Collectors.joining(", "));
        
        String sanitizedTableName = AppSecurity.sanitize(tableName);
        String sanitizedAction = AppSecurity.sanitize(action);
        String sanitizedValues = AppSecurity.sanitize(values.toString());


        //System.out.println(currentLine() + sanitizedValues);

        conn =  DbConnect.sharedConnection();

        String call = "{call updateCompetences(?, ?, ?, ?)}";

        try (CallableStatement callableStatement = conn.prepareCall(call)) {

            callableStatement.setString(1, sanitizedTableName);
            callableStatement.setString(2, sanitizedAction);
            callableStatement.setInt(3, specialisteID);
            callableStatement.setString(4, sanitizedValues);


            // Execute the stored procedure
            callableStatement.execute();

            System.out.println(currentLine() + "competences have been updated");

        } catch (SQLException e) {
            throw e;
        }

    }
    
    private static String currentLine() {
        return "line " + Thread.currentThread().getStackTrace()[2].getLineNumber() + " -> ";
    }

}
