package Core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.Serializable;

public class Board implements Serializable {
    public Chip[][] matrix;
    public String errorMsg;
    public int playersTurn;
    public int winner;
    public boolean gameOver;

    /**
     * Constructor creates the board as a 7 by 6 matrix and populates each element with empty pieces
     */
    public Board() {
        matrix = new Chip[6][7];
        errorMsg = "";
        playersTurn = 1;
        gameOver = false;
        for(int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j] = new Chip(j, i, Chip.Player.EMPTY);
            }
        }
    }

    /**
     * Adds a piece to the target (x, y) position for player 1 or 2.
     *
     * @param x     x coordinate
     * @param y     y coordinate
     * @param player    player number 1 or 2
     */
    private void addChip(int x, int y, int player) {
        x = x-1;
        y = y-1;
        if (player == 1) {
            matrix[y][x] = new Chip(x, y, Chip.Player.X);
        } else {
            matrix[y][x] = new Chip(x, y, Chip.Player.O);
        }
    }

    /**
     * Used for players to move during their turn. Calls Core.Board.addChip(int x, int y, int player) private function
     *
     * @param column    Column of the players move
     * @param player    Player who is moving
     */
    public void move(int column, int player) {
        int i = 6;
        boolean checking = true;
        while (checking) {
            if (matrix[i-1][column-1].player == Chip.Player.EMPTY) {
                addChip(column, i, player);
                errorMsg = "";
                checking = false;
            } else {
                i--;
                if (i == 0) {
                    checking = false;
                    System.out.println("Error, too many pieces");
                }
            }
        }
        if (checkWin()) {
            gameOver = true;
            winner = playersTurn;
        } else if(!checkOpenSpace()) {
            gameOver = true;
            winner = 0;
        }
    }

    /**
     * Checks to see if a player won
     *
     * @return true if a player has 4 in a row
     */
    public boolean checkWin() {
        String target;
        if (playersTurn == 1) {
            target = "X";
        } else {
            target = "O";
        }

        //Horizontal
        for (int i  = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix.length-3; j++) {
                if (matrix[i][j].toString() == target &&
                        matrix[i][j+1].toString() == target &&
                        matrix[i][j+2].toString() == target &&
                        matrix[i][j+3].toString() == target) {
                    return true;
                }
            }
        }

        //Vertical
        for (int i  = 0; i < matrix.length-3; i++) {
            for(int j = 0; j < matrix.length; j++) {
                if (matrix[i][j].toString() == target &&
                        matrix[i+1][j].toString() == target &&
                        matrix[i+2][j].toString() == target &&
                        matrix[i+3][j].toString() == target) {
                    return true;
                }
            }
        }

        //Diagonal left
        for (int i  = 0; i < 3; i++) {
            for(int j = 0; j < 4; j++) {

                if (matrix[i][j].toString() == target &&
                        matrix[i+1][j+1].toString() == target &&
                        matrix[i+2][j+2].toString() == target &&
                        matrix[i+3][j+3].toString() == target) {
                    return true;
                }
            }
        }

        //Diagonal right
        for (int i  = 0; i < 3; i++) {
            for(int j = 3; j < matrix.length; j++) {
                if (matrix[i][j].toString() == target &&
                        matrix[i+1][j-1].toString() == target &&
                        matrix[i+2][j-2].toString() == target &&
                        matrix[i+3][j-3].toString() == target) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks to see if there is an open space on the board
     *
     * @return true if there is an open space on the board
     */
    public boolean checkOpenSpace() {
        boolean openSpace = false;
        Chip c = new Chip(Chip.Player.EMPTY);
        for(int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if(matrix[i][j].toString() == c.toString()) {
                    openSpace = true;
                }
            }
        }
        return openSpace;
    }

    /**
     * Prints board and the Chips within the matrix
     *
     * @return  String of the board/pieces
     */
    public String toString() {
        String buffer = "";
        buffer += " (1) (2) (3) (4) (5) (6) (7) \n";
        for(int i = 0; i < matrix.length; i++) {
            buffer += "# ";
            for (int j = 0; j < matrix[0].length; j++) {
                buffer += matrix[i][j];
                buffer += " # ";
            }
            buffer += "\n#############################\n";
        }
        return buffer;
    }

    /**
     * Draws an image of the board on the graphics context
     *
     * @param gc    GraphicsContext to draw on
     */
    public void render(GraphicsContext gc) {
        Image boardImg = new Image("UI/Connect4Board.png");
        gc.drawImage(boardImg, 0,0);
        for(int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrix[i][j].render(gc);
            }
        }
    }
}

