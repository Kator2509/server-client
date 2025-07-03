package org.servera.inheritance;

import org.servera.DataBasePSQL.Connector;
import org.servera.config.FileManager.Manager;

import java.util.HashMap;
import java.util.Map;

public class UserManager
{
    protected Map<String, User> userMap = new HashMap<String, User>();
    private static final String prefix = "[UserManager]: ";
    private static final String defaultFolder = "User/DataUser/";

    public UserManager(){}

    public UserManager(Map<String, User> userMap){this.userMap = userMap;}

    public void createUser(String name, Manager connector, String argument)
    {


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
