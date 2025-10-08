package org.servera.commands;

import org.servera.DataBasePSQL.ConnectorManager;
import org.servera.Logger;
import org.servera.Server;
import org.servera.config.ConfigException;
import org.servera.config.Configuration;
import org.servera.config.ConfigurationManager;
import org.servera.inheritance.SPermission.PermissionManager;
import org.servera.inheritance.User;
import org.servera.inheritance.UserManager;

import java.util.*;

import static org.servera.LogArguments.*;
import static org.servera.inheritance.SPermission.PermissionManager.isUserHaveGroup;
import static org.servera.inheritance.SPermission.PermissionManager.isUserPermission;

public class CommandDispatcher implements Dispatcher
{
    protected Map<String, Command> commandMap = new HashMap<>();
    protected Configuration configuration;
    protected PermissionManager permissionManager;
    protected UserManager userManager;
    protected ConfigurationManager configurationManager;
    protected ConnectorManager connectorManager;
    protected ServerDispatcher dispatcher;
    protected Logger logger = new Logger(this.getClass());

    public CommandDispatcher(Configuration configuration, PermissionManager permissionManager, UserManager userManager, ConnectorManager connectorManager, ConfigurationManager configurationManager){
        this.configuration = configuration;
        this.permissionManager = permissionManager;
        this.userManager = userManager;
        this.connectorManager = connectorManager;
        this.configurationManager = configurationManager;
        if (this.permissionManager != null)
        {
            logger.writeLog(null, LOG, "Permission loaded success");
        }
        else
        {
            logger.writeLog(null, WARN_LOG, "Permission not loaded. That can cause a problem.");
        }
        registerDefault();
        this.dispatcher = new ServerDispatcher(this, this.userManager);
        logger.writeLog(null, LOG, "Loaded success.");
    }

    public CommandDispatcher(Map<String, Command> commandMap, Configuration configuration, PermissionManager permissionManager)
    {
        this.configuration = configuration;
        this.commandMap = commandMap;
        this.permissionManager = permissionManager;
        if (this.permissionManager != null)
        {
            logger.writeLog(null, LOG, "Permission loaded success");
        }
        else
        {
            logger.writeLog(null, WARN_LOG, "Permission not loaded. That can cause a problem.");
        }
        logger.writeLog(null, LOG, "Loaded success.");
    }

    private void registerDefault()
    {
        this.register(new Server.ShutDown("shutdown", new ArrayList<>(List.of("System.shutdown"))));
        this.register(new Server.callReboot("reboot", new ArrayList<>(List.of("System.reboot"))));
        this.register(new UserManager.UserCommand("user", connectorManager.getConnect("UserDataBase"),
                configurationManager.getConfiguration("DefaultParameters")));
        this.register(new PermissionManager.PermissionCMD("permission", connectorManager.getConnect("UserDataBase")));
        logger.writeLog(null, LOG,"Registered system commands.");
    }

    @Override
    public Command getCommand(String name)
    {
        return this.commandMap.get(name);
    }

    @Override
    public void register(Command command)
    {
        if(!(this.commandMap.containsKey(command.getName()) || this.commandMap.containsValue(command))) {
            this.commandMap.put(command.getName(), command);
            logger.writeLog(null, LOG, "Register command - " + command.getName());
        }
    }

    @Override
    public void runCommand(String name, LinkedList<String> args, User user) throws CommandException
    {
        if(commandMap.containsKey(name))
        {
            Command command = commandMap.get(name);
            if(!args.isEmpty()) {
                command.setArguments(args);
            }
            if(isUserPermission(user, command.getPermission().getFirst()) || isUserHaveGroup(user, command.getPermission().getFirst())) {
                if (!executeCommand(command, user))
                {
                    logger.writeLog(null, WARN_LOG, "Can't execute command - " + name);
                    try {
                        logger.writeLog(null, LOG, "FAQ - " + name + "\n" + configuration.getDataPath(foundCommand(name)));
                    } catch (ConfigException e) {
                        logger.writeLog(null, ERROR_LOG, "Can't call a config.");
                        logger.writeLog(null, ERROR_LOG, e.getMessage());
                    }
                }
            }
            else {
                logger.writeLog(null, WARN_LOG, "You don't have permission.");
            }
        } else
        {
            logger.writeLog(null, ERROR_LOG, "Can't execute command - " + name + ". Don't found the command.");
            if(!foundCommand(name).isEmpty()) {
                try {
                    logger.writeLog(null, LOG, "Maybe you mean \"" + configuration.getDataPath(foundCommand(name)) + "\"");
                } catch (ConfigException e) {
                    logger.writeLog(null, ERROR_LOG, "Can't call a message from language config. Exist a message?");
                    logger.writeLog(null, ERROR_LOG, e.getMessage());
                }
            }
        }
    }

    private String foundCommand(String name)
    {
        var temp = name;
        for(int i = name.length(); i > 0; i--)
        {
            temp = temp.substring(0, temp.length() - 1);
            for(String var: commandMap.keySet()) {
                 if (var.startsWith(temp) && !temp.isEmpty()) {
                     return var;
                 }
            }
        }
        return "";
    }

    private boolean executeCommand(Command command, User user)
    {
        logger.writeLog(null, LOG, "Executed command - " + command.getName());
        return command.run(user);
    }

    public void close()
    {
        this.dispatcher.callStop();
        this.commandMap = null;
        this.configuration = null;
        this.permissionManager = null;
        this.userManager = null;
        this.configurationManager = null;
        this.connectorManager = null;
    }

    private static class ServerDispatcher
    {
        protected Thread dispatcher_core;
        protected boolean run = true;
        protected Logger logger = new Logger(ServerDispatcher.class);

        public void callStop()
        {
            logger.writeLog(null, LOG, "Dispatcher call stop...");
            run = false;
        }

        public ServerDispatcher(Dispatcher dispatcher, UserManager userManager)
        {
            dispatcher_core = new Thread(new Runnable() {

                public boolean isRun()
                {
                    return run;
                }

                @Override
                public void run() {
                    LinkedList<String> var0 = new LinkedList<>();
                    Scanner entry = new Scanner(System.in);
                    while(isRun()) {
                        var0.clear();
                        var command = "";
                        var i = 0;

                        System.out.print(userManager.getUser("Console").getFirstName() + ":~$ ");
                        for (String arguments : entry.nextLine().split(" ")) {
                            if (i == 0) {
                                command = arguments;
                                i++;
                            } else {
                                var0.add(arguments);
                            }
                        }

                        try {
                            dispatcher.runCommand(command, var0, userManager.getUser("Console"));
                        } catch (CommandException e) {
                            logger.writeLog(null, ERROR_LOG, "Command running with errors.");
                            logger.writeLog(null, ERROR_LOG, e.getMessage());
                        }
                    }
                }
            });
            dispatcher_core.start();
        }
    }
}
