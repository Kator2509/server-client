package org.servera.crypto;

import org.servera.Server;

import javax.net.ssl.SSLContext;
import java.net.URL;

public class ServerCryptoProvider {
    protected SSLContext context;

    public ServerCryptoProvider()
    {

    }

    private boolean authCryptoKey()
    {

        return false;
    }


    private boolean isExistKey(String keyName)
    {
        URL url = Server.class.getResource("/crypto/" + keyName + ".cer");
        return url != null;
    }
}
