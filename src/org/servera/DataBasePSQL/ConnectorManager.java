package org.servera.DataBasePSQL;

import org.servera.Logger;
import org.servera.config.ConfigException;
import org.servera.config.ConfigurationManager;

import java.util.HashMap;
import java.util.Map;

import static org.servera.LogArguments.ERROR_LOG;
import static org.servera.LogArguments.LOG;
import static org.servera.LoggerStatement.error_log;
import static org.servera.LoggerStatement.log;
import static org.servera.config.ConfigurationManager.getConfiguration;

public class ConnectorManager
{
    protected Map<String, Connector> connectorMap = new HashMap<>();
    protected ConfigurationManager configurationManager;

    public ConnectorManager(ConfigurationManager configurationManager){
        this.configurationManager = configurationManager;
        loadConfig();
    }

    public ConnectorManager(Map<String, Connector> connectorMap, ConfigurationManager configurationManager)
    {
        this.configurationManager = configurationManager;
        this.connectorMap = connectorMap;
        loadConfig();
    }

    public void register(String name, Connector connector)
    {
        if(!connectorMap.containsValue(connector))
        {
            connectorMap.put(name, connector);
            log(null, "Created new connection - " + name);
        }
    }

    private void loadConfig()
    {
        try {
            this.register("UserDataBase", new Connector(
                    getConfiguration("DataBase").getDataPath("UserDataBase.login").toString(),
                    getConfiguration("DataBase").getDataPath("UserDataBase.password").toString(),
                    getConfiguration("DataBase").getDataPath("UserDataBase.url").toString()));
            log(null, "Loaded success.");
        } catch (ConfigException e) {
            error_log(null, "Loaded with errors.");
            error_log(null, e.getMessage());
        }
    }

    public Connector getConnect(String name)
    {
        if (this.connectorMap.containsKey(name))
        {
            return this.connectorMap.get(name);
        }
        error_log(null, "DataBase don't found - " + name + ". That can be cause a problem.");
        return null;
    }
}
