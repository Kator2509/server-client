package org.servera.inheritance.SPermission;

import org.servera.DataBasePSQL.Connector;
import org.servera.commands.Command;
import org.servera.inheritance.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PermissionManager
{
    private static Connector connector;
    private static boolean result = false;
    private static final String prefix = "[PermissionManager]: ";

    public PermissionManager(Connector connector)
    {
        PermissionManager.connector = connector;
    }

    public static boolean isUserPermission(User user, String path)
    {
        result = false;
        PermissionManager.connector.openConnection(connection ->
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
                            path + "' OR permission = '" + (path.contains(".") ? path.substring(0,index) : path) + ".*')");
                    rs = var.getResultSet();
                    rs.next();
                    if(rs.getInt(1) > 0)
                    {
                        result = true;
                    }
                }
                else
                {
                    System.out.println(prefix + "Permission don't found. Permission already registered?");
                    result = false;
                }
            } catch (SQLException e) {
                System.out.println(prefix + "[ERROR] Can't send the request to data base.");
            }
        });
        return result;
    }

    public static boolean isUserHaveGroup(User user, String path)
    {
        result = false;
        PermissionManager.connector.openConnection(connection ->
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
                            result = true;
                        }
                    } else
                    {
                        System.out.println(prefix + "[ERROR] Permission don't found. Permission already registered?");
                        result = false;
                    }
                }
                else {
                    result = false;
                }
            } catch (SQLException e) {
                System.out.println(prefix + "[ERROR] Can't send the request to data base.");
                System.out.println(prefix + "[ERROR] " + e.getMessage());
            }
        });
        return result;
    }

    public static class PermissionCMD extends Command
    {
        protected Connector connector;
        private boolean success;

        public PermissionCMD(String name, Connector connector) {
            super(name, new ArrayList<>(List.of("permission.create", "permission.remove")));
            this.connector = connector;
        }

        @Override
        public boolean run(User user) {
            success = false;
            if(!this.getArguments().isEmpty()) {
                if (this.getArguments().getFirst().toLowerCase(Locale.ROOT).equals("remove")) {
                    return removePermission(this.getArguments().get(1).toLowerCase(Locale.ROOT));
                } else if (this.getArguments().getFirst().toLowerCase(Locale.ROOT).equals("create")) {
                    return createPermission(this.getArguments().get(1).toLowerCase(Locale.ROOT));
                }
            }
            return false;
        }

        private boolean createPermission(String name)
        {
            this.connector.openConnection(connection ->
            {
                try {
                    connection.createStatement().execute("INSERT INTO perm (\"permission\", dcre) values ('" + name + "', now())");
                    success = true;
                } catch (SQLException e) {
                    System.out.println(prefix + "[ERROR] " + e.getMessage());
                }
            });
            return success;
        }

        private boolean removePermission(String name)
        {
            this.connector.openConnection(connection ->
            {
                try {
                    connection.createStatement().execute("DELETE FROM perm WHERE \"permission\" = '" + name + "'");
                } catch (SQLException e) {
                    System.out.println(prefix + "[ERROR] " + e.getMessage());
                }
                success = true;
            });
            return success;
        }
    }
}
