/*
 * 
 */
package com.medical.projet.java.models;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.medical.projet.java.utility.database.DbCreate;
import com.medical.projet.java.utility.database.DbDelete;
import com.medical.projet.java.utility.database.DbRead;
import com.medical.projet.java.utility.database.DbUpdate;

// TODO: Auto-generated Javadoc
/**
 * The Class ActeMedical.
 */
public class ActeMedical {

    /** The Constant tableName. */
    private static final String tableName = "ACTE_MED";

    /** The Constant tableNameShort. */
    private static final String tableNameShort = "_ACTE_MED";
    
    /** The id acte med. */
    private int idActeMed;

    /** The ref acte med. */
    private String refActeMed;

    /** The id client. */
    private int idClient;
    
    /** The prenom client. */
    private String prenomClient;
    
    /** The nom client. */
    private String nomClient;

    /** The id specialiste. */
    private int idSpecialiste;
    
    /** The prenom specialiste. */
    private String prenomSpecialiste;
    
    /** The nom specialiste. */
    private String nomSpecialiste;

    /** The id lieu. */
    private int idLieu;
    
    /** The nom lieu. */
    private String nomLieu;

    /** The date debut. */
    private LocalDate dateDebut;

    /** The date fin. */
    private LocalDate dateFin;
    
    /** The id competence. */
    private int idCompetence;
    
    /** The nom competence. */
    private String nomCompetence;


    /**
     * Instantiates a new acte medical.
     */
    public ActeMedical() {}
    
