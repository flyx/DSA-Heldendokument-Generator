package org.flyx.dsa.heldendokument.gui;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author flyx
 */
public class BrowserLink extends Label {
    private URI targetURL;

    public BrowserLink() {
        setOnMouseClicked((MouseEvent e) -> {
            try {
                Desktop.getDesktop().browse(targetURL);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        setOnMouseEntered((MouseEvent e) -> {
            setUnderline(true);
        });
        setOnMouseExited((MouseEvent e) -> {
            setUnderline(false);
        });
        setTextFill(Color.BLUE);
    }

    public void setTargetURL(final String value) {
        try {
            this.targetURL = new URI(value);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public String getTargetURL() {
        return targetURL.toString();
    }
}
