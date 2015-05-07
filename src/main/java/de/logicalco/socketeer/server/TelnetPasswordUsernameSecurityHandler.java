package de.logicalco.socketeer.server;

import de.logicalco.socketeer.utils.SocketeerUtils;
import de.logicalco.socketeer.utils.Telnet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A very simple SecurityHadler with a username/password authentication writing and expecting telnet commands for
 * communication with the client.
 */
public class TelnetPasswordUsernameSecurityHandler implements SecurityHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelnetPasswordUsernameSecurityHandler.class);

    private final byte[] user;

    private final byte[] password;

    private final Charset charset;

    /**
     * @param user          Username.
     * @param password      Password.
     * @param charset       Charset for String<->byte conversion
     */
    public TelnetPasswordUsernameSecurityHandler(String user, String password, Charset charset) {
        this.password = checkNotNull(password, "Password cannot be null").getBytes(charset);
        this.user = checkNotNull(user, "User cannot be null").getBytes(charset);
        this.charset = checkNotNull(charset, "Charset cannot be null.");
    }

    @Override
    public Boolean handle(InputStream in, OutputStream out) throws IOException {
        checkNotNull(in, "Input stream cannot be null.");
        checkNotNull(out, "Output stream cannot be null.");
        out.write("user : ".getBytes(charset));
        final byte[] user = SocketeerUtils.readLineUnicode(in);
        out.write("password : ".getBytes(charset));
        out.write(Telnet.buildCmdChain(Telnet.IAC, Telnet.WILL, Telnet.ECHO));
        readClientAnswer(in);
        final byte[] pass = SocketeerUtils.readLineUnicode(in);
        out.write(Telnet.buildCmdChain(Telnet.IAC, Telnet.WONT, Telnet.ECHO));
        readClientAnswer(in);
        out.write((byte) 10);
        return Arrays.equals(this.user, user) && Arrays.equals(this.password, pass);
    }

    private void readClientAnswer(InputStream in) throws IOException {
        checkNotNull(in, "Input stream cannot be null.");
        final int[] cmds = new int[3];
        for(int i=0; i<3;i++) {
            cmds[i] = in.read();
        }
        LOGGER.debug(String.format("Client returned : '%s'.", Telnet.cmdChainToString(cmds)));
    }

    @Override
    public byte[] getDenyMessage() {
        return "Authetication failed!\n".getBytes(charset);
    }
}
