package org.servera.config;

import org.servera.Server;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Configuration implements ConfigurationInterface
{
    private final String path;
    private final Yaml yaml = new Yaml();
    protected final Map<String, Object> data;

    public Configuration(String path)
    {
        this.path = path;
        this.data = readData();
    }

    private Map<String, Object> readData()
    {
        InputStream var = Server.class.getResourceAsStream("/" + getPath());
        return yaml.load(var);
    }

    @Override
    public Object getKeyData(String container)
    {
        return null;
    }

    @Override
    public Object getDataPath(String container)
    {
        Object var = this.data.get(container.split("\\.")[0]), var1 = null;
        int i = 1;
        if(container.split("\\.").length > 1)
        {
            do{
                var1 = ((HashMap<String, Object>) var).get(container.split("\\.")[i]);
                var = var1;
                i++;
            } while (i < container.split("\\.").length);
        }
        return container.split("\\.").length > 1 ? var1 : var;
    }

    @Override
    public String getPath()
    {
        return this.path;
    }
}
