package org.servera;

import org.servera.config.ConfigException;

import java.util.ArrayList;
import java.util.HashMap;

import static org.servera.LogArguments.*;
import static org.servera.Logger.writeLog;
import static org.servera.config.ConfigurationManager.getConfiguration;

public abstract class LoggerStatement
{
    public static void error_log(String name, String message){
        writeLog(name, ERROR_LOG, message);
    }

    public static void error_log(String name, ArrayList<String> mapLog){
        var map = new HashMap<String, String>();

        for(String var1:mapLog)
        {
            map.put(ERROR_LOG, var1);
        }

        writeLog(name, map);
        map.clear();
    }

    public static void log(String name, String message){
        writeLog(name, LOG, message);
    }

    public static void log(String name, ArrayList<String> mapLog){
        var map = new HashMap<String, String>();

        for(String var1:mapLog)
        {
            map.put(LOG, var1);
        }

        writeLog(name, map);
        map.clear();
    }

    public static void warn_log(String name, String message){
        writeLog(name, WARN_LOG, message);
    }

    public static void warn_log(String name, ArrayList<String> mapLog){
        var map = new HashMap<String, String>();

        for(String var1:mapLog)
        {
            map.put(WARN_LOG, var1);
        }

        writeLog(name, map);
        map.clear();
    }

    public static void debug_log(String name, String message){
        try {
            if(Boolean.parseBoolean(getConfiguration("config").getDataPath("debug").toString())) {
                writeLog(name, DEBUG, message);
            }
        } catch (ConfigException e)
        {
            warn_log(null, "Debug log can be not fully.");
        }
    }

    public static void debug_log(String name, ArrayList<String> mapLog){
        try {
            if(Boolean.parseBoolean(getConfiguration("config").getDataPath("debug").toString())) {
                var map = new HashMap<String, String>();

                for (String var1 : mapLog) {
                    map.put(DEBUG, var1);
                }

                writeLog(name, map);
                map.clear();
            }
        } catch (ConfigException e)
        {
            warn_log(null, "Debug log can be not fully.");
        }
    }
}
