package com.medical.projet.java.models;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.medical.projet.java.utility.database.DbCreate;
import com.medical.projet.java.utility.database.DbDelete;
import com.medical.projet.java.utility.database.DbRead;
import com.medical.projet.java.utility.database.DbUpdate;

public class Client {

    private static final String tableName = "CLIENT";
    
    private static final String tableNameShort = "_CLIENT";

    private String NOM_CLIENT;

    private String PRENOM_CLIENT;

    private LocalDate DATE_NAIS_CLIENT;

    private String TEL_CLIENT;

    private String EMAIL_CLIENT;


    public Client() {}

    public Client(String NOM_CLIENT, String PRENOM_CLIENT, LocalDate DATE_NAIS_CLIENT, String TEL_CLIENT, String EMAIL_CLIENT) {
        this.NOM_CLIENT = NOM_CLIENT;
        this.PRENOM_CLIENT = PRENOM_CLIENT;
        this.DATE_NAIS_CLIENT = DATE_NAIS_CLIENT;
        this.TEL_CLIENT = TEL_CLIENT;
        this.EMAIL_CLIENT = EMAIL_CLIENT;
    }

    public String getTableName() {
        return tableName;
    }

    public String getNomClient() {
        return NOM_CLIENT;
    }

    public void setNomClient(String nOM_CLIENT) {
        NOM_CLIENT = nOM_CLIENT;
    }

    public String getPrenomClient() {
        return PRENOM_CLIENT;
    }

    public void setPrenomClient(String pRENOM_CLIENT) {
        PRENOM_CLIENT = pRENOM_CLIENT;
    }

    public LocalDate getDateNaisClient() {
        return DATE_NAIS_CLIENT;
    }

    public void setDateNaisClient(LocalDate dATE_NAIS_CLIENT) {
        DATE_NAIS_CLIENT = dATE_NAIS_CLIENT;
    }

    public String getTelClient() {
        return TEL_CLIENT;
    }

    public void setTelClient(String tEL_CLIENT) {
        TEL_CLIENT = tEL_CLIENT;
    }

    public String getEmailClient() {
        return EMAIL_CLIENT;
    }

    public void setEmailClient(String eMAIL_CLIENT) {
        EMAIL_CLIENT = eMAIL_CLIENT;
    }

    public static List<List<Object>> getAllClientsData() {
        // Fetch data from the database (using DbRead or any other method)
        // Return raw data as a List<List<?>>
        return DbRead.read(tableName, "NOM" + tableNameShort);
    }

    public void insertClientDB(Client client) throws SQLException {

        List<String> columnsList = new ArrayList<>(List.of("NOM" + tableNameShort, "PRENOM" + tableNameShort, "DATE_NAIS" + tableNameShort, "TEL" + tableNameShort, "EMAIL" + tableNameShort));
        List<Object> valuesList =  new ArrayList<>(List.of(client.getNomClient(), client.getPrenomClient(), client.getDateNaisClient(), client.getTelClient(), client.getEmailClient()));

        try {
            DbCreate.insert(tableName, columnsList, valuesList);
        } catch (SQLException e) {
            throw e;
        }
    }

    public void deleteClientDB(String telValue) {
        DbDelete.delete(tableName, "TEL" + tableNameShort, telValue);
    }

    public void updateClientDB(String column, Object value, String checkColumn, String checkValue) throws SQLException {

        try {
            DbUpdate.update(tableName, column, value, checkColumn, checkValue);
        } catch (SQLException e) {
            throw e;
        }
    }

}
