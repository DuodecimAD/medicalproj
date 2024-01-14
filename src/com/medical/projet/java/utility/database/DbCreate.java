package com.medical.projet.java.utility.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.medical.projet.java.utility.AppSecurity;

// TODO: Auto-generated Javadoc
/**
 * The Class DbCreate.
 */
public class DbCreate {

    /** The conn. */
    private static Connection conn = DbConnect.sharedConnection();

    /**
     * Instantiates a new db create.
     */
    // Private constructor to prevent instantiation
    private DbCreate() {}


    public static void insert(String tableName, List<String> columnsList, List<Object> valuesList) throws SQLException {
        String sanitizedTableName = AppSecurity.sanitize(tableName);
        List<String> sanitizedColumnsList = AppSecurity.sanitize(columnsList);

        String columns = "";
        String values = "";


        for (int i = 0; i < sanitizedColumnsList.size(); i++) {

            columns += sanitizedColumnsList.get(i);
            values += valuesList.get(i).toString();


            if (i < sanitizedColumnsList.size() - 1) {
                columns += ", ";
                values += ", ";
            }
        }

System.out.println(values);
        String call = "{call InsertIfNotExists(?, ?, ?)}";


        try (CallableStatement callableStatement = conn.prepareCall(call)) {


            // Set parameter values
            callableStatement.setString(1, sanitizedTableName);
            callableStatement.setString(2, columns);
            callableStatement.setString(3, values);


            // Execute the stored procedure
            callableStatement.execute();

            System.out.println(valuesList + " has been inserted successfully!");

        } catch (SQLException e) {

            throw e;
        }

    }

}
