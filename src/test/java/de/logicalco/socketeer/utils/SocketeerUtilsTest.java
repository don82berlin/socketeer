package de.logicalco.socketeer.utils;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

import com.google.common.base.Optional;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Test class for 'SocketeerUtils'.
 */
public class SocketeerUtilsTest {

    private static final Charset UTF8 = StandardCharsets.UTF_8;

    @Test(dataProvider = "intAndByteArrayProvider")
    public void testReadLineUnicode(int[] codes, byte[] expected) throws IOException {
        final InputStream in = mock(InputStream.class);
        for(final int code : codes) {
            when(in.read()).thenReturn(code);
        }
        final byte[] actual = SocketeerUtils.readLineUnicode(in);
        assertEquals(actual, expected);
    }

    @DataProvider
    public Object[][] intAndByteArrayProvider() {
        return new Object[][] {
                {new int[]{10}, new byte[]{}},
                {new int[]{13,10}, new byte[]{}},
                {new int[]{0,10}, new byte[]{}}
        };
    }

    @Test(dataProvider = "respondUtf8Provider")
    public void testRespond(String text, boolean newLine, Optional<byte[]> expected) {
        final Optional<byte[]> actual = SocketeerUtils.respondUTF8(text, newLine);
        if(expected.isPresent()) {
            assertTrue(actual.isPresent());
            assertEquals(actual.get(), expected.get());
        } else {
            assertFalse(actual.isPresent());
        }
    }

    @DataProvider
    public Object[][] respondUtf8Provider() {
        return new Object[][] {
                {"hello", true, Optional.of("hello\n".getBytes(UTF8))},
                {"test", false, Optional.of("test".getBytes(UTF8))},
                {"", true, Optional.absent()},
                {"", false, Optional.absent()},
        };
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testRespondUtf8Invalid() {
        SocketeerUtils.respondUTF8(null, true);
    }

    @Test(dataProvider = "byteIntProvider")
    public void testToUnsignedInt(byte b, int expected) {
        assertEquals(SocketeerUtils.toUnsignedInt(b), expected);
    }

    @Test(dataProvider = "byteIntProvider")
    public void testToSignedByte(byte expected, int i) {
        assertEquals(SocketeerUtils.toSignedByte(i), expected);
    }

    @DataProvider
    public Object[][] byteIntProvider() {
        return new Object[][] {
                {(byte) -1, 255},
                {(byte) -3, 253},
                {(byte) -5, 251},
                {(byte) 3, 3},
                {(byte) 0, 0},
                {(byte) -11, 245},
                {(byte) -111, 145},
        };
    }
}
