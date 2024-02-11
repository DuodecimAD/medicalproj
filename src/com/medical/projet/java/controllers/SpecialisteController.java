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

import com.medical.projet.java.models.Specialiste;
import com.medical.projet.java.utility.AppSecurity;
import com.medical.projet.java.utility.AppSettings;
import com.medical.projet.java.utility.database.DbRead;

import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


// TODO: Auto-generated Javadoc
/**
 * The Class SpecialisteController.
 */
public class SpecialisteController {

    /** The specialistes obs list. */
    private static ObservableList<Specialiste> specialistesObsList = FXCollections.observableArrayList();
    
    /** The competences list. */
    private ObservableList<List<Object>> competencesList = FXCollections.observableArrayList();
    
    /** The checked boxes. */
    private ObservableList<List<Object>> checkedBoxes = FXCollections.observableArrayList();

    /** The Constant tableNameSuffix. */
    private static final String tableNameSuffix = "_SPECIALISTE";

    /** The body. **/

    @FXML
    private StackPane body;

    /** The table. */
    @FXML
    private TableView<Specialiste> table;

    /** The name. */
    @FXML
    private TableColumn<Specialiste, String> name;

    /** The firstname. */
    @FXML
    private TableColumn<Specialiste, String> firstname;

    /** The date nais. */
    @FXML
    private TableColumn<Specialiste, String> dateNais;

    /** The tel. */
    @FXML
    private TableColumn<Specialiste, String> tel;

