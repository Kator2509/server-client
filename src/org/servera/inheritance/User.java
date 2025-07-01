package org.servera.inheritance;

import org.servera.commands.Command;

import java.util.List;
import java.util.UUID;

public class User
{
    protected List<Permission> permissions;
    private UUID uuid;
    private String name;

    public User(String name, UUID uuid)
    {
        this.name = name;
        this.uuid = uuid;
    }

    public User(List<Permission> permissions)
    {
        this.permissions = permissions;
    }

    public void newRulePermission(String path, Command command)
    {
        if(access(path))
        {
            this.permissions.add(new Permission(path, command));
        }
    }

    public boolean access(String path)
    {
        for (Permission permission : permissions) {
            if (permission.path.equals(path)) {
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return uuid;
    }

    private static class Permission
    {
        private final String path;
        private final Command command;

        public Permission(String path, Command command)
        {
            this.path = path;
            this.command = command;
        }

        public Command getCommand() {
            return command;
        }

        public String getPath() {
            return path;
        }
    }
}
