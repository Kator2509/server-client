package org.servera.config;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationManager
{
    protected Map<String, Configuration> configurationMap = new HashMap<>();
    private static final String prefix = "[ConfigurationManager]: ";

    public ConfigurationManager(){}

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

    public Configuration getConfiguration(String name)
    {
        return this.configurationMap.get(name);
    }
}
