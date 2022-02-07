package sample.classes.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private PasswordField password_field;

    @FXML
    private Button next_button;

    @FXML
    private TextField email_phone_field;

    @FXML
    private Button log_in_button;

    @FXML
    void initialize()
    {
        next_button.setOnAction(event -> {
            System.out.println("Next page");
        });
    }

    public void shutdown()
    {
    }
}
