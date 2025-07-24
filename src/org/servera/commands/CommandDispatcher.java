package org.servera.commands;

import org.servera.Logger;
import org.servera.config.ConfigException;
import org.servera.config.Configuration;
import org.servera.inheritance.SPermission.PermissionManager;
import org.servera.inheritance.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.servera.LogArguments.*;
import static org.servera.inheritance.SPermission.PermissionManager.isUserHaveGroup;
import static org.servera.inheritance.SPermission.PermissionManager.isUserPermission;

public class CommandDispatcher implements Dispatcher
{
    protected Map<String, Command> commandMap = new HashMap<>();
    protected Configuration configuration;
    protected PermissionManager permissionManager;
    protected Logger logger = new Logger(this.getClass());

    public CommandDispatcher(Configuration configuration, PermissionManager permissionManager){
        this.configuration = configuration;
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
}
