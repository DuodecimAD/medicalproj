package com.medical.projet.java.controllers;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

import com.medical.projet.java.models.Specialiste;
import com.medical.projet.java.utility.AppSecurity;
import com.medical.projet.java.utility.AppSettings;

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


public class SpecialisteController {

    private static ObservableList<Specialiste> specialistesObsList = FXCollections.observableArrayList();

    private static final String tableNameSuffix = "_SPECIALISTE";

    /** The body. **/

    @FXML
    private StackPane body;

    @FXML
    private TableView<Specialiste> table;

    @FXML
    private TableColumn<Specialiste, String> name;

    @FXML
    private TableColumn<Specialiste, String> firstname;

    @FXML
    private TableColumn<Specialiste, String> dateNais;

    @FXML
    private TableColumn<Specialiste, String> tel;

    @FXML
    private TableColumn<Specialiste, String> email;

    @FXML
    private Button createButton;

    @FXML
    private TextField searchField;


    public void initialize() {

        dynamicCssStuff();

        loadingTableIcon();

        new Thread(() -> {
            Platform.runLater(() -> {
                readAllSpecialistes();
                updateTableView();
                searchTable();
            });
        }).start();

        // When data's row is clicked, open overlay with data from that row
        openOverlayPopulateData();

        // Create a new Specialiste
        openOverlayNewSpecialiste();

    }

    private void dynamicCssStuff() {

        // absolute position of the create button on the right side
        createButton.layoutXProperty().bind(body.widthProperty().subtract(createButton.widthProperty()));

        // auto size of the TableView columns depending of the table - scrollbar
        DoubleBinding tableWidth = table.widthProperty().subtract(22);
        name.prefWidthProperty().bind(tableWidth.multiply(0.17));
        firstname.prefWidthProperty().bind(tableWidth.multiply(0.17));
        dateNais.prefWidthProperty().bind(tableWidth.multiply(0.155));
        tel.prefWidthProperty().bind(tableWidth.multiply(0.155));
        email.prefWidthProperty().bind(tableWidth.multiply(0.35));
    }

    private void loadingTableIcon() {
        // Load the loading GIF
        Image loadingImage = new Image(getClass().getResourceAsStream(AppSettings.INSTANCE.imagesPath+"loading.gif"));
        ImageView loadingImageView = new ImageView(loadingImage);

        // Set the loading GIF as the custom placeholder
        table.setPlaceholder(loadingImageView);
    }

