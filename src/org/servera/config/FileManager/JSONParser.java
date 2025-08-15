package org.servera.config.FileManager;

import org.servera.Logger;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.servera.LogArguments.ERROR_LOG;

public class JSONParser
{
    protected Logger logger = new Logger(this.getClass());
    protected Map<String, Object> map = new HashMap<>();

    public JSONParser(String data){
        parse(data, null);
    }

    public JSONParser(String data, String key){
        parse(data, key);
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

    protected void parse(String data, String keys) throws RepeatExecption
    {
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
                    if(!this.map.containsKey(keyBuilder.toString())) {
                        if (valueBuilder.toString().startsWith("["))
                        {
                            this.map.put(keyBuilder.toString(), parseArray(valueBuilder.toString()));
                        }
                        else {
                            this.map.put(keyBuilder.toString(), valueBuilder);
                        }
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
    }

    protected List<?> parseArray(String array) throws UncorrectedFormatException
    {
        var var1 = 0;
        var json = 0;
        var var4 = false;
        var list = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        for (byte var2: array.getBytes(StandardCharsets.UTF_8))
        {
            if (var2 == 91)
            {
                var1++;
                if (var1 == 1)
                {
                    continue;
                }
            }
            else if(var2 == 93)
            {
                var1--;
            }
            if (var2 == 123)
            {
                json++;
            } else if (var2 == 125)
            {
                json--;
            }

            if (var2 != 32)
            {
                var4 = true;
            }
            if (var1 > 0)
            {
                if(!(var2 == 44 && var1 == 1 && json == 0) && var4)
                {
                    builder.append((char) var2);
                }
            }
            if ((var2 == 44 && var1 == 1 && json == 0) || var1 == 0)
            {
                list.add(builder);
                builder = new StringBuilder();
                var4 = false;
            }
        }
        return list;
    }

    protected List<ArrayList<Byte>> formatted(byte[] bytes) throws UncorrectedFormatException
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
