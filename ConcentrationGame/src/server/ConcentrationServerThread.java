package server;

import common.ConcentrationException;
import common.ConcentrationProtocol;
import game.ConcentrationBoard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * This class establishes the multi-threading and is used to handle multiple clients at the same time.
 *
 * @author Kruthi Nagabhushan
 * @author Sreenivas Reddy Sagili
 */
public class ConcentrationServerThread extends Thread implements ConcentrationProtocol {

    /**
     * the socket
     */
    private Socket socket = null;
    /**
     * the dimension of the board
     */
    private int board_dimension;
    /**
     * the game board
     */
    private ConcentrationBoard game;
    /**
     * the total number of clients
     */
    private static int numOfClients = 0;
    /**
     * the current thread
     */
    private int clientId;

    /**
     * The Constructor.
     *
     * @param socket          : socket.
     * @param board_dimension : the board dimension.
     */
    public ConcentrationServerThread(Socket socket, int board_dimension) {
        super("ConcentrationClientServerThread");
        this.socket = socket;
        numOfClients += 1;
        clientId = numOfClients;
        this.board_dimension = board_dimension;
    }

    /**
     * The run method reads the inputs from the sockets and makes a move in the game board based on the input.
     */

    public void run() {

        System.out.println("Concentration server starting on port " + ", DIM=" + board_dimension);
        System.out.println("Client# " + clientId + " connected");

        // creating the board with the dimension.
        try {
            this.game = new ConcentrationBoard(board_dimension, true);
        } catch (ConcentrationException e) {
            System.out.println(e.getMessage());
        }

        // Reads the inputs from the sockets.
        try (
                Socket clientSocket = socket;
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {

            // printing a message once the client starts
            System.out.println("Client #" + clientId + ": Client started...");

            //sending the client the board dimensions message
            out.println(String.format(BOARD_DIM_MSG, this.board_dimension));

            // the input.
            String input;

            // loop to check.
            while ((input = in.readLine()) != null) {
                System.out.print("Client #" + clientId + ": ");
                System.out.println("received: " + input);

                try {
                    Move(input, out);
                } catch (ConcentrationException e) {
                    System.out.println(e.getMessage());
                    break;
                }

                System.out.println(game);

                // loop to check, whether the game is over or not.
                if (game.gameOver()) {
                    System.out.println("Client #" + clientId + " is ending");
                    socket.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method makes a move in the board, checks for the match and game over.
     *
     * @param input : the input from client side.
     * @param out   : the output to the client.
     * @throws ConcentrationException : if the dimensions are invalid.
     */

    public void Move(String input, PrintWriter out) throws ConcentrationException {

        // String array to split.

        String[] move = input.split(" ");

        // x and y co-ordinates.
        int x = Integer.parseInt(move[1]);
        int y = Integer.parseInt(move[2]);

        ConcentrationBoard.CardMatch match = null;

        // boundary checks.
        if (x >= board_dimension || y >= board_dimension || x < 0 || y < 0) {
            out.println((ERROR_MSG) + ("The dimensions are invalid " + "(" + x + " " + y + ")"));
            throw new ConcentrationException("Invalid dimensions");
        } else if (!game.getCard(x, y).isHidden()) {
            out.println(ERROR_MSG.formatted("The card is already revealed " + "(" + x + "," + y + ")"));
            System.out.print("Sending");
            System.out.print((ERROR_MSG).formatted("%n ", "The card is already revealed " + "(" + x + "," + y + ")"));
            throw new ConcentrationException("The card is already revealed");
        }

        // Make a move and send it to the client.
        else {
            match = game.reveal(x, y);
            out.println((CARD_MSG).formatted(x, y, game.getCard(x, y).getLetter()));
            System.out.print("Client #" + clientId + ": ");
            System.out.println("sending: " + (CARD_MSG).formatted(x, y, game.getCard(x, y).getLetter()));
        }

        // condition to check for the cards.
        if (match != null && match.isReady()) {
            System.out.println("entered match");
            // if the cards are matched.
            String msg;
            if (match.isMatch()) {
                msg = MATCH_MSG.formatted(match.getCard1().getRow(), match.getCard1().getCol(), match.getCard2().getRow(), match.getCard2().getCol());
            } else {
                msg = MISMATCH_MSG.formatted(match.getCard1().getRow(), match.getCard1().getCol(), match.getCard2().getRow(), match.getCard2().getCol());
            }
            System.out.println("Sleeping");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            out.println(msg);

            System.out.print("Client #" + clientId + ": ");
            System.out.println("sending: " + msg);
        }

        // if the game is over, then send a msg.
        if (game.gameOver()) {
            String msg = GAME_OVER_MSG;
            System.out.print("Client #" + clientId + ": ");
            System.out.println("sending: " + msg);
            out.println(msg);
        }
    }
}