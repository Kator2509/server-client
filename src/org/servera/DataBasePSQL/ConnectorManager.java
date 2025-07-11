package org.servera.DataBasePSQL;

import org.servera.config.ConfigurationManager;
import org.servera.config.FileManager.JSONParser;

import java.util.HashMap;
import java.util.Map;

public class ConnectorManager
{
    protected Map<String, Connector> connectorMap = new HashMap<>();
    private static final String prefix = "[DataBaseManager]: ";
    protected ConfigurationManager configurationManager;

    public ConnectorManager(ConfigurationManager configurationManager){
        this.configurationManager = configurationManager;
        loadConfig();
        System.out.println(prefix + "Loaded.");
    }

    public ConnectorManager(Map<String, Connector> connectorMap, ConfigurationManager configurationManager)
    {
        this.configurationManager = configurationManager;
        this.connectorMap = connectorMap;
        loadConfig();
        System.out.println(prefix + "Loaded.");
    }

    public void register(String name, Connector connector)
    {
        if(!connectorMap.containsValue(connector))
        {
            connectorMap.put(name, connector);
            System.out.println(prefix + "Created new connection - " + name);
        }
    }

    private void loadConfig()
    {
        this.register("UserDataBase",
                new Connector(
                        JSONParser.getData(this.configurationManager.getConfiguration("DataBase").getDataPath("DataBase.UserDataBase").toString(), "login").toString(),
                        JSONParser.getData(this.configurationManager.getConfiguration("DataBase").getDataPath("DataBase.UserDataBase").toString(), "password").toString(),
                        JSONParser.getData(this.configurationManager.getConfiguration("DataBase").getDataPath("DataBase.UserDataBase").toString(), "url").toString()
                ));
    }

    public Connector getConnect(String name)
    {
        if (this.connectorMap.containsKey(name))
        {
            return this.connectorMap.get(name);
        }
        System.out.println(prefix + "[ERROR] DataBase don't found - " + name + ". That can be cause a problem.");
        return null;
    }
}
