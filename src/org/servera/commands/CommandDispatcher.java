package org.servera.commands;

import org.servera.config.Configuration;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CommandDispatcher
{
    protected Map<String, Command> commandMap = new HashMap<>();
    private static final String prefix = "[ListenerCommandDispatcher]: ";
    protected Configuration configuration;

    public CommandDispatcher(Configuration configuration){
        this.configuration = configuration;
    }

    public CommandDispatcher(Map<String, Command> commandMap, Configuration configuration)
    {
        this.configuration = configuration;
        this.commandMap = commandMap;
    }

    public void register(Command command)
    {
        if(!(this.commandMap.containsKey(command.getName()) || this.commandMap.containsValue(command))) {
            this.commandMap.put(command.getName(), command);
            System.out.println(prefix + "Register command - " + command.getName());
        }
    }

    public void runCommand(String name, LinkedList<String> args)
    {
        if(!executeCommand(name, args))
        {
            System.out.println(prefix + "Maybe you mean \"" + foundCommand(name) + "\"");
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
                     return (String) configuration.getDataPath(var);
                 }
            }
        }
        return "";
    }

    private boolean executeCommand(String name, LinkedList<String> args)
    {
        if(commandMap.containsKey(name))
        {
            Command command = commandMap.get(name);
            command.setArguments(args);
            System.out.println(prefix + "Executed command - " + command.getName());
            return command.run();
        }
        System.out.println(prefix + "Can't execute command - " + name + ". Don't found the command.");
        return false;
    }
}
