package org.servera.pkg;

import org.servera.config.FileManager.JSONParser;

import java.util.HashMap;
import java.util.Map;

public class Package
{
    private final Map<String, Container> containers = new HashMap<>();

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
            this.containerData = new JSONParser(JSON).getJSONData();
        }

        private Map<String, Object> getContainerData()
        {
            return this.containerData;
        }
    }
}
