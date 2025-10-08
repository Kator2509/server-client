package org.servera.pkg;

import org.servera.Logger;
import org.servera.config.FileManager.JSONParser;
import org.servera.config.FileManager.UncorrectedFormatException;

import java.util.HashMap;
import java.util.Map;

import static org.servera.LogArguments.ERROR_LOG;

public class Package
{
    private final Map<String, Container> containers = new HashMap<>();
    protected static final Logger logger = new Logger(Package.class);

    public Package(){
    }

    public void input(String containerName, String JSON)
    {
        this.containers.put(containerName, new Container(JSON));
    }

    public Map<String, Object> getContainerData(String containerName)
    {
        return this.containers.get(containerName).getContainerData();
    }

    private static class Container
    {
        protected Map<String, Object> containerData;

        public Container(String JSON)
        {
            try {
                this.containerData = new JSONParser(JSON).getJSONData();
            } catch (UncorrectedFormatException e) {
                logger.writeLog(null, ERROR_LOG, e.getMessage());
            }
        }

        private Map<String, Object> getContainerData()
        {
            return this.containerData;
        }
    }
}
