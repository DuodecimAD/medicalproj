/*
 * 
 */
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
import java.util.stream.Collectors;

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

    /**
     * Read table.
     *
     * @param tableName the table name
     * @param sortBy the sort by
     * @return the list
     */
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

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            List<List<Object>> resultList = new ArrayList<>();

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
                System.out.println("DdRead -> No records found in the table.");
            }
        } catch (SQLException e) {
            System.out.println("DdRead -> " + e.getMessage());
        }

        return Collections.emptyList(); // Return an empty list if there's an error or no results
    }

    /**
     * Read id.
     *
     * @param columnValue the column value
     * @param tableName the table name
     * @param checkColumn the check column
     * @param checkValue the check value
     * @return the int
     */
    public static int readId(String columnValue, String tableName, String checkColumn, String checkValue) {
        String sanitizedcolumnValue = AppSecurity.sanitize(columnValue);
        String sanitizedTableName = AppSecurity.sanitize(tableName);
        String sanitizedChecColumn = AppSecurity.sanitize(checkColumn);
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
            System.out.println("DdRead -> " + e.getMessage());
        }

        return -1; // Return an empty list if there's an error or no results
    }
    
    /**
     * Read string.
     *
     * @param columnValue the column value
     * @param tableName the table name
     * @param checkColumn the check column
     * @param checkValue the check value
     * @return the string
     */
    public static String readString(String columnValue, String tableName, String checkColumn, String checkValue) {
        String sanitizedcolumnValue = AppSecurity.sanitize(columnValue);
        String sanitizedTableName = AppSecurity.sanitize(tableName);
        String sanitizedChecColumn = AppSecurity.sanitize(checkColumn);
        String sanitizedCheckValue = AppSecurity.sanitize(checkValue);

        conn = DbConnect.sharedConnection();

        String call = "{call GetStringData(?, ?, ?, ?, ?)}";

        try (CallableStatement callableStatement = conn.prepareCall(call)) {
            callableStatement.setString(1, sanitizedcolumnValue);
            callableStatement.setString(2, sanitizedTableName);
            callableStatement.setString(3, sanitizedChecColumn);
            callableStatement.setString(4, sanitizedCheckValue);

            // Register the OUT parameter for the result set
            callableStatement.registerOutParameter(5, Types.VARCHAR);

            // Execute the stored procedure
            callableStatement.execute();

            // Get the result from the OUT parameter
            String result = callableStatement.getString(5);

            return result;

        } catch (SQLException e) {
            System.out.println("DdRead -> " + e.getMessage());
        }

        return "error"; // Return an empty list if there's an error or no results
    }
    
    
    /**
     * Gets the specialiste for competence.
     *
     * @param checkValue the check value
     * @return the specialiste for competence
     */
    public static List<Integer> getSpecialisteForCompetence(int checkValue) {
        conn = DbConnect.sharedConnection();
        List<BigDecimal> resultList = new ArrayList<>();
        List<Integer> finalList= null;

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
            
            finalList = resultList.stream().map(BigDecimal::intValue).collect(Collectors.toList());

        } catch (SQLException e) {
            System.out.println("DdRead ->  " + e.getMessage());
        }

        return finalList;
    }


    /**
     * Read test.
     *
     * @return the list
     */
    public static List<List<Object>> readTest() {


        conn = DbConnect.sharedConnection();

        String call = "{call test(?)}";

        try (CallableStatement callableStatement = conn.prepareCall(call)) {


            // Register the OUT parameter for the result set
            callableStatement.registerOutParameter(1, OracleTypes.CURSOR);

            // Execute the stored procedure
            callableStatement.execute();

            // Get the result set from the OUT parameter
            ResultSet rs = (ResultSet) callableStatement.getObject(1);

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            List<List<Object>> resultList = new ArrayList<>();

            while (rs.next()) {
                // creating row of client data
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                    
                }
                resultList.add(row);
            }
            
            if (!resultList.isEmpty()) {
                return resultList;
            } else {
                System.out.println("DdRead -> No records found in the table.");
            }
        } catch (SQLException e) {
            System.out.println("DdRead -> " + e.getMessage());
        }

        return Collections.emptyList(); // Return an empty list if there's an error or no results
    }
    
    /**
     * Read last id.
     *
     * @param columnValue the column value
     * @param tableName the table name
     * @return the int
     */
    public static int readLastId(String columnValue, String tableName) {
        String sanitizedcolumnValue = AppSecurity.sanitize(columnValue);
        String sanitizedTableName = AppSecurity.sanitize(tableName);


        conn = DbConnect.sharedConnection();

        String call = "{call lastIdAm(?, ?, ?)}";

        try (CallableStatement callableStatement = conn.prepareCall(call)) {
            callableStatement.setString(1, sanitizedcolumnValue);
            callableStatement.setString(2, sanitizedTableName);


            // Register the OUT parameter for the result set
            callableStatement.registerOutParameter(3, Types.INTEGER);

            // Execute the stored procedure
            callableStatement.execute();

            // Get the result from the OUT parameter
            int result = callableStatement.getInt(3);

            return result;

        } catch (SQLException e) {
            System.out.println("DdRead -> " + e.getMessage());
        }

        return -1; // Return an empty list if there's an error or no results
    }

}
