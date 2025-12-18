package org.servera;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static org.servera.LogArguments.ERROR_LOG;

public class Logger implements LoggerInterface
{
    protected Class<?> parent;
    protected String pathToSystem;

    public Logger(Class<?> parent)
    {
        this.parent = parent;
        this.pathToSystem = parent.getProtectionDomain().getCodeSource().getLocation().getPath()
                .substring(0, parent.getProtectionDomain().getCodeSource().getLocation().getPath().lastIndexOf(File.separator) + 1) + "log" + File.separator;
        new File(this.pathToSystem).mkdir();
        logIsHave(null);
    }

    public Logger(Class<?> parent, String name)
    {
        this.parent = parent;
        this.pathToSystem = parent.getProtectionDomain().getCodeSource().getLocation().getPath()
                .substring(0, parent.getProtectionDomain().getCodeSource().getLocation().getPath().lastIndexOf(File.separator) + 1) + "log" + File.separator;
        new File(this.pathToSystem).mkdir();
        logIsHave(name);
    }

    public Logger(String customPath, Class<?> parent, String name)
    {
        this.parent = parent;
        this.pathToSystem = customPath + File.separator + "log" + File.separator;
        new File(this.pathToSystem).mkdir();
        logIsHave(name);
    }

    @Override
    public void writeLog(String name, String argument, String message)
    {
        System.out.println(LocalDateTime.now() + argument + " [" + this.parent.getName().substring(this.parent.getName().lastIndexOf('.') + 1) + "]: " + message);
        var temp = LocalDateTime.now() + argument + " [" + this.parent.getName().substring(this.parent.getName().lastIndexOf('.') + 1) + "]: " + message + "\n";
        var logName = LocalDate.now() + "_log.log";

        if (name == null)
        {
            try {
                var var3 = new ByteArrayInputStream(temp.getBytes(StandardCharsets.UTF_8));
                var var1 = new FileOutputStream(this.pathToSystem + logName, true);
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

    @Override
    public void writeLog(String name, Map<LogArguments, String> mapLog)
    {
        for(Map.Entry<LogArguments, String> log:mapLog.entrySet())
        {
            System.out.println(String.valueOf(LocalDateTime.now()) + log.getKey() + " [" + this.parent.getName().substring(this.parent.getName().lastIndexOf('.') + 1) + "]: " + log.getValue());
            var temp = String.valueOf(LocalDateTime.now()) + log.getKey() + " [" + this.parent.getName().substring(this.parent.getName().lastIndexOf('.') + 1) + "]: " + log.getValue() + "\n";
            var logName = LocalDate.now() + "_log.log";

            if (name == null)
            {
                try {
                    var var3 = new ByteArrayInputStream(temp.getBytes(StandardCharsets.UTF_8));
                    var var1 = new FileOutputStream(this.pathToSystem + logName, true);
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

    private void logIsHave(String name)
    {
        try {
            if(Objects.equals(name, null))
            {
                var logName = LocalDate.now() + "_log.log";
                if (!new File(this.pathToSystem + logName).exists()) {
                    if (new File(this.pathToSystem + logName).createNewFile()) {
                        writeLog(null, LogArguments.LOG, "Create log file " + this.pathToSystem + logName);
                    }
                }
            }
            else {
                var customLogName = LocalDate.now() + name + ".log";
                if (!new File(this.pathToSystem + customLogName).exists()) {
                    if (new File(this.pathToSystem + customLogName).createNewFile()) {
                        writeLog(name, LogArguments.LOG, "Created log file " + this.pathToSystem + customLogName);
                    }
                }
            }
        } catch (IOException e) {
            writeLog(null, ERROR_LOG, "Can't create a log file.");
        }
    }

    public void logIsOverload(String name, String path, int count)
    {
        var var1 = new File(path == null ? parent.getProtectionDomain().getCodeSource().getLocation().getPath()
                .substring(0, parent.getProtectionDomain().getCodeSource().getLocation().getPath().lastIndexOf(File.separator) + 1) + "log" + File.separator : path);
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
