package org.servera.inheritance;

import org.servera.DataBasePSQL.Connector;
import org.servera.Logger;
import org.servera.commands.Command;
import org.servera.config.ConfigException;
import org.servera.config.Configuration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static org.servera.LogArguments.*;
import static org.servera.inheritance.SPermission.PermissionManager.isUserPermission;

public class UserManager
{
    protected static Map<String, User> userMap = new HashMap<String, User>();
    protected static Logger logger = new Logger(UserManager.class);
    protected Connector connector;

    public UserManager(Connector connector){
        try {
            this.connector = connector;
            loadUsers();
            logger.writeLog(null, LOG, "Loaded success.");
        } catch (SQLException e)
        {
            logger.writeLog(null, ERROR_LOG, "Loaded with errors...");
        }
    }

    private void loadUsers() throws SQLException
    {
        this.connector.openConnection(connection ->
        {
            try {
                Statement var = connection.createStatement();
                String request = "select UUID, TAB_NUM, FIRSTNAME, SECONDNAME, \"group\" from us_users";

                var.execute(request);
                ResultSet rs = var.getResultSet();

                while(rs.next())
                {
                    userMap.put(rs.getString(3), new User(UUID.fromString(rs.getString(1)),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4)));
                }

            } catch (SQLException e) {
                logger.writeLog(null, ERROR_LOG, "Can't load a user data base.");
            }
        });
    }

    public User getUser(String name)
    {
        if (userMap.containsKey(name))
        {
            throw new UnknowUser("Unknow user trying to login -> " + name);
        }
        return userMap.get(name);
    }

    public static class UserCommand extends Command
    {
        protected Connector connector;
        private final Random random = new Random();
        private boolean success = false;
        protected Configuration configuration;

        public UserCommand(String name, Connector connector, Configuration configuration) {
            super(name, new ArrayList<>(List.of("user", "user.remove", "user.update", "user.create")));
            this.configuration = configuration;
            this.connector = connector;
        }

        @Override
        public boolean run(User user) {
            success = false;
            this.connector.openConnection(connection -> {
                if (!this.getArguments().isEmpty()) {
                    if (this.getArguments().getFirst().toLowerCase(Locale.ROOT).equals("remove")) {
                        if (!(this.getArguments().size() < 2) && isUserPermission(user, "user.remove")) {
                            removeUser(this.getArguments().get(1));
                            success = true;
                        }
                    } else if (this.getArguments().getFirst().toLowerCase(Locale.ROOT).equals("update")) {
                        if (!(this.getArguments().size() < 2) && isUserPermission(user, "user.update")) {
                            callUpdateUser(this.getArguments().get(1));
                            success = true;
                        }
                    } else if (this.getArguments().getFirst().toLowerCase(Locale.ROOT).equals("create")) {
                        if (!(this.getArguments().size() < 2) && isUserPermission(user, "user.create")) {
                            if (this.getArguments().size() == 4) {
                                createUser(this.getArguments().get(1), new String[]{this.getArguments().get(2), this.getArguments().get(3)});
                            } else {
                                createUser(this.getArguments().get(1), new String[]{});
                            }
                            success = true;
                        }
                    } else if(this.getArguments().getFirst().toLowerCase(Locale.ROOT).equals("grant"))
                    {
                        if(!(this.getArguments().size() < 2) && isUserPermission(user, "user.grant"))
                        {
                            if(this.getArguments().size() == 4) {
                                success = grantUser(this.getArguments().get(2), this.getArguments().get(3), this.getArguments().get(1));
                            }
                        }
                    }
                }
            });
            return success;
        }

        private void createUser(String name, String[] arguments)
        {
            this.connector.openConnection((connection) -> {
                try {
                    String var2 = "SELECT count(*) FROM us_users WHERE firstname = '" + name + "'";
                    Statement var3 = connection.createStatement();
                    var3.execute(var2);
                    ResultSet rs = var3.getResultSet();
                    rs.next();
                    if(rs.getInt(1) == 0)
                    {
                        UUID uuid = UUID.randomUUID();
                        Integer tab = generateTab(connection);
                        String var1 = "INSERT INTO us_users (uuid, tab_num, firstname, secondname, dcre, \"group\") VALUES ('" + uuid +
                                "', 'T-" + tab + "', '" + name + "' " + (arguments.length == 2 ? ", '" + Arrays.stream(arguments).toList().getFirst() + "'" : ", null") +
                                ", now()" + ((arguments.length > 0) ? ", '" + Arrays.stream(arguments).toList().getLast() + "'" : ", null") + ")";
                        Statement var = connection.createStatement();
                        var.execute(var1);
                        userMap.put(name, new User(uuid, String.valueOf(tab), name));
                        var.close();
                        logger.writeLog(null, LOG, "User " + name + " created.");
                    }
                    else
                    {
                        logger.writeLog(null, LOG,"User " + name + " exists.");
                    }
                } catch (SQLException e) {
                    logger.writeLog(null, ERROR_LOG, "Can't create a user - " + name);
                    logger.writeLog(null, ERROR_LOG, e.getMessage());
                }
            });
        }

        private void callUpdateUser(String name)
        {
            this.connector.openConnection(connection ->
            {
                try{
                    Statement var = connection.createStatement();
                    String request = "SELECT UUID, TAB_NUM, FIRSTNAME, SECONDNAME, \"group\" FROM US_USERS WHERE FIRSTNAME = '" + name + "'";

                    var.execute(request);

                    ResultSet rs = var.getResultSet();
                    rs.next();

                    if(rs.getRow() > 0 && userMap.containsKey(name))
                    {
                        userMap.remove(name);
                        userMap.put(rs.getString(3), new User(UUID.fromString(rs.getString(1)),
                                rs.getString(2),
                                rs.getString(3),
                                rs.getString(4)));
                        logger.writeLog(null, LOG, "User " + name + " updated.");
                    }
                    else
                    {
                        logger.writeLog(null, LOG, "User " + name + " not found.");
                    }
                } catch (SQLException e)
                {
                    logger.writeLog(null, ERROR_LOG, "Can't update a user - " + name);
                    logger.writeLog(null, ERROR_LOG, e.getMessage());
                }
            });
        }

        private void removeUser(String name)
        {
            this.connector.openConnection(connection ->
            {
                try{
                    Statement var = connection.createStatement();
                    String request = "SELECT count(*) FROM US_USERS WHERE FIRSTNAME = '" + name + "'";

                    var.execute(request);
                    ResultSet rs = var.getResultSet();
                    rs.next();

                    if(rs.getInt(1) > 0 && userMap.containsKey(name))
                    {
                        var.execute("DELETE FROM US_USERS WHERE FIRSTNAME = '" + name + "'");
                        userMap.remove(name);

                        logger.writeLog(null, LOG, "User " + name + " deleted.");
                    }
                    else
                    {
                        logger.writeLog(null, LOG, "User " + name + " not found.");
                    }
                } catch (SQLException e)
                {
                    logger.writeLog(null, ERROR_LOG, "Can't delete user " + name);
                    logger.writeLog(null, ERROR_LOG, e.getMessage());
                }
            });
        }

        static boolean granted;
        private boolean grantUser(String name, String permission, String attribute)
        {
            this.connector.openConnection(connection ->
            {
                granted = false;
                try {
                    var var1 = connection.createStatement();
                    var1.execute("select index from perm where LOWER(permission) = LOWER('" + permission + "')");

                    var rs = var1.getResultSet();
                    rs.next();

                    if(rs.getRow() > 0 && Objects.equals(attribute, "add"))
                    {
                        var var2 = connection.createStatement();
                        var2.execute("select count(*) from us_perm where us_tab = (select tab_num from us_users where LOWER(firstname) = LOWER('" + name + "')) and us_permission = '" + rs.getInt(1) + "'");

                        var rs1 = var2.getResultSet();

                        rs1.next();

                        if(rs1.getInt(1) > 0)
                        {
                            logger.writeLog(null, LOG, "User " + name + " already have permission "+ permission);
                            granted = true;
                            return;
                        }
                    }

                    if (Objects.equals(attribute, "add")) {
                        if(rs.getRow() > 0)
                        {
                            var1.execute("insert into us_perm (us_tab, us_permission, dcre) values ((select tab_num from us_users where LOWER(firstname) = LOWER('" + name + "')), '" + rs.getInt(1) + "', now())");
                            logger.writeLog(null, LOG, "User " + name + " granted permission " + permission);
                        }
                        else
                        {
                            logger.writeLog(null, WARN_LOG, "Don't found a permission " + permission);
                        }
                        granted = true;
                    }
                    else if(Objects.equals(attribute, "remove"))
                    {
                        if(rs.getRow() > 0)
                        {
                            var1.execute("delete from us_perm where us_tab = (select tab_num from us_users where LOWER(firstname) = LOWER('" + name + "')) and us_permission = '" + rs.getInt(1) + "';");
                            logger.writeLog(null, LOG, "User " + name + " removed permission " + permission);
                        }
                        else
                        {
                            logger.writeLog(null, LOG, "Don't found a permission " + permission);
                        }
                        granted = true;
                    }
                } catch (SQLException e) {
                    logger.writeLog(null, ERROR_LOG, e.getMessage());
                }
            });
            return granted;
        }

        private Integer generateTab(Connection connection) throws SQLException
        {
            var var2 = 0;
            ResultSet rs;
            do {
                try {
                    var2 = (int) (100 + (random.nextDouble() * 2.0) * (Math.pow(10, (int) this.configuration.getDataPath("max-size-tab")) - 100));
                } catch (ConfigException e) {
                    logger.writeLog(null, ERROR_LOG, "Can't call a Default config.");
                    var2 = (int) (100 + (random.nextDouble() * 2.0) * (Math.pow(10, 7 - 100)));
                }
                Statement var1 = connection.createStatement();

                String request = "Select count(*) from us_users where tab_num like '%" + var2 + "%'";

                var1.execute(request);
                rs = var1.getResultSet();
                rs.next();
            } while (rs.getInt(1) > 0);
            return var2;
        }
    }
}
