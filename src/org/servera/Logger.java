package org.servera;

public class Logger {

    public static void sendLog(String name, String message)
    {
        LoggerListener.writeLog(name, message);
    }

    private static class LoggerListener
    {
        private static void writeLog(String name, String message)
        {

        }

        private void createLog()
        {

        }
    }
}
