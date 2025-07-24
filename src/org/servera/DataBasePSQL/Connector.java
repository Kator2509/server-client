package org.servera.DataBasePSQL;

import org.servera.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.servera.LogArguments.ERROR_LOG;
import static org.servera.LogArguments.WARN_LOG;

public class Connector
{
    private final String login;
    private final String password;
    private final String url;
    protected Connection connection;
    protected Logger logger = new Logger(this.getClass());

    public Connector(String login, String password, String url)
    {
        this.login = login;
        this.password = password;
        this.url = url;
        this.connection = null;
    }

    private Connection getConnection()
    {
        try {
            return DriverManager.getConnection(this.url, this.login, this.password);
        } catch (SQLException e) {
            logger.writeLog(null, ERROR_LOG, "Can't create a connection. That can cause a problem.");
            return null;
        }
    }

    public void openConnection(ExecuteConnector executeConnector)
    {
        this.connection = getConnection();
        if(!testConnect()){
            logger.writeLog(null, WARN_LOG, "Connection is not open. That can cause a problem - " + this.url);
        }
        executeConnector.execute(this.connection);
        closeConnection();
    }

    private boolean testConnect()
    {
        try {
            this.connection.isValid(1);
            this.connection.isClosed();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public void closeConnection()
    {
        try {
            this.connection.close();
        } catch (SQLException e) {
            logger.writeLog(null, ERROR_LOG, "Can't close connection.");
            logger.writeLog(null, ERROR_LOG, e.getMessage());
        }
    }
}
