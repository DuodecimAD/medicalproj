package com.medical.projet.java.models;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.medical.projet.java.utility.database.DbCreate;
import com.medical.projet.java.utility.database.DbDelete;
import com.medical.projet.java.utility.database.DbRead;
import com.medical.projet.java.utility.database.DbUpdate;

import javafx.collections.ObservableList;

public class Specialiste {

    private static final String tableName = "SPECIALISTE";

    private static final String tableNameAffix = "_SPECIALISTE";

    private int id;

    private String nom;

    private String prenom;

    private LocalDate dateNais;

    private String telephone;

    private String email;
    
    private List<Integer> competences;


    public Specialiste() {}

    public Specialiste(String nom, String prenom, LocalDate dateNais, String telephone, String email, List<Integer> competences) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNais = dateNais;
        this.telephone = telephone;
        this.email = email;
        this.competences = competences;
    }

    public Specialiste(int id, String nom, String prenom, LocalDate dateNais, String telephone, String email, List<Integer> competences) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNais = dateNais;
        this.telephone = telephone;
        this.email = email;
        this.competences = competences;
    }

    public String getTableName() {
        return tableName;
    }

    public int getSpecialisteId() {
        return id;
    }

    public void setSpecialisteId(int newId) {
        id = newId;
    }

    public String getNomSpecialiste() {
        return nom;
    }

    public void setNomSpecialiste(String newNom) {
        nom = newNom;
    }

    public String getPrenomSpecialiste() {
        return prenom;
    }

    public void setPrenomSpecialiste(String newPrenom) {
        prenom = newPrenom;
    }

    public LocalDate getDateNaisSpecialiste() {
        return dateNais;
    }

    public void setDateNaisSpecialiste(LocalDate newDateNais) {
        dateNais = newDateNais;
    }

    public String getTelSpecialiste() {
        return telephone;
    }

    public void setTelSpecialiste(String newTelephone) {
        telephone = newTelephone;
    }

    public String getEmailSpecialiste() {
        return email;
    }

    public void setEmailSpecialiste(String newEmail) {
        email = newEmail;
    }
    
    public List<Integer> getCompetencesSpecialiste() {
        return competences;
    }

    public void setCompetencesSpecialiste(List<Integer> competences) {
        this.competences = competences;
    }
    
    public void addToCompetencesSpecialiste(int value) {
        List<Integer> tempList = getCompetencesSpecialiste();
        tempList.add(value);
        setCompetencesSpecialiste(tempList);
    }

    public static List<List<Object>> getAllSpecialistesData() {
        // Fetch data from the database (using DbRead or any other method)
        // Return raw data as a List<List<?>>
        return DbRead.readTable(tableName, "NOM" + tableNameAffix);
    }

    public void setSpecialisteIdFromDb(Specialiste specialiste) {

        int newId = DbRead.readId("ID" + tableNameAffix, tableName, "EMAIL" + tableNameAffix, email.toLowerCase());
        //System.out.println("id from db is : " + newId);
        specialiste.setSpecialisteId(newId);
    }

    public void insertSpecialisteDB(Specialiste specialiste) throws SQLException {

        List<String> columnsList = new ArrayList<>(List.of("NOM" + tableNameAffix, "PRENOM" + tableNameAffix, "DATE_NAIS" + tableNameAffix, "TEL" + tableNameAffix, "EMAIL" + tableNameAffix));
        List<Object> valuesList =  new ArrayList<>(List.of(specialiste.getNomSpecialiste(), specialiste.getPrenomSpecialiste(), specialiste.getDateNaisSpecialiste(), specialiste.getTelSpecialiste(), specialiste.getEmailSpecialiste()));

        try {
            DbCreate.insert(tableName, columnsList, valuesList);
        } catch (SQLException e) {
            throw e;
        }
    }

    public void deleteSpecialisteDB(String telValue) {
        DbDelete.delete(tableName, "TEL" + tableNameAffix, telValue);
    }

    public void updateSpecialisteDB(String column, Object value, String checkColumn, String checkValue) throws SQLException {

        try {
            DbUpdate.update(tableName, column, value, checkColumn, checkValue);
        } catch (SQLException e) {
            throw e;
        }
    }

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
