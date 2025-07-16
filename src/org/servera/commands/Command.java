package org.servera.commands;

import org.servera.inheritance.User;
import org.servera.inheritance.UserManager;

import java.util.LinkedList;
import java.util.List;

public abstract class Command implements CommandInterface {
    private final String name;
    private LinkedList<String> arguments;
    protected List<String> permission;

    public Command(String name, List<String> permission)
    {
        this.permission = permission;
        this.name = name;
    }

    public abstract boolean run(User user);

    @Override
    public void setArguments(LinkedList<String> arguments)
    {
        if(!(arguments == null)) {
            this.arguments = arguments;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public LinkedList<String> getArguments() {
        return this.arguments;
    }

    @Override
    public List<String> getPermission()
    {
        return this.permission;
    }
}
