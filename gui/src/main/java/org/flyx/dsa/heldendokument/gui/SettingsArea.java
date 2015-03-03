package org.flyx.dsa.heldendokument.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * @author flyx
 */
public abstract class SettingsArea extends VBox {
    public SettingsArea(String heading) {
        setAlignment(Pos.TOP_CENTER);

        Label headingLabel = new Label(heading);
        headingLabel.setAlignment(Pos.CENTER);
        headingLabel.setFont(Font.font(null, FontWeight.BOLD, 16));
        headingLabel.setPadding(new Insets(5, 0, 10, 0));
        getChildren().addAll(headingLabel, createContent());
    }

    public abstract Node createContent();
}
