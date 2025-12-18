package org.servera.inheritance.auth;

import org.servera.LogArguments;
import org.servera.Logger;
import org.servera.inheritance.User;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.Map;

public class SessionManager
{
    protected Map<String, Session> active_session;
    protected Logger logger = new Logger(this.getClass());

    public SessionManager(){}

    public boolean create_session(User user, Inet4Address address)
    {
        try {
            var session = new Session(user, address);
            session.getAddress().isReachable(400);
            logger.writeLog(null, LogArguments.LOG, "Created session with id - ");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private int generate_id_session()
    {

        return 0;
    }

    private void create_db_log()
    {

    }
}
