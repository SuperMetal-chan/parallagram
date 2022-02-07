import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Vector;

public class Server
{
    private static final int PORT = 8080;
    static LinkedList<ServerSomething> serverList = new LinkedList<>(); // список всех потоков
    static Vector<String> usersOnline = new Vector<>();

    public static void main(String[] args) throws IOException, NullPointerException
    {
        // сервер-сокет
        ServerSocket serverSocket = new ServerSocket(PORT); // сервер-сокет прослушивает порт 8080
        System.out.println("Сервер запущен");
        try
        {
            while (true)
            {
                Socket clientSocket = serverSocket.accept(); // accept() ждёт пока кто-то не подключится
                try
                {
                    serverList.add(new ServerSomething(clientSocket)); // добавить новое соединение в список
                }
                catch (IOException e)
                {
                    clientSocket.close();
                }
            }
        }
        finally
        {
            serverSocket.close();
            System.out.println("Сервер закрыт");
        }
    }
}
