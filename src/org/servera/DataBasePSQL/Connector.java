package org.servera.DataBasePSQL;

public abstract class Connector
{
    private final String login;
    private final String password;
    private final String address;
    private final String port;

    public Connector(String login, String password, String address, String port)
    {
        this.login = login;
        this.password = password;
        this.address = address;
        this.port = port;
    }

    public abstract void createConnection();

    public boolean closeConnection()
    {

        return false;
    }

    public boolean openConnection()
    {

        return false;
    }
}
