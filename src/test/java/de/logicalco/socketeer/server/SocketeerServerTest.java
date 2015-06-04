package de.logicalco.socketeer.server;

import static org.mockito.Mockito.mock;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Test class for 'SocketeerServer'.
 */
public class SocketeerServerTest {

    @Test(dataProvider = "invalidArgsProvider", expectedExceptions = {IllegalArgumentException.class, NullPointerException.class})
    public void testInvalidCreation(Integer port, Integer maxSessions, CommandHandler commandHandler, Charset charset) {
        new SocketeerServer(port, maxSessions, commandHandler, charset);
    }

    @DataProvider
    public Object[][] invalidArgsProvider() {
        final CommandHandler commandHandler = mock(CommandHandler.class);
        final Charset charset = StandardCharsets.UTF_8;
        return new Object[][] {
                //port is null
                {null, 5, commandHandler, charset},
                //port is negative
                {-8080, 5, commandHandler, charset},
                //port is 0
                {0, 5, commandHandler, charset},
                //max sessions is null
                {8080, null, commandHandler, charset},
                //max sessions is negative
                {8080, -5, commandHandler, charset},
                //max sessions is 0
                {8080, 0, commandHandler, charset},
                //command handler is null
                {8080, 5, null, charset},
                //charset is null
                {8080, 5, commandHandler, null}
        };
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testWithSecurityHandlerNegative() {
        new SocketeerServer(8080, 5, mock(CommandHandler.class), StandardCharsets.UTF_8).withSecurityHandler(null);
    }

}
