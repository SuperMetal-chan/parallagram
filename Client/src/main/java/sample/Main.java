package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.classes.controllers.SignUpController;

public class Main extends Application
{

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Parent root = FXMLLoader.load(getClass().getResource("../FXMLs/logIn.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("FXMLs/messenger.fxml"));
        primaryStage.setTitle("Parallagram");
        primaryStage.setScene(new Scene(root, 700, 400));
        primaryStage.show();
        SignUpController.properExit(primaryStage);
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
