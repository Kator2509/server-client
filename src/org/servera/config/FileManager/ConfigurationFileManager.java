package org.servera.config.FileManager;

import org.servera.Logger;
import org.servera.Server;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.jar.JarFile;

import static org.servera.LogArguments.*;
import static org.servera.LoggerStatement.*;

public class ConfigurationFileManager
{
    private String pathToSystem;
    protected List<File> fileMap = new ArrayList<>();

    public ConfigurationFileManager()
    {
        try {
            this.pathToSystem = Server.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            load();
            if (!check_system())
            {
                log(null,"System restored.");
            }
        } catch (URISyntaxException e) {
            error_log(null, e.getMessage());
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
        } catch (IOException e) {
            error_log(null, e.getMessage());
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
            warn_log(null, "System need to override. File listener was called.");
            FileListener.restoreSystem(var1, this.pathToSystem);
            return false;
        }
        else
        {
            log(null, "System already. Continue.");
            return true;
        }
    }

    private static class FileListener
    {
        private static void restoreSystem(List<File> array, String pathToSystem) {
            for (File var : array) {
                try {
                    if (var.getPath().contains(".")) {
                        if (!var.createNewFile()) {
                            log(null, "Can't create a file - " + var.getPath());
                        }
                        log(null, "File " + var.getName() + " created. Start filling configuration.");

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
                            error_log(null, e.getMessage());
                        }
                        log(null, "Configuration " + var.getName() + " filled.");

                    } else {
                        if (!var.mkdirs()) {
                            warn_log(null, "Can't create a directory - " + var.getPath());
                        }
                    }
                } catch (IOException e) {
                    error_log(null, e.getMessage());
                }
            }
        }
    }
}
