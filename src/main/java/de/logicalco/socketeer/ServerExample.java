package de.logicalco.socketeer;

import com.google.common.base.Optional;
import de.logicalco.socketeer.server.CommandHandler;
import de.logicalco.socketeer.server.SocketeerServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ServerExample {

    public static void main(String[] args) throws IOException {
        final SocketeerServer server = new SocketeerServer(9876, 2, new CommandHandler() {
            @Override
            public Optional<byte[]> handle(String command) {
                System.out.println("SERVER got cmd : " + command);
                return Optional.of("What did you say?\n".getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public Optional<byte[]> getOpener() {
                return Optional.of("Hello there\n".getBytes(StandardCharsets.UTF_8));
            }

            @Override
            public String getEscapeSeq() {
                return "session:exit";
            }
        });
        server.start();
    }

}
