package de.logicalco.socketeer.server;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * The main server listening to a port and waiting for connections to be established.
 * The concept is pretty simple : If a connecton is established, the session is started in a thread.
 * Inside a session the client can interact with ther server as defined in the CommandHandler implementation.
 * The purpose is to have a simple interface for controlling applications.<br/>
 * <b>Note : </b>The CommandHandler is shared by all sessions, therefore it has to be thread safe.
 */
public class SocketeerServer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketeerServer.class);

    private Boolean running = Boolean.FALSE;

    private final Integer port;

    private final CommandHandler commandHandler;

    private Optional<SecurityHandler> securityHandler = Optional.absent();

    private final ExecutorService sessionPool;

    private final Charset charset;

    private final ExecutorService serverExecutor;

    /**
     * Create the server.
     *
     * @param port              Port to listen to.
     * @param maxSessions       Max amount of session that can run at the same time.
     * @param commandHandler    Handler implementation to serve the commands.
     * @param charset           Charset for byte to string conversion of commands.
     */
    public SocketeerServer(final Integer port, final Integer maxSessions, final CommandHandler commandHandler,
                           final Charset charset) {
        checkArgument(port > 0, "Port hast to be positive.");
        this.port = port;
        this.commandHandler = checkNotNull(commandHandler, "Command handler cannot be null.");
        this.charset = checkNotNull(charset, "Charset cannot be null.");
        this.sessionPool = Executors.newFixedThreadPool(maxSessions);
        this.serverExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * @param securityHandler   The security handler to use. If none is passed no security handshake will be applied.
     * @return                  this.
     */
    public SocketeerServer withSecurityHandler(final SecurityHandler securityHandler) {
        this.securityHandler = Optional.of(checkNotNull(securityHandler, "Security handler cannot be null."));
        return this;
    }

    /**
     * Start the server and wait for clients to connect.
     *
     * @throws IOException
     */
    public void start() throws IOException {
        serverExecutor.submit(this);
    }

    @Override
    public void run() {
        try {
            running = Boolean.TRUE;
            final ServerSocket server = new ServerSocket(port);
            while (running) {
                final Socket socket = server.accept();
                final SocketeerSession session = new SocketeerSession(socket, commandHandler, charset);
                if(securityHandler.isPresent()) {
                    session.withSecurityHandler(securityHandler.get());
                }
                sessionPool.submit(session);
            }
        } catch(Exception e) {
            LOGGER.error("Server crashed!",e);
        }
    }

    /**
     * Stop the server ans shutdown the session pool.
     *
     * @param halt  If set to true the session pool will shutdown by force or gently otherwise.
     *              Normally you should use halt=true, because otherwise you have to wait for the client to end his
     *              session before you can shutdown the server.
     */
    public void stop(final Boolean halt) {
        checkNotNull(halt, "Halt cannot be null");
        running = Boolean.FALSE;
        if(halt) {
            sessionPool.shutdownNow();
        } else {
            sessionPool.shutdown();
        }
        //Interrupt the server thread, to break ServerSocket#accept
        serverExecutor.shutdownNow();
    }
}
