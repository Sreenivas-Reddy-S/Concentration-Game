package client.controller;

import client.Listener;
import model.ConcentrationModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * The controller that serves as the handles input from the
 * user and the network
 *
 * @author : Kruthi Nagabhushan
 * @author : Sreenivas Reddy Sagili
 */
public class ConcentrationController {
    /**
     * to store portnumber
     */
    public int portNumber;
    /**
     * to store hostname
     */
    public String hostName;

    /**
     * model object
     */
    public ConcentrationModel cm;

    /**
     * Socket object
     */
    public Socket socket;

    /**
     * PrintWriter Object
     */
    public PrintWriter out;

    /**
     * BufferedReader object
     */
    public BufferedReader in;

    /**
     * Listener object
     */
    public Listener listener;


    /**
     * class constructor
     *
     * @param portNumber portnumber
     * @param hostName   hostname
     * @param cm         model
     */
    public ConcentrationController(int portNumber, String hostName, ConcentrationModel cm) throws IOException {
        this.hostName = hostName;
        this.portNumber = portNumber;
        this.cm = cm;

        socket = new Socket(hostName, portNumber);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        this.listener = new Listener(socket, in, out, cm);
    }

    /**
     * method to read first server message i.e board dimension and to create board
     */
    public void playGame() {
        try {
            // to read server messages
            String fromServer;
            int size;
            fromServer = in.readLine();
            size = Integer.parseInt(fromServer.split(" ")[1]);
            cm.createBoard(size);
            listener.start();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }

    /**
     * method to print client's message
     *
     * @param row    row value
     * @param column column value
     */
    public void makeReveal(int row, int column) {
        out.println("REVEAL " + row + " " + column);
    }
}

