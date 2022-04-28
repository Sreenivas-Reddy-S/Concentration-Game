package server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * This class takes port number and dimensions and starts the
 * ConcentrationClientServerThread to allow multiple clients to connect to the server.
 *
 * @author Kruthi Nagabhushan
 * @author Sreenivas Reddy Sagili
 */
public class ConcentrationServer {
    /**
     * This method checks whether the command line arguments are valid and creates thread for every client.
     *
     * @param args : args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java ConcentrationServer <port number>");
            System.exit(1);
        }

        // the port number.
        int portNumber = Integer.parseInt(args[0]);

        // the board dimensions.
        int board_dimension = Integer.parseInt(args[1]);

        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                // this method blocks until a connection is made.
                new ConcentrationServerThread(serverSocket.accept(), board_dimension).start();
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }


    }
}
