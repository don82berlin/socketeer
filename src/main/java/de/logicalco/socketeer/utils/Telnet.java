package de.logicalco.socketeer.utils;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Enum holding telnet commands and methods to with with them.
 */
public enum Telnet {

    ECHO(1, "ECHO"),
    EOF(236, "EOF"),
    SUSP(237, "SUSP"),
    ABORT(238, "ABORT"),
    EOR(239, "EOR"),
    SE(240, "SE"),
    NOP(241, "NOP"),
    // TODO what is 242?
    BREAK(243, "BREAK"),
    IP(244, "IP"),
    AO(245, "AO"),
    AYT(246, "AYT"),
    EC(247, "EC"),
    EL(248, "EL"),
    GA(249, "GA"),
    SB(250, "SB"),
    WILL(251, "WILL"),
    WONT(252, "WONT"),
    DO(253, "DO"),
    DONT(254, "DONT"),
    IAC(255, "IAC");

    private static final Logger LOGGER = LoggerFactory.getLogger(Telnet.class);

    private int code;
    private String command;

    /*
     * @param code      The numeric code, representing the telnet command character.
     * @param command   The command string.
     */
    private Telnet(int code, String command) {
        this.code = code;
        this.command = command;
    }

    /**
     * Get the command character as (signed) byte.
     *
     * @return  Byte representation of the command character.
     */
    public byte getByte() {
        return SocketeerUtils.toSignedByte(code);
    }

    /**
     * Get the command code (as unsigned integer - value from 0 to 255) representing the telnet command character.
     *
     * @return  Command code.
     */
    public int getCode() {
        return code;
    }

    /**
     * String representation of the command, only for a better readability.
     *
     * @return  String representation of a commans.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Create a Telnet value by the command code.
     *
     * @param code  Command code.
     * @return      Telnet value.
     */
    public static Optional<Telnet> forCode(int code) {
        for(final Telnet cmd : Telnet.values()) {
            if(cmd.getCode() == code) {
                return Optional.of(cmd);
            }
        }
        return Optional.absent();
    }

    /**
     * Build a command sequence returned in bytes from some Telnet values.
     *
     * @param cmds  Telnet values to build bytewiese command chain.
     * @return      Bytewiese command chain.
     */
    public static byte[] buildCmdChain(Telnet...cmds) {
        final byte[] bytes = new byte[cmds.length];
        for(int i=0;i<cmds.length;i++) {
            final Telnet cmd = cmds[i];
            checkNotNull(cmd, "Command cannot be null.");
            bytes[i] = cmd.getByte();
        }
        return bytes;
    }

    /**
     * Read a command code chain an return a Telnet array.
     *
     * @param codes Command codes.
     * @return      Telnet array.
     */
    public static Telnet[] readCmdChain(int...codes) {
        final Telnet[] cmds = new Telnet[codes.length];
        for(int i=0;i<codes.length;i++) {
            final Optional<Telnet> cmd = Telnet.forCode(codes[i]);
            if(cmd.isPresent()) {
                cmds[i] = cmd.get();
            } else {
                LOGGER.warn(String.format("Found no telnet command for code '%d'", codes[i]));
            }
        }
        return cmds;
    }

    /**
     * Create a string representing a command chain for a better readability.
     *
     * @param codes Commans codes.
     * @return      String representation of the given commans codes.
     */
    public static String cmdChainToString(int...codes) {
        final Telnet[] cmds = readCmdChain(codes);
        final StringWriter writer = new StringWriter();
        for(int i=0; i<cmds.length;i++) {
            writer.append(cmds[i].getCommand());
            if(i < cmds.length-1) {
                writer.append(" ");
            }
        }
        return writer.toString();
    }

}
