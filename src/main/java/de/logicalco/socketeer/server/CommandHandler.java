package de.logicalco.socketeer.server;

import com.google.common.base.Optional;

/**
 * Interface for the handler that evaluates the commands and optionally sends back content.
 */
public interface CommandHandler {

    /**
     * This is the opening pharse sent by the server when a session is started.
     * Optinal#absent should be returned for no opening phrase.
     *
     * @return  Opening phrase or Optional#absent
     */
    Optional<byte[]> getOpener();

    /**
     * Handle the command and optinally send back a response.
     *
     * @param command   Command to handle.
     * @return          Response to send back to the client.
     */
    Optional<byte[]> handle(String command);

    /**
     * The escape sequence is a string that destroys the current session on server side (and the connection too).
     *
     * @return  Escape sequence.
     */
    String getEscapeSeq();

}
