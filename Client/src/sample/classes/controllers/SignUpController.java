package sample.classes.controllers;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.lang.String;

import javafx.stage.WindowEvent;
import org.apache.commons.validator.routines.EmailValidator;
import sample.classes.database.DatabaseHandler;
import sample.classes.database.User;


public class SignUpController
{


    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private PasswordField repeated_password_field;

    @FXML
    private TextField email_field;

    @FXML
    private ImageView phoneCheck;

    @FXML
    private Label nicknameLabel;

    @FXML
    private ImageView emailCheck;

    @FXML
    private Button log_in_button;

    @FXML
    private Label passwordLabel;

    @FXML
    private PasswordField password_field;

    @FXML
    private Button next_button;

    @FXML
    private Label phoneLabel;

    @FXML
    private ImageView nicknameCheck;

    @FXML
    private ImageView passwordRepeatedCheck;

    @FXML
    private ImageView passwordCheck;

    @FXML
    private TextField phone_field;

    @FXML
    private TextField nickname_field;

    @FXML
    private Label emailLabel;

    @FXML
    private Label passwordRepeatedLabel;

    public static void opening_new_window(FXMLLoader loader)
    {
        try
        {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Parallagram");
            stage.setScene(new Scene(root));
            stage.show();
//            MessengerController controller = new MessengerController();
//            stage.setOnCloseRequest(event -> controller.shutdown());
            if (loader.getController() instanceof MessengerController)
            {
                MessengerController controller = loader.getController();
                properExit(stage,controller);
            }
            else
            {
                properExit(stage);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void properExit(Stage stage, MessengerController controller)
    {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent t)
            {
                try
                {
                    controller.shutdown();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                //System.out.println("Closing");
                Platform.exit();
                System.exit(0);
            }

        });
    }

    public static void properExit(Stage stage)
    {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent t)
            {
                Platform.exit();
                System.exit(0);
            }

        });
    }

    @FXML
    void initialize()
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                Platform.runLater(new Runnable()
                {
                    public void run()
                    {
                        try
                        {
                            CheckStatements();
                        }
                        catch (Exception e)
                        {
                            System.out.println(e.toString());
                        }
                    }
                });
            }
        }, 0, 1000);

        log_in_button.setOnAction(event ->
        {
            log_in_button.getScene().getWindow().hide();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/sample/FXMLs/logIn.fxml"));

            opening_new_window(loader);

        });

        DatabaseHandler dbHandler = new DatabaseHandler();


        next_button.setOnAction(event ->
        {//email phone pass

            User user = initUser();

            dbHandler.SignUpUser(user);

            next_button.getScene().getWindow().hide();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/sample/FXMLs/messenger+.fxml"));
            MessengerController.initNickname(user.getNickname());
            opening_new_window(loader);
        });

    }

    void ChangeColor(String url, int width, int height, boolean preserveRatio, boolean smooth, String color,
                     ImageView imageView, Label label)
    {
        imageView.setImage(new javafx.scene.image.Image(url, width, height, preserveRatio, smooth));
        label.setTextFill(Paint.valueOf(color));
        label.setEffect(new DropShadow(10.0, Color.valueOf(color)));
    }

    void CheckStatements() throws SQLException, ClassNotFoundException
    {
        DatabaseHandler dbHandler = new DatabaseHandler();

        User user = initUser();

        if (user.getNickname().isEmpty())
        {
            nicknameLabel.setText("This field must be filled");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f", nicknameCheck, nicknameLabel);
        }
        else if (user.getNickname().length() < 5)
        {
            nicknameLabel.setText("Nickname is too short");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f", nicknameCheck, nicknameLabel);
        }
        else if (user.getNickname().length() > 40)
        {
            nicknameLabel.setText("Nickname is too long");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f", nicknameCheck, nicknameLabel);
        }
        else if (!user.getNickname().matches(".*[a-zA-Z].*"))
        {
            nicknameLabel.setText("No letters");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f", nicknameCheck, nicknameLabel);
        }
        else if (dbHandler.CheckNickname(user).isBeforeFirst())
        {
            nicknameLabel.setText("This one is already taken");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f", nicknameCheck, nicknameLabel);
        }
        else
        {
            nicknameLabel.setText("Correct");
            ChangeColor("sample/assets/checked.png", 64, 64,
                    true, false, "#2ffa62", nicknameCheck, nicknameLabel);
        }

        if (user.getEmail().isEmpty())
        {
            emailLabel.setText("This field must be filled");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f", emailCheck, emailLabel);
        }
        else if (!EmailValidator.getInstance().isValid(user.getEmail()))
        {
            emailLabel.setText("Email is not valid");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f", emailCheck, emailLabel);
        }
        else if (dbHandler.CheckEmail(user).isBeforeFirst())
        {
            emailLabel.setText("This one is already taken");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f", emailCheck, emailLabel);
        }
        else
        {
            emailLabel.setText("Correct");
            ChangeColor("sample/assets/checked.png", 64, 64,
                    true, false, "#2ffa62", emailCheck, emailLabel);
        }

        if (user.getPhone().isEmpty())
        {
            phoneLabel.setText("This field must be filled");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f", phoneCheck, phoneLabel);
        }
        else if (user.getPhone().length() != 10 || !user.getPhone().matches("\\d*"))
        {
            phoneLabel.setText("Phone number is not valid");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f", phoneCheck, phoneLabel);
        }
        else if (dbHandler.CheckPhone(user).isBeforeFirst())
        {
            phoneLabel.setText("This one is already taken");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f", phoneCheck, phoneLabel);
        }
        else
        {
            phoneLabel.setText("Correct");
            ChangeColor("sample/assets/checked.png", 64, 64,
                    true, false, "#2ffa62", phoneCheck, phoneLabel);
        }

        if (user.getPassword().isEmpty())
        {
            passwordLabel.setText("This field must be filled");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f",
                    passwordCheck, passwordLabel);
        }
        else if (user.getPassword().length() < 6)
        {
            passwordLabel.setText("Password is too short");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f",
                    passwordCheck, passwordLabel);
        }
        else if (user.getPassword().length() > 50)
        {
            passwordLabel.setText("Password is too long");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f",
                    passwordCheck, passwordLabel);
        }
        else if (user.getPassword().equals(user.getPassword().toLowerCase()))
        {
            passwordLabel.setText("No upper case letters");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f",
                    passwordCheck, passwordLabel);
        }
        else if (user.getPassword().equals(user.getPassword().toUpperCase()))
        {
            passwordLabel.setText("No lower case letters");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f",
                    passwordCheck, passwordLabel);
        }
        else if (!user.getPassword().matches(".*\\d.*"))
        {
            passwordLabel.setText("No digits");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f",
                    passwordCheck, passwordLabel);
        }
        else if (dbHandler.CheckPassword(user).isBeforeFirst())
        {
            passwordLabel.setText("This one is already taken");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f", passwordCheck, passwordLabel);
        }
        else
        {
            passwordLabel.setText("Correct");
            ChangeColor("sample/assets/checked.png", 64, 64,
                    true, false, "#2ffa62",
                    passwordCheck, passwordLabel);
        }

        if (user.getPasswordRepeated().isEmpty())
        {
            passwordRepeatedLabel.setText("This field must be filled");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f",
                    passwordRepeatedCheck, passwordRepeatedLabel);
        }
        else if (!user.getPassword().equals(user.getPasswordRepeated()))
        {
            passwordRepeatedLabel.setText("Passwords do not match");
            ChangeColor("sample/assets/cancel.png", 64, 64,
                    true, false, "#fc4d2f",
                    passwordRepeatedCheck, passwordRepeatedLabel);
        }
        else
        {
            passwordRepeatedLabel.setText("Correct");
            ChangeColor("sample/assets/checked.png", 64, 64,
                    true, false, "#2ffa62",
                    passwordRepeatedCheck, passwordRepeatedLabel);
        }


        if (nicknameLabel.getText().equals("Correct") && emailLabel.getText().equals("Correct")
                && phoneLabel.getText().equals("Correct")
                && passwordLabel.getText().equals("Correct")
                && passwordRepeatedLabel.getText().equals("Correct"))
            next_button.setDisable(false);
        else
            next_button.setDisable(true);
    }

    User initUser()
    {
        String nickname = nickname_field.getText().trim();
        String email = email_field.getText().trim();
        String phone = phone_field.getText().trim();
        phone = phone.replace("-", "");
        String password = password_field.getText().trim();
        String passwordRepeated = repeated_password_field.getText().trim();

        User user = new User(nickname, email, phone, password, passwordRepeated);

        return user;
    }
}
