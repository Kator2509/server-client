package org.servera.commands;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CommandDispatcher
{
    protected Map<String, Command> commandMap = new HashMap<>();
    private static final String prefix = "[ListenerCommandDispatcher]: ";

    public CommandDispatcher(){}

    public CommandDispatcher(Map<String, Command> commandMap)
    {
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
            System.out.println(foundCommand(name));
        }
    }

    private String foundCommand(String name)
    {

        return null;
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

    public Map<String, Command> getCommandMap()
    {
        return this.commandMap;
    }
}
