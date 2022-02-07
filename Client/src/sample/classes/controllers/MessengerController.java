package sample.classes.controllers;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MessengerController
{
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label firstLabel;

    @FXML
    private Label messageText;

    @FXML
    private Label messageTime;

    @FXML
    private ScrollPane chat;

    @FXML
    private AnchorPane usersPane;

    @FXML
    private Label nickname;

    @FXML
    private Label name;

    @FXML
    private TextField type;

    @FXML
    private Label messageNickname;

    @FXML
    private AnchorPane anchorPaneFromScrollPane;

    private static String userNickname;
    private static String friendNickname;
    private static String messageNicknameString = "";
    private static String messageTextString = "";
    private static String messageTimeString = "";

    String userString = "";
    String lastUsersString = "";

    private static Socket clientSocket; // сокет для общения
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет

    public static String ipaddress = "localhost";
    public static int port = 8080;

    public static ClientSomething clientSomething;

    private static Vector<Button> buttonVector = new Vector<>();
    private static Vector<AnchorPane> anchorPaneVector = new Vector<>();
    private static Vector<String> userAccessVector = new Vector<>();

    void shutdown() throws IOException
    {
        clientSomething.downService();
    }

    @FXML
    void initialize()
    {
        clientSomething = new ClientSomething(ipaddress, port);
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
                            for (Button button : MessengerController.buttonVector)
                            {
                                if (!usersPane.getChildren().contains(button))
                                    usersPane.getChildren().add(button);
                                button.setOnAction(event ->
                                {
                                    initFriend(button.getText());
                                    chat.setVisible(true);
                                    firstLabel.setVisible(false);
                                    type.setVisible(true);
                                    nickname.setVisible(true);
                                    anchorPaneFromScrollPane.setVisible(true);
                                    nickname.setText(friendNickname);
                                });
                            }
                            for (int i = 0; i < anchorPaneVector.size(); i++)
                            {
                                String[] sendersArray = userAccessVector.get(i).split("!", 2);
                                if (friendNickname.equals(sendersArray[0]) || friendNickname.equals(sendersArray[1]))
                                {
                                    if (anchorPaneFromScrollPane.getChildren().contains(anchorPaneVector.get(i)))
                                        anchorPaneFromScrollPane.getChildren().get(i + 1).setVisible(true);
                                    else
                                        anchorPaneFromScrollPane.getChildren().add(anchorPaneVector.get(i));
                                }
                                else
                                    anchorPaneFromScrollPane.getChildren().get(i + 1).setVisible(false);
                            }
                            for(int i =1;i<=usersPane.getChildren().size();i++)
                            {
                                if(!buttonVector.contains(usersPane.getChildren().get(i)))
                                    usersPane.getChildren().remove(usersPane.getChildren().get(i));
                            }

                        }
                        catch (Exception e)
                        {
                            System.out.println(e.getCause());
                        }
                    }
                });
            }
        }, 0, 500);

        firstLabel.setText(userNickname + ", choose whom to speak with in the list to the right");
        name.setText(userNickname);
        type.setOnKeyPressed(event ->
        {
            if (event.getCode().getName().equals("Enter"))
            {
                userString = type.getText();
                type.clear();
            }
        });
    }

    static void initNickname(String nickname)
    {
        userNickname = nickname;
    }

    private static void initFriend(String friend)
    {
        friendNickname = friend;
    }

    public class ClientSomething
    {
        private Socket clientSocket; // сокет для общенияп
        private BufferedReader in; // поток чтения из сокета
        private BufferedWriter out; // поток записи в сокет
        private BufferedReader inputUser; //поток чтения из консоли
        private String address; // адресс клиента
        private int port; // порт соединения
        private String nickname; // имя клиента
        private Date time;
        private String dateTime;
        private SimpleDateFormat simpleDateFormat;

        ClientSomething(String address, int port)
        {
            this.address = address;
            this.port = port;
            try
            {
                this.clientSocket = new Socket(address, port);
            }
            catch (IOException e)
            {
                System.err.println("Socket failed");
            }
            try
            {
                inputUser = new BufferedReader(new InputStreamReader(System.in));
                if (clientSocket != null)
                {
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                }
                this.inputNickname(); // перед началом необходимо ввести ник
                new ClientSomething.ReadMessage().start(); // нить, читающая сообщения из сокета в бесконечном цикле
                this.getUsersList(); // также необходимо получить список всех пользователей онлайн
                new ClientSomething.WriteMessage().start();
                // нить, пишущая сообщения в сокет, приходящие из консоли в бесконечном цикле

            }
            catch (IOException e)
            {
                try
                {
                    ClientSomething.this.downService();
                }
                catch (IOException ignored)
                {

                }
            }
        }

        private void inputNickname()
        {
            try
            {
                out.write(userNickname + "\n");
                out.flush();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        void getUsersList() throws IOException
        {
            out.write("!getUsersOnlineList\n");
            out.flush();
            String usersString = in.readLine();
            if (!usersString.equals(lastUsersString))
                addUsersButtons(usersString);
        }

        private void addUsersButtons(String usersString)
        {
            buttonVector.clear();
            usersString = usersString.replace("[", "");
            usersString = usersString.replace("]", "");
            String[] usersArray = usersString.split(", ");
            for (int i = 0; i < usersArray.length; i++)
            {
                Button tempButton = new Button();
                tempButton.setStyle("-fx-background-color: E3E8EF; -fx-background-radius: 10;");
                tempButton.setLayoutX(6);
                tempButton.setLayoutY(48 * (i + 1));
                tempButton.setPrefHeight(25);
                tempButton.setPrefWidth(196);
                tempButton.setMnemonicParsing(false);
                tempButton.setCursor(Cursor.HAND);
                tempButton.setFont(Font.font("DejaVu Sans Bold", 15));
                if (usersArray[i].equals(userNickname))
                {
                    tempButton.setCursor(Cursor.DEFAULT);
                    tempButton.setDisable(true);
                    tempButton.setText(usersArray[i] + " (You)");
                }
                else
                    tempButton.setText(usersArray[i]);
                if (!buttonVector.contains(tempButton))
                {
                    buttonVector.add(tempButton);
                }
            }
            lastUsersString = usersString;
        }

        void downService() throws IOException
        {
            if (!clientSocket.isClosed())
            {
                inputUser.close();
                out.write("!exit\n");
                out.flush();
                out.close();
                clientSocket.close();
            }
        }

        // поток чтения сообщений с сервера
        private class ReadMessage extends Thread
        {
            @Override
            public void run()
            {
                String string;
                try
                {
                    while (true)
                    {
                        string = in.readLine();
                        if (string.matches("\\[.*?]"))
                        {
                            addUsersButtons(string);
                            continue;
                        }
                        String[] stringArray = string.split("!", 4);
                        AnchorPane messagePane = new AnchorPane();
                        messagePane.setLayoutY(228);
                        int counter = 0;
                        System.out.println(userAccessVector);
                        for (String str : userAccessVector)
                        {
                            String[] strArray = str.split("!", 2);
                            if ((stringArray[1].equals(strArray[0]) || stringArray[1].equals(strArray[1])) &&
                                    (stringArray[0].equals(strArray[0]) || stringArray[0].equals(strArray[1])))
                            {
                                counter++;
                            }
                        }
                        AnchorPane.setTopAnchor(messagePane, 10.0 * counter * 7);
                        Label messageTextLabel = new Label();
                        messageTextLabel.setText(stringArray[3]);
                        messageTextLabel.setLayoutX(9);
                        messageTextLabel.setLayoutY(33);
                        messageTextLabel.setMaxHeight(1000);
                        messageTextLabel.setMaxWidth(300);
                        messageTextLabel.setStyle("-fx-background-color: #e4e4e4; -fx-background-radius: 30;");
                        messageTextLabel.setWrapText(true);
                        AnchorPane.setBottomAnchor(messageTextLabel, 17.0);
                        AnchorPane.setTopAnchor(messageTextLabel, 27.0);
                        messageTextLabel.setFont(Font.font("DejaVu Sans Bold", 14));
                        messageTextLabel.setPadding(new Insets(3, 10, 3, 10));
                        Label messageTimeLabel = new Label();
                        messageTimeLabel.setText(stringArray[2]);
                        messageTimeLabel.setTextFill(Paint.valueOf("WHITE"));
                        AnchorPane.setBottomAnchor(messageTimeLabel, 0.0);
                        messageTimeLabel.setFont(Font.font("DejaVu Sans Bold", 11));
                        Label messageNicknameLabel = new Label();
                        messageNicknameLabel.setText(stringArray[0]);
                        messageNicknameLabel.setTextFill(Paint.valueOf("#fcf0f0"));
                        AnchorPane.setTopAnchor(messageNicknameLabel, 0.0);
                        messageNicknameLabel.setFont(Font.font("Magneto", 17));
                        if (stringArray[0].equals(userNickname))
                        {
                            AnchorPane.setLeftAnchor(messagePane, 309.0);
                            AnchorPane.setRightAnchor(messagePane, 0.0);
                            AnchorPane.setRightAnchor(messageTextLabel, 20.0);
                            AnchorPane.setRightAnchor(messageTimeLabel, 25.0);
                            AnchorPane.setRightAnchor(messageNicknameLabel, 22.0);
                        }
                        else
                        {
                            AnchorPane.setRightAnchor(messagePane, 309.0);
                            AnchorPane.setLeftAnchor(messagePane, 0.0);
                            AnchorPane.setLeftAnchor(messageTextLabel, 5.0);
                            AnchorPane.setLeftAnchor(messageTimeLabel, 10.0);
                            AnchorPane.setLeftAnchor(messageNicknameLabel, 7.0);
                        }
                        messagePane.getChildren().add(messageTextLabel);
                        messagePane.getChildren().add(messageTimeLabel);
                        messagePane.getChildren().add(messageNicknameLabel);
                        anchorPaneVector.add(messagePane);
                        userAccessVector.add(stringArray[0] + "!" + stringArray[1]);
                    }
                }
                catch (IOException e)
                {
                    try
                    {
                        ClientSomething.this.downService();
                    }
                    catch (IOException e1)
                    {
                        try
                        {
                            in.close();
                        }
                        catch (IOException ignore)
                        {

                        }
                    }
                }
            }
        }

        //поток, отправляющий сообщения, приходящие с консоли, на сервер
        public class WriteMessage extends Thread
        {
            @Override
            public void run()
            {
                while (true)
                {
                    time = new Date();
                    simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    dateTime = simpleDateFormat.format(time);
                    try
                    {
                        if (!userString.isEmpty())
                        {
                            if (userString.equals("!exit"))
                            {
                                ClientSomething.this.downService();
                                break;
                            }
                            else
                            {
                                out.write(userNickname + "!" + friendNickname + "!" + dateTime + "!"
                                        + userString + "\n");
                                out.flush();
                            }
                            userString = "";
                        }
                    }
                    catch (IOException e)
                    {
                        try
                        {
                            ClientSomething.this.downService();
                        }
                        catch (IOException ignored)
                        {

                        }
                    }
                }
            }
        }
    }
}
