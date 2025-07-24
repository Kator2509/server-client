package org.servera.DataBasePSQL;

import org.servera.Logger;
import org.servera.config.ConfigException;
import org.servera.config.ConfigurationManager;
import org.servera.config.FileManager.JSONParser;

import java.util.HashMap;
import java.util.Map;

import static org.servera.LogArguments.ERROR_LOG;
import static org.servera.LogArguments.LOG;

public class ConnectorManager
{
    protected Map<String, Connector> connectorMap = new HashMap<>();
    protected Logger logger = new Logger(this.getClass());
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
            logger.writeLog(null, LOG, "Created new connection - " + name);
        }
    }

    private void loadConfig()
    {
        try {
            for (String var : this.configurationManager.getConfiguration("DataBase").getKeyDataList("DataBase"))
            {
                this.register(var, new Connector(
                        JSONParser.getData(this.configurationManager.getConfiguration("DataBase").getDataPath("DataBase." + var).toString(), "login").toString(),
                        JSONParser.getData(this.configurationManager.getConfiguration("DataBase").getDataPath("DataBase." + var).toString(), "password").toString(),
                        JSONParser.getData(this.configurationManager.getConfiguration("DataBase").getDataPath("DataBase." + var).toString(), "url").toString()
                ));
            }
            logger.writeLog(null, LOG, "Loaded success.");
        } catch (ConfigException e) {
            logger.writeLog(null, ERROR_LOG, "Loaded with errors.");
            logger.writeLog(null, ERROR_LOG, e.getMessage());
        }
    }

    public Connector getConnect(String name)
    {
        if (this.connectorMap.containsKey(name))
        {
            return this.connectorMap.get(name);
        }
        logger.writeLog(null, ERROR_LOG, "DataBase don't found - " + name + ". That can be cause a problem.");
        return null;
    }
}
