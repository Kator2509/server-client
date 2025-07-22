package org.servera.config.FileManager;

import org.servera.Server;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.jar.JarEntry;
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
                System.out.println(prefix + "[WARN] System need to override. Calling Listener file.");
                FileListener.getSystemFiles();
            }
        } catch (URISyntaxException e) {
            System.out.println(prefix + "[ERROR] " + e.getMessage());
        }
    }

    protected void load()
    {
        try {
            Iterator<JarEntry> fileIterator = new JarFile(new File(this.pathToSystem)).entries().asIterator();
            while (fileIterator.hasNext())
            {
                var path = String.valueOf(fileIterator.next());
                if(path.contains("System/") && !path.equals("System/")) {
                    this.fileMap.add(new File(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().substring(0,
                            Server.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().lastIndexOf("/"))
                            + path.substring(path.indexOf('/'))));
                }
            }
        } catch (IOException | URISyntaxException e) {
            System.out.println(prefix + "[ERROR] " + e.getMessage());
        }
    }

    private boolean check_system()
    {
        try {
            System.out.println(Server.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());

        } catch (URISyntaxException e) {
            System.out.println(prefix + "[ERROR] " + e.getMessage());
        }
        return false;
    }

    private static class FileListener
    {
        private static List<File> getSystemFiles()
        {
            return null;
        }
    }
}
