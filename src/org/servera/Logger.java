package org.servera;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static org.servera.LogArguments.ERROR_LOG;

public class Logger
{
    protected static String pathToSystem;

    public static void logger_create_directory()
    {
        pathToSystem = Server.class.getProtectionDomain().getCodeSource().getLocation().getPath()
                .substring(0, Server.class.getProtectionDomain().getCodeSource().getLocation().getPath().lastIndexOf(File.separator) + 1) + "log" + File.separator;
        new File(pathToSystem).mkdir();
        logIsHave(null);
    }

    public static void logger_create_directory(String name)
    {
        pathToSystem = Server.class.getProtectionDomain().getCodeSource().getLocation().getPath()
                .substring(0, Server.class.getProtectionDomain().getCodeSource().getLocation().getPath().lastIndexOf(File.separator) + 1) + "log" + File.separator;
        new File(pathToSystem).mkdir();
        logIsHave(name);
    }

    public static void logger_create_directory(String customPath, String name)
    {
        pathToSystem = customPath + File.separator + "log" + File.separator;
        new File(pathToSystem).mkdir();
        logIsHave(name);
    }

    public static void writeLog(String name, String argument, String message)
    {
        System.out.println(LocalDateTime.now() + argument + " [" + Thread.currentThread().getStackTrace()[3].getClassName() + "]: " + message);
        var temp = LocalDateTime.now() + argument + " [" + Thread.currentThread().getStackTrace()[3].getClassName() + "]: " + message + "\n";
        var logName = LocalDate.now() + "_log.log";

        if (name == null) {
            try {
                var var3 = new ByteArrayInputStream(temp.getBytes(StandardCharsets.UTF_8));
                var var1 = new FileOutputStream(pathToSystem + logName, true);
                int len;
                byte[] var2 = temp.getBytes();

                while ((len = var3.read(var2)) > 0) {
                    var1.write(var2, 0, len);
                }

                var1.close();
                var3.close();
            } catch (IOException ignore) {
            }
        }
    }

    public static void writeLog(String name, Map<String , String> mapLog)
    {
        for(Map.Entry<String, String> log:mapLog.entrySet())
        {
            System.out.println(LocalDateTime.now() + log.getKey() + " [" + Thread.currentThread().getStackTrace()[2].getClassName() + "]: " + log.getValue());
            var temp = LocalDateTime.now() + log.getKey() + " [" + Thread.currentThread().getStackTrace()[2].getClassName() + "]: " + log.getValue() + "\n";
            var logName = LocalDate.now() + "_log.log";

            if (name == null)
            {
                try {
                    var var3 = new ByteArrayInputStream(temp.getBytes(StandardCharsets.UTF_8));
                    var var1 = new FileOutputStream(pathToSystem + logName, true);
                    int len;
                    byte[] var2 = temp.getBytes();

                    while((len = var3.read(var2)) > 0)
                    {
                        var1.write(var2, 0, len);
                    }

                    var1.close();
                    var3.close();
                } catch (IOException ignore){}
            }
        }
    }

    private static void logIsHave(String name)
    {
        try {
            if(Objects.equals(name, null))
            {
                var logName = LocalDate.now() + "_log.log";
                if (!new File(pathToSystem + logName).exists()) {
                    if (new File(pathToSystem + logName).createNewFile()) {
                        writeLog(null, LogArguments.LOG, "Create log file " + pathToSystem + logName);
                    }
                }
            }
            else {
                var customLogName = LocalDate.now() + name + ".log";
                if (!new File(pathToSystem + customLogName).exists()) {
                    if (new File(pathToSystem + customLogName).createNewFile()) {
                        writeLog(name, LogArguments.LOG, "Created log file " + pathToSystem + customLogName);
                    }
                }
            }
        } catch (IOException e) {
            writeLog(null, ERROR_LOG, "Can't create a log file.");
        }
    }

    public static void logIsOverload(String name, String path, int count)
    {
        var var1 = new File(path == null ? Server.class.getProtectionDomain().getCodeSource().getLocation().getPath()
                .substring(0, Server.class.getProtectionDomain().getCodeSource().getLocation().getPath().lastIndexOf(File.separator) + 1) + "log" + File.separator : path);
        var var2 = var1.listFiles();
        var var3 = new ArrayList<>();
        if(var2 != null) {
            for (File file : var2) {
                for (int i = 0; i <= count; i++) {
                    if (file.getName().contains(String.valueOf(LocalDate.now().minusDays(i))) && !var3.contains(file)) {
                        var3.add(file);
                    }
                }
            }

            for(File file : var2)
            {
                if(!var3.contains(file))
                {
                    var nameLog = file.getName();
                    if (file.delete())
                    {
                        writeLog(null, LogArguments.LOG, "Deleted old log file -> " + nameLog);
                    }
                }
            }
        }
    }
}
