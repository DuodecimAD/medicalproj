/*
 * 
 */
package com.medical.projet.java.controllers;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import com.medical.projet.java.models.ActeMedical;
import com.medical.projet.java.models.Client;
import com.medical.projet.java.models.Specialiste;
import com.medical.projet.java.utility.AppSecurity;
import com.medical.projet.java.utility.AppSettings;
import com.medical.projet.java.utility.database.DbCreate;
import com.medical.projet.java.utility.database.DbRead;
import com.medical.projet.java.utility.database.DbUpdate;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


// TODO: Auto-generated Javadoc
/**
 * The Class ActesMedicauxController.
 */
public class ActesMedicauxController {

    /** The actes medicaux obs list. */
    private static ObservableList<ActeMedical> actesMedicauxObsList = FXCollections.observableArrayList();
    
    /** The clients list. */
    private ObservableList<Client> clientsList = ClientController.getClientsObsList();
    
    /** The specialistes list. */
    private ObservableList<Specialiste> specialistesList = SpecialisteController.getSpecialistesObsList();
    
    /** The lieu list. */
    private ObservableList<Object> lieuList = FXCollections.observableArrayList();
    
    /** The competence list. */
    private ObservableList<Object> competenceList = FXCollections.observableArrayList();

    /** The Constant tableNameSuffix. */
    private static final String tableNameSuffix = "_ACTE_MED";


    /** The body. */
    @FXML
    private StackPane body;

    /** The table. */
    @FXML
    private TableView<ActeMedical> table;

    /** The ref acte med. */
    @FXML
    private TableColumn<ActeMedical, String> ref_acte_med;

    /** The client. */
    @FXML
    private TableColumn<ActeMedical, String> client;

    /** The specialiste. */
    @FXML
    private TableColumn<ActeMedical, String> specialiste;
    
    /** The competence. */
    @FXML
    private TableColumn<ActeMedical, String> competence;

    /** The lieu. */
    @FXML
    private TableColumn<ActeMedical, String> lieu;

    /** The date debut. */
    @FXML
    private TableColumn<ActeMedical, String> date_debut;

    /** The date fin. */
    @FXML
    private TableColumn<ActeMedical, String> date_fin;

    /** The create button. */
    @FXML
    private Button createButton;

    /** The search field. */
    @FXML
    private TextField searchField;


    /**
     * Initialize.
     */
    public void initialize() {

        dynamicCssStuff();

        loadingTableIcon();

        new Thread(() -> {
            Platform.runLater(() -> {
                //readAllActesMedicaux();
                updateTableView();
                searchTable();
                getLieuList();
                getCompetenceList();
            });
        }).start();

        // When data's row is clicked, open overlay with data from that row
        openOverlayPopulateData();

        // Create a new ActeMedical
        openOverlayNewActeMedical();

    }

    /**
     * Dynamic css stuff.
     */
    private void dynamicCssStuff() {

        // absolute position of the create button on the right side
        createButton.layoutXProperty().bind(body.widthProperty().subtract(createButton.widthProperty()));

        // auto size of the TableView columns depending of the table - scrollbar
        DoubleBinding tableWidth = table.widthProperty().subtract(24);
        ref_acte_med.prefWidthProperty().bind(tableWidth.multiply(0.08));
        client.prefWidthProperty().bind(tableWidth.multiply(0.16));
        specialiste.prefWidthProperty().bind(tableWidth.multiply(0.16));
        competence.prefWidthProperty().bind(tableWidth.multiply(0.15));
        lieu.prefWidthProperty().bind(tableWidth.multiply(0.15));
        date_debut.prefWidthProperty().bind(tableWidth.multiply(0.15));
        date_fin.prefWidthProperty().bind(tableWidth.multiply(0.15));
    }

    /**
     * Loading table icon.
     */
    private void loadingTableIcon() {
        // Load the loading GIF n TableView
        Image loadingImage = new Image(getClass().getResourceAsStream(AppSettings.INSTANCE.imagesPath+"loading.gif"));
        ImageView loadingImageView = new ImageView(loadingImage);

        // Set the loading GIF as the custom placeholder
        table.setPlaceholder(loadingImageView);
    }

