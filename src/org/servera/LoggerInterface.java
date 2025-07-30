package org.servera;

import java.util.Map;

public interface LoggerInterface
{
    void writeLog(String name, String argument, String message);

    void writeLog(String name, Map<LogArguments, String> mapLog);
}
