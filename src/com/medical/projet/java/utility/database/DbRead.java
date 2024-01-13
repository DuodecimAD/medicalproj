package com.medical.projet.java.utility.database;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
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
    private DbRead() {

    }

    public static List<List<Object>> readTable(String tableName, String sortBy) {
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

    public static int readId(String columnValue, String tableName, String checColumn, String checkValue) {
        String sanitizedcolumnValue = AppSecurity.sanitize(columnValue);
        String sanitizedTableName = AppSecurity.sanitize(tableName);
        String sanitizedChecColumn = AppSecurity.sanitize(checColumn);
        String sanitizedCheckValue = AppSecurity.sanitize(checkValue);

        conn = DbConnect.sharedConnection();

        String call = "{call GetIntData(?, ?, ?, ?, ?)}";

        try (CallableStatement callableStatement = conn.prepareCall(call)) {
            callableStatement.setString(1, sanitizedcolumnValue);
            callableStatement.setString(2, sanitizedTableName);
            callableStatement.setString(3, sanitizedChecColumn);
            callableStatement.setString(4, sanitizedCheckValue);

            // Register the OUT parameter for the result set
            callableStatement.registerOutParameter(5, Types.INTEGER);

            // Execute the stored procedure
            callableStatement.execute();

            // Get the result from the OUT parameter
            int result = callableStatement.getInt(5);

            return result;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return -1; // Return an empty list if there's an error or no results
    }
    
    public static List<BigDecimal> getSpecialisteForCompetence(int checkValue) {
        conn = DbConnect.sharedConnection();
        List<BigDecimal> resultList = new ArrayList<>();

        String call = "{call GetSpecialisteForCompetence(?, ?)}";

        try (CallableStatement callableStatement = conn.prepareCall(call)) {
            callableStatement.setInt(1, checkValue);

            // Register the OUT parameter for the result set
            callableStatement.registerOutParameter(2, Types.ARRAY, "NUMBERLIST");

            // Execute the stored procedure
            callableStatement.execute();

            // Get the result from the OUT parameter
            Array resultArray = callableStatement.getArray(2);
            
            if (resultArray != null) {
                // Convert the ARRAY to Integer array
                BigDecimal[] result = (BigDecimal[]) resultArray.getArray();

                // Convert the array to List
                for (BigDecimal value : result) {
                    resultList.add(value);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return resultList;
    }


    

}
