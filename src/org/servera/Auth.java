package org.servera;

public class Auth
{
    private static final String prefix = "[ListenerAuthUser]: ";

    public Auth(){
        AuthParser.callParser();
    }

    private static class AuthParser
    {
        protected Thread authThread;

        private AuthParser(Thread authThread)
        {
            this.authThread = authThread;
            this.authThread.start();
            System.out.println(prefix + "Loaded. Await a users.");
        }

        private static void callParser()
        {
            AuthParser parser = new AuthParser(new Thread(() ->
            {

            }));
        }
    }
}
