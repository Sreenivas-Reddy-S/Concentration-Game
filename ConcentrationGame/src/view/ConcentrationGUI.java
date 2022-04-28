package view;

import client.controller.ConcentrationController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.ConcentrationModel;
import model.Observer;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * The JavaFX compliant GUI
 *
 * @author : Kruthi Nagabhushan
 * @author : Sreenivas Reddy Sagili
 */
public class ConcentrationGUI extends Application implements Observer<ConcentrationModel> {

    /**
     * creating Model object i.e object of class ConcentrationModel
     */
    private ConcentrationModel model = new ConcentrationModel();

    /**
     * creating controller object i.e object of class ConcentrationController
     */
    private ConcentrationController controller;

    /**
     * creating a new gridpane
     */
    private GridPane gridPane;

    /**
     * initializing image
     */
    private Image empty;

    /**
     * Creating a new labels
     */
    private Label label1;
    private Label label2;
    private Label label3;

    /**
     * variable to store hostname
     */
    private String hostName;

    /**
     * variable to store portnumber
     */
    private int portNumber;


    /**
     * This method creates the model and add ourselves as an observer
     */
    public void init() throws IOException {

        List<String> args = getParameters().getRaw();
        if (args.size() != 2) {
            System.out.println("Incorrect number of arguments");
            System.exit(1);
        } else {
            this.hostName = args.get(0);
            try {
                this.portNumber = Integer.parseInt(args.get(1));
            } catch (NumberFormatException e) {
                System.out.println("Enter valid port number");
            }
        }

        this.model = new ConcentrationModel();
        this.controller = new ConcentrationController(portNumber, hostName, this.model);
        model.addObserver(this);
    }


    /**
     * start method
     *
     * @param stage stage
     */
    @Override
    public void start(Stage stage) {
        // it will instantiate the border pane
        BorderPane borderPane = new BorderPane();

        // it will instantiate the hbox
        HBox hbox = new HBox();

        //calling playgame method of controller class
        controller.playGame();

        //to create a gridPane
        makeGridPane();

        borderPane.setTop(gridPane);
        borderPane.setTop(gridPane);

        //creating labels
        label1 = new Label();
        label1.setText("Moves Made:" + model.getMoves());
        label1.setFont(new Font("Monotype Corsiva", 15));

        label2 = new Label();
        label2.setText("Matches:" + model.getNoOfMatchCards());
        label2.setFont(new Font("Monotype Corsiva", 15));

        label3 = new Label();
        label3.setText("Status: OK");
        label3.setFont(new Font("Monotype Corsiva", 15));

        // Adding labels to the hbox
        hbox.getChildren().add(label1);
        hbox.getChildren().add(label2);
        hbox.getChildren().add(label3);
        hbox.setSpacing(140);
        borderPane.setBottom(hbox);

        // It will instantiate and passes the border pane into the scene
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.setTitle("Concentration");
        stage.setWidth(1000);

        // it will not allow the stage to resize
        stage.setResizable(false);

        // it will display the stage
        stage.show();
    }

    /**
     * method to create a gridpane and its children such as buttons
     */
    private void makeGridPane() {

        // gap between items in grid pane
        int gap = 10;

        gridPane = new GridPane();
        gridPane.setVgap(gap);
        gridPane.setHgap(gap);

        //Loop for the controller to execute the move and to populate the grid
        for (int row = 0; row < model.DIM; row++) {
            for (int column = 0; column < model.DIM; column++) {
                Button button = new Button();
                String img = getImage(".");
                empty = new Image(Objects.requireNonNull(getClass().getResourceAsStream(img)));
                ImageView view = new ImageView(empty);
                button.setGraphic(view);
                gridPane.add(button, column, row);

                // lambda function for the controller to perform the action
                int finalColumn = column;
                int finalRow = row;
                button.setOnAction(actionEvent -> {
                    if (model.gameStatus != 1) {
                        controller.makeReveal(finalRow, finalColumn);
                    }
                });

            }
        }

    }


    /**
     * The GUI components, the buttons and labels, updates in this method by getting
     * the current value from the model.
     *
     * @param model the model
     */
    private void refresh(ConcentrationModel model) {
        ImageView view;

        //Loop for the player to place the disc
        for (int row = 0; row < model.DIM; row++) {
            for (int column = 0; column < model.DIM; column++) {
                Button button = (Button) gridPane.getChildren().get(row * model.DIM + column);

                String letter = model.getCellValue(row, column);
                String image = getImage(letter);
                empty = new Image(Objects.requireNonNull(getClass().getResourceAsStream(image)));
                view = new ImageView(empty);
                button.setGraphic(view);

            }
        }

        // The label is to update the moves of the game
        label1.setText("Moves Made:" + model.getMoves());
        label2.setText("Matches:" + model.getNoOfMatchCards());
        if (model.gameStatus == 1) {
            label3.setText("Status: GAME OVER");
            for (int row = 0; row < model.DIM; row++) {
                for (int column = 0; column < model.DIM; column++) {
                    Button button = (Button) gridPane.getChildren().get(row * model.DIM + column);
                    button.setDisable(true);
                }
            }
        }
    }

    /**
     * This method updates the content.
     *
     * @param model : the model
     */
    @Override
    public void update(ConcentrationModel model) {
        if (Platform.isFxApplicationThread()) {
            this.refresh(model);
        } else {
            Platform.runLater(() -> this.refresh(model));
        }
    }


    /**
     * methos to assign each character with the image using switch
     *
     * @param letter String alphabet
     * @return image string
     */
    public String getImage(String letter) {
        String img = switch (letter) {
            case "A" -> "abra.png";
            case "B" -> "bulbasaur.png";
            case "C" -> "charizard.png";
            case "D" -> "diglett.png";
            case "E" -> "golbat.png";
            case "F" -> "golem.png";
            case "G" -> "jigglypuff.png";
            case "H" -> "magikarp.png";
            case "I" -> "meowth.png";
            case "J" -> "mewtwo.png";
            case "K" -> "natu.png";
            case "L" -> "pidgey.png";
            case "M" -> "pikachu.png";
            case "." -> "pokeball.png";
            case "O" -> "poliwag.png";
            case "P" -> "psyduck.png";
            case "Q" -> "rattata.png";
            case "R" -> "slowpoke.png";
            case "S" -> "snorlak.png";
            case "N" -> "squirtle.png";
            default -> "";
        };
        return img;
    }


    /**
     * The main runs the JavaFX application.
     *
     * @param args ignored
     * @see Application#launch
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}
