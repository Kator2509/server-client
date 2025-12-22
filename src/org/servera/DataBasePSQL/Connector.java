package org.servera.DataBasePSQL;

import org.servera.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.servera.LogArguments.ERROR_LOG;
import static org.servera.LogArguments.WARN_LOG;
import static org.servera.LoggerStatement.error_log;
import static org.servera.LoggerStatement.warn_log;

public class Connector
{
    private final String login;
    private final String password;
    private final String url;
    protected Connection connection;

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
            error_log(null, "Can't create a connection. That can cause a problem.");
            return null;
        }
    }

    public void openConnection(ExecuteConnector executeConnector)
    {
        this.connection = getConnection();
        if(!testConnect()){
            warn_log(null, "Connection is not open. That can cause a problem -> " + this.url);
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

    private void closeConnection()
    {
        try {
            this.connection.close();
        } catch (SQLException e) {
            error_log(null, "Can't close connection.");
            error_log(null, e.getMessage());
        }
    }
}
