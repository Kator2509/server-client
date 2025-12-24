package org.servera.commands;

import org.servera.Server;
import org.servera.config.ConfigException;
import org.servera.inheritance.SPermission.PermissionManager;
import org.servera.inheritance.User;
import org.servera.inheritance.UserManager;

import java.util.*;

import static org.servera.DataBasePSQL.ConnectorManager.getConnect;
import static org.servera.LoggerStatement.*;
import static org.servera.config.ConfigurationManager.getConfiguration;
import static org.servera.inheritance.SPermission.PermissionManager.isUserHaveGroup;
import static org.servera.inheritance.SPermission.PermissionManager.isUserPermission;
import static org.servera.inheritance.UserManager.getUser;

public class CommandDispatcher implements Dispatcher
{
    protected Map<String, Command> commandMap = new HashMap<>();
    protected ServerDispatcher dispatcher;

    public CommandDispatcher(){
        registerDefault();
        this.dispatcher = new ServerDispatcher(this);
        log(null, "Loaded success.");
    }

    public CommandDispatcher(Map<String, Command> commandMap)
    {
        this.commandMap = commandMap;
        log(null, "Loaded success.");
    }

    private void registerDefault()
    {
        this.register(new Server.ShutDown("shutdown", new ArrayList<>(List.of("System.shutdown"))));
        this.register(new Server.callReboot("reboot", new ArrayList<>(List.of("System.reboot"))));
        this.register(new UserManager.UserCommand("user",
                getConfiguration("config")));
        this.register(new PermissionManager.PermissionCMD("permission", getConnect("UserDataBase")));
        log(null, "Registered system commands.");
    }

    @Override
    public Command getCommand(String name)
    {
        return this.commandMap.get(name);
    }

    @Override
    public void register(Command command)
    {
        if(!(this.commandMap.containsKey(command.getName()) || this.commandMap.containsValue(command))) {
            this.commandMap.put(command.getName(), command);
            log(null, "Register command - " + command.getName());
        }
    }

    @Override
    public void runCommand(String name, LinkedList<String> args, User user) throws CommandException
    {
        if(commandMap.containsKey(name))
        {
            Command command = commandMap.get(name);
            if(!args.isEmpty()) {
                command.setArguments(args);
            }
            if(isUserPermission(user, command.getPermission().getFirst()) || isUserHaveGroup(user, command.getPermission().getFirst())) {
                if (!executeCommand(command, user))
                {
                    warn_log(null, "User -> " + user.getFirstName() + ":" + user.getUUID() + " trying to send command.");
                    warn_log(null, "Can't execute command - " + name);
                    try {
                        log(null, "FAQ - " + name + "\n" + getConfiguration("language").getDataPath(foundCommand(name)));
                    } catch (ConfigException e) {
                        error_log(null, "Can't call a config.");
                        error_log(null, e.getMessage());
                    }
                }
            }
            else {
                warn_log(null, "You don't have permission.");
            }
        } else
        {
            error_log(null, "Can't execute command - " + name + ". Don't found the command.");
            if(!foundCommand(name).isEmpty()) {
                try {
                    log(null, "Maybe you mean \"" + getConfiguration("language").getDataPath(foundCommand(name)) + "\"");
                } catch (ConfigException e) {
                    error_log(null, "Can't call a message from language config. Exist a message?");
                    error_log(null, e.getMessage());
                }
            }
        }
    }

    private String foundCommand(String name)
    {
        var temp = name;
        for(int i = name.length(); i > 0; i--)
        {
            temp = temp.substring(0, temp.length() - 1);
            for(String var: commandMap.keySet()) {
                 if (var.startsWith(temp) && !temp.isEmpty()) {
                     return var;
                 }
            }
        }
        return "";
    }

    private boolean executeCommand(Command command, User user)
    {
        log(null, "Executed command -> " + command.getName());
        log(null, "By user -> " + user.getFirstName());
        return command.run(user);
    }

    public void close()
    {
        this.dispatcher.callStop();
        this.commandMap = null;
    }

    private static class ServerDispatcher
    {
        protected Thread dispatcher_core;
        protected boolean run = true;

        public void callStop()
        {
            log(null, "Dispatcher call stop...");
            run = false;
        }

        public ServerDispatcher(Dispatcher dispatcher)
        {
            dispatcher_core = new Thread(new Runnable() {

                public boolean isRun()
                {
                    return run;
                }

                @Override
                public void run() {
                    LinkedList<String> var0 = new LinkedList<>();
                    Scanner entry = new Scanner(System.in);
                    while(isRun()) {
                        var0.clear();
                        var command = "";
                        var i = 0;

                        System.out.print(getUser("Console").getFirstName() + ":~$ ");
                        for (String arguments : entry.nextLine().split(" ")) {
                            if (i == 0) {
                                command = arguments;
                                i++;
                            } else {
                                var0.add(arguments);
                            }
                        }

                        try {
                            dispatcher.runCommand(command, var0, getUser("Console"));
                        } catch (CommandException e) {
                            error_log(null, "Command running with errors.");
                            error_log(null, e.getMessage());
                        }
                    }
                }
            });
            dispatcher_core.start();
        }
    }
}
