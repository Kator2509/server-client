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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.*;

import static org.servera.LogArguments.*;

public class Server
{
    protected static boolean Run = false;
    protected static CommandDispatcher dispatcher;
    protected static UserManager userManager;
    protected static ConfigurationManager configurationManager;
    protected static ConnectorManager connectorManager;
    protected static PermissionManager permissionManager;
    protected static ConfigurationFileManager configurationFileManager;
    protected static AuthListener authListener;
    protected static Thread serverThread;
    protected static Thread dispatcherThread;
    protected static Logger logger = new Logger(Server.class);

    public static void main(String[] args)
    {
        logger.writeLog(null, LOG, "Server starting loading...");
        configurationFileManager = new ConfigurationFileManager();
        configurationManager = new ConfigurationManager();
        connectorManager = new ConnectorManager(configurationManager);

        userManager = new UserManager(connectorManager.getConnect("UserDataBase"));

        permissionManager = new PermissionManager(connectorManager.getConnect("UserDataBase"));
        authListener = new AuthListener(configurationManager);
        dispatcher = new CommandDispatcher(configurationManager.getConfiguration("language"), permissionManager, userManager, connectorManager, configurationManager);

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

            //Требуется перепись всех ядер и создание уникальных завершений, чтобы каждый поток ожидал завершения того или иного процесса. Дабы избежать крашей и ошибок системы.
            //Так же создать синхронную загрузку, чтобы один модуль дожидался загрузки другого и избежать критических ошибок и проблем.



            configurationFileManager = new ConfigurationFileManager();
            configurationManager = new ConfigurationManager();
            connectorManager = new ConnectorManager(configurationManager);

            userManager = new UserManager(connectorManager.getConnect("UserDataBase"));

            permissionManager = new PermissionManager(connectorManager.getConnect("UserDataBase"));
            authListener = new AuthListener(configurationManager);
            dispatcher = new CommandDispatcher(configurationManager.getConfiguration("language"), permissionManager, userManager, connectorManager, configurationManager);
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
            dispatcher = null;
            permissionManager = null;
            userManager = null;
            connectorManager = null;
            configurationFileManager = null;
            configurationManager = null;
            return true;
        }
    }

//    private static class ServerCommandDispatcher
//    {
//        private static boolean callReboot = false;
//
//        private static void reboot()
//        {
//            callReboot = true;
//        }
//
//        private static void run()
//        {
//            LinkedList<String> var0 = new LinkedList<>();
//            dispatcherThread = new Thread(() -> {
//                Scanner entry = new Scanner(System.in);
//                while(isRun() && !callReboot) {
//                    var0.clear();
//                    var command = "";
//                    var i = 0;
//
//                    System.out.print(userManager.getUser("Console").getFirstName() + ":~$ ");
//                    for (String arguments : entry.nextLine().split(" ")) {
//                        if (i == 0) {
//                            command = arguments;
//                            i++;
//                        } else {
//                            var0.add(arguments);
//                        }
//                    }
//
//                    try {
//                        dispatcher.runCommand(command, var0, userManager.getUser("Console"));
//                    } catch (CommandException e) {
//                        logger.writeLog(null, ERROR_LOG, "Command running with errors.");
//                        logger.writeLog(null, ERROR_LOG, e.getMessage());
//                    }
//                }
//                if (!isRun())
//                {
//                    logger.writeLog(null, LOG, "Dispatcher closed.");
//                }
//                if (callReboot)
//                {
//                    callReboot = false;
//                    run();
//                }
//            });
//            dispatcherThread.start();
//        }
//    }

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
                                logger.writeLog(null, LOG, "Server closed.");
                            }
                        } catch (SocketTimeoutException ignore) {}
                        catch (IOException e) {
                            logger.writeLog(null, ERROR_LOG, "Server stopped as crash. Trying to reboot server.");
                            logger.writeLog(null, ERROR_LOG, "If you see that cause one more. Please report as that.");
                            logger.writeLog(null, ERROR_LOG, "And call emergency stop the server.");
                            logger.writeLog(null, ERROR_LOG, e.getMessage());
                            try {
                                dispatcher.runCommand("reboot", null, userManager.getUser("Console"));
                            } catch (CommandException ex) {
                                logger.writeLog(null, ERROR_LOG, "Can't reboot server at error. Stopping thread.");
                                logger.writeLog(null, ERROR_LOG, ex.getMessage());
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
