package com.medical.projet.java.controllers;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import com.medical.projet.java.models.ActeMedical;
import com.medical.projet.java.utility.AppSecurity;
import com.medical.projet.java.utility.AppSettings;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class ActesMedicauxController {

    private ObservableList<ActeMedical> actesMedicauxObsList = FXCollections.observableArrayList();

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
    private TableColumn<ActeMedical, String> lieu;

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
        
        createButton.layoutXProperty().bind(body.widthProperty().subtract(createButton.widthProperty()));
        
        // Set percentage widths for the columns
        double tableWidth = table.getPrefWidth();
        ref_acte_med.prefWidthProperty().bind(table.widthProperty().multiply(0.1)); // 20% of table width
        client.prefWidthProperty().bind(table.widthProperty().multiply(0.2)); // 20% of table width
        specialiste.prefWidthProperty().bind(table.widthProperty().multiply(0.2)); // 20% of table width
        lieu.prefWidthProperty().bind(table.widthProperty().multiply(0.18)); // 20% of table width
        date_debut.prefWidthProperty().bind(table.widthProperty().multiply(0.15)); // 20% of table width
        date_fin.prefWidthProperty().bind(table.widthProperty().multiply(0.15)); // 20% of table width
    }

    private void loadingTableIcon() {
        // Load the loading GIF
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
            final int[] seconds = {30}; // Initial countdown value

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
                String client = (String) row.get(2);
                String specialiste = (String) row.get(3);
                String lieu = (String) row.get(4);
                // Convert date to java.time.LocalDate
                java.sql.Timestamp timestamp = (java.sql.Timestamp) row.get(5);
                LocalDate date_debut = timestamp.toLocalDateTime().toLocalDate();
                // Convert date to java.time.LocalDate
                java.sql.Timestamp timestamp2 = (java.sql.Timestamp) row.get(6);
                LocalDate date_fin = timestamp2.toLocalDateTime().toLocalDate();
                // Create a ActeMedical object and add to the list
                actesMedicauxObsList.add(new ActeMedical(ref, client, specialiste, lieu, date_debut, date_fin));
            }
        }
        return actesMedicauxObsList;
    }


    private void updateTableView() {

        // Set the items with the correct data type
        table.setItems(actesMedicauxObsList);

        // Populate columns of TableView with the data
        ref_acte_med.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getREF_ACTE_MED()));
        client.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getID_CLIENT()));
        specialiste.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getID_SPECIALISTE()));
        lieu.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getID_LIEU()));
        date_debut.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDATE_DEBUT().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        date_fin.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDATE_FIN().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

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
        Pane overlayPane = new Pane();
        overlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4);"); // Semi-transparent black background
        overlayPane.setPrefSize(stackPane.getWidth(), stackPane.getHeight());

        // Create your content pane
        BorderPane contentPane = new BorderPane();
        contentPane.setId("overlayContentPane");
        contentPane.setPrefSize(820, 560);

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
        contentPane.setLayoutX(((stackPane.getWidth() - contentPane.getPrefWidth()) / 2) + 20);
        contentPane.setLayoutY(((stackPane.getHeight() - contentPane.getPrefHeight()) / 2) + 5);
    }

    // overlay with client data
    private void openOverlayWithActeMedicalData(ActeMedical client) {
        createOverlay(body, contentPane -> populateOverlayContent(contentPane, client));
    }

    // overlay for a new client
    private void openOverlayForNewActeMedical() {
        createOverlay(body, contentPane -> populateOverlayForNewActeMedical(contentPane));
    }

    //  when clicking on a row in Tableview, populate the data of that row in the overlay
    private void populateOverlayContent(BorderPane contentPane, ActeMedical client) {
/*
        Label nameLabel = new Label("Name");
        nameLabel.setId("NOM" + tableNameShort);
        TextField nameField = new TextField();
        nameField.setText(client.getNomActeMedical());

        Label surnameLabel = new Label("Surname");
        surnameLabel.setId("PRENOM" + tableNameShort);
        TextField surnameField = new TextField();
        surnameField.setText(client.getPrenomActeMedical());

        Label date_naisLabel = new Label("Birthday");
        date_naisLabel.setId("DATE_NAIS" + tableNameShort);
        DatePicker date_naisField = new DatePicker();
        date_naisField.setValue(client.getDateNaisActeMedical());

        Label telLabel = new Label("Telephone");
        telLabel.setId("TEL" + tableNameShort);
        TextField telField = new TextField();
        telField.setText(client.getTelActeMedical());

        Label emailLabel = new Label("Email");
        emailLabel.setId("EMAIL" + tableNameShort);
        TextField emailField = new TextField();
        emailField.setText(client.getEmailActeMedical());

        VBox overLayContent = new VBox();
        overLayContent.setId("overLayContent");
        overLayContent.getChildren().addAll(nameLabel, nameField, surnameLabel, surnameField, date_naisLabel, date_naisField, telLabel, telField, emailLabel, emailField);

        Button buttonDelete = new Button("Delete");
        buttonDelete.setId("DeleteButton");
        Button buttonOk = new Button("ok");
        Button buttonCancel = new Button("Cancel");

        HBox overlayBottomButtons = new HBox();
        overlayBottomButtons.setId("overlayBottomButtons");
        overlayBottomButtons.getChildren().addAll(buttonOk, buttonCancel);

        HBox overlayTopDelete = new HBox();
        overlayTopDelete.setId("overlayTopDelete");
        overlayTopDelete.getChildren().addAll(buttonDelete);

        contentPane.setTop(overlayTopDelete);
        contentPane.setCenter(overLayContent);
        contentPane.setBottom(overlayBottomButtons);

        // buttons event logic
        buttonCancel.setOnAction(e -> {
            closeOverlay();
        });

        buttonDelete.setOnAction(e -> {
            deleteActeMedical(client);
            getActeMedicalsObsList().remove(client);
            closeOverlay();
        });

        buttonOk.setOnAction(e -> {
            updateActeMedical(client, nameLabel.getId(),         client.getNomActeMedical(),      nameField.getText(),        emailLabel.getId(),     client.getEmailActeMedical() );
            updateActeMedical(client, surnameLabel.getId(),      client.getPrenomActeMedical(),   surnameField.getText(),     emailLabel.getId(),     client.getEmailActeMedical() );
            updateActeMedical(client, date_naisLabel.getId(),    client.getDateNaisActeMedical(), date_naisField.getValue(),  emailLabel.getId(),     client.getEmailActeMedical() );
            updateActeMedical(client, telLabel.getId(),          client.getTelActeMedical(),      telField.getText(),         emailLabel.getId(),     client.getEmailActeMedical() );
            updateActeMedical(client, emailLabel.getId(),        client.getEmailActeMedical(),    emailField.getText(),       emailLabel.getId(),     client.getEmailActeMedical() );
            closeOverlay();
        });
        
*/
    }

    private void deleteActeMedical(ActeMedical acteMedical) {
        acteMedical.deleteActeMedicalDB(acteMedical.getREF_ACTE_MED());
    }

    public void updateActeMedical(ActeMedical acteMedical, String fieldName, Object oldValue, Object newValue, String checkColumn, String checkValue) {

        if(!(newValue instanceof LocalDate)) {
            newValue = AppSecurity.sanitize(newValue.toString());
        }

        switch (fieldName) {
        case "REF_ACTE_MED" -> {
            if (compare(oldValue, newValue)) {

                try {
                    acteMedical.updateActeMedicalDB(fieldName, newValue, checkColumn, checkValue);
                    acteMedical.setREF_ACTE_MED(checkValue);
                    System.out.println("ref has been changed");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        case "CLIENT" -> {
            if (compare(oldValue, newValue)) {
                try {
                    acteMedical.updateActeMedicalDB(fieldName, newValue, checkColumn, checkValue);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                acteMedical.setID_CLIENT(checkValue);
                System.out.println("client has been changed");
            }
        }
        case "SPECIALISTE" -> {
            if (compare(oldValue, newValue)) {
                try {
                    acteMedical.updateActeMedicalDB(fieldName, newValue, checkColumn, checkValue);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                acteMedical.setID_SPECIALISTE(checkValue);
                System.out.println("specialiste has been changed");
            }
        }
        case "LIEU" -> {
            if (compare(oldValue, newValue)) {
                try {
                    acteMedical.updateActeMedicalDB(fieldName, newValue, checkColumn, checkValue);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                acteMedical.setID_LIEU(checkValue);
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
                acteMedical.setDATE_DEBUT(newDate);
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
                acteMedical.setDATE_FIN(newDate);
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

        Label amLabel = new Label("Acte Medical");
        TextField amField = new TextField();

        Label clientLabel = new Label("Client");
        TextField clientField = new TextField();

        Label specialisteLabel = new Label("Specialiste");
        TextField specialisteField = new TextField();

        Label lieuLabel = new Label("Lieu");
        TextField lieuField = new TextField();

        Label date_debutLabel = new Label("Date de début");
        DatePicker date_debutField = new DatePicker();
        
        Label date_finLabel = new Label("Date de fin");
        DatePicker date_finField = new DatePicker();

        Label errorLabel = new Label("");
        errorLabel.setId("errorLabelnew");

        VBox overLayContent = new VBox();
        overLayContent.setId("overLayContent");
        overLayContent.getChildren().addAll(amLabel, amField, clientLabel, clientField, specialisteLabel, specialisteField, lieuLabel, lieuField, date_debutLabel, date_debutField, date_finLabel, date_finField, errorLabel);

        Button buttonOk = new Button("ok");
        Button buttonCancel = new Button("Cancel");

        HBox overlayBottomButtons = new HBox();
        overlayBottomButtons.setId("overlayBottomButtons");
        overlayBottomButtons.getChildren().addAll(buttonOk, buttonCancel);

        contentPane.setCenter(overLayContent);
        contentPane.setBottom(overlayBottomButtons);

        // buttons event logic
        buttonCancel.setOnAction(e -> {
            closeOverlay();
        });

        buttonOk.setOnAction(e -> {
            String newActeMedicalOK = createNewActeMedical(amField.getText(), clientField.getText(), specialisteField.getText(), lieuField.getText(), date_debutField.getValue(), date_finField.getValue());

            if(newActeMedicalOK.equals("")) {
                closeOverlay();
            }else {
                errorLabel.setText(newActeMedicalOK);
            }
        });
    }

    private String createNewActeMedical(String amField, String clientField, String specialisteField, String lieuField, LocalDate date_debutField, LocalDate date_finField) {

        ActeMedical newActeMedical = new ActeMedical(amField, clientField, specialisteField, lieuField, date_debutField, date_finField);

        try {
            newActeMedical.insertActeMedicalDB(newActeMedical);
            System.out.println(newActeMedical.toString() + " added to database without problem");
            getActeMedicalsObsList().add(newActeMedical);
        } catch (SQLException e) {
            String errorMessage = e.getMessage();
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

                // Check if any of the client attributes contain the filter text
                return acteMedical.getREF_ACTE_MED().toLowerCase().contains(lowerCaseFilter)
                        || acteMedical.getID_CLIENT().toLowerCase().contains(lowerCaseFilter)
                        || acteMedical.getID_SPECIALISTE().toLowerCase().contains(lowerCaseFilter)
                        || acteMedical.getID_LIEU().toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(acteMedical.getDATE_DEBUT()).toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(acteMedical.getDATE_FIN()).toLowerCase().contains(lowerCaseFilter);
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
}
