package com.medical.projet.java.models;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.medical.projet.java.utility.database.DbCreate;
import com.medical.projet.java.utility.database.DbDelete;
import com.medical.projet.java.utility.database.DbRead;
import com.medical.projet.java.utility.database.DbUpdate;

public class ActeMedical {

    private static final String tableName = "ACTE_MED";
    
    private static final String tableNameShort = "_ACTE_MED";

    private String REF_ACTE_MED;
    
    private String ID_CLIENT;

    private String ID_SPECIALISTE;
    
    private String ID_LIEU;

    private LocalDate DATE_DEBUT;
    
    private LocalDate DATE_FIN;


    public ActeMedical() {}

    public ActeMedical(String rEF_ACTE_MED, String iD_CLIENT, String iD_SPECIALISTE, String iD_LIEU, LocalDate dATE_DEBUT, LocalDate dATE_FIN) {
        this.REF_ACTE_MED = rEF_ACTE_MED;
        this.DATE_DEBUT = dATE_DEBUT;
        this.DATE_FIN = dATE_FIN;
        this.ID_CLIENT = iD_CLIENT;
        this.ID_SPECIALISTE = iD_SPECIALISTE;
        this.ID_LIEU = iD_LIEU;
    }

    public String getTableName() {
        return tableName;
    }

    public String getREF_ACTE_MED() {
        return REF_ACTE_MED;
    }

    public void setREF_ACTE_MED(String rEF_ACTE_MED) {
        REF_ACTE_MED = rEF_ACTE_MED;
    }

    public LocalDate getDATE_DEBUT() {
        return DATE_DEBUT;
    }

    public void setDATE_DEBUT(LocalDate dATE_DEBUT) {
        DATE_DEBUT = dATE_DEBUT;
    }

    public LocalDate getDATE_FIN() {
        return DATE_FIN;
    }

    public void setDATE_FIN(LocalDate dATE_FIN) {
        DATE_FIN = dATE_FIN;
    }

    public String getID_CLIENT() {
        return ID_CLIENT;
    }

    public void setID_CLIENT(String iD_CLIENT) {
        ID_CLIENT = iD_CLIENT;
    }

    public String getID_SPECIALISTE() {
        return ID_SPECIALISTE;
    }

    public void setID_SPECIALISTE(String iD_SPECIALISTE) {
        ID_SPECIALISTE = iD_SPECIALISTE;
    }

    public String getID_LIEU() {
        return ID_LIEU;
    }

    public void setID_LIEU(String iD_LIEU) {
        ID_LIEU = iD_LIEU;
    }

    public static List<List<Object>> getAllActesMedicauxData() {
        // Fetch data from the database (using DbRead or any other method)
        // Return raw data as a List<List<?>>
        return DbRead.read(tableName, "DATE_DEBUT");
    }

    public void insertActeMedicalDB(ActeMedical acteMedical) throws SQLException {

        List<String> columnsList = new ArrayList<>(List.of("REF" + tableNameShort, "CLIENT", "SPECIALISTE", "LIEU", "DATE_DEBUT", "DATE_FIN"));
        List<Object> valuesList =  new ArrayList<>(List.of(acteMedical.getREF_ACTE_MED(), acteMedical.getID_CLIENT(), acteMedical.getID_SPECIALISTE(), acteMedical.getID_LIEU(), acteMedical.getDATE_DEBUT(), acteMedical.getDATE_FIN()));

        try {
            DbCreate.insert(tableName, columnsList, valuesList);
        } catch (SQLException e) {
            throw e;
        }
    }

    public void deleteActeMedicalDB(String refValue) {
        DbDelete.delete(tableName, "REF" + tableNameShort, refValue);
    }

    public void updateActeMedicalDB(String column, Object value, String checkColumn, String checkValue) throws SQLException {

        try {
            DbUpdate.update(tableName, column, value, checkColumn, checkValue);
        } catch (SQLException e) {
            throw e;
        }
    }

}
