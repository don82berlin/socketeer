package de.logicalco.socketeer.utils;

import com.google.common.base.Optional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility class for working with streams.
 */
public class SocketeerUtils {

    /**
     * unicode null char
     */
    private static final int UNC = 0;
    /**
     * unicode line feed
     */
    private static final int ULF = 10;
    /**
     * unicode carriage return
     */
    private static final int UCR = 13;
    /**
     * end of stream
     */
    private static final int EOS = -1;

    private SocketeerUtils() {
        //avoid instatiation
    }

    /**
     * Read a line from a inout stream - all bytes until the unicode character 10 was detected, the stream end was
     * reached or the buffer is full). The unicode character 0 and the carraige return (13) is ignored and will not be
     * stored in the result array.
     * @param in            InoutStream to read from.
     * @param bufferSize    Size of the internal read buffer.
     * @return              Byte array with the payload read.
     * @throws IOException
     */
    public static byte[] readLineUnicode(InputStream in, int bufferSize) throws IOException {
        checkNotNull(in, "Input stream cannot be null.");
        checkArgument(bufferSize > 0, "Buffer site has to be positive.");
        int byteRead = -100;
        final byte[] buffer = new byte[bufferSize];
        int index = 0;
        while(index < bufferSize - 1) {
            byteRead = in.read();
            if(byteRead == EOS || byteRead == ULF) {
                break;
            }
            if(byteRead == UNC || byteRead == UCR) {
                continue;
            }
            buffer[index] = toSignedByte(byteRead);
            index++;
        }
        return Arrays.copyOfRange(buffer, 0, index);
    }

    /**
     * @see #readLineUnicode(InputStream, int)
     * The byte buffer is set to 256.
     *
     * @param in    InputStream to read from.
     * @return      Byte array with the payload read.
     * @throws IOException
     */
    public static byte[] readLineUnicode(InputStream in) throws IOException {
        return readLineUnicode(in, 256);
    }

    /**
     * Convert a signed byte to an unsigned int (unsigned in the sense that only values between 0 and 255 are allowed).
     *
     * @param b     Byte to convert.
     * @return      Unsigned int representation of the signed byte.
     */
    public static int toUnsignedInt(byte b) {
        return b & 0xFF;
    }

    /**
     * COnvert a unsigned int to a signed byte.
     *
     * @param i     Integer value to convert (Only values between 0 and 255 are allowed).
     * @return      Signed byte represnetation of the unsigned int.
     */
    public static byte toSignedByte(int i) {
        checkArgument(i >= 0 && i <= 255, "int value cannot be negative or bigger that 255");
        return (byte) i;
    }

    /**
     * Create an optional response using UTF8 charset.
     * @param text      Text to be responded.
     * @param newLine   Add a new line at end of the response?
     * @return          Optinal of byte array.
     */
    public static Optional<byte[]> respondUTF8(String text, boolean newLine) {
        return respond(text, StandardCharsets.UTF_8, newLine);
    }

    /**
     * Create an optional response using given charset.
     * @param text      Text to be responded.
     * @param charset   Charset to use for string-to-byte conversion.
     * @param newLine   Add a new line at end of the response?
     * @return          Optional of byte array.
     */
    public static Optional<byte[]> respond(String text, Charset charset, boolean newLine) {
        checkNotNull(text, "text cannot be null");
        checkNotNull(charset, "Charset cannot be null");
        if(text.isEmpty()) {
            return Optional.absent();
        }

        return Optional.of((newLine ? text + "\n" : text).getBytes(charset));
    }
}
