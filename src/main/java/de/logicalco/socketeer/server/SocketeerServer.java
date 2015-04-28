package de.logicalco.socketeer.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The main server listening to a port and waiting for connections to be established.
 * The concept is pretty simple : If a connecton is established, the session is started in a thread.
 * Inside a session the client can interact with ther server as defined in the CommandHandler implementation.
 * The purpose is to have a simple interface for controlling applications.<br/>
 * <b>Note : </b>The CommandHandler is shared by all sessions, therefore it has to be thread safe.
 */
public class SocketeerServer implements Runnable {

    private Boolean running = Boolean.FALSE;

    private final Integer port;

    private final CommandHandler handler;

    private final ExecutorService sessionPool;

    private final ExecutorService serverExecutor;

    /**
     * Create the server.
     *
     * @param port              Port to listen to.
     * @param maxSessions       Max amount of session that can run at the same time.
     * @param handler           Handler implementation to serve the commands.
     */
    public SocketeerServer(final Integer port, final Integer maxSessions, CommandHandler handler) {
        this.port = port;
        this.handler = handler;
        this.sessionPool = Executors.newFixedThreadPool(maxSessions);
        this.serverExecutor = Executors.newSingleThreadExecutor();
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
                sessionPool.submit(new SocketeerSession(socket, handler));
            }
        } catch(Exception e) {
            e.printStackTrace();
            //TODO log with slf4j
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
