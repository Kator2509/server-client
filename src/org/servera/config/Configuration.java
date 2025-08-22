package org.servera.config;

import org.jetbrains.annotations.NotNull;
import org.servera.Logger;
import org.servera.Server;
import org.servera.config.FileManager.JSONParser;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

import static org.servera.LogArguments.*;

public class Configuration implements ConfigurationInterface
{
    private final String path, type;
    private Yaml yaml;
    protected Map<String, Object> data;
    protected Logger logger = new Logger(this.getClass());

    public Configuration(String path, String type)
    {
        this.yaml = new Yaml();
        this.path = Server.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(0,
                Server.class.getProtectionDomain().getCodeSource().getLocation().getPath().lastIndexOf(File.separator) + 1) + path;
        this.type = type;
        if (Objects.equals(type.toLowerCase(Locale.ROOT), "yaml")) {
            this.data = readYAML();
        } else if (Objects.equals(type.toLowerCase(Locale.ROOT), "json"))
        {
            this.data = readJSON();
        }
    }

    protected Map<String, Object> readJSON()
    {
        StringBuilder dataBuilder = new StringBuilder();
        try {
            FileReader fr = new FileReader(Server.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(0,
                    Server.class.getProtectionDomain().getCodeSource().getLocation().getPath().lastIndexOf(File.separator) + 1) + "/DataBaseConfig/DBConfig.json");
            int c;
            while ((c=fr.read()) != -1)
            {
                dataBuilder.append((char) c);
            }
        } catch (IOException e) {
            logger.writeLog(null, ERROR_LOG, e.getMessage());
        }
        return new JSONParser(dataBuilder.toString()).getJSONData();
    }

    protected Map<String, Object> readYAML() {
        FileInputStream var = null;
        try {
            var = new FileInputStream(this.path);
        } catch (FileNotFoundException e) {
            logger.writeLog(null, ERROR_LOG, e.getMessage());
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
        if (var == null && var1 == null)
        {
            logger.writeLog(null, ERROR_LOG, new ConfigException("Can't get a key list. var or var1 empty.").getMessage());
            throw new ConfigException("Can't get a key list. var or var1 empty.");
        }
        return container.split("\\.").length > 1 ? ((HashMap<String, Object>) var1).keySet().stream().toList()
                : ((HashMap<String, Object>) var).keySet().stream().toList();
    }

    @Override
    public Object getDataPath(String container) throws ConfigException
    {
        if (Objects.equals(getType(), "json"))
        {
            return this.data.get(container);
        }
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
        if (var == null && var1 == null)
        {
            logger.writeLog(null, ERROR_LOG, new ConfigException("Can't get a key list. var or var1 empty.").getMessage());
            throw new ConfigException("Can't get a key list. var or var1 empty.");
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
        if(Objects.equals(this.type, "json"))
        {
            logger.writeLog(null, WARN_LOG, new ConfigException("Trying to set data in json. Supported set only for YAML.").getMessage());
            throw new ConfigException("Trying to set data in json. Supported set only for YAML.");
        }
        else if(Objects.equals(this.type, "yaml"))
        {
            if (this.data.containsKey(container))
            {
                this.data.put(container, newData);
            }
            else
            {
                logger.writeLog(null, ERROR_LOG, new ConfigException("Keys not found.").getMessage());
            }
            DumperOptions dp = new DumperOptions();
            dp.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            this.yaml = new Yaml(dp);
            try {
                FileWriter fl = new FileWriter(this.path);
                this.yaml.dump(this.data ,fl);
                fl.close();
                logger.writeLog(null, LOG, "Change configuration data " + container + " -> " + newData);
            } catch (IOException e) {
                logger.writeLog(null, ERROR_LOG, e.getMessage());
            }
        }
        else
        {
            logger.writeLog(null, ERROR_LOG, new ConfigException("Uncorrected type. Supported type json or yaml.").getMessage());
            throw new ConfigException("Uncorrected type. Supported type json or yaml.");
        }
        return false;
    }

    public String getType() {
        return type;
    }
}
