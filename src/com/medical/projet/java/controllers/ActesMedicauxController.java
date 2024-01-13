package com.medical.projet.java.controllers;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.medical.projet.java.models.ActeMedical;
import com.medical.projet.java.models.Client;
import com.medical.projet.java.models.Specialiste;
import com.medical.projet.java.utility.AppSecurity;
import com.medical.projet.java.utility.AppSettings;
import com.medical.projet.java.utility.database.DbRead;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
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


public class ActesMedicauxController {

    private ObservableList<ActeMedical> actesMedicauxObsList = FXCollections.observableArrayList();
    
    private ObservableList<Client> clientsList = ClientController.getClientsObsList();
    
    private ObservableList<Specialiste> specialistesList = SpecialisteController.getSpecialistesObsList();

    private static final String tableNameSuffix = "_ACTE_MED";

    /** The body. **/

    @FXML
    private StackPane body;

    @FXML
    private TableView<ActeMedical> table;

    @FXML
    private TableColumn<ActeMedical, String> ref_acte_med;

    @FXML
    private TableColumn<ActeMedical, String> client;

    @FXML
    private TableColumn<ActeMedical, String> specialiste;
    
    @FXML
    private TableColumn<ActeMedical, Integer> typeOperation;

    @FXML
    private TableColumn<ActeMedical, Integer> lieu;

    @FXML
    private TableColumn<ActeMedical, String> date_debut;

    @FXML
    private TableColumn<ActeMedical, String> date_fin;

    @FXML
    private Button createButton;

    @FXML
    private TextField searchField;


    public void initialize() {

        dynamicCssStuff();

        loadingTableIcon();

        new Thread(() -> {
            Platform.runLater(() -> {
                readAllActesMedicaux();
                updateTableView();
                searchTable();
            });
        }).start();

        // When data's row is clicked, open overlay with data from that row
        openOverlayPopulateData();

        // Create a new ActeMedical
        openOverlayNewActeMedical();

    }

    private void dynamicCssStuff() {

        // absolute position of the create button on the right side
        createButton.layoutXProperty().bind(body.widthProperty().subtract(createButton.widthProperty()));

        // auto size of the TableView columns depending of the table - scrollbar
        DoubleBinding tableWidth = table.widthProperty().subtract(24);
        ref_acte_med.prefWidthProperty().bind(tableWidth.multiply(0.1));
        client.prefWidthProperty().bind(tableWidth.multiply(0.15));
        specialiste.prefWidthProperty().bind(tableWidth.multiply(0.15));
        typeOperation.prefWidthProperty().bind(tableWidth.multiply(0.15));
        lieu.prefWidthProperty().bind(tableWidth.multiply(0.15));
        date_debut.prefWidthProperty().bind(tableWidth.multiply(0.15));
        date_fin.prefWidthProperty().bind(tableWidth.multiply(0.15));
    }

    private void loadingTableIcon() {
        // Load the loading GIF n TableView
        Image loadingImage = new Image(getClass().getResourceAsStream(AppSettings.INSTANCE.imagesPath+"loading.gif"));
        ImageView loadingImageView = new ImageView(loadingImage);

        // Set the loading GIF as the custom placeholder
        table.setPlaceholder(loadingImageView);
    }

