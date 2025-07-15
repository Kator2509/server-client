package org.servera.inheritance;

import org.servera.DataBasePSQL.Connector;
import org.servera.commands.Command;
import org.servera.config.ConfigException;
import org.servera.config.Configuration;
import org.servera.inheritance.SPermission.PermissionManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class UserManager
{
    protected static Map<String, User> userMap = new HashMap<String, User>();
    private static final String prefix = "[UserManager]: ";
    protected Connector connector;

    public UserManager(Connector connector){
        try {
            this.connector = connector;
            loadUsers();
            System.out.println(prefix + "Loaded.");
        } catch (SQLException e)
        {
            System.out.println(prefix + "[ERROR] Can't load a module or loaded with errors...");
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
                System.out.println(prefix + "[ERROR] Can't load a user data base.");
            }
        });
    }

    public User getUser(String name)
    {
        return userMap.get(name);
    }

    public static class UserCommand extends Command
    {
        protected Connector connector;
        private final Random random = new Random();
        private boolean success = false;
        protected Configuration configuration;
        protected PermissionManager permissionManager;

        public UserCommand(String name, Connector connector, Configuration configuration, PermissionManager permissionManager) {
            super(name, "user.change");
            this.configuration = configuration;
            this.permissionManager = permissionManager;
            this.connector = connector;
        }

        @Override
        public boolean run() {
            this.connector.openConnection(connection -> {
                if (this.getArguments().getFirst().toLowerCase(Locale.ROOT).equals("remove")) {
                    if (!(this.getArguments().size() < 2)) {
                        removeUser(this.getArguments().get(1));
                        success = true;
                    }
                } else if (this.getArguments().getFirst().toLowerCase(Locale.ROOT).equals("update")) {
                    if (!(this.getArguments().size() < 2)) {
                        callUpdateUser(this.getArguments().get(1));
                        success = true;
                    }
                } else if (this.getArguments().getFirst().toLowerCase(Locale.ROOT).equals("create")) {
                    if (!(this.getArguments().size() < 2)) {
                        if(this.getArguments().size() >= 2) {
                            createUser(this.getArguments().get(1), new String[]{this.getArguments().get(2), this.getArguments().get(3)});
                        }
                        else
                        {
                            createUser(this.getArguments().get(1), null);
                        }
                        success = true;
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
                        String var1 = "INSERT INTO us_users (uuid, tab_num, firstname, secondname, dcre, \"group\") VALUES ('" + UUID.randomUUID() +
                                "', 'T-" + generateTab(connection) + "', '" + name + "', '" + (arguments.length == 2 ? Arrays.stream(arguments).toList().getFirst() : "null") +
                                "', now(), '" + ((arguments.length > 0) ? Arrays.stream(arguments).toList().getLast() : "null") + "')";
                        Statement var = connection.createStatement();
                        var.execute(var1);
                        var.close();
                        System.out.println(prefix + "User " + name + " created.");
                    }
                    else
                    {
                        System.out.println(prefix + "User " + name + " exists.");
                    }
                } catch (SQLException e) {
                    System.out.println(prefix + "[ERROR] Can't create a user - " + name);
                }
            });
        }

        private void callUpdateUser(String name)
        {
            this.connector.openConnection(connection ->
            {
                try{
                    Statement var = connection.createStatement();
                    String request = "SELECT UUID, TAB_NUM, FIRSTNAME, SECONDNAME, \"group\" FROM US_USERS WHERE FIRSTNAME = \"" + name + "\"";

                    var.execute(request);

                    ResultSet rs = var.getResultSet();
                    rs.next();

                    if(!rs.wasNull() && userMap.containsKey(name))
                    {
                        userMap.remove(name);
                        userMap.put(rs.getString(3), new User(UUID.fromString(rs.getString(1)),
                                rs.getString(2),
                                rs.getString(3),
                                rs.getString(4)));
                        System.out.println(prefix + "User " + name + " updated.");
                    }
                    else
                    {
                        System.out.println(prefix + " User " + name + " not found.");
                    }
                } catch (SQLException e)
                {
                    System.out.println(prefix + "[ERROR] Can't update a user - " + name);
                    System.out.println(prefix + "[ERROR] " + e.getMessage());
                }
            });
        }

        private void removeUser(String name)
        {
            this.connector.openConnection(connection ->
            {
                try{
                    Statement var = connection.createStatement();
                    String request = "SELECT count(*) FROM US_USERS WHERE FIRSTNAME = \"" + name + "\"";

                    var.execute(request);
                    ResultSet rs = var.getResultSet();

                    if(rs.getInt(1) > 0 && userMap.containsKey(name))
                    {
                        var.execute("DELETE FROM US_USERS WHERE FIRSTNAME = " + name);
                        userMap.remove(name);

                        System.out.println(prefix + "User " + name + " deleted.");
                    }
                } catch (SQLException e)
                {
                    System.out.println(prefix + "[ERROR] Can't delete user " + name);
                    System.out.println(prefix + "[ERROR] " + e.getMessage());
                }
            });
        }

        private Integer generateTab(Connection connection) throws SQLException
        {
            boolean search = false;
            int var = 0;
            do {
                try {
                    var = (int) (100 + (random.nextDouble() * 2.0) * (Math.pow(10, (int) this.configuration.getDataPath("max-size-tab")) - 100));
                } catch (ConfigException e) {
                    System.out.println(prefix + "[ERROR] Can't call a Default config.");
                    var = (int) (100 + (random.nextDouble() * 2.0) * (Math.pow(10, 7 - 100)));
                }
                Statement var1 = connection.createStatement();

                String request = "Select count(*) from us_users where tab_num like '%" + var + "%'";

                var1.execute(request);
                ResultSet rs = var1.getResultSet();
                rs.next();
                search = rs.getInt(1) > 0;
            } while (search);
            return var;
        }
    }
}
