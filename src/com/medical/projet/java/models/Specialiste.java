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
 * The Class Specialiste.
 */
public class Specialiste {

    /** The Constant tableName. */
    private static final String tableName = "SPECIALISTE";

    /** The Constant tableNameAffix. */
    private static final String tableNameAffix = "_SPECIALISTE";

    /** The id. */
    private int id;

    /** The nom. */
    private String nom;

    /** The prenom. */
    private String prenom;

    /** The date nais. */
    private LocalDate dateNais;

    /** The telephone. */
    private String telephone;

    /** The email. */
    private String email;
    
    /** The competences. */
    private List<Integer> competences;


    /**
     * Instantiates a new specialiste.
     */
    public Specialiste() {}

    /**
     * Instantiates a new specialiste.
     *
     * @param nom the nom
     * @param prenom the prenom
     * @param dateNais the date nais
     * @param telephone the telephone
     * @param email the email
     * @param competences the competences
     */
    public Specialiste(String nom, String prenom, LocalDate dateNais, String telephone, String email, List<Integer> competences) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNais = dateNais;
        this.telephone = telephone;
        this.email = email;
        this.competences = competences;
    }

    /**
     * Instantiates a new specialiste.
     *
     * @param id the id
     * @param nom the nom
     * @param prenom the prenom
     * @param dateNais the date nais
     * @param telephone the telephone
     * @param email the email
     * @param competences the competences
     */
    public Specialiste(int id, String nom, String prenom, LocalDate dateNais, String telephone, String email, List<Integer> competences) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNais = dateNais;
        this.telephone = telephone;
        this.email = email;
        this.competences = competences;
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
     * Gets the specialiste id.
     *
     * @return the specialiste id
     */
    public int getSpecialisteId() {
        return id;
    }

    /**
     * Sets the specialiste id.
     *
     * @param newId the new specialiste id
     */
    public void setSpecialisteId(int newId) {
        id = newId;
    }

    /**
     * Gets the nom specialiste.
     *
     * @return the nom specialiste
     */
    public String getNomSpecialiste() {
        return nom;
    }

    /**
     * Sets the nom specialiste.
     *
     * @param newNom the new nom specialiste
     */
    public void setNomSpecialiste(String newNom) {
        nom = newNom;
    }

    /**
     * Gets the prenom specialiste.
     *
     * @return the prenom specialiste
     */
    public String getPrenomSpecialiste() {
        return prenom;
    }

    /**
     * Sets the prenom specialiste.
     *
     * @param newPrenom the new prenom specialiste
     */
    public void setPrenomSpecialiste(String newPrenom) {
        prenom = newPrenom;
    }

    /**
     * Gets the date nais specialiste.
     *
     * @return the date nais specialiste
     */
    public LocalDate getDateNaisSpecialiste() {
        return dateNais;
    }

    /**
     * Sets the date nais specialiste.
     *
     * @param newDateNais the new date nais specialiste
     */
    public void setDateNaisSpecialiste(LocalDate newDateNais) {
        dateNais = newDateNais;
    }

    /**
     * Gets the tel specialiste.
     *
     * @return the tel specialiste
     */
    public String getTelSpecialiste() {
        return telephone;
    }

    /**
     * Sets the tel specialiste.
     *
     * @param newTelephone the new tel specialiste
     */
    public void setTelSpecialiste(String newTelephone) {
        telephone = newTelephone;
    }

    /**
     * Gets the email specialiste.
     *
     * @return the email specialiste
     */
    public String getEmailSpecialiste() {
        return email;
    }

    /**
     * Sets the email specialiste.
     *
     * @param newEmail the new email specialiste
     */
    public void setEmailSpecialiste(String newEmail) {
        email = newEmail;
    }
    
    /**
     * Gets the competences specialiste.
     *
     * @return the competences specialiste
     */
    public List<Integer> getCompetencesSpecialiste() {
        return competences;
    }

    /**
     * Sets the competences specialiste.
     *
     * @param competences the new competences specialiste
     */
    public void setCompetencesSpecialiste(List<Integer> competences) {
        this.competences = competences;
    }
    
    /**
     * Adds the to competences specialiste.
     *
     * @param value the value
     */
    public void addToCompetencesSpecialiste(int value) {
        List<Integer> tempList = getCompetencesSpecialiste();
        tempList.add(value);
        setCompetencesSpecialiste(tempList);
    }

    /**
     * Gets the all specialistes data.
     *
     * @return the all specialistes data
     */
    public static List<List<Object>> getAllSpecialistesData() {
        // Fetch data from the database (using DbRead or any other method)
        // Return raw data as a List<List<?>>
        return DbRead.readTable(tableName, "NOM" + tableNameAffix);
    }

    /**
     * Sets the specialiste id from db.
     *
     * @param specialiste the new specialiste id from db
     */
    public void setSpecialisteIdFromDb(Specialiste specialiste) {

        int newId = DbRead.readId("ID" + tableNameAffix, tableName, "EMAIL" + tableNameAffix, email.toLowerCase());
        
        specialiste.setSpecialisteId(newId);
    }

    /**
     * Insert specialiste DB.
     *
     * @param specialiste the specialiste
     * @throws SQLException the SQL exception
     */
    public void insertSpecialisteDB(Specialiste specialiste) throws SQLException {

        List<String> columnsList = new ArrayList<>(List.of("NOM" + tableNameAffix, "PRENOM" + tableNameAffix, "DATE_NAIS" + tableNameAffix, "TEL" + tableNameAffix, "EMAIL" + tableNameAffix));
        List<Object> valuesList =  new ArrayList<>(List.of(specialiste.getNomSpecialiste(), specialiste.getPrenomSpecialiste(), specialiste.getDateNaisSpecialiste(), specialiste.getTelSpecialiste(), specialiste.getEmailSpecialiste()));

        try {
            DbCreate.insert(tableName, columnsList, valuesList);
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Delete specialiste DB.
     *
     * @param telValue the tel value
     */
    public void deleteSpecialisteDB(String telValue) {
        DbDelete.delete(tableName, "TEL" + tableNameAffix, telValue);
    }

    /**
     * Update specialiste DB.
     *
     * @param column the column
     * @param value the value
     * @param checkColumn the check column
     * @param checkValue the check value
     * @throws SQLException the SQL exception
     */
    public void updateSpecialisteDB(String column, Object value, String checkColumn, String checkValue) throws SQLException {

        try {
            DbUpdate.update(tableName, column, value, checkColumn, checkValue);
        } catch (SQLException e) {
            throw e;
        }
    }
    
    /**
     * Update competences specialiste DB.
     *
     * @param action the action
     * @param specialisteID the specialiste ID
     * @param values the values
     * @throws SQLException the SQL exception
     */
    public void updateCompetencesSpecialisteDB(String action, int specialisteID, List<Integer> values) throws SQLException {

        try {
            DbUpdate.update("POSSEDER", action, specialisteID, values);
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {

        return  "--------------"                            + "\r"  +
                "This Specialiste is : "                    + "\r"  +
                "id : "          + getSpecialisteId()       + "\r"  +
                "nom : "         + getNomSpecialiste()      + "\r"  +
                "prenom : "      + getPrenomSpecialiste()   + "\r"  +
                "dateNais : "    + getDateNaisSpecialiste() + "\r"  +
                "telephone : "   + getTelSpecialiste()      + "\r"  +
                "email : "       + getEmailSpecialiste()    + "\r"  +
                "competence : "  + getCompetencesSpecialiste()   + "\r"  +
                "--------------"                            + "\r"  ;
    }
    
}
