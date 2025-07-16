package org.servera.commands;

import org.servera.config.ConfigException;
import org.servera.config.Configuration;
import org.servera.inheritance.SPermission.PermissionManager;
import org.servera.inheritance.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CommandDispatcher implements Dispatcher
{
    protected Map<String, Command> commandMap = new HashMap<>();
    private static final String prefix = "[ListenerCommandDispatcher]: ";
    protected Configuration configuration;
    protected PermissionManager permissionManager;

    public CommandDispatcher(Configuration configuration){
        this.configuration = configuration;
        System.out.println(prefix + "Loaded.");
    }

    public CommandDispatcher(Map<String, Command> commandMap, Configuration configuration)
    {
        this.configuration = configuration;
        this.commandMap = commandMap;
        System.out.println(prefix + "Loaded.");
    }

    @Override
    public boolean registerPermissionManager(PermissionManager permissionManager)
    {
        this.permissionManager = permissionManager;
        System.out.println(prefix + "Permission load.");
        return this.permissionManager == null;
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
            System.out.println(prefix + "Register command - " + command.getName());
        }
    }

    @Override
    public void runCommand(String name, LinkedList<String> args, User user)
    {
        if(commandMap.containsKey(name))
        {
            Command command = commandMap.get(name);
            if(!args.isEmpty()) {
                command.setArguments(args);
            }
            if(this.permissionManager.isUserPermission(user, command.getPermission().getFirst()) || this.permissionManager.isUserHaveGroup(user, command.getPermission().getFirst())) {
                if (!executeCommand(command, user))
                {
                    System.out.println(prefix + "Can't execute command - " + name + ".");
                    try {
                        System.out.println(prefix + configuration.getDataPath(foundCommand(name)));
                    } catch (ConfigException e) {
                        System.out.println(prefix + "[ERROR] Can't call a config.");
                        System.out.println(prefix + "[ERROR] " + e.getMessage());
                    }
                }
            }
            else {
                System.out.println(prefix + "You don't have permission.");
            }
        } else
        {
            System.out.println(prefix + "[ERROR] Can't execute command - " + name + ". Don't found the command.");
            if(!foundCommand(name).isEmpty()) {
                try {
                    System.out.println(prefix + "Maybe you mean \"" + configuration.getDataPath(foundCommand(name)) + "\"");
                } catch (ConfigException e) {
                    System.out.println(prefix + "[ERROR] Can't call a message from language config. Exist a message?");
                    System.out.println(prefix + "[ERROR] " + e.getMessage());
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
        System.out.println(prefix + "Executed command - " + command.getName());
        return command.run(user);
    }
}
