package Core;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import static java.lang.Thread.sleep;

public class Connect4Client extends Application {

    int WINDOW_WIDTH = 700;
    int WINDOW_HEIGHT = 600;
    int MENU_WIDTH = 300;
    int MENU_HEIGHT = 200;
    Board board;
    Canvas canvas;
    GraphicsContext gc;
    String HOST = "localhost";
    int PORT = 8000;
    Socket socket;
    ObjectOutputStream toServer;
    ObjectInputStream fromServer;
    Text message;
    Text playerNum;
    int playerNumber;
    boolean waiting;
    int move;

    Scene gameScene;
    Scene menuScene;

    /**
     * Start the Connect4 Client
     *
     * @param primaryStage  Main window
     */
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        canvas = new Canvas(640, 480);
        gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        Image connectionScreen = new Image("UI/ConnectionScreen.png");
        gc.drawImage(connectionScreen, 0, 50);


        /************************
         * BUTTONS TO MAKE MOVE
         ************************/
        Button[] buttons = new Button[7];

        for (int i = 0; i < 7; i++) {
            buttons[i] = new Button();
            buttons[i].setText("  v  ");
            buttons[i].setMinWidth(90);
        }

        buttons[0].setOnAction(e -> {
            if(playerNumber == board.playersTurn) {
                move = 1;
                waiting = false;
            } else {
                if (!board.gameOver) {
                    message.setText("It's not your turn yet!");
                }
            }
        });
        buttons[1].setOnAction(e -> {
            if(playerNumber == board.playersTurn) {
                move = 2;
                waiting = false;
            } else {
                if (!board.gameOver) {
                    message.setText("It's not your turn yet!");
                }
            }
        });
        buttons[2].setOnAction(e -> {
            if(playerNumber == board.playersTurn) {
                move = 3;
                waiting = false;
            } else {
                if (!board.gameOver) {
                    message.setText("It's not your turn yet!");
                }
            }
        });
        buttons[3].setOnAction(e -> {
            if(playerNumber == board.playersTurn) {
                move = 4;
                waiting = false;
            } else {
                if (!board.gameOver) {
                    message.setText("It's not your turn yet!");
                }
            }
        });
        buttons[4].setOnAction(e -> {
            if(playerNumber == board.playersTurn) {
                move = 5;
                waiting = false;
            } else {
                if (!board.gameOver) {
                    message.setText("It's not your turn yet!");
                }
            }
        });
        buttons[5].setOnAction(e -> {
            if(playerNumber == board.playersTurn) {
                move = 6;
                waiting = false;
            } else {
                if (!board.gameOver) {
                    message.setText("It's not your turn yet!");
                }
            }
        });
        buttons[6].setOnAction(e -> {
            if(playerNumber == board.playersTurn) {
                move = 7;
                waiting = false;
            } else {
                if (!board.gameOver) {
                    message.setText("It's not your turn yet!");
                }
            }
        });


