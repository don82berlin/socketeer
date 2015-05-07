package de.logicalco.socketeer.server;

import com.google.common.base.Optional;
import de.logicalco.socketeer.utils.SocketeerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A session for communication with the client.
 */
class SocketeerSession implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketeerSession.class);

    private final InputStream in;
    private final OutputStream out;
    private final CommandHandler commandHandler;
    private Optional<SecurityHandler> securityHandler = Optional.absent();
    private final Charset charset;

    private Boolean sessionAlive = Boolean.TRUE;

    /**
     * @param connection        The socket-connection to the client.
     * @param commandHandler    The handler for command that were read.
     * @param charset           The caharcter set for String<->byte conversion.
     * @throws IOException
     */
    SocketeerSession(final Socket connection, final CommandHandler commandHandler, final Charset charset) throws IOException {
        checkNotNull(connection, "Connection cannot be null.");
        this.in = connection.getInputStream();
        this.out = connection.getOutputStream();
        this.charset = checkNotNull(charset, "Charset cannot be null.");
        this.commandHandler = checkNotNull(commandHandler, "Command handler cannot be null.");
    }

    @Override
    public void run() {
        try {
            if(securityHandler.isPresent() && !securityHandler.get().handle(in, out)) {
                out.write(securityHandler.get().getDenyMessage());
                return;
            }
            final Optional<byte[]> opener = commandHandler.getOpener();
            if(opener.isPresent()) {
                out.write(opener.get());
            }
            while(sessionAlive) {
                out.write(commandHandler.getAppName());
                out.write("> ".getBytes(charset));
                final byte[] command = SocketeerUtils.readLineUnicode(in);
                if(command == null || Arrays.equals(commandHandler.getEscapeSeq(), command)) {
                    sessionAlive = Boolean.FALSE;
                } else {
                    final Optional<byte[]> response = commandHandler.handle(command);
                    if(response.isPresent()) {
                        out.write(response.get());
                    }
                }
            }
        } catch(Exception e) {
            LOGGER.error("Session crashed!",e);
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                LOGGER.warn("Could not close session properly.", e);
            }
        }
    }

    /**
     * Use a security handler to enable a security handshake before starting a session.
     *
     * @param securityHandler   Security handler to use.
     * @return                  this
     */
    SocketeerSession withSecurityHandler(SecurityHandler securityHandler) {
        this.securityHandler = Optional.of(checkNotNull(securityHandler, "Security handler cannot be null."));
        return this;
    }

    private void writeException(final Exception e) {
        try(final PrintWriter writer = new PrintWriter(out)) {
            e.printStackTrace(writer);
        }
    }
}
