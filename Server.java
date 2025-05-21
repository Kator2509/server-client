import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class Server
{
    public void onInitialize()
    {
        //Trying to create a server socket.
        try(ServerSocket server = new ServerSocket(25565)) {

            DataInputStream in = null;
            //loop
            while (true)
            {
                //Cleap 30 sec. After continue.
                Thread.sleep(1800);
                //Accepting connection.
                Socket client = server.accept();
                //If client is connected.
                if(client.isConnected())
                {
                    in = new DataInputStream(client.getInputStream());
                }
                if (client.isConnected()) {
                    //If 'in' not null read else null.
                    String message = in != null ? in.readUTF() : null;
                    System.out.println("Entry ip-address: " + message);
                }
            }


        } catch (IOException | InterruptedException e) {
            System.out.println("При запуске сервера произошла ошибка. Возможно порт занят.");
            throw new RuntimeException(e);
        }
    }

}
