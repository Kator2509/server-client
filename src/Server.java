import commands.Command;
import commands.CommandDispatcher;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server
{

    public static boolean Run = false;
    public static CommandDispatcher dispatcher;
    public static Thread server;
    public static final String prefix = "[MainServer]: ";

    public static void main(String[] args)
    {
        dispatcher = new CommandDispatcher();
        registerModules.registerCommands(dispatcher);
        ServerExecute.run();
        Scanner entry = new Scanner(System.in);

        while(Run)
        {
            List<String> var0 = new ArrayList<>();
            String command = "";
            int i = 0;
            for (String arguments : entry.nextLine().split(" "))
            {
                if (i == 0)
                {
                    command = arguments;
                    i++;
                }
                else {
                    var0.add(arguments);
                }
            }

            if (dispatcher.getCommandMap().containsKey(command));
            {
                dispatcher.executeCommand(command, var0);
            }
        }
    }

    public static class callShutDown extends Command
    {
        public callShutDown(String name) {
            super(name);
        }

        @Override
        public void run() {
            Run = false;
            dispatcher = null;
        }
    }

    public static class callReboot extends Command
    {

        public callReboot(String name) {
            super(name);
        }

        @Override
        public void run() {
            if(server.isAlive())
            {
                System.out.println("Alive");
                server.start();
            }
            else
            {
                System.out.println(prefix + "Reboot server core.");
                ServerExecute.callReboot();
            }
        }
    }

    private static class registerModules
    {
        private static void registerCommands(CommandDispatcher dispatcher)
        {
            dispatcher.register(new callShutDown("shutdown"));
            dispatcher.register(new callReboot("reboot"));
        }
    }

    private static class ServerExecute
    {
        private static boolean reboot = false;
        public static void callReboot()
        {
            reboot = true;
        }

        public static void run()
        {
            server = new Thread(() -> {
                try(ServerSocket server1 = new ServerSocket(25565)) {
                    Run = true;
                    DataInputStream in = null;

                    while(Run && !reboot)
                    {
                        server1.setSoTimeout(120);
                        Socket client = server1.accept();
                        if(client.isConnected())
                        {
                            in = new DataInputStream(client.getInputStream());
                        }
                        if (client.isConnected()) {
                            String message = in != null ? in.readUTF() : null;
                            System.out.println("Entry ip-address: " + message);
                        }
                    }
                } catch (SocketTimeoutException ignore) {}
                catch (IOException e) {
                    System.out.println(prefix + "Server stopped as crash. Trying to reboot server.");
                    dispatcher.executeCommand("reboot", null);
                    throw new RuntimeException(e);
                }
                reboot = false;
                if(!server.isAlive()) {
                    System.out.println(prefix + "Server stopped.");
                    if (reboot) {
                        run();
                    }
                }
            });

            server.start();
            System.out.println(prefix + "Server is start. Await a command: ");
        }
    }
}
