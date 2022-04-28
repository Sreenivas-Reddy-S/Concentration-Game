package model;

import java.util.LinkedList;
import java.util.List;

/**
 * : The model class that contains the representation of the grid of
 * cards, along with statistics and the status of the game
 *
 * @author : Kruthi Nagabhushan
 * @author : Sreenivas Reddy Sagili
 */
public class ConcentrationModel {

    /**
     * variable to store grid dimension
     */
    public int DIM;

    /**
     * variable to store number of moves made by user
     */
    public int moves;

    /**
     * variable to store the count of matched cards
     */
    private int noOfMatchCards;

    /**
     * the observers of this model
     */
    private final List<Observer<ConcentrationModel>> observers;
    /**
     * the game board
     */
    private String[][] board;

    /**
     * flag for game status
     */
    public int gameStatus;


    /**
     * Create a new board.
     */
    public ConcentrationModel() {
        this.observers = new LinkedList<>();
    }

    /**
     * The view calls this method to add themselves as an observer of the model.
     *
     * @param observer the observer
     */
    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    /**
     * When the model changes, the observers are notified via their update() method
     */
    private void notifyObservers() {
        for (Observer obs : this.observers) {
            obs.update(this);
        }
    }


    /**
     * This method creates the client board.
     */
    public void createBoard(int DIM) {
        this.DIM = DIM;
        this.board = new String[DIM][DIM];
        for (int row = 0; row < DIM; ++row) {
            for (int col = 0; col < DIM; ++col) {
                this.board[row][col] = ".";
            }
        }
    }

    /**
     * This method checks for the boundaries and sets a character on the board.
     *
     * @param row    : row
     * @param col    : col
     * @param letter : character
     */
    public void editBoard(int row, int col, String letter) {
        this.board[row][col] = letter;

    }

    /**
     * to get the value in particular cell
     *
     * @param row row value
     * @param col col value
     * @return String
     */
    public String getCellValue(int row, int col) {
        return this.board[row][col];
    }


    /**
     * method to read card coordinates from server
     *
     * @param fromServer string
     */
    public void readCard(String fromServer) {
        moves++;
        this.editBoard(Integer.parseInt(fromServer.split(" ")[1]), Integer.parseInt(fromServer.split(" ")[2]), fromServer.split(" ")[3]);
        notifyObservers();
    }

    /**
     * method to mismatch messages from server
     *
     * @param fromServer string
     */
    public void readMismatch(String fromServer) {
        String[] list = fromServer.split(" ");
        this.editBoard(Integer.parseInt(list[1]), Integer.parseInt(list[2]), ".");
        this.editBoard(Integer.parseInt(list[3]), Integer.parseInt(list[4]), ".");
        notifyObservers();

    }

    /**
     * method to read match messages from server and to update the count of number of matched cards
     */
    public void readMatch() {
        this.noOfMatchCards++;
        notifyObservers();
    }

    /**
     * method to change the game status flag
     */
    public void gameWon() {
        gameStatus = 1;
    }

    /**
     * getter method of number of matched cards
     *
     * @return int matched cards
     */
    public int getNoOfMatchCards() {
        return this.noOfMatchCards;
    }


    /**
     * getter method of number of moves
     *
     * @return int moves
     */
    public int getMoves() {
        return moves;
    }
}
