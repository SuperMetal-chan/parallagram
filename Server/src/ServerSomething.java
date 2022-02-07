import java.io.*;
import java.net.Socket;

public class ServerSomething extends Thread
{
    private Socket socket; // сокет, через который сервер общается с клиетом
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // потом записи в
    private String nickname = "Unknown";

    private String getNickname()
    {
        return nickname;
    }

    ServerSomething(Socket socket) throws IOException
    {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        start();
    }

    @Override
    public void run()
    {
        String word;
        try
        {
            int i = 0;
            while (i < 2)
            {
                try
                {
                    word = in.readLine();
                }
                catch (IOException ee)
                {
                    downService();
                    break;
                }
                String message = null;
                boolean isClose = false, entered = false;
                if (socket.isClosed() || word.equals("!exit"))
                {
                    i++;
                    Server.usersOnline.remove(nickname);
                    isClose = true;
                }
                else if (word.equals("!getUsersOnlineList"))
                {
                    message = "" + Server.usersOnline;
                    for (ServerSomething serverSomething : Server.serverList)
                        serverSomething.send(message);
                }
                else if (i == 0)
                {
                    nickname = word;
                    i++;
                    Server.usersOnline.add(nickname);
                    entered = true;
                }
                else
                {
                    message = word;
                    String[] messageArray = message.split("!", 3);
                    for (ServerSomething serverSomething : Server.serverList)
                    {
                        if (serverSomething.getNickname().equals(messageArray[1])
                                || serverSomething.getNickname().equals(messageArray[0]))
                            serverSomething.send(message);
                    }
                }
                if (isClose)
                {
                    downService();
                    break;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void send(String message) throws IOException
    {
        System.out.println(message);
        out.write(message + "\n");
        out.flush();
    }

    private void downService() throws IOException
    {
        if (!socket.isClosed())
        {
            Server.usersOnline.remove(nickname);
            for (ServerSomething serverSomething : Server.serverList)
            {
                if(!serverSomething.nickname.equals(nickname))
                    serverSomething.send("" + Server.usersOnline);
            }
            socket.close();
            in.close();
            out.close();
            for (ServerSomething serverSomething : Server.serverList)
            {
                if (serverSomething.equals(this))
                    serverSomething.interrupt();
                Server.serverList.remove(this);
            }
        }
    }
}