    /**
     * Instantiates a new acte medical.
     *
     * @param dateDebut the date debut
     * @param dateFin the date fin
     * @param idClient the id client
     * @param idLieu the id lieu
     * @param idSpecialiste the id specialiste
     */
    public ActeMedical(LocalDate dateDebut, LocalDate dateFin, int idClient, int idLieu, int idSpecialiste) {
        this.idClient = idClient;
        this.idSpecialiste = idSpecialiste;
        this.idLieu = idLieu;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    /**
     * Instantiates a new acte medical.
     *
     * @param refActeMed the ref acte med
     * @param dateDebut the date debut
     * @param dateFin the date fin
     * @param idClient the id client
     * @param idLieu the id lieu
     * @param idSpecialiste the id specialiste
     */
    public ActeMedical(String refActeMed, LocalDate dateDebut, LocalDate dateFin, int idClient, int idLieu, int idSpecialiste) {
        this.refActeMed = refActeMed;
        this.idClient = idClient;
        this.idSpecialiste = idSpecialiste;
        this.idLieu = idLieu;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    /**
     * Instantiates a new acte medical.
     *
     * @param idActeMed the id acte med
     * @param refActeMed the ref acte med
     * @param idClient the id client
     * @param prenomClient the prenom client
     * @param nomClient the nom client
     * @param idSpecialiste the id specialiste
     * @param prenomSpecialiste the prenom specialiste
     * @param nomSpecialiste the nom specialiste
     * @param idLieu the id lieu
     * @param nomLieu the nom lieu
     * @param dateDebut the date debut
     * @param dateFin the date fin
     * @param idCompetence the id competence
     * @param nomCompetence the nom competence
     */
    public ActeMedical(int idActeMed, String refActeMed, 
                        int idClient, String prenomClient, String nomClient, 
                        int idSpecialiste, String prenomSpecialiste, String nomSpecialiste, 
                        int idLieu, String nomLieu, 
                        LocalDate dateDebut, LocalDate dateFin, 
                        int idCompetence, String nomCompetence) {
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

    /**
     * Gets the table name.
     *
     * @return the table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Gets the id acte med.
     *
     * @return the id acte med
     */
    public int getIdActeMed() {
        return idActeMed;
    }

    /**
     * Sets the id acte med.
     *
     * @param idActeMed the new id acte med
     */
    public void setIdActeMed(int idActeMed) {
        this.idActeMed = idActeMed;
    }

    /**
     * Gets the ref acte med.
     *
     * @return the ref acte med
     */
    public String getRefActeMed() {
        return refActeMed;
    }

    /**
     * Sets the ref acte med.
     *
     * @param refActeMed the new ref acte med
     */
    public void setRefActeMed(String refActeMed) {
        this.refActeMed = refActeMed;
    }

    /**
     * Gets the id client.
     *
     * @return the id client
     */
    public int getIdClient() {
        return idClient;
    }

    /**
     * Sets the id client.
     *
     * @param idClient the new id client
     */
    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    /**
     * Gets the id specialiste.
     *
     * @return the id specialiste
     */
    public int getIdSpecialiste() {
        return idSpecialiste;
    }

    /**
     * Gets the prenom client.
     *
     * @return the prenom client
     */
    public String getPrenomClient() {
        return prenomClient;
    }

    /**
     * Sets the prenom client.
     *
     * @param prenomClient the new prenom client
     */
    public void setPrenomClient(String prenomClient) {
        this.prenomClient = prenomClient;
    }

    /**
     * Gets the nom client.
     *
     * @return the nom client
     */
    public String getNomClient() {
        return nomClient;
    }

    /**
     * Sets the nom client.
     *
     * @param nomClient the new nom client
     */
    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    /**
     * Gets the prenom specialiste.
     *
     * @return the prenom specialiste
     */
    public String getPrenomSpecialiste() {
        return prenomSpecialiste;
    }

    /**
     * Sets the prenom specialiste.
     *
     * @param prenomSpecialiste the new prenom specialiste
     */
    public void setPrenomSpecialiste(String prenomSpecialiste) {
        this.prenomSpecialiste = prenomSpecialiste;
    }

    /**
     * Gets the nom specialiste.
     *
     * @return the nom specialiste
     */
    public String getNomSpecialiste() {
        return nomSpecialiste;
    }

    /**
     * Sets the nom specialiste.
     *
     * @param nomSpecialiste the new nom specialiste
     */
    public void setNomSpecialiste(String nomSpecialiste) {
        this.nomSpecialiste = nomSpecialiste;
    }

    /**
     * Gets the nom lieu.
     *
     * @return the nom lieu
     */
    public String getNomLieu() {
        return nomLieu;
    }

    /**
     * Sets the nom lieu.
     *
     * @param nomLieu the new nom lieu
     */
    public void setNomLieu(String nomLieu) {
        this.nomLieu = nomLieu;
    }

    /**
     * Gets the id competence.
     *
     * @return the id competence
     */
    public int getIdCompetence() {
        return idCompetence;
    }

    /**
     * Sets the id competence.
     *
     * @param idCompetence the new id competence
     */
    public void setIdCompetence(int idCompetence) {
        this.idCompetence = idCompetence;
    }

    /**
     * Gets the nom competence.
     *
     * @return the nom competence
     */
    public String getNomCompetence() {
        return nomCompetence;
    }

    /**
     * Sets the nom competence.
     *
     * @param nomCompetence the new nom competence
     */
    public void setNomCompetence(String nomCompetence) {
        this.nomCompetence = nomCompetence;
    }

    /**
     * Sets the id specialiste.
     *
     * @param idSpecialiste the new id specialiste
     */
    public void setIdSpecialiste(int idSpecialiste) {
        this.idSpecialiste = idSpecialiste;
    }

    /**
     * Gets the id lieu.
     *
     * @return the id lieu
     */
    public int getIdLieu() {
        return idLieu;
    }

    /**
     * Sets the id lieu.
     *
     * @param idLieu the new id lieu
     */
    public void setIdLieu(int idLieu) {
        this.idLieu = idLieu;
    }

    /**
     * Gets the date debut.
     *
     * @return the date debut
     */
    public LocalDate getDateDebut() {
        return dateDebut;
    }

    /**
     * Sets the date debut.
     *
     * @param dateDebut the new date debut
     */
    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    /**
     * Gets the date fin.
     *
     * @return the date fin
     */
    public LocalDate getDateFin() {
        return dateFin;
    }

    /**
     * Sets the date fin.
     *
     * @param dateFin the new date fin
     */
    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    /**
     * Gets the all actes medicaux data.
     *
     * @return the all actes medicaux data
     */
    public static List<List<Object>> getAllActesMedicauxData() {
        // Fetch data from the database (using DbRead or any other method)
        // Return raw data as a List<List<?>>
        return DbRead.readTable(tableName, "REF_ACTE_MED");
    }

    /**
     * Insert acte medical DB.
     *
     * @param acteMedical the acte medical
     * @throws SQLException the SQL exception
     */
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

    /**
     * Delete acte medical DB.
     *
     * @param refValue the ref value
     */
    public void deleteActeMedicalDB(String refValue) {
        DbDelete.delete(tableName, "REF" + tableNameShort, refValue);
    }

    /**
     * Update acte medical DB.
     *
     * @param column the column
     * @param value the value
     * @param checkColumn the check column
     * @param checkValue the check value
     * @throws SQLException the SQL exception
     */
    public void updateActeMedicalDB(String column, Object value, String checkColumn, String checkValue) throws SQLException {

        try {
            DbUpdate.update(tableName, column, value, checkColumn, checkValue);
        } catch (SQLException e) {
            throw e;
        }
    }

}
