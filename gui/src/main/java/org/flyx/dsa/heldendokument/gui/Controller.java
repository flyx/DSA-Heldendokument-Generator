package org.flyx.dsa.heldendokument.gui;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import org.flyx.dsa.heldendokument.generator.Resources;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author flyx
 */
public class Controller {
    @FXML private TabPane tabs;

    @FXML public void initialize() {
        InputStream stream = Resources.getParameterYAML();
        Map params = (Map) new Yaml().load(stream);
        for (Object entry : params.entrySet()) {
            String group = (String) ((Map.Entry) entry).getKey();
            Map content = (Map) ((Map.Entry) entry).getValue();

            Tab tab = new Tab(group);
            tabs.getTabs().add(tab);
            HBox tabContent = new HBox();
            tab.setContent(tabContent);

            tab.setClosable(false);
            tabContent.setFillHeight(true);
            tabContent.setAlignment(Pos.CENTER);
            tabContent.setSpacing(20);

            if (content.containsKey("Zeilen")) {
                VBox area = new VBox();
                tabContent.getChildren().add(area);
                area.setAlignment(Pos.TOP_CENTER);

                Label heading = new Label("Zeilenanzahl");
                heading.getStyleClass().add("heading");
                area.getChildren().add(heading);

                List lines = (List) content.get("Zeilen");
                GridPane pane = new GridPane();
                area.getChildren().add(pane);
                pane.setAlignment(Pos.CENTER);
                pane.setHgap(10);
                pane.setVgap(5);
                pane.getColumnConstraints().addAll(
                        new ColumnConstraints(50, 200, 200, Priority.NEVER, HPos.RIGHT, false),
                        new ColumnConstraints(25, 50, 50, Priority.NEVER, HPos.LEFT, false));

                for (int i = 0; i < lines.size(); i++) {
                    Map.Entry line = (Map.Entry) ((Map) lines.get(i)).entrySet().iterator().next();

                    Label label = new Label(line.getKey() + ":");
                    label.setAlignment(Pos.BASELINE_RIGHT);

                    IntField input = new IntField(0, 100, (Integer) line.getValue());
                    GridPane.setConstraints(label, 0, i);
                    GridPane.setConstraints(input, 1, i);
                    pane.getChildren().addAll(label, input);
                }
            }

            if (content.containsKey("Optionen")) {
                VBox area = new VBox();
                tabContent.getChildren().add(area);
                area.setSpacing(10);
                area.setAlignment(Pos.TOP_CENTER);

                Label heading = new Label("Optionen");
                heading.getStyleClass().add("heading");
                area.getChildren().add(heading);

                List lines = (List) content.get("Optionen");

                for (Object line1 : lines) {
                    Map.Entry line = (Map.Entry) ((Map) line1).entrySet().iterator().next();

                    CheckBox checkbox = new CheckBox((String) line.getKey());
                    checkbox.setSelected((Boolean) line.getValue());
                    checkbox.setPrefWidth(200);
                    checkbox.setAlignment(Pos.CENTER_LEFT);
                    area.getChildren().add(checkbox);
                }
            }

            if (content.containsKey("Seiten")) {
                Label label = new Label("Seiten:");
                IntField input = new IntField(1, 100, (Integer) content.get("Seiten"));
                tabContent.getChildren().addAll(label, input);
            }
        }
    }
}