    private ObservableList<ActeMedical> readAllActesMedicaux() {

        // Get raw data from the ActeMedical model
        List<List<Object>> rawActeMedicalData = null;
        Label placeholderLabel = new Label(); // Create label outside of the timer

        try {
            rawActeMedicalData = ActeMedical.getAllActesMedicauxData();
        } catch (Exception e) {
            final int[] seconds = {10}; // Initial countdown value

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        placeholderLabel.setText("Connection to the database failed. Retrying in " + seconds[0] + " sec.");
                        table.setPlaceholder(placeholderLabel);
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

                String ref = (String) row.get(1);
                BigDecimal clientBD = (BigDecimal) row.get(4);
                int client = clientBD.intValue();
                BigDecimal specialisteBD = (BigDecimal) row.get(6);
                int specialiste = specialisteBD.intValue();
                BigDecimal lieuBD = (BigDecimal) row.get(5);
                int lieu = lieuBD.intValue();
                // Convert date to java.time.LocalDate
                java.sql.Timestamp timestamp = (java.sql.Timestamp) row.get(2);
                LocalDate date_debut = timestamp.toLocalDateTime().toLocalDate();
                // Convert date to java.time.LocalDate
                java.sql.Timestamp timestamp2 = (java.sql.Timestamp) row.get(3);
                LocalDate date_fin = timestamp2.toLocalDateTime().toLocalDate();
                // Create a ActeMedical object and add to the list
                actesMedicauxObsList.add(new ActeMedical(ref, date_debut, date_fin, client, lieu, specialiste));
            }
        }
        return actesMedicauxObsList;
    }


    private void updateTableView() {

        // Set the items with the correct data type
        table.setItems(actesMedicauxObsList);

        // Populate columns of TableView with the data
        ref_acte_med.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRefActeMed()));
        
        //client.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getIdClient()).asObject());
        client.setCellValueFactory(param -> new SimpleStringProperty(clientsList.get(param.getValue().getIdClient()).getPrenomClient() + " " + clientsList.get(param.getValue().getIdClient()).getNomClient()));
        
        //specialiste.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getIdSpecialiste()).asObject());
        specialiste.setCellValueFactory(param -> new SimpleStringProperty(specialistesList.get(param.getValue().getIdSpecialiste()).getPrenomSpecialiste() + " " + specialistesList.get(param.getValue().getIdSpecialiste()).getNomSpecialiste()));
        
        typeOperation.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getIdSpecialiste()).asObject());
        lieu.setCellValueFactory(param -> new SimpleIntegerProperty(param.getValue().getIdLieu()).asObject());
        date_debut.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDateDebut().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        date_fin.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDateFin().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

    }



    private void openOverlayPopulateData() {
        table.setRowFactory(tv -> {
            TableRow<ActeMedical> row = new TableRow<>();
            row.setOnMousePressed(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    ActeMedical rowData = row.getItem();
                    openOverlayWithActeMedicalData(rowData);
                }
            });
            return row;
        });
    }

    private void openOverlayNewActeMedical() {
        createButton.setOnMouseClicked(event -> {
            openOverlayForNewActeMedical();
        });
    }

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

    // overlay with client data
    private void openOverlayWithActeMedicalData(ActeMedical acteMedical) {
        createOverlay(body, contentPane -> populateOverlayContent(contentPane, acteMedical));
    }

    // overlay for a new client
    private void openOverlayForNewActeMedical() {
        createOverlay(body, contentPane -> populateOverlayForNewActeMedical(contentPane));
    }

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
        
        Label competenceLabel = new Label("Acte Medical");
        ComboBox<Integer> competenceChoiceBox = new ComboBox<>();
        List<Integer> competenceList = new ArrayList<>(List.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30));
        competenceChoiceBox.getItems().addAll(competenceList);
        competenceChoiceBox.setPrefWidth(230);
        competenceChoiceBox.setVisibleRowCount(10);
        
        VBox competenceAmVbox = new VBox();
        competenceAmVbox.getChildren().addAll(competenceLabel, competenceChoiceBox);
        
        HBox topAmHbox = new HBox();
        topAmHbox.setId("topAmHbox");
        topAmHbox.getChildren().addAll(refAmVbox, competenceAmVbox);
        

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

        //buttonDelete.layoutXProperty().bind(body.widthProperty().subtract(buttonDelete.widthProperty()));


        Label clientLabel = new Label("Client");
        clientLabel.setId("ID_CLIENT");

        TextField clientField = new TextField();
        clientField.setText(clientsList.get(acteMedical.getIdClient()).getPrenomClient() + " " + clientsList.get(acteMedical.getIdClient()).getNomClient());
        clientField.setEditable(false);

        VBox clientAmVbox = new VBox();
        clientAmVbox.getChildren().addAll(clientLabel, clientField);

        Label specialisteLabel = new Label("Specialiste");
        specialisteLabel.setId("ID_SPECIALISTE");

        TextField specialisteField = new TextField();
        specialisteField.setText(Integer.toString(acteMedical.getIdSpecialiste()));
        specialisteField.setEditable(false);

        VBox specialisteAmVbox = new VBox();
        specialisteAmVbox.getChildren().addAll(specialisteLabel, specialisteField);

        HBox overlayAmCenter = new HBox();
        overlayAmCenter.setId("overlayAmCenter");
        overlayAmCenter.getChildren().addAll(clientAmVbox, specialisteAmVbox);


        Label lieuLabel = new Label("Lieu");
        lieuLabel.setId("ID_LIEU");

        ChoiceBox<Integer> lieuChoiceBox = new ChoiceBox<>();
        lieuChoiceBox.setValue(acteMedical.getIdLieu());
        List<Integer> lieuList = new ArrayList<>(List.of(1,2,3,4,5,6,7,8,9,10));
        lieuChoiceBox.getItems().addAll(lieuList);

        VBox lieuAmVbox = new VBox();
        lieuAmVbox.getChildren().addAll(lieuLabel, lieuChoiceBox);

        Label date_debutLabel = new Label("Date de début");
        date_debutLabel.setId("DATE_DEBUT");

        DatePicker date_debutField = new DatePicker();
        date_debutField.setValue(acteMedical.getDateDebut());

        VBox date_debutAmVbox = new VBox();
        date_debutAmVbox.getChildren().addAll(date_debutLabel, date_debutField);

        Label date_finLabel = new Label("Date de fin");
        date_finLabel.setId("DATE_FIN");

        DatePicker date_finField = new DatePicker();
        date_finField.setValue(acteMedical.getDateFin());

        VBox date_finAmVbox = new VBox();
        date_finAmVbox.getChildren().addAll(date_finLabel, date_finField);

        HBox overlayAmBottom = new HBox();
        overlayAmBottom.setId("overlayAmBottom");
        overlayAmBottom.getChildren().addAll(lieuAmVbox, date_debutAmVbox, date_finAmVbox );

        TableView<Client> tableAmClient = new TableView<>();
        tableAmClient.setId("tableAmClient");
        TableView<Specialiste> tableAmSpecialiste = new TableView<>();
        tableAmSpecialiste.setId("tableAmSpecialiste");
        
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
            updateActeMedical(acteMedical, clientLabel.getId(),         acteMedical.getIdClient(),        Integer.parseInt(clientField.getText()),      refLabel.getId(),   acteMedical.getRefActeMed() );
            updateActeMedical(acteMedical, specialisteLabel.getId(),    acteMedical.getIdSpecialiste(),   Integer.parseInt(specialisteField.getText()), refLabel.getId(),   acteMedical.getRefActeMed() );
            updateActeMedical(acteMedical, lieuLabel.getId(),           acteMedical.getIdLieu(),          lieuChoiceBox.getValue(),                     refLabel.getId(),   acteMedical.getRefActeMed() );
            updateActeMedical(acteMedical, date_debutLabel.getId(),     acteMedical.getDateDebut(),       date_debutField.getValue(),                   refLabel.getId(),   acteMedical.getRefActeMed() );
            updateActeMedical(acteMedical, date_finLabel.getId(),       acteMedical.getDateFin(),         date_finField.getValue(),                     refLabel.getId(),   acteMedical.getRefActeMed() );
            closeOverlay();
        });

        competenceChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Perform your action here
            //System.out.println("Selected: " + newValue);

            List<BigDecimal> specialisteWithCompetence = DbRead.getSpecialisteForCompetence(newValue);
            
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

    private void deleteActeMedical(ActeMedical acteMedical) {
        acteMedical.deleteActeMedicalDB(acteMedical.getRefActeMed());
    }

    public void updateActeMedical(ActeMedical acteMedical, String fieldName, Object oldValue, Object newValue, String checkColumn, String checkValue) {

        if(newValue instanceof LocalDate) {
            //System.out.println("this is a local date");
            String newValueTemp = AppSecurity.sanitize(newValue.toString());
            newValue = LocalDate.parse(newValueTemp, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }else if(newValue instanceof Integer){
            //System.out.println("this is an integer");
            String newValueTemp = AppSecurity.sanitize(newValue.toString());
            newValue = Integer.parseInt(newValueTemp);
        }else {
            //System.out.println("fuck this");
            newValue = AppSecurity.sanitize(newValue.toString());
        }

        switch (fieldName) {
            case "REF_ACTE_MED" -> {
                if (compare(oldValue, newValue)) {

                    try {
                        acteMedical.updateActeMedicalDB(fieldName, newValue, checkColumn, checkValue);
                        acteMedical.setRefActeMed(checkValue);
                        System.out.println("ref has been changed");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            case "ID_CLIENT" -> {
                if (compare(oldValue, newValue)) {
                    try {
                        acteMedical.updateActeMedicalDB(fieldName, newValue, checkColumn, checkValue);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    acteMedical.setIdClient((int) newValue);
                    System.out.println("client has been changed");
                }
            }
            case "ID_SPECIALISTE" -> {
                if (compare(oldValue, newValue)) {
                    try {
                        acteMedical.updateActeMedicalDB(fieldName, newValue, checkColumn, checkValue);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    acteMedical.setIdSpecialiste((int) newValue);
                    System.out.println("specialiste has been changed");
                }
            }
            case "ID_LIEU" -> {
                if (compare(oldValue, newValue)) {
                    try {
                        acteMedical.updateActeMedicalDB(fieldName, newValue, checkColumn, checkValue);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    acteMedical.setIdLieu((int) newValue);
                    System.out.println("lieu has been changed");
                }
            }
            case "DATE_DEBUT" -> {
                if (compare(oldValue, newValue)) {
                    try {
                        acteMedical.updateActeMedicalDB(fieldName, newValue, checkColumn, checkValue);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    LocalDate newDate = LocalDate.parse(newValue.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    acteMedical.setDateDebut(newDate);
                    System.out.println("date_debut has been changed");
                }
            }
            case "DATE_FIN" -> {
                if (compare(oldValue, newValue)) {
                    try {
                        acteMedical.updateActeMedicalDB(fieldName, newValue, checkColumn, checkValue);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    LocalDate newDate = LocalDate.parse(newValue.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    acteMedical.setDateFin(newDate);
                    System.out.println("date_fin has been changed");
                }
            }
            default -> {}
        }
        table.refresh();
    }

    private boolean compare(Object oldValue, Object newValue) {
        return !Objects.equals(oldValue, newValue);
    }


    // populating inputs in the overlay when clicking create new client
    private void populateOverlayForNewActeMedical(BorderPane contentPane) {

        Label competenceLabel = new Label("Acte Medical");
        ComboBox<Integer> competenceChoiceBox = new ComboBox<>();
        List<Integer> competenceList = new ArrayList<>(List.of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30));
        competenceChoiceBox.getItems().addAll(competenceList);
        competenceChoiceBox.setPrefWidth(230);
        competenceChoiceBox.setVisibleRowCount(10);
        //refField.setDisable(true);
        VBox competenceAmVbox = new VBox();
        competenceAmVbox.getChildren().addAll(competenceLabel, competenceChoiceBox);

        HBox overlayAmTop = new HBox();
        overlayAmTop.setId("overlayAmTop");
        overlayAmTop.getChildren().addAll(competenceAmVbox);


        Label clientLabel = new Label("Client");
        TextField clientField = new TextField();
        clientField.setEditable(false);
        VBox clientAmVbox = new VBox();
        clientAmVbox.getChildren().addAll(clientLabel, clientField);

        Label specialisteLabel = new Label("Specialiste");
        TextField specialisteField = new TextField();
        specialisteField.setEditable(false);
        VBox specialisteAmVbox = new VBox();
        specialisteAmVbox.getChildren().addAll(specialisteLabel, specialisteField);

        HBox overlayAmCenter = new HBox();
        overlayAmCenter.setId("overlayAmCenter");
        overlayAmCenter.getChildren().addAll(clientAmVbox, specialisteAmVbox);


        Label lieuLabel = new Label("Lieu");
        ComboBox<Integer> lieuChoiceBox = new ComboBox<>();
        List<Integer> lieuList = new ArrayList<>(List.of(1,2,3,4,5,6,7,8,9,10));
        lieuChoiceBox.getItems().addAll(lieuList);
        lieuChoiceBox.setPrefWidth(230);
        VBox lieueAmVbox = new VBox();
        lieueAmVbox.getChildren().addAll(lieuLabel, lieuChoiceBox);

        Label date_debutLabel = new Label("Date de début");
        DatePicker date_debutField = new DatePicker();
        VBox date_debutAmVbox = new VBox();
        date_debutAmVbox.getChildren().addAll(date_debutLabel, date_debutField);

        Label date_finLabel = new Label("Date de fin");
        DatePicker date_finField = new DatePicker();
        VBox date_finAmVbox = new VBox();
        date_finAmVbox.getChildren().addAll(date_finLabel, date_finField);

        HBox overlayAmBottom = new HBox();
        overlayAmBottom.setId("overlayAmBottom");
        overlayAmBottom.getChildren().addAll(lieueAmVbox, date_debutAmVbox, date_finAmVbox );

        TableView<Client> tableAmClient = new TableView<>();
        tableAmClient.setId("tableAmClient");
        TableView<Specialiste> tableAmSpecialiste = new TableView<>();
        tableAmSpecialiste.setId("tableAmSpecialiste");
        
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
            String newActeMedicalOK = createNewActeMedical( competenceChoiceBox.getValue(),
                                                            date_debutField.getValue(),
                                                            date_finField.getValue(),
                                                            Integer.parseInt(clientField.getText()),
                                                            lieuChoiceBox.getValue(),
                                                            Integer.parseInt(specialisteField.getText())
                                                            );

            if(newActeMedicalOK.equals("")) {
                closeOverlay();
            }else {
                errorLabel.setText(newActeMedicalOK);
            }
        });
        
        competenceChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Perform your action here
            //System.out.println("Selected: " + newValue);

            List<BigDecimal> specialisteWithCompetence = DbRead.getSpecialisteForCompetence(newValue);
            
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

    private void amRowToClient(TableView<Client> tableAm, TextField clientField) {
        tableAm.setRowFactory(tv -> {
            TableRow<Client> row = new TableRow<>();
            row.setOnMousePressed(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    Client rowData = row.getItem();
                    clientField.setText(rowData.getPrenomClient()+ " "+ rowData.getNomClient());
                }
            });
            return row;
        });
    }
    
    private void amRowToSpecialiste(TableView<Specialiste> tableAm, TextField specialisteField) {
        tableAm.setRowFactory(tv -> {
            TableRow<Specialiste> row = new TableRow<>();
            row.setOnMousePressed(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    Specialiste rowData = row.getItem();
                    specialisteField.setText(rowData.getPrenomSpecialiste()+ " "+ rowData.getNomSpecialiste());
                }
            });
            return row;
        });
    }

    private String createNewActeMedical(int competenceID, LocalDate date_debutField, LocalDate date_finField, int clientField, int lieuField, int specialisteField) {

        ActeMedical newActeMedical = new ActeMedical(competenceID, date_debutField, date_finField, clientField, lieuField, specialisteField);

        try {
            newActeMedical.insertActeMedicalDB(newActeMedical);
            System.out.println(newActeMedical.toString() + " added to database without problem");
            getActeMedicalsObsList().add(newActeMedical);
        } catch (SQLException e) {
            String errorMessage = e.getMessage();
            System.out.println(errorMessage);
            /*
            int startIndex = errorMessage.indexOf("ORA-20001: ");
            if (startIndex != -1) {
                // Extract the substring from the index to the end of the line
                int endIndex = errorMessage.indexOf('\n', startIndex);
                String cleanErrorMessage;
                if (endIndex != -1) {
                    cleanErrorMessage = errorMessage.substring(startIndex + "ORA-20001: ".length(), endIndex).trim();
                    System.out.println(cleanErrorMessage);
                    return cleanErrorMessage;
                } else {
                    cleanErrorMessage = errorMessage.substring(startIndex + "ORA-20001: ".length()).trim();
                    System.out.println(cleanErrorMessage);
                    return cleanErrorMessage;
                }
            } else {
                System.out.println(errorMessage);
                return errorMessage;
            }
             */
        }
        return "";
    }

    public ObservableList<ActeMedical> getActeMedicalsObsList() {
        return actesMedicauxObsList;
    }


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

                String idClient = Integer.toString(acteMedical.getIdClient());
                String idSpecialiste = Integer.toString(acteMedical.getIdSpecialiste());
                String idLieu = Integer.toString(acteMedical.getIdLieu());

                // Check if any of the client attributes contain the filter text
                return acteMedical.getRefActeMed().toLowerCase().contains(lowerCaseFilter)
                        || idClient.toLowerCase().contains(lowerCaseFilter)
                        || idSpecialiste.toLowerCase().contains(lowerCaseFilter)
                        || idLieu.toLowerCase().contains(lowerCaseFilter)
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
    
    private void tableAmOverlaySpecialistes(TableView<Specialiste> thisTableParam, List<BigDecimal> specialisteWithCompetence) {
        
        // Add data to the TableView
        TableView<Specialiste> thisTable = thisTableParam;
        
        // Transform the list of BigDecimal to a list of Integer
        List<Integer> integerList = new ArrayList<>();
        for (BigDecimal item : specialisteWithCompetence) {
            integerList.add(item.intValue());
        }
        
     

        // Filter the list based on provided IDs and set it to the table
        thisTable.getItems().setAll(specialistesList.filtered(specialiste -> integerList.contains(specialiste.getSpecialisteId())));

       //System.out.println("Specialiste IDs: " + specialisteWithCompetence);
       //System.out.println(specialistesList.filtered(specialiste -> integerList.contains(specialiste.getSpecialisteId())));
        
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
        
        thisTable.getColumns().clear();
        
        thisTable.getColumns().addAll(firstname, name, dateNais, tel, email);

    }

    
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
    
    private void searchTableOverlaySpecialiste(TableView<Specialiste> tableAm, TextField amSearchField) {
        FilteredList<Specialiste> filteredData = new FilteredList<>(specialistesList, p -> true);
        

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
}
