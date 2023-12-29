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

    private int idActeMed;

    private String refActeMed;

    private int idClient;

    private int idSpecialiste;

    private int idLieu;

    private LocalDate dateDebut;

    private LocalDate dateFin;


    public ActeMedical() {}

    public ActeMedical(String refActeMed, int idClient, int idSpecialiste, int idLieu, LocalDate dateDebut, LocalDate dateFin) {
        this.refActeMed = refActeMed;
        this.idClient = idClient;
        this.idSpecialiste = idSpecialiste;
        this.idLieu = idLieu;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public ActeMedical(int idActeMed, String refActeMed, int idClient, int idSpecialiste, int idLieu, LocalDate dateDebut, LocalDate dateFin) {
        this.idActeMed = idActeMed;
        this.refActeMed = refActeMed;
        this.idClient = idClient;
        this.idSpecialiste = idSpecialiste;
        this.idLieu = idLieu;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public String getTableName() {
        return tableName;
    }

    public int getIdActeMed() {
        return idActeMed;
    }

    public void setIdActeMed(int idActeMed) {
        this.idActeMed = idActeMed;
    }

    public String getRefActeMed() {
        return refActeMed;
    }

    public void setRefActeMed(String refActeMed) {
        this.refActeMed = refActeMed;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdSpecialiste() {
        return idSpecialiste;
    }

    public void setIdSpecialiste(int idSpecialiste) {
        this.idSpecialiste = idSpecialiste;
    }

    public int getIdLieu() {
        return idLieu;
    }

    public void setIdLieu(int idLieu) {
        this.idLieu = idLieu;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public static List<List<Object>> getAllActesMedicauxData() {
        // Fetch data from the database (using DbRead or any other method)
        // Return raw data as a List<List<?>>
        return DbRead.readTable(tableName, "DATE_DEBUT");
    }

    public void insertActeMedicalDB(ActeMedical acteMedical) throws SQLException {

        List<String> columnsList = new ArrayList<>(List.of("REF" + tableNameShort, "CLIENT", "SPECIALISTE", "LIEU", "DATE_DEBUT", "DATE_FIN"));
        List<Object> valuesList =  new ArrayList<>(
                                                    List.of(
                                                            acteMedical.getRefActeMed(), acteMedical.getIdClient(),
                                                            acteMedical.getIdSpecialiste(), acteMedical.getIdLieu(),
                                                            acteMedical.getDateDebut(), acteMedical.getDateFin()
                                                    ));

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
