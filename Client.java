import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Client
{
    public static void main(String[] args)
    {
        //Trying to connect the server.
        try(Socket server = new Socket("127.0.0.1", 25565)) {
            DataOutputStream out;
            //if connected to server next.
            if (server.isConnected())
            {
                out = new DataOutputStream(server.getOutputStream());

                //Getting ip-address from client.
                try {
                    final DatagramSocket socket = new DatagramSocket();
                    socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                    InetAddress ip = socket.getLocalAddress();
                    //Send address to server.
                    out.writeUTF(String.valueOf(ip).substring(1));
                }
                catch (IOException e)
                {
                    System.out.println("Ошибка отправки данных на сервер авторизации.");
                    throw new IOException(e);
                }
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время подключения к серверу. Возможно сервер выключен.");
            throw new RuntimeException(e);
        }
    }
}
