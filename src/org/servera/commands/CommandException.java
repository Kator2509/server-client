package org.servera.commands;

public class CommandException extends Exception
{
    public CommandException()
    {
        super("Can't executed command in system. You calling command correctly?");
    }
}
