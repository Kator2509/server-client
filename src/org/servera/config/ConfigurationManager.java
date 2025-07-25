package org.servera.config;

import org.servera.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.servera.LogArguments.ERROR_LOG;
import static org.servera.LogArguments.LOG;

public class ConfigurationManager
{
    protected Map<String, Configuration> configurationMap = new HashMap<>();
    private static final String prefix = "[ConfigurationManager]: ";
    protected Logger logger = new Logger(this.getClass());

    public ConfigurationManager()
    {
        loadConfigs();
        logger.writeLog(null, LOG, "Loaded success.");
    }

    public ConfigurationManager(Map<String, Configuration> configurationMap)
    {
        this.configurationMap = configurationMap;
    }

    public void register(String name, Configuration configuration)
    {
        if(!this.configurationMap.containsValue(configuration))
        {
            this.configurationMap.put(name, configuration);
            logger.writeLog(null, LOG, "Register configuration " + configuration.getPath());
        }
    }

    private void loadConfigs()
    {
        try {
            this.register("DataBase", new Configuration("DataBaseConfig/DBConfig.yml"));
            this.register("DefaultParameters", new Configuration("Default.yml"));
            this.register("language",
                    new Configuration("language/" + this.getConfiguration("DefaultParameters").getDataPath("language") + ".yml"));
        } catch (ConfigException e) {
            logger.writeLog(null, ERROR_LOG, "Can't loaded a language config.");
            logger.writeLog(null, ERROR_LOG, e.getMessage());
        }
    }

    public Configuration getConfiguration(String name)
    {
        return this.configurationMap.get(name);
    }
}
