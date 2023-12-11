package com.medical.projet.java.models;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.medical.projet.java.utility.database.DbCreate;
import com.medical.projet.java.utility.database.DbDelete;
import com.medical.projet.java.utility.database.DbRead;
import com.medical.projet.java.utility.database.DbUpdate;

public class Specialiste {

    private static final String tableName = "SPECIALISTE";
    private static final String tableNameShort = "_SPECIALISTE";
    

    private String NOM_SPECIALISTE;

    private String PRENOM_SPECIALISTE;

    private LocalDate DATE_NAIS_SPECIALISTE;

    private String TEL_SPECIALISTE;

    private String EMAIL_SPECIALISTE;


    public Specialiste() {}

    public Specialiste(String NOM_SPECIALISTE, String PRENOM_SPECIALISTE, LocalDate DATE_NAIS_SPECIALISTE, String TEL_SPECIALISTE, String EMAIL_SPECIALISTE) {
        this.NOM_SPECIALISTE = NOM_SPECIALISTE;
        this.PRENOM_SPECIALISTE = PRENOM_SPECIALISTE;
        this.DATE_NAIS_SPECIALISTE = DATE_NAIS_SPECIALISTE;
        this.TEL_SPECIALISTE = TEL_SPECIALISTE;
        this.EMAIL_SPECIALISTE = EMAIL_SPECIALISTE;
    }

    public String getTableName() {
        return tableName;
    }

    public String getNomSpecialiste() {
        return NOM_SPECIALISTE;
    }

    public void setNomSpecialiste(String nOM_SPECIALISTE) {
        NOM_SPECIALISTE = nOM_SPECIALISTE;
    }

    public String getPrenomSpecialiste() {
        return PRENOM_SPECIALISTE;
    }

    public void setPrenomSpecialiste(String pRENOM_SPECIALISTE) {
        PRENOM_SPECIALISTE = pRENOM_SPECIALISTE;
    }

    public LocalDate getDateNaisSpecialiste() {
        return DATE_NAIS_SPECIALISTE;
    }

    public void setDateNaisSpecialiste(LocalDate dATE_NAIS_SPECIALISTE) {
        DATE_NAIS_SPECIALISTE = dATE_NAIS_SPECIALISTE;
    }

    public String getTelSpecialiste() {
        return TEL_SPECIALISTE;
    }

    public void setTelSpecialiste(String tEL_SPECIALISTE) {
        TEL_SPECIALISTE = tEL_SPECIALISTE;
    }

    public String getEmailSpecialiste() {
        return EMAIL_SPECIALISTE;
    }

    public void setEmailSpecialiste(String eMAIL_SPECIALISTE) {
        EMAIL_SPECIALISTE = eMAIL_SPECIALISTE;
    }

    public static List<List<Object>> getAllSpecialistesData() {
        // Fetch data from the database (using DbRead or any other method)
        // Return raw data as a List<List<?>>
        return DbRead.read(tableName, "NOM" + tableNameShort);
    }

    public void insertSpecialisteDB(Specialiste specialiste) throws SQLException {

        List<String> columnsList = new ArrayList<>(List.of("NOM" + tableNameShort, "PRENOM" + tableNameShort, "DATE_NAIS" + tableNameShort, "TEL" + tableNameShort, "EMAIL" + tableNameShort));
        List<Object> valuesList =  new ArrayList<>(List.of(specialiste.getNomSpecialiste(), specialiste.getPrenomSpecialiste(), specialiste.getDateNaisSpecialiste(), specialiste.getTelSpecialiste(), specialiste.getEmailSpecialiste()));

        try {
            DbCreate.insert(tableName, columnsList, valuesList);
        } catch (SQLException e) {
            throw e;
        }
    }

    public void deleteSpecialisteDB(String telValue) {
        DbDelete.delete(tableName, "TEL" + tableNameShort, telValue);
    }

    public void updateSpecialisteDB(String column, Object value, String checkColumn, String checkValue) throws SQLException {

        try {
            DbUpdate.update(tableName, column, value, checkColumn, checkValue);
        } catch (SQLException e) {
            throw e;
        }
    }

}
