import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Holds overall game state: score, health, pause/game-over flags,
 * and reads/writes the persistent high score file.
 */
public class Game {

    private static final String HIGHSCORE_FILE = "highscore.txt";

    int score;
    int health;
    boolean gameOver;
    boolean paused;
    int highScore;

    public Game() {
        highScore = loadHighScore();
        reset();
    }

    public void reset() {
        score = 0;
        health = 3;
        gameOver = false;
        paused = false;
    }

    public void addScore(int amount) {
        score += amount;
    }

    /**
     * Called when the player collides with a zombie.
     * Reduces health and ends the game (updating the high score) at zero.
     */
    public void loseHealth() {
        if (gameOver) return;

        health--;
        if (health <= 0) {
            health = 0;
            gameOver = true;
            if (score > highScore) {
                highScore = score;
                saveHighScore(highScore);
            }
        }
    }

    private int loadHighScore() {
        File file = new File(HIGHSCORE_FILE);
        if (!file.exists()) return 0;

        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextInt()) {
                return scanner.nextInt();
            }
        } catch (IOException e) {
            System.out.println("Could not read high score: " + e.getMessage());
        }
        return 0;
    }

    private void saveHighScore(int value) {
        try (FileWriter writer = new FileWriter(HIGHSCORE_FILE)) {
            writer.write(String.valueOf(value));
        } catch (IOException e) {
            System.out.println("Could not save high score: " + e.getMessage());
        }
    }
}
