package org.servera.config.FileManager;

import org.servera.Server;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.jar.JarFile;

public class ConfigurationFileManager
{
    private String pathToSystem;
    private static final String prefix = "[ConfigurationFileManager]: ";
    protected List<File> fileMap = new ArrayList<>();

    public ConfigurationFileManager()
    {
        try {
            this.pathToSystem = Server.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            load();
            if (!check_system())
            {
                System.out.println(prefix + "System restored.");
            }
        } catch (URISyntaxException e) {
            System.out.println(prefix + "[ERROR] " + e.getMessage());
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
            this.fileMap.add(new File(this.pathToSystem.substring(0, this.pathToSystem.lastIndexOf(File.separator)) + "/log"));
            this.fileMap.add(new File(this.pathToSystem.substring(0, this.pathToSystem.lastIndexOf(File.separator)) + "/plugins"));
        } catch (IOException e) {
            System.out.println(prefix + "[ERROR] " + e.getMessage());
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
            System.out.println(prefix + "[WARN] System need to override. Calling Listener file.");
            FileListener.restoreSystem(var1, this.pathToSystem);
            return false;
        }
        else
        {
            System.out.println(prefix + "System already. Continue.");
            return true;
        }
    }

    private static class FileListener
    {
        private static void restoreSystem(List<File> array, String pathToSystem) {
            for (File var : array) {
                try {
                    if (var.getPath().contains(".yml")) {
                        if (!var.createNewFile()) {
                            System.out.println(prefix + "[WARN] Can't create a file - " + var.getPath());
                        }
                        System.out.println(prefix + "File " + var.getName() + " created. Start filling configuration.");

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
                            System.out.println(prefix + "[ERROR] " + e.getMessage());
                        }
                        System.out.println(prefix + "Configuration " + var.getName() + " filled.");

                    } else {
                        if (!var.mkdirs()) {
                            System.out.println(prefix + "[WARN] Can't create a directory - " + var.getPath());
                        }
                        System.out.println(prefix + "Directory " + var.getName() + " created. Start filling configuration.");
                    }
                } catch (IOException e) {
                    System.out.println(prefix + "[ERROR] " + e.getMessage());
                }
            }
        }
    }
}
