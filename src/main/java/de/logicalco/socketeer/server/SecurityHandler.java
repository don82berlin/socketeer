package de.logicalco.socketeer.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Interface for security handler to be called to init a new socketeer session.
 */
public interface SecurityHandler {

    /**
     * This method is called by SocketeerSession and should handle the security handshake.
     *
     * @param in        InputStream to read from.
     * @param out       OutputStream to write to.
     * @return          Result of the handshake (true = success, false = failure).
     * @throws IOException
     */
    Boolean handle(InputStream in, OutputStream out) throws IOException;

    /**
     * The deny message to send, if the handshake fails.
     *
     * @return  Deny message as byte array.
     */
    byte[] getDenyMessage();

}
