package de.logicalco.socketeer.utils;

import com.google.common.base.Optional;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test class for enum 'de.logicalco.socketeer.utils.Telnet'.
 */
public class TelnetTest {

    @Test(dataProvider = "forCodeProvider")
    public void testForCode(int code, Optional<Telnet> expected) {
        final Optional<Telnet> actual = Telnet.forCode(code);
        if(expected.isPresent()) {
            assertTrue(actual.isPresent());
            assertEquals(actual.get(), expected.get());
        } else {
            assertFalse(actual.isPresent());
        }
    }

    @DataProvider
    public Object[][] forCodeProvider() {
        return new Object[][] {
                {1, Optional.of(Telnet.ECHO)},
                {-11, Optional.absent()},
                {255, Optional.of(Telnet.IAC)},
                {5654, Optional.absent()}
        };
    }

    @Test(dataProvider = "buildCmdChainProvider")
    public void testBuildCmdChain(Telnet[] cmds, byte[] expected) {
        assertEquals(Telnet.buildCmdChain(cmds), expected);
    }

    @DataProvider
    public Object[][] buildCmdChainProvider() {
        return new Object[][] {
                {new Telnet[]{}, new byte[]{}},
                {new Telnet[]{Telnet.IAC, Telnet.WILL, Telnet.ECHO}, new byte[]{(byte) 255, (byte) 251, (byte) 1}}
        };
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testBuildCmdChainInvalid() {
        Telnet.buildCmdChain(null);
    }

    @Test(dataProvider = "readCmdChainProvider")
    public void testReadCmdChain(int[] codes, Telnet[] expected) {
        assertEquals(Telnet.readCmdChain(codes), expected);
    }

    @DataProvider
    public Object[][] readCmdChainProvider() {
        return new Object[][] {
                {new int[]{}, new Telnet[]{}},
                {new int[]{255, 251, 1}, new Telnet[]{Telnet.IAC, Telnet.WILL, Telnet.ECHO}}
        };
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testReadCmdChainInvalid() {
        Telnet.readCmdChain(null);
    }

    @Test(dataProvider = "cmdChainToStringProvider")
    public void testCmdChainToString(int[] codes, String expected) {
        assertEquals(Telnet.cmdChainToString(codes), expected);
    }

    @DataProvider
    public Object[][] cmdChainToStringProvider() {
        return new Object[][]{
                {new int[]{}, ""},
                {new int[]{255, 251, 1}, "IAC WILL ECHO"}
        };
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testCmdChainToStringInvalid() {
        Telnet.cmdChainToString(null);
    }

}
