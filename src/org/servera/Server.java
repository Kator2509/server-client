package org.servera;

import org.servera.DataBasePSQL.ConnectorManager;
import org.servera.commands.Command;
import org.servera.commands.CommandDispatcher;
import org.servera.commands.CommandException;
import org.servera.config.ConfigurationManager;
import org.servera.config.FileManager.ConfigurationFileManager;
import org.servera.inheritance.SPermission.PermissionManager;
import org.servera.inheritance.User;
import org.servera.inheritance.UserManager;
import org.servera.inheritance.auth.AuthListener;
import org.servera.inheritance.auth.SessionManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.*;

import static org.servera.Logger.logIsOverload;
import static org.servera.LoggerStatement.*;
import static org.servera.config.ConfigurationManager.getConfiguration;

public class Server
{
    protected static boolean Run = false;
    protected static CommandDispatcher dispatcher;
    protected static UserManager userManager;
    protected static ConfigurationManager configurationManager;
    protected static ConnectorManager connectorManager;
    protected static PermissionManager permissionManager;
    protected static ConfigurationFileManager configurationFileManager;
    protected static SessionManager sessionManager;
    protected static AuthListener authListener;
    protected static Thread serverThread;

    public static void main(String[] args)
    {
        log(null, "Server starting loading...");
        configurationFileManager = new ConfigurationFileManager();
        configurationManager = new ConfigurationManager();
        try {
            logIsOverload(null, null, (Integer) getConfiguration("config").getDataPath("log-out-date"));
        } catch (Exception e) {
            error_log(null, "Can't get access or correctly format from config.yml.");
            error_log(null, e.getMessage());
        }
        connectorManager = new ConnectorManager();

        userManager = new UserManager();

        permissionManager = new PermissionManager();
        sessionManager = new SessionManager();
        authListener = new AuthListener();
        dispatcher = new CommandDispatcher();

        /*
        * TEST - ZONE
        * */



        /*
         * TEST - ZONE
         * */

        Run = true;
        ServerExecute.run();
    }

    public static class callReboot extends Command
    {
        public callReboot(String name, List<String> permission) {
            super(name, permission);
        }
        @Override
        public boolean run(User user) {

            dispatcher.close();
            //Требуется перепись всех ядер и создание уникальных завершений, чтобы каждый поток ожидал завершения того или иного процесса. Дабы избежать крашей и ошибок системы.
            //Так же создать синхронную загрузку, чтобы один модуль дожидался загрузки другого и избежать критических ошибок и проблем.


            configurationFileManager = new ConfigurationFileManager();
            configurationManager = new ConfigurationManager();
            connectorManager = new ConnectorManager();

            userManager = new UserManager();

            permissionManager = new PermissionManager();
            authListener = new AuthListener();
            dispatcher = new CommandDispatcher();
            ServerExecute.reboot();
            return true;
        }
    }

    public static class ShutDown extends Command
    {
        public ShutDown(String name, List<String> permission) {
            super(name, permission);
        }

        @Override
        public boolean run(User user) {
            Run = false;
            dispatcher.close();
            permissionManager = null;
            userManager = null;
            connectorManager = null;
            configurationFileManager = null;
            configurationManager = null;
            return true;
        }
    }

        private static class ServerExecute
        {
            private static boolean callReboot = false;

            private static void reboot()
            {
                callReboot = true;
            }

            private static void run()
            {
                serverThread = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try(ServerSocket server = new ServerSocket(25565)) {
                            while(isRun() && !callReboot)
                            {
                                //Server thread code. Connection with client. Need override for login manager and SSLServerSocket.
                                server.setSoTimeout(150);



                            }
                            if(!isRun())
                            {
                                log(null, "Server closed.");
                            }
                        } catch (SocketTimeoutException ignore) {}
                        catch (IOException e) {
                            error_log(null, "Server stopped as crash. Trying to reboot server.");
                            error_log(null, "If you see that cause one more. Please report as that.");
                            error_log(null, "And call emergency stop the server.");
                            error_log(null, e.getMessage());
                            try {
                                dispatcher.runCommand("reboot", null, userManager.getUser("Console"));
                            } catch (CommandException ex) {
                                error_log(null, "Can't reboot server at error. Stopping thread.");
                                error_log(null, ex.getMessage());
                                System.exit(1);
                            }
                        }
                        if (callReboot)
                        {
                            callReboot = false;
                            run();
                        }
                    }
                });
                serverThread.start();
            }
    }

    public static boolean isRun()
    {
        return Run;
    }
}