    public static ObservableList<Specialiste> readAllSpecialistes() {

        // Get raw data from the Specialiste model
        List<List<Object>> rawSpecialisteData = null;
        Label placeholderLabel = new Label(); // Create label outside of the timer

        try {
            rawSpecialisteData = Specialiste.getAllSpecialistesData();
        } catch (Exception e) {
            final int[] seconds = {30}; // Initial countdown value

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
                            readAllSpecialistes(); // Optionally, trigger another attempt here
                        }
                    });
                }
            }, 0, 1000);
        }


        if (rawSpecialisteData != null) {
            for (List<Object> row : rawSpecialisteData) {
                BigDecimal id = (BigDecimal) row.get(0);

                // Check if the client with the same id already exists in clientsObsList
                boolean specialisteExists = specialistesObsList.stream()
                        .anyMatch(specialiste -> specialiste.getSpecialisteId() == id.intValue());

                if (!specialisteExists) {
                    String nom = (String) row.get(1);
                    String prenom = (String) row.get(2);
                    // Convert date to java.time.LocalDate
                    java.sql.Timestamp timestamp = (java.sql.Timestamp) row.get(3);
                    LocalDate date_nais = timestamp.toLocalDateTime().toLocalDate();
                    String tel = (String) row.get(4);
                    String email = (String) row.get(5);
                    // Create a Specialiste object and add to the list
                    specialistesObsList.add(new Specialiste(id.intValue(), nom, prenom, date_nais, tel, email));
                }
            }
        }
        return specialistesObsList;
    }


    private void updateTableView() {

        // Set the items with the correct data type
        table.setItems(specialistesObsList);

        // Populate columns of TableView with the data
        name.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getNomSpecialiste()));
        firstname.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getPrenomSpecialiste()));
        dateNais.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDateNaisSpecialiste().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        tel.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTelSpecialiste()));
        email.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getEmailSpecialiste()));

    }

    private void openOverlayPopulateData() {
        table.setRowFactory(tv -> {
            TableRow<Specialiste> row = new TableRow<>();
            row.setOnMousePressed(event -> {
                if (event.getClickCount() == 1 && !row.isEmpty()) {
                    Specialiste rowData = row.getItem();
                    openOverlayWithSpecialisteData(rowData);
                }
            });
            return row;
        });
    }

    private void openOverlayNewSpecialiste() {
        createButton.setOnMouseClicked(event -> {
            openOverlayForNewSpecialiste();
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
        contentPane.setMaxSize(500, 500);

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
        //contentPane.setLayoutX((stackPane.getWidth() - contentPane.getPrefWidth()) / 2);
        //contentPane.setLayoutY(((stackPane.getHeight() - contentPane.getPrefHeight()) / 2));
        overlayPane.setAlignment(Pos.CENTER);

    }

    // overlay with specialiste data
    private void openOverlayWithSpecialisteData(Specialiste specialiste) {
        createOverlay(body, contentPane -> populateOverlayContent(contentPane, specialiste));
    }

    // overlay for a new specialiste
    private void openOverlayForNewSpecialiste() {
        createOverlay(body, contentPane -> populateOverlayForNewSpecialiste(contentPane));
    }

    //  when clicking on a row in Tableview, populate the data of that row in the overlay
    private void populateOverlayContent(BorderPane contentPane, Specialiste specialiste) {

        //System.out.println(specialiste.toString());

        Label nameLabel = new Label("Name");
        nameLabel.setId("NOM" + tableNameSuffix);
        TextField nameField = new TextField();
        nameField.setText(specialiste.getNomSpecialiste());

        Label firstnameLabel = new Label("Firsname");
        firstnameLabel.setId("PRENOM" + tableNameSuffix);
        TextField firstnameField = new TextField();
        firstnameField.setText(specialiste.getPrenomSpecialiste());

        Label date_naisLabel = new Label("DateNais");
        date_naisLabel.setId("DATE_NAIS" + tableNameSuffix);
        DatePicker date_naisField = new DatePicker();
        date_naisField.setValue(specialiste.getDateNaisSpecialiste());

        Label telLabel = new Label("Telephone");
        telLabel.setId("TEL" + tableNameSuffix);
        TextField telField = new TextField();
        telField.setText(specialiste.getTelSpecialiste());

        Label emailLabel = new Label("Email");
        emailLabel.setId("EMAIL" + tableNameSuffix);
        TextField emailField = new TextField();
        emailField.setText(specialiste.getEmailSpecialiste());

        VBox overLayContent = new VBox();
        overLayContent.setId("overLayContent");
        overLayContent.getChildren().addAll(nameLabel, nameField, firstnameLabel, firstnameField, date_naisLabel, date_naisField, telLabel, telField, emailLabel, emailField);

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
            deleteSpecialiste(specialiste);
            getSpecialistesObsList().remove(specialiste);
            closeOverlay();
        });

        buttonOk.setOnAction(e -> {
            updateSpecialiste(specialiste, nameLabel.getId(),         specialiste.getNomSpecialiste(),      nameField.getText(),        emailLabel.getId(),     specialiste.getEmailSpecialiste() );
            updateSpecialiste(specialiste, firstnameLabel.getId(),      specialiste.getPrenomSpecialiste(),   firstnameField.getText(),     emailLabel.getId(),     specialiste.getEmailSpecialiste() );
            updateSpecialiste(specialiste, date_naisLabel.getId(),    specialiste.getDateNaisSpecialiste(), date_naisField.getValue(),  emailLabel.getId(),     specialiste.getEmailSpecialiste() );
            updateSpecialiste(specialiste, telLabel.getId(),          specialiste.getTelSpecialiste(),      telField.getText(),         emailLabel.getId(),     specialiste.getEmailSpecialiste() );
            updateSpecialiste(specialiste, emailLabel.getId(),        specialiste.getEmailSpecialiste(),    emailField.getText(),       emailLabel.getId(),     specialiste.getEmailSpecialiste() );
            closeOverlay();
        });
    }

    private void deleteSpecialiste(Specialiste specialiste) {
        specialiste.deleteSpecialisteDB(specialiste.getTelSpecialiste());
    }

    public void updateSpecialiste(Specialiste specialiste, String fieldName, Object oldValue, Object newValue, String checkColumn, String checkValue) {

        if(!(newValue instanceof LocalDate)) {
            newValue = AppSecurity.sanitize(newValue.toString());
        }

        switch (fieldName) {
        case "NOM_SPECIALISTE" -> {
            if (compare(oldValue, newValue)) {

                try {
                    specialiste.updateSpecialisteDB(fieldName, newValue, checkColumn, checkValue);
                    specialiste.setNomSpecialiste(newValue.toString());
                    System.out.println("name has been changed");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        case "PRENOM_SPECIALISTE" -> {
            if (compare(oldValue, newValue)) {
                try {
                    specialiste.updateSpecialisteDB(fieldName, newValue, checkColumn, checkValue);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                specialiste.setPrenomSpecialiste(newValue.toString());
                System.out.println("firstname has been changed");
            }
        }
        case "DATE_NAIS_SPECIALISTE" -> {
            if (compare(oldValue, newValue)) {
                try {
                    specialiste.updateSpecialisteDB(fieldName, newValue, checkColumn, checkValue);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                LocalDate newDate = LocalDate.parse(newValue.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                specialiste.setDateNaisSpecialiste(newDate);
                System.out.println("date_nais has been changed");
            }
        }
        case "TEL_SPECIALISTE" -> {
            if (compare(oldValue, newValue)) {
                try {
                    specialiste.updateSpecialisteDB(fieldName, newValue, checkColumn, checkValue);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                specialiste.setTelSpecialiste(newValue.toString());
                System.out.println("tel has been changed");
            }
        }
        case "EMAIL_SPECIALISTE" -> {
            if (compare(oldValue, newValue)) {
                try {
                    specialiste.updateSpecialisteDB(fieldName, newValue, checkColumn, checkValue);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                specialiste.setEmailSpecialiste(newValue.toString());
                System.out.println("email has been changed");
            }
        }
        default -> {}
        }
        table.refresh();
    }

    private boolean compare(Object oldValue, Object newValue) {
        return !Objects.equals(oldValue, newValue);
    }


    // populating inputs in the overlay when clicking create new specialiste
    private void populateOverlayForNewSpecialiste(BorderPane contentPane) {

        Label nameLabel = new Label("Name");
        TextField nameField = new TextField();

        Label firstnameLabel = new Label("Firstname");
        TextField firstnameField = new TextField();

        Label date_naisLabel = new Label("Date_Nais");
        DatePicker date_naisField = new DatePicker();

        Label telLabel = new Label("Tel");
        TextField telField = new TextField();

        Label emailLabel = new Label("Email");
        TextField emailField = new TextField();

        Label errorLabel = new Label("");
        errorLabel.setId("errorLabelnew");

        VBox overLayContent = new VBox();
        overLayContent.setId("overLayContent");
        overLayContent.getChildren().addAll(nameLabel, nameField, firstnameLabel, firstnameField, date_naisLabel, date_naisField, telLabel, telField, emailLabel, emailField, errorLabel);

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
            String newSpecialisteOK = createNewSpecialiste(nameField.getText(), firstnameField.getText(), date_naisField.getValue(), telField.getText(), emailField.getText());

            if(newSpecialisteOK.equals("")) {
                closeOverlay();
            }else {
                errorLabel.setText(newSpecialisteOK);
            }
        });
    }

    private String createNewSpecialiste(String nameField, String firsnameField, LocalDate date_naisField, String telField, String emailField) {

        Specialiste newSpecialiste = new Specialiste(nameField, firsnameField, date_naisField, telField, emailField);

        try {
            newSpecialiste.insertSpecialisteDB(newSpecialiste);
            System.out.println("insert done");
            newSpecialiste.setSpecialisteIdFromDb(newSpecialiste);
            getSpecialistesObsList().add(newSpecialiste);
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

    public static ObservableList<Specialiste> getSpecialistesObsList() {
        return specialistesObsList;
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
        FilteredList<Specialiste> filteredData = new FilteredList<>(specialistesObsList, p -> true);

        // Add listener to the searchField text property
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
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
                return specialiste.getNomSpecialiste().toLowerCase().contains(lowerCaseFilter)
                        || specialiste.getPrenomSpecialiste().toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(specialiste.getDateNaisSpecialiste().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).toLowerCase().contains(lowerCaseFilter)
                        || specialiste.getTelSpecialiste().toLowerCase().contains(lowerCaseFilter)
                        || specialiste.getEmailSpecialiste().toLowerCase().contains(lowerCaseFilter);
            });

            // Wrap the FilteredList in a SortedList
            SortedList<Specialiste> sortedData = new SortedList<>(filteredData);

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
