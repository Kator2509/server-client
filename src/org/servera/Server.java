package org.servera;

import org.servera.DataBasePSQL.ConnectorManager;
import org.servera.commands.Command;
import org.servera.commands.CommandDispatcher;
import org.servera.config.ConfigurationManager;
import org.servera.inheritance.SPermission.PermissionManager;
import org.servera.inheritance.User;
import org.servera.inheritance.UserManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Server
{
    private static boolean Run = false;
    protected static CommandDispatcher dispatcher;
    protected static UserManager userManager;
    protected static ConfigurationManager configurationManager;
    protected static ConnectorManager connectorManager;
    protected static PermissionManager permissionManager;
    protected static Thread serverThread;
    protected static Thread dispatcherThread;
    private static final String prefix = "[ServerThread]: ";

    public static void main(String[] args)
    {
        configurationManager = new ConfigurationManager();
        connectorManager = new ConnectorManager(configurationManager);

        userManager = new UserManager(connectorManager.getConnect("UserDataBase"));

        dispatcher = new CommandDispatcher(configurationManager.getConfiguration("language"));
        permissionManager = new PermissionManager(connectorManager.getConnect("UserDataBase"), dispatcher);
        registerModules.registerCommands(dispatcher);
        if(dispatcher.registerPermissionManager(permissionManager))
        {
            System.out.println(prefix + "[ERROR] Permissions not loaded. That can cause a problem.");
        }


        ServerExecute.run();
        ServerCommandDispatcher.run();
    }

    private static class registerModules
    {
        private static void registerCommands(CommandDispatcher dispatcher)
        {
            dispatcher.register(new callShutDown("shutdown", new ArrayList<>(List.of("System.shutdown"))));
            dispatcher.register(new callReboot("reboot", new ArrayList<>(List.of("System.reboot"))));
            dispatcher.register(new UserManager.UserCommand("user", connectorManager.getConnect("UserDataBase"),
                    configurationManager.getConfiguration("DefaultParameters"), permissionManager));
            System.out.println(prefix + "Registered system commands.");
        }
    }

    private static class callShutDown extends Command
    {
        public callShutDown(String name, List<String> permission) {
            super(name, permission);
        }

        @Override
        public boolean run(User user) {
            Run = false;
            dispatcher = null;
            userManager = null;
            System.out.println(prefix + "Server stopped.");
            return true;
        }
    }

    private static class callReboot extends Command
    {
        public callReboot(String name, List<String> permission) {
            super(name, permission);
        }
        @Override
        public boolean run(User user) {
            if(serverThread.isAlive())
            {
                serverThread.start();
                System.out.println(prefix + "Server was offline. Starting server thread.");
                if(dispatcherThread.isAlive())
                {
                    dispatcherThread.start();
                    System.out.println(prefix + "Dispatcher was offline. Starting dispatcher thread.");
                }
            }
            else
            {
                System.out.println(prefix + "Reboot server modules.");
                configurationManager = null;
                connectorManager = null;
                userManager = null;
                dispatcher = null;
                permissionManager = null;

                System.out.println(prefix + "Starting loading modules...");
                configurationManager = new ConfigurationManager();
                connectorManager = new ConnectorManager(configurationManager);

                userManager = new UserManager(connectorManager.getConnect("UserDataBase"));

                dispatcher = new CommandDispatcher(configurationManager.getConfiguration("language"));
                registerModules.registerCommands(dispatcher);
                permissionManager = new PermissionManager(connectorManager.getConnect("UserDataBase"), dispatcher);
                if(dispatcher.registerPermissionManager(permissionManager))
                {
                    System.out.println(prefix + "[ERROR] Permissions not loaded. That can cause a problem.");
                }
                System.out.println(prefix + "Reboot success.");
            }
            return true;
        }
    }

    private static class ServerCommandDispatcher
    {
        private static void run()
        {
            dispatcherThread = new Thread(() -> {
                Scanner entry = new Scanner(System.in);
                while(Run)
                {
                    LinkedList<String> var0 = new LinkedList<>();
                    String command = "";
                    int i = 0;

                    System.out.print(userManager.getUser("Console").getFirstName() + ":~$ ");

                    for (String arguments : entry.nextLine().split(" ")) {
                        if (i == 0) {
                            command = arguments;
                            i++;
                        } else {
                            var0.add(arguments);
                        }
                    }

                    dispatcher.runCommand(command, var0, userManager.getUser("Console"));
                }
            });

            dispatcherThread.start();
        }
    }

    private static class ServerExecute
    {
        private static void run()
        {
            serverThread = new Thread(() -> {
                try(ServerSocket server = new ServerSocket(25565)) {
                    Run = true;
                    DataInputStream in = null;

                    while(Run)
                    {
                        //Server thread code. Connection with client. Need override for login manager.
                        server.setSoTimeout(120);
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
                } catch (SocketTimeoutException ignore) {}
                catch (IOException e) {
                    System.out.println(prefix + "[ERROR] Server stopped as crash. Trying to reboot server.");
                    System.out.println(prefix + "[ERROR] If you see that cause one more. Please report as that.");
                    System.out.println(prefix + "[ERROR] And call emergency stop the server.");
                    dispatcher.runCommand("reboot", null, userManager.getUser("Console"));
                    e.getStackTrace();
                }
            });

            serverThread.start();
            System.out.println(prefix + "Server is start. Await a command: ");
        }
    }
}
