package Core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


public class Connect4Server extends Application {
    int WINDOW_WIDTH = 600;
    int WINDOW_HEIGHT = 400;
    int PORT = 8000;
    TextArea logArea = new TextArea();
    StringBuilder sb = new StringBuilder();

    /**
     * Start Server UI
     *
     * @param primaryStage  Main window
     */
    @Override
    public void start(Stage primaryStage) {
        logArea.setMinWidth(WINDOW_WIDTH);
        logArea.setMaxWidth(WINDOW_WIDTH);
        logArea.setMinHeight(WINDOW_HEIGHT);
        logArea.setMaxHeight(WINDOW_HEIGHT);
        logArea.setEditable(false);
        VBox logPane = new VBox();
        logPane.getChildren().add(logArea);
        log("Server starting up...", sb, logArea);

        Scene scene = new Scene(logPane, WINDOW_WIDTH, WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();

        //Thread needed to prevent JavaFX UI Freezing
        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(PORT);
                log("Server started on port " + PORT, sb, logArea);
                int sessionNum = 0;
                int waitingPlayers = 0;

                Socket socket[] = new Socket[2];
                ObjectOutputStream toClient[] = new ObjectOutputStream[2];
                ObjectInputStream fromClient[] = new ObjectInputStream[2];

                Socket incomingPlayer;
                ObjectOutputStream toIncomingPlayer;
                ObjectInputStream fromIncomingPlayer;

                /*************
                 * SERVER LOOP
                 *************/
                while (true) {
                    incomingPlayer = serverSocket.accept();
                    log("New Player joined", sb, logArea);
                    log("Player's IP: " + incomingPlayer.getInetAddress().getHostAddress(), sb, logArea);
                    toIncomingPlayer = new ObjectOutputStream(incomingPlayer.getOutputStream());
                    log("Player's output stream established", sb, logArea);
                    fromIncomingPlayer = new ObjectInputStream(incomingPlayer.getInputStream());
                    log("Player's input stream established", sb, logArea);
                    Integer x = (Integer)fromIncomingPlayer.readObject();
                    if (x.equals(new Integer(1))) {
                        sessionNum++;
                        log("Session: " + sessionNum +
                                " - Player 1 vs Computer", sb, logArea);
                        HandleClient task = new HandleClient(incomingPlayer, fromIncomingPlayer, toIncomingPlayer, sessionNum);
                        new Thread(task).start();
                    } else {
                        waitingPlayers++;
                        if (waitingPlayers == 1) {
                            log("Player 1 vs Player 2", sb, logArea);
                            socket[waitingPlayers-1] = incomingPlayer;
                            toClient[waitingPlayers-1] = toIncomingPlayer;
                            fromClient[waitingPlayers-1] = fromIncomingPlayer;
                            log("Waiting for another player...", sb, logArea);
                        } else if (waitingPlayers == 2){
                            sessionNum++;
                            log("Session: " + sessionNum +
                                    " - Two players ready!", sb, logArea);
                            socket[waitingPlayers-1] = incomingPlayer;
                            toClient[waitingPlayers-1] = toIncomingPlayer;
                            fromClient[waitingPlayers-1] = fromIncomingPlayer;
                            waitingPlayers = 0;
                            HandleClient task = new HandleClient(socket, fromClient, toClient, sessionNum);
                            new Thread(task).start();
                            socket = new Socket[2];
                            toClient = new ObjectOutputStream[2];
                            fromClient = new ObjectInputStream[2];
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }).start();
    }

    /**
     * Display message on the Server UI
     *
     * @param message   Message to display
     * @param sb    StringBuilder used to append message
     * @param ta    TextArea to setText
     */
    public void log(String message, StringBuilder sb, TextArea ta) {
        Platform.runLater(() -> sb.append("[" + new Date() + "]" + " -- " + message + "\n"));
        Platform.runLater(() -> ta.setText(sb.toString()));
    }

    /**************************************
     * Handles an instance of a game thread
     **************************************/
    class HandleClient implements Runnable {
        int sessionNum;
        Socket socket[];
        ObjectOutputStream toClient[];
        ObjectInputStream fromClient[];
        Board board;
        boolean singlePlayer;
        Connect4ComputerPlayer connect4ComputerPlayer;

        /**
         * Handles Multiplayer gameplay
         *
         * @param s Socket[2] for each player
         * @param fromC ObjectInputStream[2] for each player
         * @param toC   ObjectInputStream[2] for each player
         * @param session   Session number
         */
        public HandleClient(Socket[] s, ObjectInputStream[] fromC, ObjectOutputStream[] toC, int session) {
            sessionNum = session;
            log("Session: " + sessionNum +
                    " - Game thread launched!", sb, logArea);
            socket = s;
            toClient = toC;
            log("Session: " + sessionNum +
                    " - Both player's output attached to thread", sb, logArea);
            fromClient = fromC;
            log("Session: " + sessionNum +
                    " - Both player's input attached to thread", sb, logArea);
            board = new Board();
            singlePlayer = false;
        }

        /**
         * Handles Singleplayer gameplay
         *
         * @param s Socket for the player
         * @param fromC ObjectInputStream from player
         * @param toC   ObjectOutputStream to player
         * @param session Session number
         */
        public HandleClient(Socket s, ObjectInputStream fromC, ObjectOutputStream toC, int session) {
            sessionNum = session;
            log("Session: " + sessionNum +
                    " - Game thread launched!", sb, logArea);
            System.out.println("Launch game thread");
            socket = new Socket[1];
            socket[0] = s;
            System.out.println("Assigned Socket");
            toClient = new ObjectOutputStream[1];
            toClient[0] = toC;
            System.out.println("Assigned Output stream");
            log("Session: " + sessionNum +
                    " - Both player's output attached to thread", sb, logArea);
            fromClient = new ObjectInputStream[1];
            fromClient[0] = fromC;
            log("Session: " + sessionNum +
                    " - Both player's input attached to thread", sb, logArea);
            board = new Board();
            singlePlayer = true;
            connect4ComputerPlayer = new Connect4ComputerPlayer();
        }

        /**
         * Main Game loop
         */
        public void run() {
            sendPlayerNumber();     //send 1 int
            sendBoard();            //send 1 board;
            while (!board.gameOver) {
                getPlayersMove();   //get 1 int
                sendBoard();        //send 1 board
            }
            switch (board.winner) {
                case 0:
                    log("No more spaces.. Tie game.", sb, logArea);
                    break;
                case 1:
                    log("PLAYER 1 WINS!", sb, logArea);
                    break;
                case 2:
                    log("PLAYER 2 WINS!", sb, logArea);
                    break;
            }
        }

        /**
         * Sends a Board object to each player
         */
        public void sendBoard() {
            try {
                if (!singlePlayer) {
                    toClient[1].reset();
                    toClient[1].writeObject(board);
                }
                toClient[0].reset();
                toClient[0].writeObject(board);
                log("Session: " + sessionNum +
                        " - Sent updated board to players" , sb, logArea);
            } catch (Exception e) {
                System.err.println(e);
            }
        }

        /**
         * Reads a move from the player who's turn it is
         */
        public void getPlayersMove() {
            try {
                if (board.playersTurn == 1) {
                    int move = (Integer)fromClient[0].readObject();
                    log("Session: " + sessionNum +
                            " - Received Player " + board.playersTurn +"'s move of " + move, sb, logArea);
                    board.move(move, board.playersTurn);
                    if (board.gameOver) {

                    } else {
                        board.playersTurn = 2;
                    }
                } else {
                    if(!singlePlayer) {
                        int move = (Integer)fromClient[1].readObject();
                        log("Session: " + sessionNum +
                                " - Received Player " + board.playersTurn +"'s move of " + move, sb, logArea);
                        board.move(move, board.playersTurn);
                    } else {
                        board.move(connect4ComputerPlayer.generateMove(), 2);
                    }
                    if (board.gameOver) {

                    } else {
                        board.playersTurn = 1;
                    }
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }

        /**
         * Sends an Integer to each player as their Player Number
         */
        public void sendPlayerNumber() {
            try {
                if (!singlePlayer) {
                    toClient[1].writeObject(new Integer(2));
                }
                toClient[0].writeObject(new Integer(1));
            } catch (Exception e) {
                System.err.println(e);
            }
        }

    }


    public static void main(String[] args) {
        launch(args);
    }
}

