package org.servera;

import org.servera.DataBasePSQL.Connector;
import org.servera.DataBasePSQL.ConnectorManager;
import org.servera.commands.Command;
import org.servera.commands.CommandDispatcher;
import org.servera.config.Configuration;
import org.servera.config.ConfigurationManager;
import org.servera.config.FileManager.JSONParser;
import org.servera.inheritance.SPermission.PermissionManager;
import org.servera.inheritance.UserManager;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.UUID;

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
        connectorManager = new ConnectorManager();

        registerModules.registerConfigurations(configurationManager);
        registerModules.registerConnection(connectorManager, configurationManager);
        userManager = new UserManager(connectorManager.getConnect("UserDataBase"), configurationManager.getConfiguration("DefaultParameters"));

        dispatcher = new CommandDispatcher(configurationManager.getConfiguration("language"));
        registerModules.registerCommands(dispatcher);
        permissionManager = new PermissionManager(connectorManager.getConnect("UserDataBase"), dispatcher);
        if(!dispatcher.registerPermissionManager(permissionManager))
        {
            System.out.println(prefix + "[ERROR] Permissions not loaded. That can cause a problem.");
        }

        ServerExecute.run();
        ServerCommandDispatcher.run();
    }

    private static class registerModules
    {
        private static void registerConnection(ConnectorManager connectorManager, ConfigurationManager configurationManager)
        {
            connectorManager.register("UserDataBase",
                    new Connector(
                            JSONParser.getData(configurationManager.getConfiguration("DataBase").getDataPath("DataBase.UserDataBase").toString(), "login").toString(),
                            JSONParser.getData(configurationManager.getConfiguration("DataBase").getDataPath("DataBase.UserDataBase").toString(), "password").toString(),
                            JSONParser.getData(configurationManager.getConfiguration("DataBase").getDataPath("DataBase.UserDataBase").toString(), "url").toString()
                    ));
        }

        private static void registerCommands(CommandDispatcher dispatcher)
        {
            dispatcher.register(new callShutDown("shutdown", "System.shutdown"));
            dispatcher.register(new callReboot("reboot", "System.reboot"));
            System.out.println(prefix + "Registered system commands.");
        }

        private static void registerConfigurations(ConfigurationManager configurationManager)
        {
            configurationManager.register("DataBase", new Configuration("DBConfig.yml"));
            configurationManager.register("DefaultParameters", new Configuration("System/Default.yml"));
            configurationManager.register("language",
                    new Configuration("language/" + configurationManager.getConfiguration("DefaultParameters").getDataPath("language") + ".yml"));
        }
    }

    private static class callShutDown extends Command
    {
        public callShutDown(String name, String permission) {
            super(name, permission);
        }

        @Override
        public boolean run() {
            Run = false;
            dispatcher = null;
            userManager = null;
            System.out.println(prefix + "Server stopped.");
            return true;
        }
    }

    private static class callReboot extends Command
    {
        public callReboot(String name, String permission) {
            super(name, permission);
        }
        @Override
        public boolean run() {
            if(serverThread.isAlive())
            {
                serverThread.start();
                System.out.println(prefix + "Server was offline. Starting server thread.");
            }
            else
            {
                System.out.println(prefix + "Reboot server thread.");
                System.out.println(prefix + "Starting loading modules...");
                configurationManager = new ConfigurationManager();
                connectorManager = new ConnectorManager();

                registerModules.registerConfigurations(configurationManager);
                registerModules.registerConnection(connectorManager, configurationManager);
                userManager = new UserManager(connectorManager.getConnect("UserDataBase"), configurationManager.getConfiguration("DefaultParameters"));

                dispatcher = new CommandDispatcher(configurationManager.getConfiguration("language"));
                registerModules.registerCommands(dispatcher);
                permissionManager = new PermissionManager(connectorManager.getConnect("UserDataBase"), dispatcher);
                if(!dispatcher.registerPermissionManager(permissionManager))
                {
                    System.out.println(prefix + "[ERROR] Permissions not loaded. That can cause a problem.");
                }

                ServerExecute.callReboot();
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

                    dispatcher.runCommand(command, var0, userManager.getUser("Console"));
                }
            });

            dispatcherThread.start();
        }
    }

    private static class ServerExecute
    {
        private static boolean reboot = false;
        private static void callReboot()
        {
            reboot = true;
        }

        private static void run()
        {
            serverThread = new Thread(() -> {
                try(ServerSocket server = new ServerSocket(25565)) {
                    Run = true;
                    DataInputStream in = null;

                    while(Run && !reboot)
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
                    System.out.println(prefix + "Server stopped as crash. Trying to reboot server.");
                    System.out.println(prefix + "If you see that cause one more. Please report as that.");
                    System.out.println(prefix + "And call emergency stop the server.");
                    dispatcher.runCommand("reboot", null, userManager.getUser("Console"));
                    e.getStackTrace();
                }
                reboot = false;
                if(!serverThread.isAlive()) {
                    System.out.println(prefix + "Server stopped.");
                    if (reboot) {
                        run();
                        System.out.println(prefix + "Server rebooted success");
                    }
                }
            });

            serverThread.start();
            System.out.println(prefix + "Server is start. Await a command: ");
        }
    }

    private static class ServerSetDefaultParameters{
        private static boolean setParametersToDefault()
        {
            connectorManager.getConnect("UserDataBase").openConnection(connection ->
            {
                try {
                    Statement var = connection.createStatement();
                    var.execute("SELECT count(*) FROM us_users WHERE tab_num = 'T-CONSOLE' AND firstName = 'Console'");
                    ResultSet rs = var.getResultSet();
                    rs.next();
                    if(!(rs.getInt(1) > 0)) {
                        var.execute("insert into us_users (uuid, tab_num, firstname, dcre) values ('" + UUID.randomUUID() + "', 'T-CONSOLE', 'Console', now())");
                        System.out.println(prefix + "Created user for console.");


                    }
                } catch (SQLException e)
                {
                    System.out.println(prefix + "[ERROR] Can't load a checker.");
                    e.printStackTrace();
                }
            });
            return false;
        }
    }
}
