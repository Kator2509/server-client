package org.servera.inheritance.auth;

import org.servera.Server;
import org.servera.config.ConfigException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static org.servera.LoggerStatement.*;
import static org.servera.config.ConfigurationManager.getConfiguration;
import static org.servera.inheritance.auth.AuthListener.ServerAuth.ServerKeyStore.getStore;
import static org.servera.inheritance.auth.AuthListener.ServerAuth.ServerTrustStore.getTrustStore;

public class AuthListener extends SessionManager
{
    protected Map<Integer, Session> auth;
    protected ServerAuth server;

    public AuthListener(){
        super();
        this.server = new ServerAuth();
    }

    public void close()
    {
        this.server.callStop();
        this.auth = null;
        log(null, "Server auth is closing.");
    }

    protected static class ServerAuth
    {
        protected Thread server;
        protected static ServerKeyStore keyStore;
        protected SSLServerSocket socket;
        protected Boolean run = false;
        protected Thread listener;

        public void callStop()
        {
            run = false;
        }

        public ServerAuth(){
            keyStore = new ServerKeyStore();
            log(null, "Launch auth server with session manager.");
            launch();
            run = true;
            listener = new Thread(new Runnable() {
                public boolean isRun()
                {
                    return run;
                }

                @Override
                public void run() {
                    while (isRun())
                    {
                        try {
                            socket.setSoTimeout((Integer) getConfiguration("config").getDataPath("time-out"));
                            Socket var = socket.accept();
                            if (var.isConnected())
                            {
                                log(null, "User from ip-address -> " + var.getInetAddress() + " with using a user name.");
                            }
                        } catch (SocketException ignore) {} catch (ConfigException e) {
                            warn_log(null, e.getMessage());
                        } catch (IOException e) {
                            error_log(null, e.getMessage());
                        }
                    }
                }
            });
            listener.start();
        }

        protected void launch()
        {
            if (!Objects.equals(getContext(), null)) {
                SSLServerSocketFactory ssf = getContext().getServerSocketFactory();
                try {
                    socket = (SSLServerSocket) ssf.createServerSocket((Integer) getConfiguration("config").getDataPath("port"));
                } catch (IOException | ConfigException e) {
                    error_log(null, e.getMessage());
                }
            }
            else
            {
                warn_log(null, "SSL context is null.");
            }
        }

        public static SSLContext getContext()
        {
            try {
                SSLContext context = SSLContext.getInstance("TLSv1.3");
                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

                kmf.init(getStore(), getConfiguration("config").getDataPath("crypto-password").toString().toCharArray());

                tmf.init(getTrustStore());

                context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

                return context;
            } catch (NoSuchAlgorithmException | ConfigException | KeyStoreException | UnrecoverableKeyException |
                     KeyManagementException e) {
                error_log(null, e.getMessage());
                return null;
            }
        }

        protected static class ServerTrustStore
        {
            protected final String systemPath = Server.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(0,
                    Server.class.getProtectionDomain().getCodeSource().getLocation().getPath().lastIndexOf(File.separator) + 1);
            protected static KeyStore store;
            protected char[] password = getConfiguration("config").getDataPath("crypto-password").toString().toCharArray();

            public ServerTrustStore() throws ConfigException {
                try {
                    store = KeyStore.getInstance("JCEKS");

                    if (password.length == 0)
                    {
                        warn_log(null, "Detected a null password. Don't load a Trust store.");
                        debug_log(null, "Starting creating a new trust store with new password from Server store in next launch after filling a crypto-password.");
                    } else if(password.length > 1 && new File(systemPath + "truststore.jks").exists())
                    {

                    }
                    //Требуется дописать структуру загрузки доверенных сертификатов.
                } catch (KeyStoreException e) {
                    error_log(null, e.getMessage());
                }
            }

            public static KeyStore getTrustStore()
            {
                return store;
            }
        }

        protected static class ServerKeyStore
        {
            protected final String systemPath = Server.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(0,
                    Server.class.getProtectionDomain().getCodeSource().getLocation().getPath().lastIndexOf(File.separator) + 1);
            protected static KeyStore store;
            protected KeyGenerator generator;
            protected SecretKey key;
            protected char[] password;

            public ServerKeyStore()
            {
                try{
                    store = KeyStore.getInstance("JCEKS");
                    this.generator = KeyGenerator.getInstance("AES");
                    this.generator.init(256);
                    this.key = this.generator.generateKey();
                    if(Objects.equals(getConfiguration("config").getDataPath("crypto-password"), "")) {
                        this.password = generateKeyPassword(Integer.parseInt((String) getConfiguration("config").getDataPath("crypto-size")));
                    } else {
                        this.password = this.loadPWD();
                    }
                    this.initializeStore(this.password);
                } catch (KeyStoreException | NoSuchAlgorithmException | ConfigException e) {
                    error_log(null, "Can't initialize a key store!");
                    error_log(null, e.getMessage());
                }
            }

            static KeyStore getStore()
            {
                return store;
            }

            private char[] loadPWD()
            {
                try {
                    return getConfiguration("config").getDataPath("crypto-password").toString().toCharArray();
                } catch (ConfigException e) {
                    error_log(null, e.getMessage());
                }
                return null;
            }

            public void initializeStore(char[] pwd)
            {
                    try{
                        try (InputStream keyStoreData = new FileInputStream(this.systemPath + "store.jks")) {
                            store.load(keyStoreData, pwd);
                        } catch (IOException e) {
                            debug_log(null, e.getMessage());
                            warn_log(null, "Trying a create new store.");
                            store.load(null, pwd);
                            log(null, "Created new key store. Copy the password and entry to config to input for line 'crypto-password'.");

                            KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(this.key);
                            KeyStore.PasswordProtection pass = new KeyStore.PasswordProtection(pwd);

                            try {
                                store.setEntry("key", entry, pass);
                            } catch (KeyStoreException ex) {
                                error_log(null, e.getMessage());
                            }
                        }
                    } catch (IOException | NoSuchAlgorithmException | CertificateException e)
                    {
                        error_log(null, e.getMessage());
                    }

                try (FileOutputStream keyStoreOutput = new FileOutputStream(this.systemPath + "store.jks")) {
                    log(null, "Load certificate to connection.");
                    store.store(keyStoreOutput, pwd);
                } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
                    error_log(null, e.getMessage());
                }
            }

            protected char[] generateKeyPassword(int size)
            {
                var random = new Random();
                char[] list = new char[size];
                for(int i = 0; i < size;)
                {
                    var temp = Math.abs((int) (100 + (random.nextDouble() * 2.0) * (Math.pow(10, 1) - 100)));
                    if(temp > 32 && temp < 127 && temp != 34 && temp != 92){
                        list[i] = (char) temp;
                        i++;
                    }
                }

                var var1 = new StringBuilder();
                for (char i: list)
                {
                    var1.append(i);
                }

                warn_log(null, "Created PWD to certificate connection.");
                warn_log(null, var1.toString());

                return list;
            }
        }
    }
}
