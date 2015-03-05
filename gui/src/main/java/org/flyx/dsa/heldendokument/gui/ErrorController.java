package org.flyx.dsa.heldendokument.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.flyx.dsa.heldendokument.generator.ExternalCallException;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
* @author flyx
*/
public final class ErrorController {
    private Exception e;

    @FXML
    private Label heading;
    @FXML private TextArea text;

    @FXML void initialize() {
        heading.setText(e.getMessage());
        if (e instanceof ExternalCallException) {
            text.setText(((ExternalCallException)e).getExternalCallOutput());
        } else {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            text.setText(sw.toString());
            pw.close();
        }
        text.setEditable(false);
    }

    void setException(Exception e) {
        this.e = e;
    }

    @FXML void close(ActionEvent e) {
        ((Node)e.getSource()).getScene().getWindow().hide();
    }
}
