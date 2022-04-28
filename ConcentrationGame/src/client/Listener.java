package client;

import model.ConcentrationModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Listener for the server. Class that reads the messages sent from server.
 *
 * @author : Kruthi Nagabhushan
 * @author : Sreenivas Reddy Sagili
 */
public class Listener extends Thread {
    /**
     * Model variable
     */
    public ConcentrationModel model;

    /**
     * Socket variable
     */
    public Socket socket;

    /**
     * BufferedReader variable
     */
    public BufferedReader in;

    /**
     * PrintWriter variable
     */
    public PrintWriter out;

    /**
     * class constructor to initialize all the variable
     *
     * @param socket socket
     * @param in     BufferedReader
     * @param out    PrintWriter
     * @param model  ConcentrationModel
     */
    public Listener(Socket socket, BufferedReader in, PrintWriter out, ConcentrationModel model) {
        this.socket = socket;
        this.model = model;
        this.out = out;
        this.in = in;
    }

    /**
     * run method
     */
    public void run() {
        // to read server messages
        String fromServer = null;
        try {
            //loop to read all type of server's message
            while ((fromServer = in.readLine()) != null) {
                System.out.println(fromServer);

                if (fromServer.startsWith("CARD")) {
                    model.readCard(fromServer);
                } else if (fromServer.startsWith("MISMATCH")) {
                    model.readMismatch(fromServer);
                } else if (fromServer.startsWith("MATCH")) {
                    model.readMatch();
                } else if (fromServer.contains("GAME_OVER")) {
                    model.gameWon();
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}





