package org.servera.config.FileManager;

import java.util.Arrays;
import java.util.HashMap;
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
                if (Objects.equals(iterator.next().replace(" ", ""), container))
                {
                    return iterator.next();
                }
            }
        }
        return null;
    }

    public static Object getAllData(String data)
    {
        var map = new HashMap<String, Object>();
        data = data.substring(1, data.lastIndexOf('}'));
        var temp = data.split(",");
        for(String var : temp)
        {

        }


        /*
        {
            "action": "command",
            "send": {"1": "user", "2":  "add", "3":  "test"}
        }
        * */

        //if data contains another json -> create dataName + dataNameJson. Example:
        //send.1 -> user; send.2 -> add; send.3 -> test
        //action -> command
        return null;
    }

    protected boolean containJSON(String data)
    {

        return false;
    }
}