        /********
         * TITLE
         ********/
        Text title = new Text("Connect4");
        title.setStyle("-fx-font: 30 arial;");
        title.setFill(Color.DARKRED);
        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0f);
        ds.setColor(Color.color(0.4f, 0.4f, 0.4f));
        title.setEffect(ds);

        message = new Text("Choose an option to begin");
        message.setStyle("-fx-font: 20 arial;");


        //Top Pane
        HBox topPane = new HBox();
        VBox centerPane = new VBox();
        centerPane.setAlignment(Pos.CENTER);
        topPane.setAlignment(Pos.CENTER);
        topPane.setSpacing(100);
        playerNum = new Text("");

        //Add buttons
        HBox buttonPane = new HBox();
        buttonPane.setAlignment(Pos.CENTER);
        for(int i = 0; i < 7; i++) {
            buttonPane.getChildren().add(buttons[i]);
        }

        /**************************************
         * Choice menu to VS Computer or Player
         **************************************/
        gameScene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        Button vsComp = new Button("vs Computer");
        Button vsPlayer = new Button("vs Player");
        vsComp.setMinWidth(200);
        vsComp.setMinHeight(45);
        vsComp.setOnAction(e->{
            waiting = true;
            joinGame(1);
            primaryStage.setMinWidth(WINDOW_WIDTH);
            primaryStage.setMaxWidth(WINDOW_WIDTH);
            primaryStage.setMinHeight(WINDOW_HEIGHT);
            primaryStage.setMaxHeight(WINDOW_HEIGHT);
            topPane.getChildren().add(title);
            topPane.getChildren().add(playerNum);
            centerPane.getChildren().add(buttonPane);
            centerPane.getChildren().add(canvas);
            centerPane.getChildren().add(message);
            root.setTop(topPane);
            root.setCenter(centerPane);
            primaryStage.setScene(gameScene);
        });
        vsPlayer.setMinWidth(200);
        vsPlayer.setMinHeight(45);
        vsPlayer.setOnAction(e->{
            waiting = true;
            joinGame(2);
            primaryStage.setMinWidth(WINDOW_WIDTH);
            primaryStage.setMaxWidth(WINDOW_WIDTH);
            primaryStage.setMinHeight(WINDOW_HEIGHT);
            primaryStage.setMaxHeight(WINDOW_HEIGHT);
            topPane.getChildren().add(title);
            topPane.getChildren().add(playerNum);
            centerPane.getChildren().add(buttonPane);
            centerPane.getChildren().add(canvas);
            centerPane.getChildren().add(message);
            root.setTop(topPane);
            root.setCenter(centerPane);
            primaryStage.setScene(gameScene);
        });

        //Add menu items
        VBox menuPane = new VBox();
        menuPane.setAlignment(Pos.CENTER);
        menuPane.getChildren().add(title);
        menuPane.getChildren().add(vsComp);
        menuPane.getChildren().add(vsPlayer);
        menuPane.getChildren().add(message);
        menuScene = new Scene(menuPane, MENU_WIDTH, MENU_HEIGHT);

        //Display
        primaryStage.setTitle("Connect4");
        primaryStage.setMinWidth(MENU_WIDTH);
        primaryStage.setMaxWidth(MENU_WIDTH);
        primaryStage.setMinHeight(MENU_HEIGHT);
        primaryStage.setMaxHeight(MENU_HEIGHT);
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    /**
     * Spawns thread to connectToServer
     *
     * @param buttonPressed int for user's choice of vsComputer(1) or vsPlayer(2)
     */
    public void joinGame(int buttonPressed) {
        message.setText("Please Wait...");
        new Thread(()-> connectToServer(buttonPressed)).start();
    }

    /**
     * Main game loop for the Client
     */
    public void beginGameSession() {
        requestPlayerNumber();
        requestBoard();
        while(!board.gameOver) {
            System.out.println("in while");
            if(playerNumber == board.playersTurn) {
                System.out.println("in if");
                waitForPlayerAction();
                sendTurn(move);
            }
            requestBoard();
        }
        switch (board.winner) {
            case 0:
                message.setText("No more spaces.. Tie game.");
                break;
            case 1:
                message.setText("PLAYER 1 WINS!");
                break;
            case 2:
                message.setText("PLAYER 2 WINS!");
                break;
        }

    }

    /**
     * Waits until player makes their move
     */
    private void waitForPlayerAction() {
        try{
            System.out.println("Waiting for player's move");
            while (waiting) {
                Thread.sleep(100);
            }
            System.out.println("Done Waiting");
            waiting = true;
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Read a Board object from the server
     */
    public void requestBoard() {
        try {
            System.out.println("Attempting to receive board");
            board = (Board)fromServer.readObject();
            System.out.println("Received board");
            System.out.println("board.playersTurn = " + board.playersTurn + "\n");
            message.setText("Player " + board.playersTurn + "'s Turn");
            System.out.println(board.toString());
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            board.render(gc);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Read an Integer object from the server
     */
    public void requestPlayerNumber() {
        try {
            playerNumber = (Integer)fromServer.readObject();
            System.out.println("Player number:" + playerNumber);
            playerNum.setText("You are Player " + playerNumber);
            message.setText("Player 1 goes first..");
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Send the column that player would like to move to the server
     *
     * @param col   Column of the player's move
     */
    public void sendTurn(int col) {
        try {
            System.out.println("Attempting to send move " + col);
            toServer.writeObject(new Integer(col));
            System.out.println("Sent move to server : " + col);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * Establish connection to server with sockets and output/input streams
     *
     * @param buttonPressed int for user's choice of vsComputer(1) or vsPlayer(2)
     */
    public void connectToServer(int buttonPressed) {
        try {
            sleep(1000);
            socket = new Socket(HOST, PORT);
            toServer = new ObjectOutputStream(socket.getOutputStream());
            fromServer = new ObjectInputStream(socket.getInputStream());
            toServer.writeObject(new Integer(buttonPressed));
            beginGameSession();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
