package org.servera.config.FileManager;

import org.servera.Logger;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.servera.LogArguments.ERROR_LOG;
import static org.servera.LogArguments.WARN_LOG;

public class JSONParser
{
    protected Map<String, Object> dataMap;

    public JSONParser(String data) throws UncorrectedFormatException {
        this.dataMap = Parser.parse(data, null);
    }

    public JSONParser(String data, String key) throws UncorrectedFormatException {
        this.dataMap = Parser.parse(data, key);
    }

    public Map<String, Object> getJSONData()
    {
        return this.dataMap;
    }

    private static class Parser
    {
        protected static Map<String, Object> map = new HashMap<>();
        protected static Logger logger = new Logger(Parser.class);

        protected static Map<String, Object> parse(String data, String keys) throws UncorrectedFormatException {
            for(ArrayList<Byte> var:formatted(data.getBytes(StandardCharsets.UTF_8)))
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

                if (keyBuilder.charAt(0) == 34 && keyBuilder.charAt(keyBuilder.length() - 1) == 34) {
                    keyBuilder.deleteCharAt(keyBuilder.indexOf("\"")).deleteCharAt(keyBuilder.lastIndexOf("\""));
                }
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
                    try {
                        valueBuilder.delete(valueBuilder.lastIndexOf("\""), valueBuilder.length());
                        valueBuilder.deleteCharAt(valueBuilder.indexOf("\""));
                    }
                    catch (StringIndexOutOfBoundsException ex)
                    {
                        logger.writeLog(null, ERROR_LOG, "Uncorrected format json -> " + keyBuilder + " key extended value " + valueBuilder + " ?");
                        throw new UncorrectedFormatException("Uncorrected format json -> " + keyBuilder + " key extended value " + valueBuilder + " ?");
                    }
                }
                else if(valueBuilder.indexOf(" ") > -1)
                {
                    valueBuilder.delete(valueBuilder.indexOf(" "), valueBuilder.length());
                }
                if(!var3) {
                    if(!map.containsKey(keyBuilder.toString())) {
                        if (valueBuilder.toString().startsWith("["))
                        {
                            map.put(keyBuilder.toString(), parseArray(valueBuilder.toString()));
                        }
                        else {
                            map.put(keyBuilder.toString(), valueBuilder);
                        }
                    }
                    else
                    {
                        logger.writeLog(null, WARN_LOG, "Key already exist -> " + keyBuilder);
                        throw new RepeatException("Key already exist -> " + keyBuilder);
                    }
                }
            }
            return map;
        }

        protected static List<?> parseArray(String array)
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

        protected static List<ArrayList<Byte>> formatted(byte[] bytes) throws UncorrectedFormatException {
            var var2 = new ArrayList<Byte>();
            var var4 = new ArrayList<ArrayList<Byte>>();
            var json = 0;
            var array = 0;
            if (bytes[0] != 123 || bytes[bytes.length - 1] != 125) {
                logger.writeLog(null, ERROR_LOG, "Uncorrected format json ended on -> " + (char) (bytes[0] != 123 ? bytes[0] : bytes[bytes.length - 1]) + " ?");
                throw new UncorrectedFormatException("Uncorrected format json ended on -> " + (char) (bytes[0] != 123 ? bytes[0] : bytes[bytes.length - 1]) + " ?");
            }
            var var6 = false;
            var skip = false;
            var var3 = new StringBuilder();
            for (byte var1 : bytes)
            {
                if(var1 == 34 && !skip)
                {
                    skip = true;
                }
                else if(var1 == 34 && skip)
                {
                    skip = false;
                }

                var3.append((char) var1);
                if (!skip) {
                    if (var1 == 58 && var6) {
                        logger.writeLog(null, ERROR_LOG, "Uncorrected format json, separation containers -> " + var3 + " ?");
                    }

                    if (var1 == 58) {
                        var6 = true;
                    }
                    if (var1 == 44 && var6) {
                        var6 = false;
                        var3 = new StringBuilder();
                    } else if (var1 == 44 || var1 == 125 && !var6) {
                        logger.writeLog(null, ERROR_LOG, "Uncorrected format json, there container -> " + var3 + " ?");
                    }
                }
            }

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
                    if(!var2.isEmpty() && var2.get(var2.size() - 1) == 44)
                    {
                        var2.remove(var2.size() - 1);
                    }
                    if(!var2.isEmpty()) {
                        if (!var4.contains(new ArrayList<>(var2))) {
                            var4.add(new ArrayList<Byte>(var2));
                        }
                        else
                        {
                            logger.writeLog(null, WARN_LOG, "Uncorrected format. Found duplicate container -> " + Arrays.toString(var2.toArray()));
                            throw new RepeatException("Uncorrected format. Found duplicate container -> " + Arrays.toString(var2.toArray()));
                        }
                    }
                    var2.clear();
                }
            }
            return var4;
        }
    }
}
