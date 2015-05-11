package de.logicalco.socketeer.server;

import com.google.common.base.Optional;
import com.sun.javaws.exceptions.InvalidArgumentException;
import static org.mockito.Mockito.mock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

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

    @Test public void handlePositive() throws IOException {
        InputStream in = mock(InputStream.class);
        OutputStream out = mock(OutputStream.class);
        //TODO setup in and out
        assertTrue(handler.handle(in, out));
        //TODO setup in and out for negative username
        assertFalse(handler.handle(in, out));
        //TODO setup in and out for negative password
        assertFalse(handler.handle(in, out));
    }

    //TODO test handle negative in = null, out = null

    @Test
    public void testValidDenyMessage() {
        assertNotNull(handler.getDenyMessage());
    }

}
