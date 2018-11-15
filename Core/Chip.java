package Core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.Serializable;

public class Chip implements Serializable {
    enum Player {X, O, EMPTY}
    Player player;

    int chipSheetX;
    int chipSheetY;
    int width;
    int height;
    int xPos;
    int yPos;

    /**
     * Constructor to create new Chip and assign pixel info for rendering
     *
     * @param col   Column
     * @param row   Row
     * @param p     Ownership of the chip, enum Player {X, O, EMPTY}
     */
    public Chip(int col, int row, Player p) {
        player = p;
        switch(player) {
            case X:
                chipSheetX = 0;
                chipSheetY = 0;
                break;
            case O:
                chipSheetX = 0;
                chipSheetY = 67;
                break;
            case EMPTY:
                chipSheetX = 67;
                chipSheetY = 67;
                break;
        }
        width = 67;
        height = 67;
        if (col == 0) {
            xPos = 16 + (70 * col);
        } else {
            xPos = 17 + (20 * col) + (70 * col);
        }
        if (row == 0) {
            yPos = 7 + (70 * row);
        } else {
            yPos = 7 + (10 * row) + (70 * row);
        }
    }

    /**
     * Constructor to create a new Chip
     *
     * @param p Ownership of the chip, enum Player {X, O, EMPTY}
     */
    public Chip(Player p) {
        player = p;
    }



    /**
     * Prints the type of Chip it is
     *
     * @return  can return "X", "O", or " "
     */
    public String toString() {
        switch(player) {
            case X:
                return "X";
            case O:
                return "O";
            default:
                return " ";
        }
    }

    /**
     * Draws an image of the chip on the graphics context
     *
     * @param gc GraphicsContext to draw on
     */
    public void render(GraphicsContext gc) {
        Image chips = new Image("UI/chips.png");
        gc.drawImage(chips, chipSheetX, chipSheetY, width, height, xPos, yPos, width, height);
    }
}
