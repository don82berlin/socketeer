package de.logicalco.socketeer.server;

import static org.mockito.Mockito.mock;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Test class for 'SocketeerTest'.
 */
public class SocketeerSessionTest {

    @Test(dataProvider = "invalidArgsProvider", expectedExceptions = NullPointerException.class)
    public void testInvalidCreation(Socket connection, CommandHandler commandHandler, Charset charset) throws IOException {
        new SocketeerSession(connection, commandHandler, charset);
    }

    @DataProvider
    public Object[][] invalidArgsProvider() {
        final Socket conn = mock(Socket.class);
        final CommandHandler commandHandler = mock(CommandHandler.class);
        final Charset charset = StandardCharsets.UTF_8;
        return new Object[][] {
                //connection is null
                {null, commandHandler, charset},
                //commandHandler is null
                {conn, null, charset},
                //charset is null
                {conn, commandHandler, null}
        };
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testWithSecurityHandlerNegative() throws IOException {
        new SocketeerSession(mock(Socket.class), mock(CommandHandler.class), StandardCharsets.UTF_8).withSecurityHandler(null);
    }

    //TODO test run and with security handler

}
