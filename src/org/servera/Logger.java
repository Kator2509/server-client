package org.servera;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.servera.LogArguments.LOG;

public class Logger
{
    protected Class<?> parent;

    public Logger(Class<?> parent)
    {
        this.parent = parent;
    }

    public static void writeLog(String arguments, String message, Class<?> parent)
    {
        if(logIsHave(null)) {

        }
        System.out.println(LocalDateTime.now() + arguments + "[" + parent.getName().substring(parent.getName().lastIndexOf('.') + 1) + "] " + message);
    }

    private static boolean logIsHave(String name)
    {
        if(name == null)
        {
            writeLog(LOG, "Created a new log file ", Logger.class);
        }
        else {

        }
        return false;
    }
}
