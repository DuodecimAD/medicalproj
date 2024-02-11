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
 * The Class Client.
 */
public class Client {

    /** The Constant tableName. */
    private static final String tableName = "CLIENT";

    /** The Constant tableNameAffix. */
    private static final String tableNameAffix = "_CLIENT";

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


    /**
     * Instantiates a new client.
     */
    public Client() {}

    /**
     * Instantiates a new client.
     *
     * @param nom the nom
     * @param prenom the prenom
     * @param dateNais the date nais
     * @param telephone the telephone
     * @param email the email
     */
    public Client(String nom, String prenom, LocalDate dateNais, String telephone, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNais = dateNais;
        this.telephone = telephone;
        this.email = email;
    }

    /**
     * Instantiates a new client.
     *
     * @param id the id
     * @param nom the nom
     * @param prenom the prenom
     * @param dateNais the date nais
     * @param telephone the telephone
     * @param email the email
     */
    public Client(int id, String nom, String prenom, LocalDate dateNais, String telephone, String email) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNais = dateNais;
        this.telephone = telephone;
        this.email = email;
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
     * Gets the client id.
     *
     * @return the client id
     */
    public int getClientId() {
        return id;
    }

    /**
     * Sets the client id.
     *
     * @param newId the new client id
     */
    public void setClientId(int newId) {
        id = newId;
    }

    /**
     * Gets the nom client.
     *
     * @return the nom client
     */
    public String getNomClient() {
        return nom;
    }

    /**
     * Sets the nom client.
     *
     * @param newNom the new nom client
     */
    public void setNomClient(String newNom) {
        nom = newNom;
    }

    /**
     * Gets the prenom client.
     *
     * @return the prenom client
     */
    public String getPrenomClient() {
        return prenom;
    }

    /**
     * Sets the prenom client.
     *
     * @param newPrenom the new prenom client
     */
    public void setPrenomClient(String newPrenom) {
        prenom = newPrenom;
    }

    /**
     * Gets the date nais client.
     *
     * @return the date nais client
     */
    public LocalDate getDateNaisClient() {
        return dateNais;
    }

    /**
     * Sets the date nais client.
     *
     * @param newDateNais the new date nais client
     */
    public void setDateNaisClient(LocalDate newDateNais) {
        dateNais = newDateNais;
    }

    /**
     * Gets the tel client.
     *
     * @return the tel client
     */
    public String getTelClient() {
        return telephone;
    }

    /**
     * Sets the tel client.
     *
     * @param newTelephone the new tel client
     */
    public void setTelClient(String newTelephone) {
        telephone = newTelephone;
    }

    /**
     * Gets the email client.
     *
     * @return the email client
     */
    public String getEmailClient() {
        return email;
    }

    /**
     * Sets the email client.
     *
     * @param newEmail the new email client
     */
    public void setEmailClient(String newEmail) {
        email = newEmail;
    }

    /**
     * Gets the all clients data.
     *
     * @return the all clients data
     */
    public static List<List<Object>> getAllClientsData() {
        // Fetch data from the database (using DbRead or any other method)
        // Return raw data as a List<List<?>>
        return DbRead.readTable(tableName, "ID" + tableNameAffix);
    }

    /**
     * Sets the client id from db.
     *
     * @param client the new client id from db
     */
    public void setClientIdFromDb(Client client) {

        int newId = DbRead.readId("ID" + tableNameAffix, tableName, "EMAIL" + tableNameAffix, email.toLowerCase());

        client.setClientId(newId);
    }

    /**
     * Insert client DB.
     *
     * @param client the client
     * @throws SQLException the SQL exception
     */
    public void insertClientDB(Client client) throws SQLException {

        List<String> columnsList = new ArrayList<>(List.of("NOM" + tableNameAffix, "PRENOM" + tableNameAffix, "DATE_NAIS" + tableNameAffix, "TEL" + tableNameAffix, "EMAIL" + tableNameAffix));
        List<Object> valuesList =  new ArrayList<>(List.of(client.getNomClient(), client.getPrenomClient(), client.getDateNaisClient(), client.getTelClient(), client.getEmailClient()));

        try {
            DbCreate.insert(tableName, columnsList, valuesList);
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Delete client DB.
     *
     * @param checkColumn the check column
     * @param checkValue the check value
     */
    public void deleteClientDB(String checkColumn, String checkValue) {
        DbDelete.delete(tableName, checkColumn + tableNameAffix, checkValue);
    }

    /**
     * Update client DB.
     *
     * @param column the column
     * @param newValue the new value
     * @param checkColumn the check column
     * @param checkValue the check value
     * @throws SQLException the SQL exception
     */
    public void updateClientDB(String column, Object newValue, String checkColumn, String checkValue) throws SQLException {

        try {
            DbUpdate.update(tableName, column, newValue, checkColumn, checkValue);
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

        return  "--------------"                        + "\r"  +
                "This Client is : "                     + "\r"  +
                "id : "          + getClientId()        + "\r"  +
                "nom : "         + getNomClient()       + "\r"  +
                "prenom : "      + getPrenomClient()    + "\r"  +
                "dateNais : "    + getDateNaisClient()  + "\r"  +
                "telephone : "   + getTelClient()       + "\r"  +
                "email : "       + getEmailClient()     + "\r"  +
                "--------------"                        + "\r"  ;
    }

}
