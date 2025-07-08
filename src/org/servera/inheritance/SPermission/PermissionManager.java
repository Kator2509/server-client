package org.servera.inheritance.SPermission;

import org.servera.DataBasePSQL.Connector;
import org.servera.commands.Command;

import java.util.Map;

public class PermissionManager
{
    protected Map<Permission, Command> permissionMap;
    private Connector connector;

    public PermissionManager(Connector connector)
    {
        this.connector = connector;
        loadPermission();
    }

    private void loadPermission()
    {
        this.connector.openConnection(connection ->
        {

        });
    }

    public void createNewPermission()
    {
        this.connector.openConnection(connection ->
        {

        });
    }
}