    /** The email. */
    @FXML
    private TableColumn<Specialiste, String> email;

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
                //readAllSpecialistes();
                updateTableView();
                searchTable();
            });
        }).start();

        // When data's row is clicked, open overlay with data from that row
        openOverlayPopulateData();

        // Create a new Specialiste
        openOverlayNewSpecialiste();
        
    }

    /**
     * Dynamic css stuff.
     */
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

    /**
     * Loading table icon.
     */
    private void loadingTableIcon() {
        // Load the loading GIF
        Image loadingImage = new Image(getClass().getResourceAsStream(AppSettings.INSTANCE.imagesPath+"loading.gif"));
        ImageView loadingImageView = new ImageView(loadingImage);

        // Set the loading GIF as the custom placeholder
        table.setPlaceholder(loadingImageView);
    }

    /**
     * Read all specialistes.
     *
     * @return the observable list
     */
    public static ObservableList<Specialiste> readAllSpecialistes() {

        // Get raw data from the Specialiste model
        List<List<Object>> rawSpecialisteData = null;
        Label placeholderLabel = new Label(); // Create label outside of the timer

        try {
            rawSpecialisteData = Specialiste.getAllSpecialistesData();
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
                            readAllSpecialistes(); // Optionally, trigger another attempt here
                        }
                    });
                }
            }, 0, 1000);
        }


        if (rawSpecialisteData != null) {
                        
            for (List<Object> row : rawSpecialisteData) {
                BigDecimal idDB = (BigDecimal) row.get(0);
                int id = idDB.intValue();

                // Check if the specialiste with the same id already exists in clientsObsList
                boolean specialisteExists = specialistesObsList.stream()
                        .anyMatch(specialiste -> specialiste.getSpecialisteId() == id);
                
                BigDecimal competencesSpecialisteDB = (BigDecimal) row.get(6);
                int competencesSpecialiste = competencesSpecialisteDB.intValue();
                List<Integer> competencesSpecialisteList = new ArrayList<Integer>();
                competencesSpecialisteList.add(competencesSpecialiste);


                if (!specialisteExists) {
                    String nom = (String) row.get(1);
                    String prenom = (String) row.get(2);
                    // Convert date to java.time.LocalDate
                    java.sql.Timestamp timestamp = (java.sql.Timestamp) row.get(3);
                    LocalDate date_nais = timestamp.toLocalDateTime().toLocalDate();
                    String tel = (String) row.get(4);
                    String email = (String) row.get(5);
                    
                    // Create a Specialiste object and add to the list
                    specialistesObsList.add(new Specialiste(id, nom, prenom, date_nais, tel, email, competencesSpecialisteList));
                }else {
                    // Target the last element
                    Specialiste lastElement = specialistesObsList.get(specialistesObsList.size() - 1);
                    
                    lastElement.addToCompetencesSpecialiste(competencesSpecialiste);
                }
            }
        }
        //System.out.println(currentLine() + specialistesObsList);
        return specialistesObsList;
    }


    /**
     * Update table view.
     */
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

    /**
     * Open overlay populate data.
     */
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

    /**
     * Open overlay new specialiste.
     */
    private void openOverlayNewSpecialiste() {
        createButton.setOnMouseClicked(event -> {
            openOverlayForNewSpecialiste();
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

    /**
     * Open overlay with specialiste data.
     *
     * @param specialiste the specialiste
     */
    // overlay with specialiste data
    private void openOverlayWithSpecialisteData(Specialiste specialiste) {
        createOverlay(body, contentPane -> populateOverlayContent(contentPane, specialiste));
    }

    /**
     * Open overlay for new specialiste.
     */
    // overlay for a new specialiste
    private void openOverlayForNewSpecialiste() {
        createOverlay(body, contentPane -> populateOverlayForNewSpecialiste(contentPane));
    }

    /**
     * Populate overlay content.
     *
     * @param contentPane the content pane
     * @param specialiste the specialiste
     */
    //  when clicking on a row in Tableview, populate the data of that row in the overlay
    private void populateOverlayContent(BorderPane contentPane, Specialiste specialiste) {

        //System.out.println(currentLine() + specialiste.toString());

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
        

        Label competenceLabel = new Label("");
        //competenceLabel.setStyle("-fx-text-fill: rgb(221,230,237)");
        TableView<List<Object>> competencesTable = new TableView<>();
        competencesTable.setPrefWidth(200);
        //competencesTable.setMaxWidth(100);
        competencesTable.setId("competencesTable");
        Label competenceTableError = new Label("No competences found, error");
        competencesTable.setPlaceholder(competenceTableError);
        competencesTable.getItems().clear();

        
        // Ensure that competencesList is populated before using it
        if (competencesList == null || competencesList.isEmpty()) {
            competencesList = getCompetenceList();
        }

        competencesTable.setItems(competencesList);
        

        TableColumn<List<Object>, Boolean> checkBoxColumn = new TableColumn<>("");

   
        //checkBoxColumn.setCellFactory(CheckBoxTableCell.forTableColumn(checkBoxColumn));

        checkedBoxes = getCheckedBoxes(specialiste);
        
        checkBoxColumn.setCellValueFactory(param -> {
            
            for (int i = 0; i < specialiste.getCompetencesSpecialiste().size(); i++) {
                
                if(param.getValue().get(0).equals(specialiste.getCompetencesSpecialiste().get(i))) {
                    return new SimpleBooleanProperty(true);
                }
            }
            return new SimpleBooleanProperty(false);

        });
        
        
        SimpleBooleanProperty checkBoxValue = new SimpleBooleanProperty();
        
        checkBoxColumn.setCellFactory(column -> {
            CheckBoxTableCell<List<Object>, Boolean> cell = new CheckBoxTableCell<>();
            
            // Add a listener to the graphic property of the cell
            cell.graphicProperty().addListener((obs, oldGraphic, newGraphic) -> {
                if (newGraphic instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) newGraphic;
                    // Add the onMouseClicked event handler to the checkbox
                    checkBox.setOnMouseClicked(event -> {
                        // Handle the mouse click event on the CheckBox here
                        int index = cell.getIndex();
                        Boolean isChecked = checkBox.isSelected();
                        
                        // Update the BooleanProperty when the checkbox is clicked
                        checkBoxValue.set(checkBox.isSelected());
                        //int compIndex = (int) competencesList.get(index).get(0);
                        checkedBoxes.get(index).set(1, isChecked);
                    });
                }
            });
            
            cell.setSelectedStateCallback(index -> {
                // Retrieve the state of the checkbox from the checkedBoxes list
                boolean checked = (boolean) checkedBoxes.get(index).get(1);
                return new SimpleBooleanProperty(checked);
            });
            
            // Add a listener to the CheckBox to update the checkedBoxes list when clicked
            cell.selectedProperty().addListener((obs, oldValue, newValue) -> {
                int index = cell.getIndex();
                if (index >= 0 && index < checkedBoxes.size()) {
                    checkedBoxes.get(index).set(1, newValue);
                }
            });
            
            return cell;
        });



        
        // keep this code to re-use, can check the node clicked and chilren
        /*
        competencesTable.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                
                Node source = event.getPickResult().getIntersectedNode();
                if(source instanceof CheckBoxTableCell) {
                    Parent parent = (Parent) source;
                    
                    for (Node child : parent.getChildrenUnmodifiable()) {
                        System.out.println(currentLine() + child);
                        
                        // Recursively print children of children
                        //printAllChildren(child);
                    }
                }
            }
        });
        */
        
        
        TableColumn<List<Object>, String> competenceNameColumn = new TableColumn<>("Competences");
        competenceNameColumn.setCellValueFactory(param -> new SimpleStringProperty((String) param.getValue().get(1)));
        
        // Assuming you have TableColumn instances named column1, column2, etc.
        checkBoxColumn.setSortable(false);
        competenceNameColumn.setSortable(false);

        competencesTable.getColumns().addAll(checkBoxColumn, competenceNameColumn);
        
        VBox competencesCheckboxes = new VBox();
        competencesCheckboxes.getChildren().addAll(competenceLabel, competencesTable);    
        
        DoubleBinding tableWidth = competencesTable.widthProperty().subtract(20);
        checkBoxColumn.prefWidthProperty().bind(tableWidth.multiply(0.15));
        competenceNameColumn.prefWidthProperty().bind(tableWidth.multiply(0.85));
        
        competencesTable.setEditable(true);
        //System.out.println(currentLine() + competencesList);

        contentPane.setTop(overlayTopDelete);
        contentPane.setCenter(overLayContent);
        contentPane.setBottom(overlayBottomButtons);
        contentPane.setRight(competencesCheckboxes);

        overlayTopDelete.setStyle("-fx-padding: 20 0 0 540");
        overlayBottomButtons.setStyle("-fx-padding: 20 0 20 440");
        overLayContent.setStyle("-fx-padding: 0 20 0 0");
        
        // buttons event logic
        buttonCancel.setOnAction(e -> {
            //System.out.println(currentLine() + specialiste.getCompetencesSpecialiste());
            closeOverlay();
        });

        buttonDelete.setOnAction(e -> {
            deleteSpecialiste(specialiste);
            getSpecialistesObsList().remove(specialiste);
            closeOverlay();
        });

        buttonOk.setOnAction(e -> {
            
            
            List<Integer> checkItems = new ArrayList<>();
                    
            for (int i = 0; i < checkedBoxes.size(); i++) {
                if((boolean) checkedBoxes.get(i).get(1) == true) {
                    checkItems.add((int) checkedBoxes.get(i).get(0));
                }
                
            }
            
            //System.out.println(currentLine() + competencesList);
            //System.out.println(currentLine() + checkedBoxes);
            
            
            updateSpecialiste(specialiste, nameLabel.getId(),         specialiste.getNomSpecialiste(),      nameField.getText(),        emailLabel.getId(),     specialiste.getEmailSpecialiste() );
            updateSpecialiste(specialiste, firstnameLabel.getId(),    specialiste.getPrenomSpecialiste(),   firstnameField.getText(),   emailLabel.getId(),     specialiste.getEmailSpecialiste() );
            updateSpecialiste(specialiste, date_naisLabel.getId(),    specialiste.getDateNaisSpecialiste(), date_naisField.getValue(),  emailLabel.getId(),     specialiste.getEmailSpecialiste() );
            updateSpecialiste(specialiste, telLabel.getId(),          specialiste.getTelSpecialiste(),      telField.getText(),         emailLabel.getId(),     specialiste.getEmailSpecialiste() );
            updateSpecialiste(specialiste, emailLabel.getId(),        specialiste.getEmailSpecialiste(),    emailField.getText(),       emailLabel.getId(),     specialiste.getEmailSpecialiste() );

            updateCompetences(specialiste, competencesTable.getId(),  specialiste.getCompetencesSpecialiste(),    checkItems);
            closeOverlay();
        });
        
        
    }
    

    /**
     * Delete specialiste.
     *
     * @param specialiste the specialiste
     */
    private void deleteSpecialiste(Specialiste specialiste) {
        specialiste.deleteSpecialisteDB(specialiste.getTelSpecialiste());
    }
    
    
    /**
     * Update competences.
     *
     * @param specialiste the specialiste
     * @param fieldName the field name
     * @param oldValue the old value
     * @param newValue the new value
     */
    public void updateCompetences(Specialiste specialiste, String fieldName, List<Integer> oldValue, List<Integer> newValue) {
        

        List<Integer> commonElements = new ArrayList<>(oldValue);
        commonElements.retainAll(newValue); // Retain only the common elements

        List<Integer> toDeleteDB = new ArrayList<>(oldValue);
        toDeleteDB.removeAll(commonElements); // Remove common elements from list1

        List<Integer> toAddDB = new ArrayList<>(newValue);
        toAddDB.removeAll(commonElements); 
        /*
        System.out.println(currentLine() + " oldValue : " + oldValue);
        System.out.println(currentLine() + " newValue : " + newValue);
        System.out.println(currentLine() + " difference1 : " + toDeleteDB);
        System.out.println(currentLine() + " difference1 : " + toAddDB);
        */
        
        // db logic
        try {
            
            if(!toAddDB.isEmpty()) {
                specialiste.updateCompetencesSpecialisteDB("INSERT", specialiste.getSpecialisteId(), toAddDB);
            }
            
            if(!toDeleteDB.isEmpty()) {
                specialiste.updateCompetencesSpecialisteDB("DELETE", specialiste.getSpecialisteId(), toDeleteDB);
            }
            
            // update specialiste obs
            for (Integer element : toDeleteDB) {
                specialiste.getCompetencesSpecialiste().remove(element);
            }
            specialiste.getCompetencesSpecialiste().addAll(toAddDB);
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
    

    /**
     * Update specialiste.
     *
     * @param specialiste the specialiste
     * @param fieldName the field name
     * @param oldValue the old value
     * @param newValue the new value
     * @param checkColumn the check column
     * @param checkValue the check value
     */
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
                        System.out.println(currentLine() + "name has been changed");
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
                    System.out.println(currentLine() + "firstname has been changed");
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
                    System.out.println(currentLine() + "date_nais has been changed");
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
                    System.out.println(currentLine() + "tel has been changed");
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
                    System.out.println(currentLine() + "email has been changed");
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
     * Populate overlay for new specialiste.
     *
     * @param contentPane the content pane
     */
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
        
        
        Label competenceLabel = new Label("");
        //competenceLabel.setStyle("-fx-text-fill: rgb(221,230,237)");
        TableView<List<Object>> competencesTable = new TableView<>();
        competencesTable.setPrefWidth(200);
        //competencesTable.setMaxWidth(100);
        competencesTable.setId("competencesTable");
        Label competenceTableError = new Label("No competences found, error");
        competencesTable.setPlaceholder(competenceTableError);
        competencesTable.getItems().clear();
        
        
        // Ensure that competencesList is populated before using it
        if (competencesList == null || competencesList.isEmpty()) {
            competencesList = getCompetenceList();
        }

        competencesTable.setItems(competencesList);
        
        TableColumn<List<Object>, Boolean> checkBoxColumn = new TableColumn<>("");
        
        //System.out.println(currentLine() + competencesList);
        
        getCheckedBoxes();
        
        SimpleBooleanProperty checkBoxValue = new SimpleBooleanProperty();
        
        checkBoxColumn.setCellFactory(column -> {
            CheckBoxTableCell<List<Object>, Boolean> cell = new CheckBoxTableCell<>();
            
            // Add a listener to the graphic property of the cell
            cell.graphicProperty().addListener((obs, oldGraphic, newGraphic) -> {

                if (newGraphic instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) newGraphic;
                    // Add the onMouseClicked event handler to the checkbox
                    checkBox.setOnMouseClicked(event -> {
                        // Handle the mouse click event on the CheckBox here
                        int index = cell.getIndex();
                        Boolean isChecked = checkBox.isSelected();
                        
                        // Update the BooleanProperty when the checkbox is clicked
                        checkBoxValue.set(checkBox.isSelected());
                        //int compIndex = (int) competencesList.get(index).get(0);
                        checkedBoxes.get(index).set(1, isChecked);
                        
                    });
                }
            });
            
            cell.setSelectedStateCallback(index -> {
                // Retrieve the state of the checkbox from the checkedBoxes list
                boolean checked = (boolean) checkedBoxes.get(index).get(1);
                return new SimpleBooleanProperty(checked);
            });
            
            // Add a listener to the CheckBox to update the checkedBoxes list when clicked
            cell.selectedProperty().addListener((obs, oldValue, newValue) -> {
                int index = cell.getIndex();
                if (index >= 0 && index < checkedBoxes.size()) {
                    checkedBoxes.get(index).set(1, newValue);
                }
            });
            
            return cell;
        });
        
        checkBoxColumn.setCellValueFactory(param -> new SimpleBooleanProperty(false));
        
        
        TableColumn<List<Object>, String> competenceNameColumn = new TableColumn<>("Competences");
        competenceNameColumn.setCellValueFactory(param -> new SimpleStringProperty((String) param.getValue().get(1)));
        
        // Assuming you have TableColumn instances named column1, column2, etc.
        checkBoxColumn.setSortable(false);
        competenceNameColumn.setSortable(false);

        competencesTable.getColumns().addAll(checkBoxColumn, competenceNameColumn);
        
        VBox competencesCheckboxes = new VBox();
        competencesCheckboxes.getChildren().addAll(competenceLabel, competencesTable);    
        
        DoubleBinding tableWidth = competencesTable.widthProperty().subtract(20);
        checkBoxColumn.prefWidthProperty().bind(tableWidth.multiply(0.15));
        competenceNameColumn.prefWidthProperty().bind(tableWidth.multiply(0.85));
        
        competencesTable.setEditable(true);

        
        Button buttonOk = new Button("ok");
        Button buttonCancel = new Button("Cancel");

        HBox overlayBottomButtons = new HBox();
        overlayBottomButtons.setId("overlayBottomButtons");
        overlayBottomButtons.getChildren().addAll(buttonOk, buttonCancel);

        contentPane.setCenter(overLayContent);
        contentPane.setBottom(overlayBottomButtons);
        contentPane.setRight(competencesCheckboxes);

        overlayBottomButtons.setStyle("-fx-padding: 20 0 20 440");
        overLayContent.setStyle("-fx-padding: 50 20 0 0");
        

        // buttons event logic
        buttonCancel.setOnAction(e -> {
            //System.out.println(currentLine() + checkedBoxes);
            closeOverlay();
        });

        buttonOk.setOnAction(e -> {
            String newSpecialisteOK = createNewSpecialiste(nameField.getText(), firstnameField.getText(), date_naisField.getValue(), telField.getText(), emailField.getText());
            
            
            if(newSpecialisteOK.equals("")) {
                
                // Get the specialiste just created
                int lastIndex = specialistesObsList.size() - 1;
                Specialiste specialiste = specialistesObsList.get(lastIndex);
                //System.out.println(currentLine() + " specialiste ID : " + specialiste.getSpecialisteId());
                
                List<Integer> checkedItems = new ArrayList<>();
                for (int i = 0; i < checkedBoxes.size(); i++) {
                    if((boolean) checkedBoxes.get(i).get(1) == true) {
                        checkedItems.add((int) checkedBoxes.get(i).get(0));
                    }
                    
                }
                insertCompetences(specialiste, checkedItems);
                
                closeOverlay();
            }else {
                errorLabel.setText(newSpecialisteOK);
            }
        });
    }
    
    /**
     * Insert competences.
     *
     * @param specialiste the specialiste
     * @param values the values
     */
    private void insertCompetences(Specialiste specialiste, List<Integer> values) {
        
        //System.out.println(currentLine() + " values : " + values);

        // db logic
        try {

            specialiste.updateCompetencesSpecialisteDB("INSERT", specialiste.getSpecialisteId(), values);
            
            //System.out.println(currentLine() + "before adding competences : " + specialiste.getSpecialisteId() + specialiste.getCompetencesSpecialiste());
            specialiste.getCompetencesSpecialiste().addAll(values);
            //System.out.println(currentLine() + "after adding competences : " + specialiste.getSpecialisteId() + specialiste.getCompetencesSpecialiste());
            
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Creates the new specialiste.
     *
     * @param nameField the name field
     * @param firsnameField the firsname field
     * @param date_naisField the date nais field
     * @param telField the tel field
     * @param emailField the email field
     * @return the string
     */
    private String createNewSpecialiste(String nameField, String firsnameField, LocalDate date_naisField, String telField, String emailField) {
        
        List<Integer> competencesSpecialisteToFix = new ArrayList<>();

        Specialiste newSpecialiste = new Specialiste(nameField, firsnameField, date_naisField, telField, emailField, competencesSpecialisteToFix);

        try {
            newSpecialiste.insertSpecialisteDB(newSpecialiste);
            System.out.println(currentLine() + "insert done");
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
                    System.out.println(currentLine() + cleanErrorMessage);
                    return cleanErrorMessage;
                } else {
                    cleanErrorMessage = errorMessage.substring(startIndex + "ORA-20001: ".length()).trim();
                    System.out.println(currentLine() + cleanErrorMessage);
                    return cleanErrorMessage;
                }
            } else {
                System.out.println(currentLine() + errorMessage);
                return errorMessage;
            }

        }
        return "";
    }

    /**
     * Gets the specialistes obs list.
     *
     * @return the specialistes obs list
     */
    public static ObservableList<Specialiste> getSpecialistesObsList() {
        return specialistesObsList;
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
    
    /**
     * Gets the competence list.
     *
     * @return the competence list
     */
    private ObservableList<List<Object>> getCompetenceList() {
        
        List<List<Object>> competenceListDB;
        
        competenceListDB = DbRead.readTable("COMPETENCE", "NOM_COMPETENCE");
        
        if(competenceListDB != null) {
            for (List<Object> row : competenceListDB) {
            
                BigDecimal idCompetenceDB = (BigDecimal) row.get(0);
                int idCompetence = idCompetenceDB.intValue();
                
                String nomCompetence = (String) row.get(1);
                

                List<Object> rowData = new ArrayList<>();
                rowData.add(idCompetence);
                rowData.add(nomCompetence);

                competencesList.add(rowData);
            }

        }
        return competencesList;
    }
    
    /**
     * Gets the checked boxes.
     *
     * @param specialiste the specialiste
     * @return the checked boxes
     */
    private ObservableList<List<Object>> getCheckedBoxes(Specialiste specialiste) {
        
        checkedBoxes.clear();
     
        for (int i = 0; i < competencesList.size(); i++) {
            
            List<Object> rowData = new ArrayList<>();
            
            int competenceID = (int) competencesList.get(i).get(0);

            boolean checked = false;
            
            for (int j = 0; j < specialiste.getCompetencesSpecialiste().size(); j++) {
                
                int specialisteComps = specialiste.getCompetencesSpecialiste().get(j);

                if(competenceID == specialisteComps) {

                    checked = true;
                    break;
                }
            }
            rowData.add(competenceID);
            rowData.add(checked); 
            checkedBoxes.add(rowData);
        }

        return checkedBoxes;
    }
    
    /**
     * Gets the checked boxes.
     *
     * @return the checked boxes
     */
    private ObservableList<List<Object>> getCheckedBoxes() {
        
        checkedBoxes.clear();
     
        for (int i = 0; i < competencesList.size(); i++) {
            
            List<Object> rowData = new ArrayList<>();
            
            int competenceID = (int) competencesList.get(i).get(0);

            rowData.add(competenceID);
            rowData.add(false); 
            checkedBoxes.add(rowData);
        }

        return checkedBoxes;
    }
    
    /**
     * Current line.
     *
     * @return the string
     */
    private String currentLine() {
        return "Class SpecialisteController @ line " + Thread.currentThread().getStackTrace()[2].getLineNumber() + " -> ";
    }
}
