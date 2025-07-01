package org.servera.inheritance;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserManager
{
    protected Map<String, User> userMap = new HashMap<String, User>();
    private static final String prefix = "[UserManager]: ";

    public UserManager(){}

    public UserManager(Map<String, User> userMap){this.userMap = userMap;}

    public void createUser(String name)
    {
        if(!userMap.containsKey(name))
        {
            UUID uuid = UUID.randomUUID();
            userMap.put(name, new User(name, uuid));
            System.out.println(prefix + "Create new user with name - " + name + " and UUID - " + uuid);
        }
    }

    public User getUser(String name)
    {
        return userMap.get(name);
    }
}
