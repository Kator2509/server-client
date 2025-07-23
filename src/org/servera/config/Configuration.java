package org.servera.config;

import org.jetbrains.annotations.NotNull;
import org.servera.Server;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Configuration implements ConfigurationInterface
{
    private final String path;
    private final Yaml yaml = new Yaml();
    protected final Map<String, Object> data;
    private static final String prefix = "[ConfigurationManager]: ";

    public Configuration(String path)
    {
        this.path = Server.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(0,
                Server.class.getProtectionDomain().getCodeSource().getLocation().getPath().lastIndexOf(File.separator) + 1) + path;
        this.data = readData();
    }

    private Map<String, Object> readData() {
        FileInputStream var = null;
        try {
            var = new FileInputStream(this.path);
        } catch (FileNotFoundException e) {
            System.out.println(prefix + "[ERROR] " + e.getMessage());
        }
        return yaml.load(var);
    }

    @Override
    public List<String> getKeyDataList(String container) throws ConfigException {
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
        return container.split("\\.").length > 1 ? ((HashMap<String, Object>) var1).keySet().stream().toList()
                : ((HashMap<String, Object>) var).keySet().stream().toList();
    }

    @Override
    public Object getDataPath(String container) throws ConfigException
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

    @Override
    public boolean setDataPath(@NotNull String container, @NotNull Object newData) throws ConfigException {

        return false;
    }
}
