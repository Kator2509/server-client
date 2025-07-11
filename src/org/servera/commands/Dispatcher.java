package org.servera.commands;

import org.servera.inheritance.SPermission.PermissionManager;
import org.servera.inheritance.User;

import java.util.LinkedList;

public interface Dispatcher
{
    //Регистрация менеджера прав внутри исполнителя команд.
    boolean registerPermissionManager(PermissionManager permissionManager);
    //Получить команду.
    Command getCommand(String name);
    //Зарегистрировать команду.
    void register(Command command);
    //Исполнить команду.
    void runCommand(String name, LinkedList<String> args, User user);
}
