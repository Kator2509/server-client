package org.servera.config.FileManager;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class JSONParser
{
    public static Object getData(String data, String container)
    {
        data = data.replace("{", "").replace("}", "");
        String[] var = data.split(",");
        for(String var1 : var) {
            Iterator<String> iterator = Arrays.stream(var1.split("=")).iterator();
            if (iterator.hasNext())
            {
                if (Objects.equals(iterator.next(), container))
                {
                    return iterator.next();
                }
            }
        }
        return null;
    }
}
