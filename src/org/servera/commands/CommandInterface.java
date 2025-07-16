package org.servera.commands;

import java.util.LinkedList;
import java.util.List;

public interface CommandInterface
{
    void setArguments(LinkedList<String> arguments);
    String getName();
    LinkedList<String> getArguments();
    List<String> getPermission();
}
