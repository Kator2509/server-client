package org.servera.config;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationManager
{
    protected Map<String, Configuration> configurationMap = new HashMap<>();
    private static final String prefix = "[ConfigurationManager]: ";

    public ConfigurationManager()
    {
        loadConfigs();
        System.out.println(prefix + "Loaded.");
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
            System.out.println(prefix + "Register configuration " + configuration.getPath());
        }
    }

    private void loadConfigs()
    {
        try {
        this.register("DataBase", new Configuration("DBConfig.yml"));
        this.register("DefaultParameters", new Configuration("System/Default.yml"));
            this.register("language",
                    new Configuration("language/" + this.getConfiguration("DefaultParameters").getDataPath("language") + ".yml"));
        } catch (ConfigException e) {
            System.out.println(prefix + "[ERROR] Can't loaded a language config.");
            System.out.println(e.getMessage());
        }
    }

    public Configuration getConfiguration(String name)
    {
        return this.configurationMap.get(name);
    }
}
