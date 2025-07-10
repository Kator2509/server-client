package org.servera.commands;

import org.servera.DataBasePSQL.Connector;
import org.servera.Server;
import org.servera.inheritance.User;
import org.servera.inheritance.UserManager;

import java.util.Locale;

public class PermissionCMD extends Command
{

    public PermissionCMD(String name, String permission) {
        super(name, permission);
    }

    @Override
    public boolean run() {
        if(this.getArguments().get(0).toLowerCase(Locale.ROOT).equals("add")) {
            addPermission(this.getArguments().get(1), this.getArguments().get(2).toLowerCase(Locale.ROOT), this.getArguments().get(3));
            return true;
        }
        return false;
    }

    private void addPermission(String userName, String path, String permissionName)
    {
        User user = Server.getterModules.getUserManager().getUser(userName);


    }

    private void deletePermission()
    {

    }

    private void resetPermission()
    {

    }


}

