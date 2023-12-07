package com.medical.projet.java.utility.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.medical.projet.java.utility.AppSecurity;

import oracle.jdbc.OracleTypes;

// TODO: Auto-generated Javadoc
/**
 * The Class DbRead.
 */
public class DbRead {

    /** The conn. */
    private static Connection conn;

    /**
     * Instantiates a new db read.
     */
    // Private constructor to prevent instantiation
    private DbRead() {}



    public static List<List<Object>> read(String tableName, String sortBy) {
        String sanitizedTableName = AppSecurity.sanitize(tableName);
        String sanitizedSortBy = AppSecurity.sanitize(sortBy);

        conn = DbConnect.sharedConnection();

        String call = "{call GetTableData(?, ?, ?)}";

        try (CallableStatement callableStatement = conn.prepareCall(call)) {

            callableStatement.setString(1, sanitizedTableName);
            callableStatement.setString(2, sanitizedSortBy);

            // Register the OUT parameter for the result set
            callableStatement.registerOutParameter(3, OracleTypes.CURSOR);


            // Execute the stored procedure
            callableStatement.execute();

            // Get the result set from the OUT parameter
            ResultSet rs = (ResultSet) callableStatement.getObject(3);

            List<List<Object>> resultList = new ArrayList<>();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                // creating row of client data
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }

                // not adding client who are set to isDeleted to the observable list
                if ("0".equals(row.get(row.size() - 1).toString())) {
                    resultList.add(row);
                }
            }


            if (!resultList.isEmpty()) {
                return resultList;
            } else {
                System.out.println("No records found in the table.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return Collections.emptyList(); // Return an empty list if there's an error or no results
    }

}
