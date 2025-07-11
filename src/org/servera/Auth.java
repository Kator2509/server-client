package org.servera;

public class Auth
{
    public Auth(){
        AuthParser parser = new AuthParser(new Thread(() ->
        {
            
        }));
    }

    private static class AuthParser
    {
        protected Thread authThread;

        private AuthParser(Thread authThread)
        {
            this.authThread = authThread;
        }
    }
}
