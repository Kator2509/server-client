package org.servera.config;

import org.servera.Logger;

import java.util.HashMap;
import java.util.Map;

import static org.servera.LogArguments.ERROR_LOG;
import static org.servera.LogArguments.LOG;
import static org.servera.LoggerStatement.error_log;
import static org.servera.LoggerStatement.log;

public class ConfigurationManager
{
    protected static Map<String, Configuration> configurationMap = new HashMap<>();

    public ConfigurationManager()
    {
        loadConfigs();
        log(null, "Loaded success.");
    }

    public ConfigurationManager(Map<String, Configuration> configurationMap)
    {
        ConfigurationManager.configurationMap = configurationMap;
    }


    public void register(String name, Configuration configuration)
    {
        if(!configurationMap.containsValue(configuration))
        {
            configurationMap.put(name, configuration);
            log(null, "Register configuration " + configuration.getPath());
        }
    }

    private void loadConfigs()
    {
        try {
            this.register("eula", new Configuration("eula.yml", "yaml"));
            this.register("DataBase", new Configuration("DataBaseConfig/DBConfig.json", "json"));
            this.register("config", new Configuration("config.yml", "yaml"));
            this.register("language",
                    new Configuration("language/" + getConfiguration("config").getDataPath("language") + ".yml", "yaml"));
        } catch (ConfigException e) {
            error_log(null, "Can't loaded a language config.");
            error_log(null, e.getMessage());
        }
    }

    public static Configuration getConfiguration(String name)
    {
        return configurationMap.get(name);
    }
}
