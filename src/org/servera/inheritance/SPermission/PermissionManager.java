package org.servera.inheritance.SPermission;

import org.servera.DataBasePSQL.Connector;
import org.servera.commands.CommandDispatcher;
import org.servera.inheritance.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PermissionManager
{
    protected CommandDispatcher dispatcher;
    private final Connector connector;
    private static final String prefix = "[PermissionManager]: ";

    public PermissionManager(Connector connector, CommandDispatcher dispatcher)
    {
        this.connector = connector;
        this.dispatcher = dispatcher;
    }

    public boolean isUserPermission(User user, String path)
    {
        var ref = new Object() {
            boolean result = false;
        };
        this.connector.openConnection(connection ->
        {
            try {
                Statement var = connection.createStatement();
                var.execute("SELECT count(*) FROM perm WHERE permission = '" + path + "'");
                ResultSet rs = var.getResultSet();
                rs.next();
                if(rs.getInt(1) > 0)
                {
                    int index = path.lastIndexOf('.');
                    var.execute("SELECT count(*) FROM us_perm WHERE us_tab = '" + user.getTab() + "' AND us_permission in (select index from perm where permission = '" +
                            path + "' OR permission = '" + path.substring(0,index) + ".*')");
                    rs = var.getResultSet();
                    rs.next();
                    if(rs.getInt(1) > 0)
                    {
                        ref.result = true;
                    }
                }
                else
                {
                    System.out.println(prefix + "Permission don't found. Permission already registered?");
                    ref.result = false;
                }
            } catch (SQLException e) {
                System.out.println(prefix + "[ERROR] Can't send the request to data base.");
            }
        });
        return ref.result;
    }

    public boolean isUserHaveGroup(User user, String path)
    {
        var ref = new Object() {
            boolean result = false;
        };
        this.connector.openConnection(connection ->
        {
            try {
                Statement var = connection.createStatement();
                var.execute("SELECT \"group\" FROM us_users WHERE uuid = '" + user.getUUID() + "'");
                ResultSet rs = var.getResultSet();
                rs.next();
                var temp = rs.getString(1);
                if(rs.getString(1) != null)
                {
                    int index = path.lastIndexOf('.');
                    var.execute("SELECT count(*) FROM perm WHERE permission = '" + path + "'");
                    rs = var.getResultSet();
                    rs.next();

                    if(rs.getInt(1) > 0)
                    {
                        var.execute("SELECT count(*) FROM us_gr_permission WHERE sg_group = '"
                                + temp + "' AND sg_permission in (select index from perm where permission = '"
                                + path + "' or permission = '"
                                + path.substring(0,index) + ".*')");
                        rs = var.getResultSet();
                        rs.next();
                        if(rs.getInt(1) > 0)
                        {
                            ref.result = true;
                        }
                    } else
                    {
                        System.out.println(prefix + "Permission don't found. Permission already registered?");
                        ref.result = false;
                    }
                }
                else {
                    ref.result = false;
                }
            } catch (SQLException e) {
                System.out.println(prefix + "[ERROR] Can't send the request to data base.");
                e.printStackTrace();
            }
        });
        return ref.result;
    }
}
