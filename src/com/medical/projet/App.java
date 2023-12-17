package com.medical.projet;

//import com.medical.projet.java.utility.AppMemory;
//import com.medical.projet.myapp.java.utility.AppSecurity;
//import com.medical.projet.myapp.java.utility.AppTree;
import com.medical.projet.java.utility.AppSettings;
import com.medical.projet.java.utility.LoggerUtil;
import com.medical.projet.java.utility.database.DbConnect;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


/**
 * The Class App.
 */
public class App extends Application {

    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        launch(args);
        LoggerUtil.getLogger().info("Main Closing");
        DbConnect.closeConnection();
    }

    /**
     * Start.
     *
     * @param primaryStage the primary stage
     * @throws Exception the exception
     */

    @Override
    public void start(Stage primaryStage)  throws Exception {
        //LoggerUtil.setupLogging();
        LoggerUtil.getLogger().info("Start method called");

        setPrimaryStage(primaryStage);

        new Thread(() -> {
            Platform.runLater(() -> {
                //AppTree.printScene(primaryStage.getScene());
                //AppMemory.printMemoryUsage();
                DbConnect.sharedConnection();
            });
        }).start();

        LoggerUtil.getLogger().info("start method finished");

    }

    /**
     * Sets the primary stage.
     *
     * @param primaryStage the new primary stage
     */
    private void setPrimaryStage(Stage primaryStage) {

        try {
            URL pathUrl = new URL(AppSettings.INSTANCE.appMainPath + ".fxml");
            FXMLLoader loader = new FXMLLoader(pathUrl);
            LoggerUtil.getLogger().info("initialize will be called now");
            Parent root = loader.load();
            LoggerUtil.getLogger().info("fxml loaded");
            root.getStylesheets().add(AppSettings.INSTANCE.cssPath+"styles.css");
            LoggerUtil.getLogger().info("css loaded");
            // icon root access by Alexiuz As on IconScout
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream(AppSettings.INSTANCE.imagesPath+"icon.png")));
            primaryStage.setScene(new Scene(root));
            primaryStage.setTitle("My JavaFX Template");
            primaryStage.setMinWidth(1026);
            primaryStage.setMinHeight(649);
            //primaryStage.initStyle(StageStyle.DECORATED);
            //primaryStage.setResizable(false); // Prevent window resizing
            //primaryStage.setMaximized(true); // Allow maximizing
            primaryStage.show();
            LoggerUtil.getLogger().info("primaryStage loaded");

        } catch (IOException e) {
            e.printStackTrace();
            LoggerUtil.getLogger().severe(e.getMessage());
        }
    }

}
