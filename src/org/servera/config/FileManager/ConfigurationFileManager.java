package org.servera.config.FileManager;

import org.servera.Logger;
import org.servera.Server;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.jar.JarFile;

import static org.servera.LogArguments.*;

public class ConfigurationFileManager
{
    private String pathToSystem;
    protected List<File> fileMap = new ArrayList<>();
    protected Logger logger = new Logger(this.getClass());

    public ConfigurationFileManager()
    {
        try {
            this.pathToSystem = Server.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            load();
            if (!check_system())
            {
                logger.writeLog(null, LOG,"System restored.");
            }
        } catch (URISyntaxException e) {
            logger.writeLog(null, ERROR_LOG, e.getMessage());
        }
    }

    protected void load()
    {
        try {
            var fileIterator = new JarFile(new File(this.pathToSystem)).entries().asIterator();
            while (fileIterator.hasNext())
            {
                var path = String.valueOf(fileIterator.next());
                if(path.contains("System/") && !path.equals("System/")) {
                    this.fileMap.add(new File(this.pathToSystem.substring(0, this.pathToSystem.lastIndexOf(File.separator))
                            + path.substring(path.indexOf(File.separator))));
                }
            }
            this.fileMap.add(new File(this.pathToSystem.substring(0, this.pathToSystem.lastIndexOf(File.separator)) + "/plugins"));
        } catch (IOException e) {
            logger.writeLog(null, ERROR_LOG, e.getMessage());
        }
    }

    private boolean check_system()
    {
        var var1 = new ArrayList<File>();
        for(File file:this.fileMap)
        {
            if (!file.exists()) {
                var1.add(file);
            }
        }
        if (!var1.isEmpty()) {
            FileListener.restoreSystem(var1, this.pathToSystem, logger);
            logger.writeLog(null, WARN_LOG, "System need to override. File listener was called.");
            return false;
        }
        else
        {
            logger.writeLog(null, LOG, "System already. Continue.");
            return true;
        }
    }

    private static class FileListener
    {
        private static void restoreSystem(List<File> array, String pathToSystem, Logger logger) {
            for (File var : array) {
                try {
                    if (var.getPath().contains(".yml")) {
                        if (!var.createNewFile()) {
                            logger.writeLog(null, LOG, "Can't create a file - " + var.getPath());
                        }
                        logger.writeLog(null, LOG, "File " + var.getName() + " created. Start filling configuration.");

                        try {
                            var var1 = Server.class.getResourceAsStream("/System/" + var.getPath().substring(pathToSystem
                                    .substring(0, pathToSystem.lastIndexOf(File.separator)).length() + 1));

                            var var3 = new FileOutputStream(var.getPath());
                            int len;
                            byte[] var4 = new byte[1024];

                            while ((len = var1.read(var4)) > 0)
                            {
                                var3.write(var4, 0, len);
                            }

                            var3.close();
                            var1.close();
                        } catch (IOException e) {
                            logger.writeLog(null, ERROR_LOG, e.getMessage());
                        }
                        logger.writeLog(null, LOG, "Configuration " + var.getName() + " filled.");

                    } else {
                        if (!var.mkdirs()) {
                            logger.writeLog(null, WARN_LOG, "Can't create a directory - " + var.getPath());
                        }
                    }
                } catch (IOException e) {
                    logger.writeLog(null, ERROR_LOG, e.getMessage());
                }
            }
        }
    }
}
