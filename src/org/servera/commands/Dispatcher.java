package org.servera.commands;

import org.servera.inheritance.SPermission.PermissionManager;
import org.servera.inheritance.User;

import java.util.LinkedList;

public interface Dispatcher
{
    //Получить команду.
    Command getCommand(String name);
    //Зарегистрировать команду.
    void register(Command command);
    //Исполнить команду.
    void runCommand(String name, LinkedList<String> args, User user) throws CommandException;
}
