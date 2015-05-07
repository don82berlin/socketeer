#Socketeer

## What is socketeer?
Socketeer is a very simple framework to enable your service-application to have a control interface while running.
This is done by listening to a configured port and providing an interface to be implemented : `Command Handler`.

## How to use it?
Using socketeer is very simple, you just have to implement a `CommandHandler`, connect it with the server and start the
server.
Example :

    public static void main(String[] args) throws IOException {
            final SocketeerServer server = new SocketeerServer(
                    //server runs on port 9876
                    6789,
                    //5 sessions at a time are allowed
                    5,
                    //simple command handler as anonymous class
                    new CommandHandler() {
                        @Override
                        public Optional<byte[]> getOpener() {
                            //optionally return a welcome message
                            return SocketeerUtils.respondUTF8("Welcome", true);
                        }
    
                        @Override
                        public byte[] getAppName() {
                            //return the application's name
                            return SocketeerUtils.respondUTF8("myApp", false).get();
                        }
    
                        @Override
                        public Optional<byte[]> handle(byte[] command) {
                            //handle your commands here
                            final String cmd = new String(command, StandardCharsets.UTF_8);
                            switch(cmd) {
                                case "ping":
                                    return SocketeerUtils.respondUTF8("pong", true);
                                case "act":
                                    //do something
                                    // ...
                                    // return no response
                                    return Optional.absent();
                                default:
                                    return SocketeerUtils.respondUTF8(String.format("%s is unknown.", cmd), true);
                            }
                        }
    
                        @Override
                        public byte[] getEscapeSeq() {
                            //destroy session on command 'exit'
                            return SocketeerUtils.respondUTF8("exit", false).get();
                        }
                    },
                    //charset to be used for string to byte conversion
                    StandardCharsets.UTF_8
            );
            //add a security handler ...
            server.withSecurityHandler(new TelnetPasswordUsernameSecurityHandler(
                            "john", //username
                            "pass", // password
                            StandardCharsets.UTF_8)
            );
            server.start(); //throws IOException
    }

## Security 
As shown in the example above, you can add a security handler for invoking a security handshake before staring a
session. One basic implemetation is provided, the `TelnetPasswordUsernameSecurityHandler`, which asks for a username 
and password using telnet commands to communicate with the client. 
This handler is pretty simple and does not support encryption.
If you need a more secure solution you can provide your own security handler, by implementing the `SecurityHandler`
interface.

## Logging
Logging is implemented using slf4j. If you want to enable server-side logging you have to add a slf4j-binding to your
classpath.