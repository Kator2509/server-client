package org.servera.commands;

import org.servera.Server;
import org.servera.inheritance.User;

import java.util.Locale;

public class PermissionCMD extends Command
{
    public PermissionCMD(String name) {
        super(name);
    }

    @Override
    public void run() {
        if(this.getArguments().get(0).toLowerCase(Locale.ROOT).equals("add")) {
            addPermission(this.getArguments().get(1), this.getArguments().get(2).toLowerCase(Locale.ROOT), this.getArguments().get(3));
        }
    }

    public void addPermission(String userName, String path, String commandName)
    {
        CommandDispatcher dispatcher = Server.getterModules.getCommandDispatcher();
        User user = Server.getterModules.getUserManager().getUser(userName);

        if (user.access(path)) {
            user.newRulePermission(path, dispatcher.getCommandMap().get(commandName));
        }
    }
}

