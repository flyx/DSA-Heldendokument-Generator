package org.flyx.dsa.heldendokument.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.flyx.dsa.heldendokument.generator.IBuilder;
import org.flyx.dsa.heldendokument.generator.TexBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author flyx
 */
public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private static App instance;
    public static App getInstance() {return instance;}

    public IBuilder builder;
    public Stage parameterWindow;

    public boolean environmentValid;
    public List<String> envMessages;

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;

        builder = new TexBuilder();
        envMessages = new ArrayList<>();
        environmentValid = builder.isEnvironmentValid(envMessages);

        Parent parameterDef = FXMLLoader.load(getClass().getResource("/parameterWindow.fxml"));
        Scene parameterScene = new Scene(parameterDef, 600, 200);

        parameterWindow = new Stage(StageStyle.UNIFIED);
        parameterWindow.initModality(Modality.WINDOW_MODAL);
        parameterWindow.initOwner(primaryStage);
        parameterWindow.setTitle("DSA Heldendokument Generator: TeX Parameter");
        parameterWindow.setScene(parameterScene);
        parameterWindow.setResizable(false);

        if (!builder.areAllParametersValid()) {
            parameterWindow.showAndWait();
        }

        if (builder.areAllParametersValid()) {
            builder.prepare();

            Parent root = FXMLLoader.load(getClass().getResource("/mainWindow.fxml"));

            Scene scene = new Scene(root, 920, 400);

            primaryStage.setTitle("DSA Heldendokument Generator");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }

    @Override public void stop() {
        builder.cleanup();
    }
}
