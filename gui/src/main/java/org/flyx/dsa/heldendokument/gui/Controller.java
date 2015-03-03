package org.flyx.dsa.heldendokument.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import org.flyx.dsa.heldendokument.generator.DocumentConfiguration;
import org.flyx.dsa.heldendokument.generator.Lines;
import org.flyx.dsa.heldendokument.generator.YamlMapping;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author flyx
 */
public class Controller {
    @FXML private TabPane tabs;

    private DocumentConfiguration configuration;

    @FXML public void initialize() throws IllegalAccessException {
        configuration = new DocumentConfiguration();
        configuration.loadDefaults();

        for (Field field: DocumentConfiguration.class.getFields()) {
            YamlMapping[] mappings = field.getAnnotationsByType(YamlMapping.class);
            if (mappings.length > 0) {
                Tab tab = new Tab(mappings[0].value());
                tabs.getTabs().add(tab);
                HBox tabContent = new HBox();
                tab.setContent(tabContent);

                tab.setClosable(false);
                tabContent.setFillHeight(true);
                tabContent.setAlignment(Pos.CENTER);
                tabContent.setSpacing(20);

                Object container = field.get(configuration);
                List<Field> linesFields = new ArrayList<Field>();
                List<Field> optionFields = new ArrayList<Field>();
                List<Field> backgroundFields = new ArrayList<Field>();
                List<Field> otherFields = new ArrayList<Field>();

                for (Field childField: container.getClass().getFields()) {
                    Lines[] linesAnnotations = childField.getAnnotationsByType(Lines.class);
                    if (linesAnnotations.length > 0) {
                        linesFields.add(childField);
                    } else if (boolean.class.equals(childField.getType())) {
                        optionFields.add(childField);
                    } else if (int.class.equals(childField.getType())) {
                        otherFields.add(childField);
                    } else if (DocumentConfiguration.Hintergrund.Hintergrundbild.class.equals(childField.getType())) {
                        backgroundFields.add(childField);
                    } else {
                        throw new RuntimeException("Unsupported field!");
                    }
                }

                if (linesFields.size() > 0) {
                    tabContent.getChildren().add(createIntMappingArea("Zeilenanzahl", container, linesFields));
                }

                if (optionFields.size() > 0) {
                    tabContent.getChildren().add(createOptionArea("Optionen", container, optionFields));
                }

                if (backgroundFields.size() > 0) {
                    tabContent.getChildren().add(createBackgroundArea("Hintergrundbilder", container, backgroundFields));
                }

                if (otherFields.size() > 0) {
                    tabContent.getChildren().add(createIntMappingArea("Sonstiges", container, otherFields));
                }
            }
        }
    }

    private VBox createIntMappingArea(String heading, Object container, List<Field> fields) {
        return new SettingsArea(heading) {
            @Override
            public Node createContent() {
                GridPane pane = new GridPane();
                pane.setAlignment(Pos.CENTER);
                pane.setHgap(10);
                pane.setVgap(5);
                pane.getColumnConstraints().addAll(
                        new ColumnConstraints(50, 200, 200, Priority.NEVER, HPos.RIGHT, false),
                        new ColumnConstraints(25, 50, 50, Priority.NEVER, HPos.LEFT, false));

                for (int i = 0; i < fields.size(); i++) {
                    final Field field = fields.get(i);
                    YamlMapping[] mappings = field.getAnnotationsByType(YamlMapping.class);
                    if (mappings.length == 0) {
                        throw new RuntimeException("Field without mapping!");
                    }

                    Label label = new Label(mappings[0].value() + ":");
                    label.setAlignment(Pos.BASELINE_RIGHT);

                    ForwardingIntField input = null;
                    try {
                        input = new ForwardingIntField(0, 100, (Integer)field.get(container));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("This should never happen.", e);
                    }
                    input.valueProperty().forwardBind(container, field);

                    GridPane.setConstraints(label, 0, i);
                    GridPane.setConstraints(input, 1, i);
                    pane.getChildren().addAll(label, input);
                }
                return pane;
            }
        };
    }

    private VBox createOptionArea(String heading, Object container, List<Field> fields) {
        return new SettingsArea(heading) {
            @Override
            public Node createContent() {
                VBox area = new VBox();
                area.setSpacing(10);
                area.setAlignment(Pos.TOP_CENTER);

                for (final Field field : fields) {
                    final YamlMapping mappings[] = field.getAnnotationsByType(YamlMapping.class);
                    if (mappings.length == 0) {
                        throw new RuntimeException("Field without mapping!");
                    }

                    ForwardingCheckbox checkbox = new ForwardingCheckbox(mappings[0].value());
                    checkbox.forwardBind(container, field);
                    try {
                        checkbox.setSelected((Boolean) field.get(container));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("This should never happen.", e);
                    }
                    checkbox.setPrefWidth(200);
                    checkbox.setAlignment(Pos.CENTER_LEFT);
                    area.getChildren().add(checkbox);
                }
                return area;
            }
        };
    }

    private VBox createBackgroundArea(String heading, Object container, List<Field> fields) {
        return new SettingsArea(heading) {
            @Override
            public Node createContent() {
                GridPane pane = new GridPane();
                pane.setAlignment(Pos.CENTER);
                pane.setHgap(10);
                pane.setVgap(5);
                pane.getColumnConstraints().addAll(
                        new ColumnConstraints(50, 100, 100, Priority.NEVER, HPos.RIGHT, false),
                        new ColumnConstraints(25, 150, 150, Priority.NEVER, HPos.LEFT, false));

                for (int i = 0; i < fields.size(); i++) {
                    final Field field = fields.get(i);
                    final YamlMapping[] mappings = field.getAnnotationsByType(YamlMapping.class);
                    if (mappings.length == 0) {
                        throw new RuntimeException("Field without mapping!");
                    }

                    Label label = new Label(mappings[0].value() + ":");
                    label.setAlignment(Pos.BASELINE_RIGHT);

                    BackgroundPictureInput input = new BackgroundPictureInput();
                    try {
                        input.forwardBind((DocumentConfiguration.Hintergrund.Hintergrundbild) field.get(container));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("This should never happen.", e);
                    }
                    input.setAlignment(Pos.CENTER_LEFT);

                    GridPane.setConstraints(label, 0, i);
                    GridPane.setConstraints(input, 1, i);
                    pane.getChildren().addAll(label, input);
                }
                return pane;
            }
        };
    }

    @FXML private void handleCreateAction(@SuppressWarnings("unused")ActionEvent event) {
        // TODO
    }

    @FXML private void handleParameterAction(@SuppressWarnings("unused")ActionEvent event) {
        App.getInstance().parameterWindow.show();
    }
}