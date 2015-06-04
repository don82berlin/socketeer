package de.logicalco.socketeer.server;

import com.google.common.base.Optional;
import com.sun.javaws.exceptions.InvalidArgumentException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.logicalco.socketeer.utils.Telnet;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Test class for 'TelnetPasswordUsernameSecurityHandler'.
 */
public class TelnetPasswordUsernameSecurityHandlerTest {

    private TelnetPasswordUsernameSecurityHandler handler;

    @BeforeClass
    public void setUp() {
        handler = new TelnetPasswordUsernameSecurityHandler("user", "pass", StandardCharsets.UTF_8);
    }

    @Test(dataProvider = "invalidArgsProvider", expectedExceptions = NullPointerException.class)
    public void testInvalidCreation(String user, String password, Charset charset) {
        new TelnetPasswordUsernameSecurityHandler(user, password, charset);
    }

    @DataProvider
    public Object[][] invalidArgsProvider() {
        final Charset charset = StandardCharsets.UTF_8;
        return new Object[][] {
                //user is null
                {null, "pass", charset},
                //password is null
                {"user", null, charset},
                //charset is null
                {"user", "pass", null}
        };
    }

    @Test
    public void testHandlePositive() throws IOException {
        InputStream in = mock(InputStream.class);
        OutputStream out = mock(OutputStream.class);
        when(in.read()).thenReturn(
                //user\n
                117, 115, 101, 114, 10,
                // Response to IAC WILL ECHO
                Telnet.IAC.getCode(), Telnet.DONT.getCode(), Telnet.ECHO.getCode(),
                //pass\n
                112, 97, 115, 115, 10,
                // Response to IAC WONT ECHO
                Telnet.IAC.getCode(), Telnet.DO.getCode(), Telnet.ECHO.getCode());
        assertTrue(handler.handle(in, out));
        when(in.read()).thenReturn(
                //user\n
                117, 115, 101, 114, 10,
                // Response to IAC WILL ECHO
                Telnet.IAC.getCode(), Telnet.DONT.getCode(), Telnet.ECHO.getCode(),
                //pasu\n ==> wrong password
                112, 97, 115, 117, 10,
                // Response to IAC WONT ECHO
                Telnet.IAC.getCode(), Telnet.DO.getCode(), Telnet.ECHO.getCode());
        assertFalse(handler.handle(in, out));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testHandleInNull() throws IOException {
        handler.handle(null, mock(OutputStream.class));
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testHandleOutNull() throws IOException {
        handler.handle(mock(InputStream.class), null);
    }

    @Test
    public void testValidDenyMessage() {
        assertNotNull(handler.getDenyMessage());
    }

    private int[] createIntArrayUtf8(String bytes) {
        return null;
    }

}
