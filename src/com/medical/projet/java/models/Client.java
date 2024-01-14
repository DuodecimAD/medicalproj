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

    private static final String tableNameAffix = "_CLIENT";

    private int id;

    private String nom;

    private String prenom;

    private LocalDate dateNais;

    private String telephone;

    private String email;


    public Client() {}

    public Client(String nom, String prenom, LocalDate dateNais, String telephone, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNais = dateNais;
        this.telephone = telephone;
        this.email = email;
    }

    public Client(int id, String nom, String prenom, LocalDate dateNais, String telephone, String email) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNais = dateNais;
        this.telephone = telephone;
        this.email = email;
    }

    public String getTableName() {
        return tableName;
    }

    public int getClientId() {
        return id;
    }

    public void setClientId(int newId) {
        id = newId;
    }

    public String getNomClient() {
        return nom;
    }

    public void setNomClient(String newNom) {
        nom = newNom;
    }

    public String getPrenomClient() {
        return prenom;
    }

    public void setPrenomClient(String newPrenom) {
        prenom = newPrenom;
    }

    public LocalDate getDateNaisClient() {
        return dateNais;
    }

    public void setDateNaisClient(LocalDate newDateNais) {
        dateNais = newDateNais;
    }

    public String getTelClient() {
        return telephone;
    }

    public void setTelClient(String newTelephone) {
        telephone = newTelephone;
    }

    public String getEmailClient() {
        return email;
    }

    public void setEmailClient(String newEmail) {
        email = newEmail;
    }

    public static List<List<Object>> getAllClientsData() {
        // Fetch data from the database (using DbRead or any other method)
        // Return raw data as a List<List<?>>
        return DbRead.readTable(tableName, "ID" + tableNameAffix);
    }

    public void setClientIdFromDb(Client client) {

        int newId = DbRead.readId("ID" + tableNameAffix, tableName, "EMAIL" + tableNameAffix, email.toLowerCase());
        //System.out.println("id from db is : " + newId);
        client.setClientId(newId);
    }

    public void insertClientDB(Client client) throws SQLException {

        List<String> columnsList = new ArrayList<>(List.of("NOM" + tableNameAffix, "PRENOM" + tableNameAffix, "DATE_NAIS" + tableNameAffix, "TEL" + tableNameAffix, "EMAIL" + tableNameAffix));
        List<Object> valuesList =  new ArrayList<>(List.of(client.getNomClient(), client.getPrenomClient(), client.getDateNaisClient(), client.getTelClient(), client.getEmailClient()));

        try {
            DbCreate.insert(tableName, columnsList, valuesList);
        } catch (SQLException e) {
            throw e;
        }
    }

    public void deleteClientDB(String checkColumn, String checkValue) {
        DbDelete.delete(tableName, checkColumn + tableNameAffix, checkValue);
    }

    public void updateClientDB(String column, Object newValue, String checkColumn, String checkValue) throws SQLException {

        try {
            DbUpdate.update(tableName, column, newValue, checkColumn, checkValue);
        } catch (SQLException e) {
            throw e;
        }
    }

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
