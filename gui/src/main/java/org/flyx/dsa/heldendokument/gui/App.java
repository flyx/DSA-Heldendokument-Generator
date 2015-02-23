package org.flyx.dsa.heldendokument.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author flyx
 */
public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/mainWindow.fxml"));

        Scene scene = new Scene(root, 920, 400);

        primaryStage.setTitle("DSA Heldendokument Generator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
