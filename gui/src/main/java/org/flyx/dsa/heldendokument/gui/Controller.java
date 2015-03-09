package org.flyx.dsa.heldendokument.gui;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.flyx.dsa.heldendokument.generator.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author flyx
 */
public class Controller {
    @FXML private TabPane tabs;
    @FXML private Button generateButton;
    @FXML private Label shortStatus;
    @FXML private ProgressBar progress;

    private DocumentConfiguration configuration;



    @FXML public void initialize() throws IllegalAccessException {
        configuration = new DocumentConfiguration();
        configuration.loadDefaults();

        generateButton.setDisable(!App.getInstance().environmentValid);

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
                List<Field> linesFields = new ArrayList<>();
                List<Field> optionFields = new ArrayList<>();
                List<Field> backgroundFields = new ArrayList<>();
                List<Field> otherFields = new ArrayList<>();
                Map<DocumentConfiguration.Talentbogen.Gruppen, Integer> layout = null;
                List<DocumentConfiguration.Talentbogen.Sonstiges> sonstiges = null;

                for (Field childField: container.getClass().getFields()) {
                    Lines[] linesAnnotations = childField.getAnnotationsByType(Lines.class);
                    Layout[] layoutsAnnotations = childField.getAnnotationsByType(Layout.class);
                    if (linesAnnotations.length > 0) {
                        linesFields.add(childField);
                    } else if (layoutsAnnotations.length > 0) {
                        Object obj = childField.get(container);
                        if (obj instanceof Map) {
                            layout = (Map<DocumentConfiguration.Talentbogen.Gruppen, Integer>) childField.get(container);
                        } else if (obj instanceof List) {
                            sonstiges = (List<DocumentConfiguration.Talentbogen.Sonstiges>) childField.get(container);
                        }
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

                if (layout != null) {
                    tabContent.getChildren().add(createLayoutArea("Zeilenanzahl", layout));
                }

                if (sonstiges != null) {
                    tabContent.getChildren().add(createCustomTalentsArea("Zusätzliche Talentgruppen", sonstiges));
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

    private VBox createLayoutArea(String heading, Map<DocumentConfiguration.Talentbogen.Gruppen, Integer> layout) {
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

                for (int i = 0; i < DocumentConfiguration.Talentbogen.Gruppen.values().length; i++) {
                    final DocumentConfiguration.Talentbogen.Gruppen gruppe =
                            DocumentConfiguration.Talentbogen.Gruppen.values()[i];
                    Label label = new Label(gruppe.toString() + ":");
                    label.setAlignment(Pos.BASELINE_RIGHT);

                    int defaultValue = 0;
                    if (layout.containsKey(gruppe)) {
                        defaultValue = layout.get(gruppe);
                    }
                    final ForwardingIntField input = new ForwardingIntField(0, 100, defaultValue);

                    input.valueProperty().forwardBind(layout, gruppe);

                    GridPane.setConstraints(label, 0, i);
                    GridPane.setConstraints(input, 1, i);
                    pane.getChildren().addAll(label, input);
                }
                return pane;
            }
        };
    }

    private VBox createCustomTalentsArea(String heading, List<DocumentConfiguration.Talentbogen.Sonstiges> values) {
        return new SettingsArea(heading) {
            @Override
            public Node createContent() {
                final VBox area = new VBox();
                area.setSpacing(10);
                area.setAlignment(Pos.TOP_CENTER);
                area.setPrefWidth(300);

                for (DocumentConfiguration.Talentbogen.Sonstiges value : values) {
                    final CustomBoxEntry entry = new CustomBoxEntry(values, value, area);
                    area.getChildren().add(entry);
                }

                Button addButton = new Button("+");
                addButton.setOnAction((value) -> {
                    DocumentConfiguration.Talentbogen.Sonstiges newValue =
                            new DocumentConfiguration.Talentbogen.Sonstiges("Neue Gruppe", 1);
                    values.add(newValue);
                    final CustomBoxEntry newEntry = new CustomBoxEntry(values, newValue, area);
                    area.getChildren().add(values.size() - 1, newEntry);
                });
                area.getChildren().add(addButton);

                return area;
            }
        };
    }

    @FXML private void handleCreateAction(@SuppressWarnings("unused")ActionEvent event) {
        final Button button = (Button)event.getSource();

        Task<File> buildTask = new Task<File>() {
            @Override
            protected File call() throws Exception {
                final IBuilderCallback callback = new IBuilderCallback() {
                    @Override
                    public void nowDoing(String shortDescription, String description) {
                        updateTitle(shortDescription);
                        updateMessage(description);
                    }

                    @Override
                    public void nowAt(int percent) {
                        updateProgress(percent, 100);
                    }
                };
                App.getInstance().builder.setCallback(callback);
                return App.getInstance().builder.build(configuration);
            }

            @Override
            protected void succeeded() {
                shortStatus.textProperty().unbind();
                shortStatus.setText("");
                progress.progressProperty().unbind();
                progress.setVisible(false);
                button.setDisable(false);
                // the javafx thing for this doesn't work
                try {
                    Desktop.getDesktop().open(getValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void failed() {
                Exception e = (Exception)getException();
                if (e != null) {
                    ErrorWindow ew = new ErrorWindow(e);
                    ew.show();
                }

                shortStatus.textProperty().unbind();
                shortStatus.setText("");
                progress.progressProperty().unbind();
                progress.setVisible(false);
                button.setDisable(false);
            }
        };

        button.setDisable(true);
        shortStatus.textProperty().bind(buildTask.titleProperty());
        progress.progressProperty().bind(buildTask.progressProperty());
        progress.setVisible(true);
        new Thread(buildTask).start();
    }

    @FXML private void handleParameterAction(@SuppressWarnings("unused")ActionEvent event) {
        App.getInstance().parameterWindow.show();
    }
}
