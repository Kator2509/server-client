package org.servera;

import org.servera.commands.Command;
import org.servera.commands.CommandDispatcher;
import org.servera.commands.PermissionCMD;
import org.servera.config.Configuration;
import org.servera.inheritance.UserManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.Scanner;

public class Server
{

    private static boolean Run = false;
    protected static CommandDispatcher dispatcher;
    protected static UserManager userManager;
    private static Thread serverThread;
    private static final String prefix = "[MainCore]: ";

    public static void main(String[] args)
    {
        dispatcher = new CommandDispatcher();
        userManager = new UserManager();
        registerModules.registerCommands(dispatcher);
        registerModules.registerUserManager(userManager);
        ServerExecute.run();
        Scanner entry = new Scanner(System.in);

        while(Run)
        {
            LinkedList<String> var0 = new LinkedList<>();
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

            if(dispatcher.getCommandMap().containsKey(command))
            {
                dispatcher.executeCommand(command, var0);
            }
        }
    }

    public static class getterModules
    {
        public static CommandDispatcher getCommandDispatcher()
        {
            return dispatcher;
        }

        public static UserManager getUserManager()
        {
            return userManager;
        }
    }

    private static class registerModules
    {
        private static void registerCommands(CommandDispatcher dispatcher)
        {
            dispatcher.register(new callShutDown("shutdown"));
            dispatcher.register(new callReboot("reboot"));
            dispatcher.register(new PermissionCMD("permission"));
        }

        private static void registerUserManager(UserManager manager)
        {
            manager.createUser("TEST");
        }
    }

    private static class callShutDown extends Command
    {
        public callShutDown(String name) {
            super(name);
        }

        @Override
        public void run() {
            Run = false;
            dispatcher = null;
            userManager = null;
            System.out.println(prefix + "Server stopped.");
        }
    }

    private static class callReboot extends Command
    {
        public callReboot(String name) {
            super(name);
        }
        @Override
        public void run() {
            if(serverThread.isAlive())
            {
                serverThread.start();
            }
            else
            {
                System.out.println(prefix + "Reboot server core.");
                ServerExecute.callReboot();
            }
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
            serverThread = new Thread(() -> {
                try(ServerSocket server = new ServerSocket(25565)) {
                    Run = true;
                    DataInputStream in = null;

                    while(Run && !reboot)
                    {
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
                    System.out.println(prefix + "Server stopped as crash. Trying to reboot server.");
                    System.out.println(prefix + "If you see that cause one more. Please report as that.");
                    System.out.println(prefix + "And call emergency stop the server.");
                    dispatcher.executeCommand("reboot", null);
                    throw new RuntimeException(e);
                }
                reboot = false;
                if(!serverThread.isAlive()) {
                    System.out.println(prefix + "Server stopped.");
                    if (reboot) {
                        run();
                    }
                }
            });

            serverThread.start();
            System.out.println(prefix + "Server is start. Await a command: ");
        }
    }
}
