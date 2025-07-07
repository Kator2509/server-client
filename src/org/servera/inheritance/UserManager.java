package org.servera.inheritance;

import org.servera.DataBasePSQL.Connector;
import org.servera.DataBasePSQL.ConnectorManager;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class UserManager
{
    protected Map<String, User> userMap = new HashMap<String, User>();
    private static final String prefix = "[UserManager]: ";

    public UserManager(){}

    public UserManager(Map<String, User> userMap){this.userMap = userMap;}

    public void createUser(String name, Connector connector, String argument)
    {
        connector.openConnection((connection) -> {
            try {
                Statement var = connection.createStatement();
                
            } catch (SQLException e) {
                System.out.println(prefix + "Error with creating user.");
            }
        });


//        if(!userMap.containsKey(name))
//        {
//            UUID uuid = UUID.randomUUID();
//            userMap.put(name, new User(name, uuid));
//            System.out.println(prefix + "Create new user with name - " + name + " and UUID - " + uuid);
//        }
    }

    public User getUser(String name)
    {
        return userMap.get(name);
    }
}
