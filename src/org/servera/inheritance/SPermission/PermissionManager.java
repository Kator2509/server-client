package org.servera.inheritance.SPermission;

import org.servera.DataBasePSQL.Connector;
import org.servera.Logger;
import org.servera.commands.Command;
import org.servera.inheritance.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.servera.LogArguments.*;

public class PermissionManager
{
    private static Connector connector;
    private static boolean result = false;
    protected static Logger logger = new Logger(PermissionManager.class);

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
                var.execute("SELECT count(*) FROM perm WHERE LOWER(permission) = LOWER('" + path + "') and enable = true");
                ResultSet rs = var.getResultSet();
                rs.next();
                if(rs.getInt(1) > 0)
                {
                    int index = path.lastIndexOf('.');
                    var.execute("SELECT count(*) FROM us_perm WHERE us_tab = '" + user.getTab() + "' AND us_permission in (select index from perm where " +
                            "(LOWER(permission) = LOWER('" + path + "') " +
                            "OR LOWER(permission) = LOWER('" + (path.contains(".") ? path.substring(0,index) : path) + ".*')) and enable = true)");
                    rs = var.getResultSet();
                    rs.next();
                    if(rs.getInt(1) > 0)
                    {
                        result = true;
                    }
                }
                else
                {
                    logger.writeLog(null, WARN_LOG, "Permission don't found. Permission already registered?");
                    result = false;
                }
            } catch (SQLException e) {
                logger.writeLog(null, ERROR_LOG, "Can't send the request to data base.");
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
                    var index = path.lastIndexOf('.');
                    var.execute("SELECT count(*) FROM perm WHERE LOWER(permission) = LOWER('" + path + "')");
                    rs = var.getResultSet();
                    rs.next();

                    if(rs.getInt(1) > 0)
                    {
                        var.execute("SELECT count(*) FROM us_gr_permission WHERE sg_group = '"
                                + temp + "' AND sg_permission in (select index from perm where LOWER(permission) = LOWER('"
                                + path + "') or LOWER(permission) = LOWER('"
                                + path.substring(0,index) + ".*') and enable = true) and enable = true");
                        rs = var.getResultSet();
                        rs.next();
                        if(rs.getInt(1) > 0)
                        {
                            result = true;
                        }
                    } else
                    {
                        logger.writeLog(null, ERROR_LOG, "Permission don't found. Permission already registered?");
                        result = false;
                    }
                }
                else {
                    result = false;
                }
            } catch (SQLException e) {
                logger.writeLog(null, ERROR_LOG, "Can't send the request to data base.");
                logger.writeLog(null, ERROR_LOG, e.getMessage());
            }
        });
        return result;
    }

    public static class PermissionCMD extends Command
    {
        protected Connector connector;
        private boolean success;

        public PermissionCMD(String name, Connector connector) {
            super(name, new ArrayList<>(List.of("permission.create", "permission.remove", "permission.toggle")));
            this.connector = connector;
        }

        @Override
        public boolean run(User user) {
            success = false;
            if(!(this.getArguments().isEmpty())) {
                if (this.getArguments().getFirst().toLowerCase(Locale.ROOT).equals("remove")) {
                    return removePermission(this.getArguments().get(1).toLowerCase(Locale.ROOT));
                } else if (this.getArguments().getFirst().toLowerCase(Locale.ROOT).equals("create")) {
                    return createPermission(this.getArguments().get(1).toLowerCase(Locale.ROOT));
                } else if (this.getArguments().getFirst().toLowerCase(Locale.ROOT).equals("toggle")) {
                    return togglePermission(this.getArguments().get(1).toLowerCase(Locale.ROOT));
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
                    logger.writeLog(null, LOG, "Created new permission - " + name);
                    success = true;
                } catch (SQLException e) {
                    logger.writeLog(null, ERROR_LOG, "Can't created new permission.");
                    logger.writeLog(null, ERROR_LOG, e.getMessage());
                }
            });
            return success;
        }

        private boolean removePermission(String name)
        {
            this.connector.openConnection(connection ->
            {
                try {
                    connection.createStatement().execute("DELETE FROM perm WHERE LOWER(\"permission\") = LOWER('" + name + "')");
                    logger.writeLog(null, LOG, "Removed permission - " + name);
                    success = true;
                } catch (SQLException e) {
                    logger.writeLog(null, ERROR_LOG, "Can't found permission.");
                    logger.writeLog(null, ERROR_LOG, e.getMessage());
                }
            });
            return success;
        }

        private boolean togglePermission(String name)
        {
            this.connector.openConnection(connection ->
            {
                try {
                    Statement var = connection.createStatement();
                    var.execute("select enable from perm where LOWER(permission) = LOWER('" + name + "')");

                    ResultSet rs = var.getResultSet();
                    rs.next();

                    if (rs.getBoolean(1))
                    {
                        var.execute("update perm set enable = false where LOWER(permission) = LOWER('" + name + "')");
                        logger.writeLog(null, LOG, "Permission " + name + " disable");
                    } else
                    {
                        var.execute("update perm set enable = true where LOWER(permission) = LOWER('" + name + "')");
                        logger.writeLog(null, LOG, "Permission " + name + " enable");
                    }
                    success = true;
                } catch (SQLException e) {
                    logger.writeLog(null, ERROR_LOG, "Can't found permission.");
                    logger.writeLog(null, ERROR_LOG, e.getMessage());
                }
            });
            return success;
        }
    }
}
