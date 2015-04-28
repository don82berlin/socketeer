package de.logicalco.socketeer.server;

import com.google.common.base.Optional;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SocketeerSession implements Runnable {

    private final InputStream in;
    private final OutputStream out;
    private final CommandHandler handler;

    private Boolean sessionAlive = Boolean.TRUE;

    SocketeerSession(final Socket connection, final CommandHandler handler) throws IOException {
        this.in = connection.getInputStream();
        this.out = connection.getOutputStream();
        this.handler = handler;
    }

    @Override
    public void run() {
        try(final BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            final Optional<byte[]> opener = handler.getOpener();
            if(opener.isPresent()) {
                out.write(opener.get());
            }
            while(sessionAlive) {
                final String command = reader.readLine();
                if(command == null || handler.getEscapeSeq().equals(command.trim())) {
                    sessionAlive = Boolean.FALSE;
                } else {
                    final Optional<byte[]> response = handler.handle(command.trim());
                    if(response.isPresent()) {
                        out.write(response.get());
                    }
                }
            }
        } catch(IOException e) {
            //TODO log using slf4j
            writeException(e);
        } catch (RuntimeException e) {
            //TODO log using slf4j
        }
    }

    private void writeException(final Exception e) {
        try(final PrintWriter writer = new PrintWriter(out)) {
            e.printStackTrace(writer);
        }
    }
}
