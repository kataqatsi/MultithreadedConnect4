package Core;
import java.util.Random;

public class Connect4ComputerPlayer {
    int theMove;
    Random rand;

    /**
     * Initializes random value for move
     */
    public Connect4ComputerPlayer() {
        rand = new Random();
        theMove = rand.nextInt(7) + 1;
    }

    /**
     * Generate a move for the computer player
     *
     * @return an int between 1 and 7
     */
    public int generateMove() {
        theMove = rand.nextInt(7) + 1;
        return theMove;
    }
}