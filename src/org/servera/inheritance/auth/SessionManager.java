package org.servera.inheritance.auth;

import org.servera.inheritance.User;

import java.io.IOException;
import java.net.Inet4Address;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.servera.DataBasePSQL.ConnectorManager.getConnect;
import static org.servera.LoggerStatement.*;

public class SessionManager
{
    protected Map<String, Session> active_session = new HashMap<>();

    public SessionManager(){}

    public SessionManager(Map<String, Session> map) {this.active_session = map;}

    public void open_session(User user, Inet4Address address)
    {
        if(!active_session.containsValue(new Session(user, address))) {
            debug_log(null, "Active session is null. Send new request to create session.");
            if(create_session(user, address))
            {
                log(null, "Created new session with user -> " + user.getFirstName() + " and address -> " + address);
            }
            else
            {
                warn_log(null, "Connection was aborted.");
            }
        }
    }

    private boolean create_session(User user, Inet4Address address)
    {
        try {
            var session = new Session(user, address);
            debug_log(null, "Creating new session -> " + user);
            debug_log(null, "Address -> " + address);
            session.getAddress().isReachable(400);
            active_session.put(Arrays.toString(address.getAddress()), session);
            debug_log(null, "Putting session in active. Start creating session on Data Base.");
            create_date_base_session(user, address);
            return true;
        } catch (IOException | NullPointerException e) {
            error_log(null, e.getMessage());
            return false;
        }
    }

    private void create_date_base_session(User user, Inet4Address address)
    {
        var request = "INSERT INTO US_SESSION (US_UUID, US_STATUS, ADDRESS) VALUES ('" + user.getUUID() + "', '1', " + address + ")";

        getConnect("UserDataBase").openConnection(connection -> {
            try {
                var var1 = connection.createStatement();
                debug_log(null, "Create statement to data base. " + connection);
                var1.execute(request);
                debug_log(null, "Running executed on connection -> " + connection);
                var1.close();
                debug_log(null, "Closed statement. " + connection);
            } catch (SQLException e) {
                error_log(null, "Error connection to data base. Connection configurate correctly?");
            }
        });
    }
}
