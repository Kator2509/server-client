package org.servera.config.FileManager;

import org.servera.Server;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Manager
{
    private static final String prefix = "[FileSystemManager]: ";

    public Manager(){}

    public void createSystemDirectory(String filePath)
    {
            if(!new File(getSystemPath(), filePath).exists())
            {
                new File(getSystemPath(), filePath).mkdirs();
                System.out.println(prefix + "Creating directory \"" + filePath + "\"");
            }
    }

    public void createSystemFile(String pathWithFile)
    {
        if(!new File(getSystemPath(), pathWithFile).exists())
        {
            try {
                new File(getSystemPath(), pathWithFile).createNewFile();
                System.out.println(prefix + "Creating file \"" + pathWithFile + "\"");
            } catch (IOException e) {
                System.out.println(prefix + "Can't creating a file \"" + pathWithFile + "\"");
                e.getStackTrace();
            }
        }
    }

    public String getSystemPath()
    {
        return new File(URLDecoder.decode(Server.class.getProtectionDomain().getCodeSource().getLocation().getPath(), StandardCharsets.UTF_8)).getParent();
    }
}
