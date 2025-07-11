package org.servera.commands;

import java.util.LinkedList;

public interface CommandInterface
{
    void setArguments(LinkedList<String> arguments);
    String getName();
    LinkedList<String> getArguments();
    String getPermission();
}
