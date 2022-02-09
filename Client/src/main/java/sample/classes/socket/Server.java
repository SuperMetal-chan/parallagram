package sample.classes.socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    private static Socket clientSocket; // сокет для общения
    private static ServerSocket serverSocket; // сервер-сокет
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет

    public static void main(String[] args) throws IOException, NullPointerException
    {
        try
        {
            serverSocket = new ServerSocket(8080); // сервер-сокет прослушивает порт 8080

            System.out.println("Сервер запущен");
            clientSocket = serverSocket.accept(); // accept() ждёт пока кто-то не подключится

            try
            {
                while (true)
                {
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    String word = in.readLine();
                    System.out.println(word);
                    out.write("Вы написали: " + word + "\n");
                    out.flush();
                    if (word.equals("exit"))
                        break;
                }
            }
            finally
            {
                System.out.println("Сервер сейчас закроется");
                clientSocket.close();
                in.close();
                out.close();
            }
        }
        finally
        {
            serverSocket.close();
            System.out.println("Сервер закрыт");
        }
    }
}
