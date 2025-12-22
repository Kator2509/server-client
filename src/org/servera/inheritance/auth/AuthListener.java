package org.servera.inheritance.auth;

import org.servera.Logger;
import org.servera.Server;
import org.servera.config.ConfigException;
import org.servera.config.ConfigurationManager;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static org.servera.LogArguments.*;
import static org.servera.LoggerStatement.*;
import static org.servera.config.ConfigurationManager.getConfiguration;

public class AuthListener
{
    protected Map<Integer, Session> auth;
    protected ConfigurationManager configurationManager;

    public AuthListener(ConfigurationManager configurationManager){
        this.configurationManager = configurationManager;
        ServerAuth auth = new ServerAuth(this.configurationManager);
    }

    private static class ServerAuth
    {
        protected ServerKeyStore keyStore;
        protected Thread server;
        protected SSLSocket socket;

        public ServerAuth(ConfigurationManager configurationManager)
        {
            this.keyStore = new ServerKeyStore(configurationManager);
        }

        private boolean isServer()
        {

            return false;
        }

        public static SSLContext context()
        {
            return null;
        }

        protected static class ServerKeyStore
        {
            protected Logger logger = new Logger();
            protected final String systemPath = Server.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(0,
                    Server.class.getProtectionDomain().getCodeSource().getLocation().getPath().lastIndexOf(File.separator) + 1);
            protected KeyStore store;
            protected KeyGenerator generator;
            protected SecretKey key;
            protected char[] password;

            public ServerKeyStore(ConfigurationManager configurationManager)
            {
                try{
                    this.store = KeyStore.getInstance("JCEKS");
                    if(Objects.equals(getConfiguration("config").getDataPath("certificate-password"), "")) {
                        this.generator = KeyGenerator.getInstance("AES");
                        this.generator.init(256);
                        this.key = this.generator.generateKey();
                        this.password = generateKeyPassword().toString().toCharArray();
                    }
                    else {
                        this.password = this.loadPWD(configurationManager);
                    }
                    this.initializeStore();
                } catch (KeyStoreException | NoSuchAlgorithmException | ConfigException e) {
                    error_log(null, "Can't initialize a key store!");
                    error_log(null, e.getMessage());
                }
            }

            private char[] loadPWD(ConfigurationManager configurationManager)
            {
                try {
                    return getConfiguration("config").getDataPath("certificate-password").toString().toCharArray();
                } catch (ConfigException e) {
                    error_log(null, e.getMessage());
                }
                return null;
            }

            public void initializeStore()
            {
                    try{
                        try (InputStream keyStoreData = new FileInputStream(this.systemPath + "certificate.cre")) {
                            this.store.load(keyStoreData, password);
                        } catch (IOException | CertificateException e) {
                            warn_log(null, e.getMessage());
                            warn_log(null, "Trying a create new store.");
                            this.store.load(null, null);
                        }
                    } catch (IOException | NoSuchAlgorithmException | CertificateException e)
                    {
                        error_log(null, e.getMessage());
                    }

                    KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(this.key);
                    KeyStore.PasswordProtection pass = new KeyStore.PasswordProtection(password);

                try {
                    this.store.setEntry("key", entry, pass);
                } catch (KeyStoreException e) {
                    error_log(null, e.getMessage());
                }

                try (FileOutputStream keyStoreOutput = new FileOutputStream(this.systemPath + "certificate.cre")) {
                    this.store.store(keyStoreOutput, password);
                } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
                    error_log(null, e.getMessage());
                }
            }

            protected ArrayList<Character> generateKeyPassword()
            {
                var random = new Random();
                var list = new ArrayList<Character>();
                for(int i = 0; i < 2048; i++)
                {
                    var temp = Math.abs((int) (100 + (random.nextDouble() * 2.0) * (Math.pow(10, 1) - 100)));
                    if(temp > 32 && temp < 127){
                        list.add((char) temp);
                    }
                }
                for(char i:list)
                {
                    System.out.print(i);
                }
                return list;
            }
        }
    }
}
