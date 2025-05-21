import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class Server
{
    public void onInitialize()
    {
        try(ServerSocket server = new ServerSocket(25565)) {

            DataInputStream in = null;
            while (true)
            {
                Thread.sleep(1800);
                Socket client = server.accept();
                if(client.isConnected())
                {
                    in = new DataInputStream(client.getInputStream());
                }
                if (client.isConnected()) {
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
