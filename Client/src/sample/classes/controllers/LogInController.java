package sample.classes.controllers;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import sample.classes.database.DatabaseHandler;
import sample.classes.database.User;

public class LogInController
{

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
    private Button sign_up_button;


    @FXML
    void initialize()
    {
        next_button.setOnAction(event ->
        {
            String emailPhone = email_phone_field.getText().trim();
            String password = password_field.getText().trim();
            User user = new User("", emailPhone, "", password, "");

            if (user.getEmail() != null && user.getPassword() != null)
            {
                try
                {
                    loginUser(emailPhone, password);
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
            else
                System.out.println("One or both of the fields are empty");
        });

        sign_up_button.setOnAction(event ->
        {
            sign_up_button.getScene().getWindow().hide();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/sample/FXMLs/signUp.fxml"));

            SignUpController.opening_new_window(loader);
        });

    }

    private static Socket clientSocket; // сокет для общения
    private static BufferedWriter out; // поток записи в сокет

    private void loginUser(String loginText, String passwordText) throws SQLException, ClassNotFoundException, IOException
    {
        DatabaseHandler dbHandler = new DatabaseHandler();

        String emailPhone = email_phone_field.getText().trim();
        String password = password_field.getText().trim();
        User user = new User("", emailPhone, "", password, "");
        ResultSet resultSet = dbHandler.LogInUser(user);
        if(resultSet.isBeforeFirst())
        {
            next_button.getScene().getWindow().hide();

//            try
//            {
//                clientSocket = new Socket("localhost", 8080); // запрашивает у сервера доступ на соединение
//                out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
//                String nickname = user.getNickname();
//                System.out.println(nickname);
//                out.write("nickname\n");
//                out.flush();
//            }
//            finally
//            {
//                clientSocket.close();
//                out.close();
//                System.out.println("Клиет был закрыт");
//            }

            resultSet.next();
            user.setNickname(resultSet.getString("nickname"));
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/sample/FXMLs/messenger+.fxml"));
            MessengerController.initNickname(user.getNickname());
            SignUpController.opening_new_window(loader);
        }
        else
        {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("   Input is not valid");
            errorAlert.setContentText("Check your email/phone and password and try again or sign up if you haven't" +
                    " signed up yet");
            DialogPane dialogPane = errorAlert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource
                    ("/sample/CSSs/errorMessage.css").toExternalForm());
            errorAlert.showAndWait();
        }
    }


}
