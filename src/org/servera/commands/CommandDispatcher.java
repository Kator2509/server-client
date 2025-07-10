package org.servera.commands;

import org.servera.config.Configuration;
import org.servera.inheritance.SPermission.PermissionManager;
import org.servera.inheritance.User;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CommandDispatcher
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

    public boolean registerPermissionManager(PermissionManager permissionManager)
    {
        this.permissionManager = permissionManager;
        System.out.println(prefix + "Permission load.");
        return this.permissionManager != null;
    }

    public Command getCommand(String name)
    {
        return this.commandMap.get(name);
    }

    public void register(Command command)
    {
        if(!(this.commandMap.containsKey(command.getName()) || this.commandMap.containsValue(command))) {
            this.commandMap.put(command.getName(), command);
            System.out.println(prefix + "Register command - " + command.getName());
        }
    }

    public void runCommand(String name, LinkedList<String> args, User user)
    {
        Command command = commandMap.get(name);
        if(!args.isEmpty()) {
            command.setArguments(args);
        }
        if(commandMap.containsKey(name))
        {
            if(this.permissionManager.isUserPermission(user, command.getPermission()) || this.permissionManager.isUserHaveGroup(user, command.getPermission())) {
                if (!executeCommand(command))
                {
                    System.out.println(prefix + "Can't execute command - " + name + ".");
                }
            }
            else {
                System.out.println(prefix + "You don't have permission.");
            }
        }else
        {
            System.out.println(prefix + "[ERROR] Can't execute command - " + name + ". Don't found the command.");
            if(!foundCommand(name).isEmpty()) {
                System.out.println(prefix + "Maybe you mean \"" + configuration.getDataPath(foundCommand(name)) + "\"");
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

    private boolean executeCommand(Command command)
    {
        System.out.println(prefix + "Executed command - " + command.getName());
        return command.run();
    }
}
