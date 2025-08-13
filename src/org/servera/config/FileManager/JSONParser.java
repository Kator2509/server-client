package org.servera.config.FileManager;

import org.servera.Logger;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.servera.LogArguments.ERROR_LOG;

public class JSONParser
{
    protected Logger logger = new Logger(this.getClass());

    public JSONParser(){

    }

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

    public void getAllData(String data) {
        var map = parse(data, null);
    }

    protected Map<String, Object> parse(String data, String keys) throws RepeatExecption
    {
        var map = new HashMap<String, Object>();
        try {
            for(ArrayList<Byte> var:formatted(data.replace("\n", "").getBytes(StandardCharsets.UTF_8)))
            {
                var key = true;
                var space = true;
                var var3 = false;
                var keyBuilder = new StringBuilder();
                var valueBuilder = new StringBuilder();
                for(byte var1:var)
                {
                    if (var1 != 32 && space)
                    {
                        space = false;
                    }
                    if(var1 == 58 && key)
                    {
                        key = false;
                        space = true;
                    }
                    if (key && !space) {
                        keyBuilder.append((char) var1);
                    }
                    if (!key && !space)
                    {
                        valueBuilder.append((char) var1);
                    }
                }

                keyBuilder.deleteCharAt(keyBuilder.indexOf("\"")).deleteCharAt(keyBuilder.lastIndexOf("\""));
                if (keys != null)
                {
                    keyBuilder.insert(0, keys + ".");
                }
                if (valueBuilder.toString().startsWith("{"))
                {
                    var3 = true;
                    parse(valueBuilder.toString(), keyBuilder.toString());
                }
                else if (valueBuilder.toString().startsWith("\""))
                {
                    valueBuilder.deleteCharAt(valueBuilder.indexOf("\"")).deleteCharAt(valueBuilder.lastIndexOf("\""));
                }
                if(!var3) {
                    if(!map.containsKey(keyBuilder.toString())) {
                        map.put(keyBuilder.toString(), valueBuilder);
                    }
                    else
                    {
                        throw new RepeatExecption("Key already exist -> " + keyBuilder);
                    }
                }
            }
        } catch (UncorrectedFormatException e) {
            logger.writeLog(null, ERROR_LOG, e.getMessage());
        }
        return map;
    }

    protected static List<?> parseArray(String array) throws UncorrectedFormatException
    {
        return null;
    }

    protected static List<ArrayList<Byte>> formatted(byte[] bytes) throws UncorrectedFormatException
    {
        var var2 = new ArrayList<Byte>();
        var var4 = new ArrayList<ArrayList<Byte>>();
        var json = 0;
        var array = 0;

        for(byte var1:bytes) {
            if (var1 == 123)
            {
                json++;
                if(json == 1)
                {
                    continue;
                }
            } else if (var1 == 125)
            {
                json--;
            }
            if (var1 == 91)
            {
                array++;
            } else if (var1 == 93)
            {
                array--;
            }
            if(json > 0)
            {
                var2.add(var1);
            }
            if((var1 == 44 && json == 1 && array == 0) || json == 0)
            {
                if(!var2.isEmpty() && var2.getLast() == 44)
                {
                    var2.removeLast();
                }
                if(!var2.isEmpty()) {
                    var4.add(new ArrayList<Byte>(var2));
                }
                var2.clear();
            }
        }
        return var4;
    }
}
