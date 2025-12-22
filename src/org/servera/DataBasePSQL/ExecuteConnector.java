package org.servera.DataBasePSQL;

import java.sql.Connection;

public interface ExecuteConnector
{
    void execute(Connection connection);
}