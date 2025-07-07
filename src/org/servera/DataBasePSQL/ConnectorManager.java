package org.servera.DataBasePSQL;

import java.util.HashMap;
import java.util.Map;

public class ConnectorManager
{
    protected Map<String, Connector> connectorMap = new HashMap<>();
    private static final String prefix = "[DataBaseManager]: ";

    public ConnectorManager(){}

    public ConnectorManager(Map<String, Connector> connectorMap)
    {
        this.connectorMap = connectorMap;
    }

    public void register(String name, Connector connector)
    {
        if(!connectorMap.containsValue(connector))
        {
            connectorMap.put(name, connector);
            System.out.println(prefix + "Created new connection - " + name);
        }
    }

    public Connector getConnect(String name)
    {
        if (this.connectorMap.containsKey(name))
        {
            return this.connectorMap.get(name);
        }
        System.out.println(prefix + "DataBase don't found - " + name + " That can be cause a problem.");
        return null;
    }
}
