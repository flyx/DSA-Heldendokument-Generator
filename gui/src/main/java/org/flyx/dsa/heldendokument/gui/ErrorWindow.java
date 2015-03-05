package org.flyx.dsa.heldendokument.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.flyx.dsa.heldendokument.generator.ExternalCallException;

import java.io.IOException;

/**
 * @author flyx
 */
public class ErrorWindow {

    private Stage s;

    public ErrorWindow(Exception error) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/errorWindow.fxml"));
            loader.setControllerFactory(paramClass -> {
                ErrorController controller = new ErrorController();
                controller.setException(error);
                return controller;
            });
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("This should never happen", e);
        }

        s = new Stage();
        Scene scene = new Scene(root, 920, 400);
        s.setScene(scene);
    }

    public void show() {
        s.showAndWait();
    }
}
