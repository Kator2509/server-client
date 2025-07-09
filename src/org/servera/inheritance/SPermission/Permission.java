package org.servera.inheritance.SPermission;

import org.servera.commands.Command;

public class Permission
{
    private String permPath;
    private String[] alies;

    public Permission(String permPath, String[] alies)
    {
        this.permPath = permPath;
        this.alies = alies;
    }

    public boolean access(Command command)
    {

        return false;
    }

    public String getPermPath() {
        return permPath;
    }

    public String[] getAlies() {
        return alies;
    }
}
