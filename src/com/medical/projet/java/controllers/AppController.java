/*
 * 
 */
package com.medical.projet.java.controllers;


import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.medical.projet.java.utility.AppSettings;
import com.medical.projet.java.utility.LoggerUtil;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

// TODO: Auto-generated Javadoc
/**
 * The Class AppController.
 */
public class AppController {

    /** The body. */
    @FXML private BorderPane appBody;

    /** The menu pane. */
    @FXML private BorderPane menuPane;

    /** The content. */
    @FXML private StackPane content;

    /** The buttons menu. */
    static List<Button> buttonsMenu = new ArrayList<>();

    /** The fxml cache. */
    private static Map<String, CachedFXML> fxmlCache = new HashMap<>();

    /**
     * Initialize.
     */
    @FXML
    public void initialize() {
        LoggerUtil.getLogger().info("Initialize method called");

        setMenu();

        /* to stop having a button focused while content is not connected to it */
        Platform.runLater(() -> {
            appBody.requestFocus();
            loadingIcon();
        });

        LoggerUtil.getLogger().info("Initialize done");
    }


    /**
     * Sets the menu.
     */
    private void setMenu() {
        setMenuContentButtons();
        setMenuSettingsButton();
    }

    /**
     * Sets the menu content buttons.
     */
    private void setMenuContentButtons() {

        File directory = new File(AppSettings.INSTANCE.appFullPath+"content");


        File[] files = directory.listFiles((dir, name) -> name.endsWith(".fxml"));

        if (files != null) {

            for (File file : files) {
                Button button = new Button(file.getName().substring(0, file.getName().lastIndexOf(".")));

                button.setOnAction(event -> {
                    loadContent(file.getName());
                    updateActiveState(buttonsMenu, button);
                });

                buttonsMenu.add(button);
            }
            VBox menuButtons = new VBox();
            menuButtons.setId("menuButtons");
            menuButtons.getChildren().addAll(buttonsMenu);
            menuPane.setCenter(menuButtons);


        }else {
            System.out.println("File isn't working, so probably within a jar : ");

            // change this to app/ for install jpackage IMPORTANT
            //try (JarFile jarFile = new JarFile("app/medicalproj.jar")) {
            try (JarFile jarFile = new JarFile("medicalproj.jar")) {

                Enumeration<JarEntry> entries = jarFile.entries();

                long startTime = System.currentTimeMillis();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String path = entry.getName();

                    if (path.startsWith(AppSettings.INSTANCE.contentPath) && path.endsWith(".fxml")) {

                        String fileName = path.substring((AppSettings.INSTANCE.contentPath).length(), path.length() - (".fxml").length());
                        System.out.println(fileName);
                        Button button = new Button(fileName);

                        button.setOnAction(event -> {
                            loadContent(fileName + ".fxml");
                            updateActiveState(buttonsMenu, button);
                        });

                        buttonsMenu.add(button);

                    }
                }
                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;

                // print the total time to the console
                System.out.println("Total time: " + totalTime + " milliseconds");

                VBox menuButtons = new VBox();
                menuButtons.setId("menuButtons");
                menuButtons.getChildren().addAll(buttonsMenu);
                menuPane.setCenter(menuButtons);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * Update active state.
     *
     * @param buttons the buttons
     * @param activeButton the active button
     */
    private void updateActiveState(List<Button> buttons, Button activeButton) {
        buttons.forEach(button -> setActiveButtonStyle(button, button == activeButton));
    }

    /**
     * Sets the active button style.
     *
     * @param button the button
     * @param isActive the is active
     */
    private void setActiveButtonStyle(Button button, boolean isActive) {
        if (isActive) {
            button.setId("activeMenuButton");
        } else {
            button.setId(null); // Remove the ID
        }
    }

    /**
     * Sets the menu settings button.
     */
    private void setMenuSettingsButton() {
        Button settingsButton = new Button();
        settingsButton.setText("Settings");
        settingsButton.setId("settingsButton");
        settingsButton.setOnAction(event -> {
            loadContent("Settings.fxml");
            updateActiveState(buttonsMenu, settingsButton);
        });
        menuPane.setBottom(settingsButton);
        buttonsMenu.add(settingsButton);

    }

    /**
     * Loading icon.
     */
    private void loadingIcon() {
        // Load the loading GIF
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(AppSettings.INSTANCE.pathLocation+"ressources/images/loader.gif");
        Image svgLoader = new Image(inputStream);
        ImageView loadingImageView = new ImageView(svgLoader);
        loadingImageView.setFitHeight(80);
        loadingImageView.setPreserveRatio(true);

        //BorderPane loadingPane = new BorderPane();
        VBox loadingPane = new VBox();
        //loadingPane.setCenter(loadingImageView);
        loadingPane.getChildren().add(loadingImageView);
        loadingPane.setId("content");

        loadingPane.setAlignment(Pos.CENTER);
        appBody.setCenter(loadingPane);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int seconds = 7;
            @Override
            public void run() {
                Platform.runLater(() -> {

                    seconds--;

                    if(seconds == 2) {
                        fadeAnimation(loadingPane);

                    }else if(seconds == 0) {
                        Label hello = new Label("<< App is ready, click a page to start");
                        loadingPane.getChildren().add(hello);
                    }
                });
            }
        }, 0, 1000);
    }

    /**
     * Fade animation.
     *
     * @param thisNode the this node
     */
    private void fadeAnimation(VBox thisNode) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), thisNode.getChildren().get(0));
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> thisNode.getChildren().clear());
        fadeOut.play();
    }

    /**
     * Load content.
     *
     * @param fxmlName the fxml name
     */
    public void loadContent(String fxmlName) {
        try {
            Label loading = new Label("loading...");
            appBody.setCenter(loading);

            // Check if the FXML content is already cached
            if (fxmlCache.containsKey(fxmlName)) {
                CachedFXML cachedFXML = fxmlCache.get(fxmlName);
                appBody.setCenter(cachedFXML.getContent());
            } else {
                URL pathUrl;
                if(!fxmlName.equals("Settings.fxml")) {
                    pathUrl = new URL(AppSettings.INSTANCE.appUrlPath +"content/" + fxmlName);
                }else {
                    pathUrl = new URL(AppSettings.INSTANCE.appUrlPath + fxmlName);
                }

                FXMLLoader loader = new FXMLLoader(pathUrl);
                Node content = loader.load();
                Object controller = loader.getController();

                // Cache the FXML content and its controller
                fxmlCache.put(fxmlName, new CachedFXML(content, controller));

                content.setId("content");
                appBody.setCenter(content);
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
     }



    /**
     * The Class CachedFXML.
     */
    public class CachedFXML {

        /** The content. */
        private final Node content;

        /** The controller. */
        private final Object controller;

        /**
         * Instantiates a new cached FXML.
         *
         * @param content the content
         * @param controller the controller
         */
        public CachedFXML(Node content, Object controller) {
            this.content = content;
            this.controller = controller;
        }

        /**
         * Gets the content.
         *
         * @return the content
         */
        public Node getContent() {
            return content;
        }

        /**
         * Gets the controller.
         *
         * @return the controller
         */
        public Object getController() {
            return controller;
        }
    }
}