    /**
     * Read all actes medicaux.
     *
     * @return the observable list
     */
    public static ObservableList<ActeMedical> readAllActesMedicaux() {

        // Get raw data from the ActeMedical model
        List<List<Object>> rawActeMedicalData = null;
        Label placeholderLabel = new Label(); // Create label outside of the timer

        try {
            rawActeMedicalData = ActeMedical.getAllActesMedicauxData();
            //System.out.println(currentLine() + rawActeMedicalData);
        } catch (Exception e) {
            final int[] seconds = {10}; // Initial countdown value

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        placeholderLabel.setText("Connection to the database failed. Retrying in " + seconds[0] + " sec.");
                        //table.setPlaceholder(placeholderLabel);
                        seconds[0]--;

                        if (seconds[0] < 0) {
                            timer.cancel(); // Stop the timer when the countdown reaches zero
                            readAllActesMedicaux(); // Optionally, trigger another attempt here
                        }
                    });
                }
            }, 0, 1000);
        }


        if (rawActeMedicalData != null) {
            for (List<Object> row : rawActeMedicalData) {

                BigDecimal idActeMedDB = (BigDecimal) row.get(0);
                int idActeMed = idActeMedDB.intValue();

                String refActeMed = (String) row.get(1);
                
                BigDecimal idClientBD = (BigDecimal) row.get(2);
                int idClient = idClientBD.intValue();
                String prenomClient = (String) row.get(3);
                String nomClient = (String) row.get(4);
                
                BigDecimal idSpecialisteBD = (BigDecimal) row.get(5);
                int idSpecialiste = idSpecialisteBD.intValue();
                String prenomSpecialiste = (String) row.get(6);
                String nomSpecialiste = (String) row.get(7);
                
                BigDecimal idLieuBD = (BigDecimal) row.get(8);
                int idLieu = idLieuBD.intValue();
                String nomLieu = (String) row.get(9);
                
                // Convert date to java.time.LocalDate
                java.sql.Timestamp timestamp = (java.sql.Timestamp) row.get(10);
                LocalDate date_debut = timestamp.toLocalDateTime().toLocalDate();
                
                // Convert date to java.time.LocalDate
                java.sql.Timestamp timestamp2 = (java.sql.Timestamp) row.get(11);
                LocalDate date_fin = timestamp2.toLocalDateTime().toLocalDate();
                
                BigDecimal idCompetenceBD = (BigDecimal) row.get(12);
                int idCompetence = idCompetenceBD.intValue();
                String nomCompetence = (String) row.get(13);
                
                // Create a ActeMedical object and add to the list
                actesMedicauxObsList.add(new ActeMedical(idActeMed, refActeMed, idClient, prenomClient, nomClient, idSpecialiste, 
                                                            prenomSpecialiste, nomSpecialiste, idLieu, nomLieu, date_debut, date_fin, idCompetence, nomCompetence));
            }
        }
        return actesMedicauxObsList;
    }


    /**
     * Update table view.
     */
    private void updateTableView() {

        // Set the items with the correct data type
        table.setItems(actesMedicauxObsList);

        // Populate columns of TableView with the data
        ref_acte_med.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRefActeMed()));
        
        //client.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getIdClient()).asObject());
        client.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPrenomClient() + " " + param.getValue().getNomClient()));
        
        competence.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getNomCompetence()));
        
        //specialiste.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getIdSpecialiste()).asObject());
        specialiste.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPrenomSpecialiste() + " " + param.getValue().getNomSpecialiste()));

        lieu.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getNomLieu()));
        date_debut.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        date_fin.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

    }



    /**
     * Open overlay populate data.
     */
    private void openOverlayPopulateData() {
        table.setRowFactory(tv -> {
            TableRow<ActeMedical> row = new TableRow<>();
            row.setOnMousePressed(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    ActeMedical rowData = row.getItem();
                    openOverlayWithActeMedicalData(rowData);
                    //System.out.println(currentLine() + DbRead.readLastId("id_acte_med", "acte_med"));
                }
            });
            return row;
        });
    }

    /**
     * Open overlay new acte medical.
     */
    private void openOverlayNewActeMedical() {
        createButton.setOnMouseClicked(event -> {
            openOverlayForNewActeMedical();
        });
    }

    /**
     * Creates the overlay.
     *
     * @param stackPane the stack pane
     * @param contentPopulationCallback the content population callback
     */
    private void createOverlay(StackPane stackPane, Consumer<BorderPane> contentPopulationCallback) {
        // Create a darkened overlay pane
        VBox overlayPane = new VBox();
        overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);"); // Semi-transparent black background
        overlayPane.setPrefSize(stackPane.getWidth(), stackPane.getHeight());

        // Create your content pane
        BorderPane contentPane = new BorderPane();
        contentPane.setId("overlayContentPane");
        contentPane.setMaxSize(820, 560);

        // Use the callback to populate the content
        contentPopulationCallback.accept(contentPane);

        // Ensure overlayPane is visible and on top
        overlayPane.setVisible(true);
        overlayPane.toFront();
        // Add the content pane to the overlay pane
        overlayPane.getChildren().add(contentPane);

        // Add the overlay pane to the root StackPane
        stackPane.getChildren().add(overlayPane);

        // Set the dimensions after the stage is shown
        //contentPane.setLayoutX(((stackPane.getWidth() - contentPane.getPrefWidth()) / 2) + 20);
        //contentPane.setLayoutY(((stackPane.getHeight() - contentPane.getPrefHeight()) / 2) + 10);
        overlayPane.setAlignment(Pos.CENTER);
    }

    /**
     * Open overlay with acte medical data.
     *
     * @param acteMedical the acte medical
     */
    // overlay with client data
    private void openOverlayWithActeMedicalData(ActeMedical acteMedical) {
        createOverlay(body, contentPane -> populateOverlayContent(contentPane, acteMedical));
    }

    /**
     * Open overlay for new acte medical.
     */
    // overlay for a new client
    private void openOverlayForNewActeMedical() {
        createOverlay(body, contentPane -> populateOverlayForNewActeMedical(contentPane));
    }

    /**
     * Populate overlay content.
     *
     * @param contentPane the content pane
     * @param acteMedical the acte medical
     */
    //  when clicking on a row in Tableview, populate the data of that row in the overlay
    private void populateOverlayContent(BorderPane contentPane, ActeMedical acteMedical) {

        Label refLabel = new Label("Ref AM");
        refLabel.setId("REF" + tableNameSuffix);

        TextField refField = new TextField();
        refField.setText(acteMedical.getRefActeMed());
        refField.setId("refAm");
        //refField.setDisable(true);
        refField.setEditable(false);
        
        VBox refAmVbox = new VBox();
        refAmVbox.getChildren().addAll(refLabel, refField);
        
        Label clientLabel = new Label("Client");
        clientLabel.setId("ID_CLIENT");

        TextField clientField = new TextField();
        clientField.setText(acteMedical.getPrenomClient() + " " + acteMedical.getNomClient());
        clientField.setUserData(acteMedical.getIdClient());
        clientField.setEditable(false);

        VBox clientAmVbox = new VBox();
        clientAmVbox.getChildren().addAll(clientLabel, clientField);
        
        HBox topAmHbox = new HBox();
        topAmHbox.setId("topAmHbox");
        topAmHbox.getChildren().addAll(refAmVbox, clientAmVbox);
        

        Button buttonDelete = new Button("Delete");
        buttonDelete.setId("DeleteButton");

        Button buttonPrint = new Button("Print Document");
        buttonPrint.setId("PrintButton");

        HBox overlayTopDelete = new HBox();
        overlayTopDelete.setId("overlayAmTopDelete");
        overlayTopDelete.getChildren().addAll(buttonPrint, buttonDelete);

        HBox overlayAmTop = new HBox();
        overlayAmTop.setId("overlayAmTop");
        overlayAmTop.getChildren().addAll(topAmHbox, overlayTopDelete);


        Label competenceLabel = new Label("Acte Medical");
        ChoiceBox<String> competenceChoiceBox = new ChoiceBox<>();
        competenceChoiceBox.setValue(acteMedical.getNomCompetence());
        List<String> nomCompetenceList = new ArrayList<>();

        for (int i = 1; i < competenceList.size(); i += 2) {
            Object name = competenceList.get(i);
            if (name != null) {
                nomCompetenceList.add(String.valueOf(name));
            }
        }
        competenceChoiceBox.getItems().addAll(nomCompetenceList);
        competenceChoiceBox.setPrefWidth(230);
        //competenceChoiceBox.setVisibleRowCount(10); //change to combobox but less pretty
        competenceChoiceBox.setId("ID_COMPETENCE");
        competenceChoiceBox.setUserData(acteMedical.getIdCompetence());
        
        
        VBox competenceAmVbox = new VBox();
        competenceAmVbox.getChildren().addAll(competenceLabel, competenceChoiceBox);

        Label specialisteLabel = new Label("Specialiste");
        specialisteLabel.setId("ID_SPECIALISTE");

        TextField specialisteField = new TextField();
        specialisteField.setText(acteMedical.getPrenomSpecialiste() + " " + acteMedical.getNomSpecialiste());
        specialisteField.setUserData(acteMedical.getIdSpecialiste());
        specialisteField.setEditable(false);

        VBox specialisteAmVbox = new VBox();
        specialisteAmVbox.getChildren().addAll(specialisteLabel, specialisteField);

        HBox overlayAmCenter = new HBox();
        overlayAmCenter.setId("overlayAmCenter");
        overlayAmCenter.getChildren().addAll(competenceAmVbox, specialisteAmVbox);


        Label lieuLabel = new Label("Lieu");
        lieuLabel.setId("ID_LIEU");

        ChoiceBox<String> lieuChoiceBox = new ChoiceBox<>();
        lieuChoiceBox.setValue(acteMedical.getNomLieu());

        List<String> nomLieuList = new ArrayList<>();

        for (int i = 1; i < lieuList.size(); i += 2) {
            Object name = lieuList.get(i);
            if (name != null) {
                nomLieuList.add(String.valueOf(name));
            }
        }

        lieuChoiceBox.getItems().addAll(nomLieuList);
        lieuChoiceBox.setId("lieuChoiceBox");
        lieuChoiceBox.setUserData(acteMedical.getIdLieu());

        VBox lieuAmVbox = new VBox();
        lieuAmVbox.getChildren().addAll(lieuLabel, lieuChoiceBox);
        lieuAmVbox.setId("lieuAmVbox");

        Label date_debutLabel = new Label("Date de début");
        date_debutLabel.setId("DATE_DEBUT");

        DatePicker date_debutField = new DatePicker();
        date_debutField.setValue(acteMedical.getDateDebut());
        date_debutField.setEditable(false);
        
        VBox date_debutAmVbox = new VBox();
        date_debutAmVbox.getChildren().addAll(date_debutLabel, date_debutField);

        Label date_finLabel = new Label("Date de fin");
        date_finLabel.setId("DATE_FIN");

        DatePicker date_finField = new DatePicker();
        date_finField.setValue(acteMedical.getDateFin());
        date_finField.setEditable(false);

        VBox date_finAmVbox = new VBox();
        date_finAmVbox.getChildren().addAll(date_finLabel, date_finField);

        HBox overlayAmBottom = new HBox();
        overlayAmBottom.setId("overlayAmBottom");
        overlayAmBottom.getChildren().addAll(lieuAmVbox, date_debutAmVbox, date_finAmVbox );

        TableView<Client> tableAmClient = new TableView<>();
        tableAmClient.setId("tableAmClient");
        Label tableAmClientLabel = new Label("Please click on the field Client, or choose an Acte Medical to show a specialiste");
        tableAmClient.setPlaceholder(tableAmClientLabel);

        TableView<Specialiste> tableAmSpecialiste = new TableView<>();
        tableAmSpecialiste.setId("tableAmSpecialiste");
        Label tableAmSpecialisteLabel = new Label("No specialiste found for that Acte Medical, choose another one");
        tableAmSpecialiste.setPlaceholder(tableAmSpecialisteLabel);
        
        // Initially set one of them to not be managed and not visible.
        tableAmSpecialiste.setManaged(false);
        tableAmSpecialiste.setVisible(false);
        
        StackPane tableBoth = new StackPane();
        tableBoth.getChildren().addAll(tableAmClient, tableAmSpecialiste);


        Label errorLabel = new Label("");
        errorLabel.setId("errorLabelnew");

        VBox overLayContent = new VBox();
        overLayContent.setId("overLayContent");
        overLayContent.getChildren().addAll(overlayAmTop, overlayAmCenter, overlayAmBottom, tableBoth, errorLabel);


        Label amSearchLabel = new Label("Search : ");
        amSearchLabel.setId("amSearchLabel");
        TextField amSearchField = new TextField();
        amSearchField.setId("amSearchField");
        HBox overlayAmSearch = new HBox();
        overlayAmSearch.setId("overlayAmSearch");
        overlayAmSearch.getChildren().addAll(amSearchLabel, amSearchField);

        Button buttonOk = new Button("ok");
        Button buttonCancel = new Button("Cancel");

        HBox overlayAmBottomButtons = new HBox();
        overlayAmBottomButtons.setId("overlayAmBottomButtons");
        overlayAmBottomButtons.getChildren().addAll(buttonOk, buttonCancel);

        HBox overlayAmBottomStuff = new HBox();
        overlayAmBottomStuff.setId("overlayAmBottomStuff");
        overlayAmBottomStuff.getChildren().addAll(overlayAmSearch, overlayAmBottomButtons);

        contentPane.setCenter(overLayContent);
        contentPane.setBottom(overlayAmBottomStuff);

        // buttons event logic
        buttonCancel.setOnAction(e -> {
            closeOverlay();
        });

        buttonDelete.setOnAction(e -> {
            deleteActeMedical(acteMedical);
            getActeMedicalsObsList().remove(acteMedical);
            closeOverlay();
        });

        buttonOk.setOnAction(e -> {

            Object[] lieuArray = {lieuChoiceBox.getUserData(), lieuChoiceBox.getValue()};

            Object[] competenceArray = {competenceChoiceBox.getUserData(), competenceChoiceBox.getValue()};
            
            updateActeMedical(acteMedical, clientLabel.getId(),         acteMedical.getIdClient(),        clientField.getUserData(),        refLabel.getId(),   acteMedical.getRefActeMed() );
            updateActeMedical(acteMedical, specialisteLabel.getId(),    acteMedical.getIdSpecialiste(),   specialisteField.getUserData(),   refLabel.getId(),   acteMedical.getRefActeMed() );
            updateActeMedical(acteMedical, lieuLabel.getId(),           acteMedical.getIdLieu(),          lieuArray,                        refLabel.getId(),   acteMedical.getRefActeMed() );
            updateActeMedical(acteMedical, date_debutLabel.getId(),     acteMedical.getDateDebut(),       date_debutField.getValue(),       refLabel.getId(),   acteMedical.getRefActeMed() );
            updateActeMedical(acteMedical, date_finLabel.getId(),       acteMedical.getDateFin(),         date_finField.getValue(),         refLabel.getId(),   acteMedical.getRefActeMed() );
            updateCompetence( acteMedical, competenceChoiceBox.getId(), acteMedical.getIdCompetence(),    competenceArray,         specialisteField.getText(),  acteMedical.getIdActeMed()  );
            closeOverlay();
        });
        
        lieuChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {

            for (int i = 1; i < lieuList.size(); i+=2) {
                if(lieuList.get(i).equals(newValue)) {
                    lieuChoiceBox.setUserData(lieuList.get(i-1));
                    System.out.println(currentLine() + "lieu id user data : " + lieuChoiceBox.getUserData());
                }
            }
        });

        competenceChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            
            specialisteField.setText("");
            
            for (int i = 1; i < competenceList.size(); i += 2) {
                if (competenceList.get(i).equals(newValue)) {       
                    competenceChoiceBox.setUserData(competenceList.get(i - 1));
                    System.out.println(currentLine() + "competence id user data : " + competenceChoiceBox.getUserData());
                }
            }
            
            List<Integer> specialisteWithCompetence = DbRead.getSpecialisteForCompetence((int) competenceChoiceBox.getUserData());
            
            tableAmClient.setManaged(false);
            tableAmClient.setVisible(false);
            tableAmSpecialiste.setManaged(true);
            tableAmSpecialiste.setVisible(true);

            tableAmOverlaySpecialistes(tableAmSpecialiste, specialisteWithCompetence);
            searchTableOverlaySpecialiste(tableAmSpecialiste, amSearchField);
            amRowToSpecialiste(tableAmSpecialiste, specialisteField);

        });

        clientField.setOnMouseClicked(e -> {
            
            tableAmSpecialiste.setManaged(false);
            tableAmSpecialiste.setVisible(false);
            tableAmClient.setManaged(true);
            tableAmClient.setVisible(true);
            
            tableAmOverlayClients(tableAmClient);
            searchTableOverlayClient(tableAmClient, amSearchField);
            amRowToClient(tableAmClient, clientField);
        });
        

    }

    /**
     * Delete acte medical.
     *
     * @param acteMedical the acte medical
     */
    private void deleteActeMedical(ActeMedical acteMedical) {
        acteMedical.deleteActeMedicalDB(acteMedical.getRefActeMed());
    }
    
    /**
     * Update competence.
     *
     * @param acteMedical the acte medical
     * @param columnField the column field
     * @param oldValue the old value
     * @param competenceArray the competence array
     * @param checkSpecialiste the check specialiste
     * @param checkValue the check value
     */
    public void updateCompetence(ActeMedical acteMedical, String columnField, Object oldValue, Object[] competenceArray, String checkSpecialiste, int checkValue) {
        
        if (compare(oldValue, competenceArray[0])) {
            System.out.println(currentLine() + "competenceid : " + oldValue + " and " + competenceArray[0] + " are different");
            if(checkSpecialiste != "") {
                //System.out.println(currentLine() + "yes");
                try {
                    DbUpdate.update("Necessiter", columnField, competenceArray[0], "ID_ACTE_MED", Integer.toString(checkValue));
                    acteMedical.setIdCompetence((int) competenceArray[0]);
                    acteMedical.setNomCompetence((String) competenceArray[1]);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }else {
                //System.out.println(currentLine() + "nope");
            }
            
        }else {
            System.out.println(currentLine() + "ID_competence : "+ oldValue + " and " + competenceArray[0] + " are same");
        }
        Platform.runLater(() -> table.refresh());
    }

    /**
     * Update acte medical.
     *
     * @param acteMedical the acte medical
     * @param columnField the column field
     * @param oldValue the old value
     * @param newValue the new value
     * @param checkColumn the check column
     * @param checkValue the check value
     */
    public void updateActeMedical(ActeMedical acteMedical, String columnField, Object oldValue, Object newValue, String checkColumn, String checkValue) {

        if(newValue instanceof LocalDate) {
            //System.out.println(currentLine() + "this is a local date");
            String newValueTemp = AppSecurity.sanitize(newValue.toString());
            newValue = LocalDate.parse(newValueTemp, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }else if(newValue instanceof Integer){
            //System.out.println(currentLine() + "this is an integer");
            String newValueTemp = AppSecurity.sanitize(newValue.toString());
            newValue = Integer.parseInt(newValueTemp);
        }else if(newValue instanceof Object[]) {
            //System.out.println(currentLine() + "this an object[]");
        }
        else {
            //System.out.println(currentLine() + "fuck this");
            newValue = AppSecurity.sanitize(newValue.toString());
        }

        switch (columnField) {
            case "REF_ACTE_MED" -> {
                if (compare(oldValue, newValue)) {

                    try {
                        acteMedical.updateActeMedicalDB(columnField, newValue, checkColumn, checkValue);
                        acteMedical.setRefActeMed(checkValue);
                        System.out.println(currentLine() + "ref has been changed");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }else {
                    System.out.println(currentLine() + "REF_ACTE_MED : "+ oldValue + " and " + newValue + " are same");
                }
            }
            case "ID_CLIENT" -> {
                if (compare(oldValue, newValue)) {
                    try {
                        acteMedical.updateActeMedicalDB(columnField, newValue, checkColumn, checkValue);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    acteMedical.setIdClient((int) newValue);
                    int checkVal = (int) newValue;
                    Client foundClient = clientsList.filtered(client -> client.getClientId() == checkVal).stream().findFirst().orElse(null);
                    acteMedical.setPrenomClient(foundClient.getPrenomClient());
                    acteMedical.setNomClient(foundClient.getNomClient());
                    System.out.println(currentLine() + "client has been changed");
                }else {
                    System.out.println(currentLine() + "ID_CLIENT : "+ oldValue + " and " + newValue + " are same");
                }
            }
            case "ID_SPECIALISTE" -> {
                if (compare(oldValue, newValue)) {
                    try {
                        acteMedical.updateActeMedicalDB(columnField, newValue, checkColumn, checkValue);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    acteMedical.setIdSpecialiste((int) newValue);
                    int checkVal = (int) newValue;
                    Specialiste foundSpecialiste = specialistesList.filtered(specialiste -> specialiste.getSpecialisteId() == checkVal).stream().findFirst().orElse(null);
                    acteMedical.setPrenomSpecialiste(foundSpecialiste.getPrenomSpecialiste());
                    acteMedical.setNomSpecialiste(foundSpecialiste.getNomSpecialiste());
                    System.out.println(currentLine() + "specialiste has been changed");
                }else {
                    System.out.println(currentLine() + "ID_SPECIALISTE : "+ oldValue + " and " + newValue + " are same");
                }
            }
            case "ID_LIEU" -> {
                
                int lieuID = -1;
                String lieuName = null;

                Object[] lieuArray = (Object[]) newValue;
                lieuID = (int) lieuArray[0];
                lieuName = (String) lieuArray[1];

                if (compare(oldValue, lieuID)) {
                    try {
                        acteMedical.updateActeMedicalDB(columnField, lieuID, checkColumn, checkValue);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    acteMedical.setIdLieu(lieuID);
                    acteMedical.setNomLieu(lieuName);
                    System.out.println(currentLine() + "lieu has been changed");
                }else {
                    System.out.println(currentLine() + "ID_LIEU : "+ oldValue + " and " + lieuID + " are same");
                }
            }
            case "DATE_DEBUT" -> {
                if (compare(oldValue, newValue)) {
                    try {
                        acteMedical.updateActeMedicalDB(columnField, newValue, checkColumn, checkValue);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    LocalDate newDate = LocalDate.parse(newValue.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    acteMedical.setDateDebut(newDate);
                    System.out.println(currentLine() + "date_debut has been changed");
                }else {
                    System.out.println(currentLine() + "DATE_DEBUT : "+ oldValue + " and " + newValue + " are same");
                }
            }
            case "DATE_FIN" -> {
                if (compare(oldValue, newValue)) {
                    try {
                        acteMedical.updateActeMedicalDB(columnField, newValue, checkColumn, checkValue);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    LocalDate newDate = LocalDate.parse(newValue.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    acteMedical.setDateFin(newDate);
                    System.out.println(currentLine() + "date_fin has been changed");
                }else {
                    System.out.println(currentLine() + "DATE_FIN : "+ oldValue + " and " + newValue + " are same");
                }
            }
            default -> {}
        }
        table.refresh();
    }

    /**
     * Compare.
     *
     * @param oldValue the old value
     * @param newValue the new value
     * @return true, if successful
     */
    private boolean compare(Object oldValue, Object newValue) {
        return !Objects.equals(oldValue, newValue);
    }


    /**
     * Populate overlay for new acte medical.
     *
     * @param contentPane the content pane
     */
    // populating inputs in the overlay when clicking create new client
    private void populateOverlayForNewActeMedical(BorderPane contentPane) {

        Label clientLabel = new Label("Client");
        TextField clientField = new TextField();
        clientField.setEditable(false);
        VBox clientAmVbox = new VBox();
        clientAmVbox.getChildren().addAll(clientLabel, clientField);

        HBox overlayAmTop = new HBox();
        overlayAmTop.setId("overlayAmTop");
        overlayAmTop.getChildren().addAll(clientAmVbox);

        
        Label competenceLabel = new Label("Acte Medical");
        ChoiceBox<String> competenceChoiceBox = new ChoiceBox<>();
        List<String> nomCompetenceList = new ArrayList<>();
        
        for (int i = 1; i < competenceList.size(); i += 2) {
            Object name = competenceList.get(i);
            if (name != null) {
                nomCompetenceList.add(String.valueOf(name));
            }
        }
        competenceChoiceBox.getItems().addAll(nomCompetenceList);
        competenceChoiceBox.setPrefWidth(230);
        //competenceChoiceBox.setVisibleRowCount(10); works with ComboBox
        competenceChoiceBox.setId("ID_COMPETENCE");
        
        //refField.setDisable(true);
        VBox competenceAmVbox = new VBox();
        competenceAmVbox.getChildren().addAll(competenceLabel, competenceChoiceBox);

        Label specialisteLabel = new Label("Specialiste");
        TextField specialisteField = new TextField();
        specialisteField.setEditable(false);
        VBox specialisteAmVbox = new VBox();
        specialisteAmVbox.getChildren().addAll(specialisteLabel, specialisteField);

        HBox overlayAmCenter = new HBox();
        overlayAmCenter.setId("overlayAmCenter");
        overlayAmCenter.getChildren().addAll(competenceAmVbox, specialisteAmVbox);


        Label lieuLabel = new Label("Lieu");
        ChoiceBox<String> lieuChoiceBox = new ChoiceBox<>();
        List<String> nomLieuList = new ArrayList<>();

        for (int i = 1; i < lieuList.size(); i += 2) {
            Object name = lieuList.get(i);
            if (name != null) {
                nomLieuList.add(String.valueOf(name));
            }
        }

        lieuChoiceBox.getItems().addAll(nomLieuList);
        lieuChoiceBox.setPrefWidth(230);
        lieuChoiceBox.setId("lieuChoiceBox");
        
        VBox lieuAmVbox = new VBox();
        lieuAmVbox.getChildren().addAll(lieuLabel, lieuChoiceBox);
        lieuAmVbox.setId("lieuAmVbox");

        Label date_debutLabel = new Label("Date de début");
        DatePicker date_debutField = new DatePicker();
        date_debutField.setEditable(false);
        VBox date_debutAmVbox = new VBox();
        date_debutAmVbox.getChildren().addAll(date_debutLabel, date_debutField);

        Label date_finLabel = new Label("Date de fin");
        DatePicker date_finField = new DatePicker();
        date_finField.setEditable(false);
        VBox date_finAmVbox = new VBox();
        date_finAmVbox.getChildren().addAll(date_finLabel, date_finField);

        HBox overlayAmBottom = new HBox();
        overlayAmBottom.setId("overlayAmBottom");
        overlayAmBottom.getChildren().addAll(lieuAmVbox, date_debutAmVbox, date_finAmVbox );

        TableView<Client> tableAmClient = new TableView<>();
        tableAmClient.setId("tableAmClient");
        Label tableAmClientLabel = new Label("Please click on the field Client, or choose an Acte Medical to show a specialiste");
        tableAmClient.setPlaceholder(tableAmClientLabel);
        
        TableView<Specialiste> tableAmSpecialiste = new TableView<>();
        tableAmSpecialiste.setId("tableAmSpecialiste");
        Label tableAmSpecialisteLabel = new Label("No specialiste found for that Acte Medical, choose another one");
        tableAmSpecialiste.setPlaceholder(tableAmSpecialisteLabel);
        
        // Initially set one of them to not be managed and not visible.
        tableAmSpecialiste.setManaged(false);
        tableAmSpecialiste.setVisible(false);
        
        StackPane tableBoth = new StackPane();
        tableBoth.getChildren().addAll(tableAmClient, tableAmSpecialiste);

        Label errorLabel = new Label("");
        errorLabel.setId("errorLabelnew");

        VBox overLayContent = new VBox();
        overLayContent.setId("overLayContent");
        overLayContent.getChildren().addAll(overlayAmTop, overlayAmCenter, overlayAmBottom, tableBoth, errorLabel);


        Label amSearchLabel = new Label("Search : ");
        amSearchLabel.setId("amSearchLabel");
        TextField amSearchField = new TextField();
        amSearchField.setId("amSearchField");
        HBox overlayAmSearch = new HBox();
        overlayAmSearch.setId("overlayAmSearch");
        overlayAmSearch.getChildren().addAll(amSearchLabel, amSearchField);

        Button buttonOk = new Button("ok");
        Button buttonCancel = new Button("Cancel");

        HBox overlayAmBottomButtons = new HBox();
        overlayAmBottomButtons.setId("overlayAmBottomButtons");
        overlayAmBottomButtons.getChildren().addAll(buttonOk, buttonCancel);

        HBox overlayAmBottomStuff = new HBox();
        overlayAmBottomStuff.setId("overlayAmBottomStuff");
        overlayAmBottomStuff.getChildren().addAll(overlayAmSearch, overlayAmBottomButtons);

        contentPane.setCenter(overLayContent);
        contentPane.setBottom(overlayAmBottomStuff);

        // buttons event logic
        buttonCancel.setOnAction(e -> {
            closeOverlay();
        });

        buttonOk.setOnAction(e -> {
            
            Object[] lieuArray = {lieuChoiceBox.getUserData(), lieuChoiceBox.getValue()};

            Object[] competenceArray = {competenceChoiceBox.getUserData(), competenceChoiceBox.getValue()};
            
            String newActeMedicalOK = createNewActeMedical( 
                                                            date_debutField.getValue(),
                                                            date_finField.getValue(),
                                                            (int) clientField.getUserData(),
                                                            lieuArray,
                                                            (int) specialisteField.getUserData(),
                                                            competenceArray
                                                            );
            

            if(newActeMedicalOK.equals("")) {
                closeOverlay();
            }else {
                errorLabel.setText(newActeMedicalOK);
            }
        });
        
        lieuChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {

            for (int i = 1; i < lieuList.size(); i+=2) {
                if(lieuList.get(i).equals(newValue)) {
                    lieuChoiceBox.setUserData(lieuList.get(i-1));
                    System.out.println(currentLine() + "lieu id user data : " + lieuChoiceBox.getUserData());
                }
            }
        });

        competenceChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            
            specialisteField.setText("");
            
            for (int i = 1; i < competenceList.size(); i += 2) {
                if (competenceList.get(i).equals(newValue)) {       
                    competenceChoiceBox.setUserData(competenceList.get(i - 1));
                    System.out.println(currentLine() + "competence id user data : " + competenceChoiceBox.getUserData());
                }
            }

            List<Integer> specialisteWithCompetence = DbRead.getSpecialisteForCompetence((int) competenceChoiceBox.getUserData());
            
            tableAmClient.setManaged(false);
            tableAmClient.setVisible(false);
            tableAmSpecialiste.setManaged(true);
            tableAmSpecialiste.setVisible(true);

            tableAmOverlaySpecialistes(tableAmSpecialiste, specialisteWithCompetence);
            searchTableOverlaySpecialiste(tableAmSpecialiste, amSearchField);
            amRowToSpecialiste(tableAmSpecialiste, specialisteField);
        });

        clientField.setOnMouseClicked(e -> {
            
            tableAmSpecialiste.setManaged(false);
            tableAmSpecialiste.setVisible(false);
            tableAmClient.setManaged(true);
            tableAmClient.setVisible(true);
            
            tableAmOverlayClients(tableAmClient);
            searchTableOverlayClient(tableAmClient, amSearchField);
            amRowToClient(tableAmClient, clientField);
        });
/*
        if(competenceChoiceBox != null) {
            specialisteField.setOnMouseClicked(e -> {
                tableAmOverlay(tableAm, "Specialistes");
                searchTableOverlaySpecialiste((TableView<Specialiste>) tableAm, amSearchField);
                
                amRowToSpecialiste((TableView<Specialiste>) tableAm, specialisteField);
            });
        }
       */ 
        

    }

    /**
     * Am row to client.
     *
     * @param tableAm the table am
     * @param clientField the client field
     */
    private void amRowToClient(TableView<Client> tableAm, TextField clientField) {
        tableAm.setRowFactory(tv -> {
            TableRow<Client> row = new TableRow<>();
            row.setOnMousePressed(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    Client rowData = row.getItem();
                    clientField.setText(rowData.getPrenomClient()+ " "+ rowData.getNomClient());
                    clientField.setUserData(rowData.getClientId());
                }
            });
            return row;
        });
    }
    
    /**
     * Am row to specialiste.
     *
     * @param tableAm the table am
     * @param specialisteField the specialiste field
     */
    private void amRowToSpecialiste(TableView<Specialiste> tableAm, TextField specialisteField) {
        tableAm.setRowFactory(tv -> {
            TableRow<Specialiste> row = new TableRow<>();
            row.setOnMousePressed(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    Specialiste rowData = row.getItem();
                    specialisteField.setText(rowData.getPrenomSpecialiste()+ " "+ rowData.getNomSpecialiste());
                    specialisteField.setUserData(rowData.getSpecialisteId());
                }
            });
            return row;
        });
    }

    /**
     * Creates the new acte medical.
     *
     * @param date_debutField the date debut field
     * @param date_finField the date fin field
     * @param clientField the client field
     * @param lieuArray the lieu array
     * @param specialisteField the specialiste field
     * @param competenceArray the competence array
     * @return the string
     */
    private String createNewActeMedical(LocalDate date_debutField, LocalDate date_finField, int clientField, Object[] lieuArray, int specialisteField, Object[] competenceArray) {
        
        int AmNewId = DbRead.readLastId("id_acte_med", "acte_med")+1;
        //System.out.println(currentLine() + AmNewId);
        int idAM = AmNewId;
        String refAm = "REF" + (AmNewId);
        
        Client clientReference = clientsList.filtered(client -> client.getClientId() == clientField).stream().findFirst().orElse(null);       
        
        String clientPrenom = clientReference.getPrenomClient();
        String clientNom = clientReference.getNomClient();
        
        Specialiste specialisteReference = specialistesList.filtered(specialiste -> specialiste.getSpecialisteId() == specialisteField).stream().findFirst().orElse(null); 
        
        String specialistePrenom = specialisteReference.getPrenomSpecialiste();
        String specialisteNom = specialisteReference.getNomSpecialiste();

        int lieuID = (int) lieuArray[0];
        String lieuName = (String) lieuArray[1];
        
        int competenceID = (int) competenceArray[0];
        String competenceName = (String) competenceArray[1];
        
        ActeMedical newActeMedical = new ActeMedical(refAm, date_debutField, date_finField, clientField, lieuID, specialisteField);

        try {
            newActeMedical.insertActeMedicalDB(newActeMedical);
            //System.out.println(currentLine() + newActeMedical.toString() + " added to database without problem");
            
            int thisAmID = DbRead.readLastId("id_acte_med", "acte_med");
            List<String> columnsList = new ArrayList<>(List.of("ID_COMPETENCE","ID_ACTE_MED"));
            List<Object> valuesList =  new ArrayList<>(List.of(competenceID, thisAmID));

            try {
                DbCreate.insert("Necessiter", columnsList, valuesList);
            } catch (SQLException e) {
                throw e;
            }
            
            ActeMedical newActeMedicalObs = new ActeMedical(idAM, refAm, 
                                                            clientField, clientPrenom, clientNom, 
                                                            specialisteField, specialistePrenom, specialisteNom, 
                                                            lieuID, lieuName, date_debutField, date_finField,
                                                            competenceID, competenceName);
            getActeMedicalsObsList().add(newActeMedicalObs);
        } catch (SQLException e) {
            String errorMessage = e.getMessage();
            System.out.println(currentLine() + errorMessage);
            
        }
        return "";
    }

    /**
     * Gets the acte medicals obs list.
     *
     * @return the acte medicals obs list
     */
    public ObservableList<ActeMedical> getActeMedicalsObsList() {
        return actesMedicauxObsList;
    }


    /**
     * Close overlay.
     */
    private void closeOverlay() {
        // Remove the last added overlay pane
        int lastIndex = body.getChildren().size() - 1;
        if (lastIndex >= 0) {
            body.getChildren().remove(lastIndex);
        }
    }


    /*
     * Works out of the box, thanks chatGPT
     */

    /**
     * Search table.
     */
    private void searchTable() {
        FilteredList<ActeMedical> filteredData = new FilteredList<>(actesMedicauxObsList, p -> true);

        // Add listener to the searchField text property
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Create a filtered list to apply the search

            // Set the predicate for the filter
            filteredData.setPredicate(acteMedical -> {
                // If filter text is empty, display all clients
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Convert client information to lowercase for case-insensitive search
                String lowerCaseFilter = newValue.toLowerCase();

                // Check if any of the client attributes contain the filter text
                return acteMedical.getRefActeMed().toLowerCase().contains(lowerCaseFilter)
                        || (acteMedical.getPrenomClient() + " " + acteMedical.getNomClient()).toLowerCase().contains(lowerCaseFilter)
                        || (acteMedical.getPrenomSpecialiste() + " " + acteMedical.getNomSpecialiste()).toLowerCase().contains(lowerCaseFilter)
                        || acteMedical.getNomLieu().toLowerCase().contains(lowerCaseFilter)
                        || acteMedical.getNomCompetence().toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(acteMedical.getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(acteMedical.getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).toLowerCase().contains(lowerCaseFilter);
            });

            // Wrap the FilteredList in a SortedList
            SortedList<ActeMedical> sortedData = new SortedList<>(filteredData);

            // Bind the SortedList comparator to the TableView comparator
            sortedData.comparatorProperty().bind(table.comparatorProperty());

            // Set the items in the TableView with the filtered and sorted data
            table.setItems(sortedData);

            // Display a message when there are no items in the table
            if (sortedData.isEmpty()) {
                // Set a message in the TableView or somewhere appropriate
                // For example, assuming you have a label to display messages:
                table.setPlaceholder(new Label("Try something else, nothing left to see here !!!"));
            }
        });

        // Add event handler to clear the searchField when clicked
        searchField.setOnMouseClicked(event -> {
            // Clear the searchField text
            searchField.clear();

        });
    }

    /**
     * Table am overlay clients.
     *
     * @param thisTableParam the this table param
     */
    private void tableAmOverlayClients(TableView<Client> thisTableParam) {
        
        TableView<Client> thisTable = thisTableParam;
        
        // Add data to the TableView
        thisTable.setItems(clientsList);

        // Create columns
        TableColumn<Client, String> firstname = new TableColumn<>("FirstName");
        TableColumn<Client, String> name = new TableColumn<>("Name");
        TableColumn<Client, String> dateNais = new TableColumn<>("DateNais");
        TableColumn<Client, String> tel = new TableColumn<>("Tel");
        TableColumn<Client, String> email = new TableColumn<>("Email");

        // Define how to get values from the object for each column
        firstname.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPrenomClient()));
        name.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getNomClient()));
        dateNais.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDateNaisClient().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        tel.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTelClient()));
        email.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getEmailClient()));

        DoubleBinding tableWidth = thisTableParam.widthProperty().subtract(23);
        firstname.prefWidthProperty().bind(tableWidth.multiply(0.17));
        name.prefWidthProperty().bind(tableWidth.multiply(0.17));

        dateNais.prefWidthProperty().bind(tableWidth.multiply(0.155));
        tel.prefWidthProperty().bind(tableWidth.multiply(0.155));
        email.prefWidthProperty().bind(tableWidth.multiply(0.35));

        thisTable.getColumns().clear();

        thisTable.getColumns().addAll(firstname, name, dateNais, tel, email);

    }
    
    /**
     * Table am overlay specialistes.
     *
     * @param thisTableParam the this table param
     * @param specialisteWithCompetence the specialiste with competence
     */
    private void tableAmOverlaySpecialistes(TableView<Specialiste> thisTableParam, List<Integer> specialisteWithCompetence) {
        
        // Filter the list based on provided IDs
        List<Specialiste> filteredList = specialistesList.filtered(specialiste -> specialisteWithCompetence.contains(specialiste.getSpecialisteId()));

        // Create a new ObservableList and set it to the table
        ObservableList<Specialiste> observableList = FXCollections.observableArrayList(filteredList);
        thisTableParam.setItems(observableList);

        // Filter the list based on provided IDs and set it to the table
        //thisTableParam.getItems().setAll(specialistesList.filtered(specialiste -> specialisteWithCompetence.contains(specialiste.getSpecialisteId())));

        //System.out.println(currentLine() + "Specialiste IDs: " + specialisteWithCompetence);
        //System.out.println(currentLine() + specialistesList.filtered(specialiste -> integerList.contains(specialiste.getSpecialisteId())));
        
        //thisTable.setItems(specialistesList);
        
        // Create columns
        TableColumn<Specialiste, String> firstname = new TableColumn<>("FirstName");
        TableColumn<Specialiste, String> name = new TableColumn<>("Name");
        TableColumn<Specialiste, String> dateNais = new TableColumn<>("DateNais");
        TableColumn<Specialiste, String> tel = new TableColumn<>("Tel");
        TableColumn<Specialiste, String> email = new TableColumn<>("Email");
        
        // Define how to get values from the object for each column
        firstname.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPrenomSpecialiste()));
        name.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getNomSpecialiste()));
        dateNais.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDateNaisSpecialiste().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        tel.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTelSpecialiste()));
        email.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getEmailSpecialiste()));
        
        DoubleBinding tableWidth = thisTableParam.widthProperty().subtract(23);
        firstname.prefWidthProperty().bind(tableWidth.multiply(0.17));
        name.prefWidthProperty().bind(tableWidth.multiply(0.17));
        dateNais.prefWidthProperty().bind(tableWidth.multiply(0.155));
        tel.prefWidthProperty().bind(tableWidth.multiply(0.155));
        email.prefWidthProperty().bind(tableWidth.multiply(0.35));
        
        thisTableParam.getColumns().clear();
        
        thisTableParam.getColumns().addAll(firstname, name, dateNais, tel, email);

    }

    
    /**
     * Search table overlay client.
     *
     * @param tableAm the table am
     * @param amSearchField the am search field
     */
    private void searchTableOverlayClient(TableView<Client> tableAm, TextField amSearchField) {
        
        FilteredList<Client> filteredData = new FilteredList<>(clientsList, p -> true);
        
        // Add listener to the searchField text property
        amSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Create a filtered list to apply the search

         // Set the predicate for the filter
            filteredData.setPredicate(client -> {
                // If filter text is empty, display all clients
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Convert client information to lowercase for case-insensitive search
                String lowerCaseFilter = newValue.toLowerCase();

                // Check if any of the client attributes contain the filter text
                return 
                        client.getPrenomClient().toLowerCase().contains(lowerCaseFilter)
                        || client.getNomClient().toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(client.getDateNaisClient().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).toLowerCase().contains(lowerCaseFilter)
                        || client.getTelClient().toLowerCase().contains(lowerCaseFilter)
                        || client.getEmailClient().toLowerCase().contains(lowerCaseFilter);
            });

            // Wrap the FilteredList in a SortedList
            SortedList<Client> sortedData = new SortedList<>(filteredData);

            // Bind the SortedList comparator to the TableView comparator
            sortedData.comparatorProperty().bind(tableAm.comparatorProperty());

            // Set the items in the TableView with the filtered and sorted data
            tableAm.setItems(sortedData);

            // Display a message when there are no items in the table
            if (sortedData.isEmpty()) {
                // Set a message in the TableView or somewhere appropriate
                // For example, assuming you have a label to display messages:
                tableAm.setPlaceholder(new Label("Try something else, nothing left to see here !!!"));
            }
        });
        
     // Add event handler to clear the searchField when clicked
        amSearchField.setOnMouseClicked(event -> {
            // Clear the searchField text
            amSearchField.clear();
            
        });
        
    }
    
    /**
     * Search table overlay specialiste.
     *
     * @param tableAm the table am
     * @param amSearchField the am search field
     */
    private void searchTableOverlaySpecialiste(TableView<Specialiste> tableAm, TextField amSearchField) {
        FilteredList<Specialiste> filteredData = new FilteredList<>(tableAm.getItems(), p -> true);
        

        // Add listener to the searchField text property
        amSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
        // Create a filtered list to apply the search


        // Set the predicate for the filter
        filteredData.setPredicate(specialiste -> {
            // If filter text is empty, display all specialistes
            if (newValue == null || newValue.isEmpty()) {
                return true;
            }

            // Convert specialiste information to lowercase for case-insensitive search
            String lowerCaseFilter = newValue.toLowerCase();

            // Check if any of the specialiste attributes contain the filter text
            return specialiste.getPrenomSpecialiste().toLowerCase().contains(lowerCaseFilter)
                    ||specialiste.getNomSpecialiste().toLowerCase().contains(lowerCaseFilter)
                    || String.valueOf(specialiste.getDateNaisSpecialiste().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).toLowerCase().contains(lowerCaseFilter)
                    || specialiste.getTelSpecialiste().toLowerCase().contains(lowerCaseFilter)
                    || specialiste.getEmailSpecialiste().toLowerCase().contains(lowerCaseFilter);
            });

            // Wrap the FilteredList in a SortedList
            SortedList<Specialiste> sortedData = new SortedList<>(filteredData);

            // Bind the SortedList comparator to the TableView comparator
            sortedData.comparatorProperty().bind(tableAm.comparatorProperty());

            // Set the items in the TableView with the filtered and sorted data
            tableAm.setItems(sortedData);

            // Display a message when there are no items in the table
            if (sortedData.isEmpty()) {
                // Set a message in the TableView or somewhere appropriate
                // For example, assuming you have a label to display messages:
                tableAm.setPlaceholder(new Label("Try something else, nothing left to see here !!!"));
            }
        });
        
     // Add event handler to clear the searchField when clicked
        amSearchField.setOnMouseClicked(event -> {
            // Clear the searchField text
            amSearchField.clear();
            
        });
        
    }
    
    /**
     * Gets the lieu list.
     *
     * @return the lieu list
     */
    private ObservableList<Object> getLieuList() {
        
        List<List<Object>> lieuListDB;
        
        lieuListDB = DbRead.readTable("LIEU", "NOM_LIEU");
        
        if(lieuListDB != null) {
            for (List<Object> row : lieuListDB) {
            
                BigDecimal idLieuDB = (BigDecimal) row.get(0);
                int idLieu = idLieuDB.intValue();
                
                String nomLieu = (String) row.get(1);
                
                
                lieuList.addAll(idLieu, nomLieu);
            }

        }
        return lieuList;
    }
    
    /**
     * Gets the competence list.
     *
     * @return the competence list
     */
    private ObservableList<Object> getCompetenceList() {
        
        List<List<Object>> competenceListDB;
        
        competenceListDB = DbRead.readTable("COMPETENCE", "NOM_COMPETENCE");
        
        if(competenceListDB != null) {
            for (List<Object> row : competenceListDB) {
            
                BigDecimal idCompetenceDB = (BigDecimal) row.get(0);
                int idCompetence = idCompetenceDB.intValue();
                
                String nomCompetence = (String) row.get(1);
                
                
                competenceList.addAll(idCompetence, nomCompetence);
            }

        }
        return competenceList;
    }
    
    /**
     * Current line.
     *
     * @return the string
     */
    private String currentLine() {
        return "Class ActesMedicauxController @ line " + Thread.currentThread().getStackTrace()[2].getLineNumber() + " -> ";
    }

}
