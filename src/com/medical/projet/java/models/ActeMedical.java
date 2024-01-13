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
    
    private String prenomClient;
    
    private String nomClient;

    private int idSpecialiste;
    
    private String prenomSpecialiste;
    
    private String nomSpecialiste;

    private int idLieu;
    
    private String nomLieu;

    private LocalDate dateDebut;

    private LocalDate dateFin;
    
    private int idCompetence;
    
    private String nomCompetence;


    public ActeMedical() {}
    
    public ActeMedical(LocalDate dateDebut, LocalDate dateFin, int idClient, int idLieu, int idSpecialiste) {
        this.idClient = idClient;
        this.idSpecialiste = idSpecialiste;
        this.idLieu = idLieu;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public ActeMedical(String refActeMed, LocalDate dateDebut, LocalDate dateFin, int idClient, int idLieu, int idSpecialiste) {
        this.refActeMed = refActeMed;
        this.idClient = idClient;
        this.idSpecialiste = idSpecialiste;
        this.idLieu = idLieu;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    public ActeMedical(int idActeMed, String refActeMed, int idClient, String prenomClient, String nomClient, int idSpecialiste, 
                        String prenomSpecialiste, String nomSpecialiste, int idLieu, String nomLieu, LocalDate dateDebut, 
                        LocalDate dateFin, int idCompetence, String nomCompetence) {
        this.idActeMed = idActeMed;
        this.refActeMed = refActeMed;
        this.idClient = idClient;
        this.prenomClient = prenomClient;
        this.nomClient = nomClient;
        this.idSpecialiste = idSpecialiste;
        this.prenomSpecialiste = prenomSpecialiste;
        this.nomSpecialiste = nomSpecialiste;
        this.idLieu = idLieu;
        this.nomLieu = nomLieu;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.idCompetence = idCompetence;
        this.nomCompetence = nomCompetence;
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

    public String getPrenomClient() {
        return prenomClient;
    }

    public void setPrenomClient(String prenomClient) {
        this.prenomClient = prenomClient;
    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getPrenomSpecialiste() {
        return prenomSpecialiste;
    }

    public void setPrenomSpecialiste(String prenomSpecialiste) {
        this.prenomSpecialiste = prenomSpecialiste;
    }

    public String getNomSpecialiste() {
        return nomSpecialiste;
    }

    public void setNomSpecialiste(String nomSpecialiste) {
        this.nomSpecialiste = nomSpecialiste;
    }

    public String getNomLieu() {
        return nomLieu;
    }

    public void setNomLieu(String nomLieu) {
        this.nomLieu = nomLieu;
    }

    public int getIdCompetence() {
        return idCompetence;
    }

    public void setIdCompetence(int idCompetence) {
        this.idCompetence = idCompetence;
    }

    public String getNomCompetence() {
        return nomCompetence;
    }

    public void setNomCompetence(String nomCompetence) {
        this.nomCompetence = nomCompetence;
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
        return DbRead.readTable(tableName, "REF_ACTE_MED");
    }

    public void insertActeMedicalDB(ActeMedical acteMedical) throws SQLException {

        List<String> columnsList = new ArrayList<>(List.of("REF" + tableNameShort, "DATE_DEBUT", "DATE_FIN", "ID_CLIENT", "ID_LIEU", "ID_SPECIALISTE"));
        List<Object> valuesList =  new ArrayList<>(
                                                    List.of(
                                                            acteMedical.getRefActeMed(),
                                                            acteMedical.getDateDebut(),
                                                            acteMedical.getDateFin(),
                                                            acteMedical.getIdClient(),
                                                            acteMedical.getIdLieu(),
                                                            acteMedical.getIdSpecialiste()
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
