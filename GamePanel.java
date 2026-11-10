import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

/**
 * The main game surface. Runs the ~60 FPS game loop via a Swing Timer,
 * reads keyboard input, updates all game objects, checks collisions,
 * spawns zombies, and paints everything to the screen.
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    private static final int FPS = 60;
    private static final int FRAME_DELAY_MS = 1000 / FPS;

    private final Timer timer;
    private final Random random;
    private final HashSet<Integer> pressedKeys;

    private Player player;
    private ArrayList<Zombie> zombies;
    private Game game;

    private int frameCount;
    private int spawnIntervalFrames; // frames between zombie spawns
    private int secondsSurvived;
    private int difficultyFrameCounter;
    private double zombieSpeedMultiplier;

    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.DARK_GRAY);
        setFocusable(true);
        addKeyListener(this);

        random = new Random();
        pressedKeys = new HashSet<>();
        game = new Game();

        initGame();

        timer = new Timer(FRAME_DELAY_MS, this);
        timer.start();
    }

    /** Resets everything needed for a fresh run (also used to restart). */
    private void initGame() {
        player = new Player(PANEL_WIDTH / 2, PANEL_HEIGHT / 2);
        zombies = new ArrayList<>();

        frameCount = 0;
        spawnIntervalFrames = FPS * 2; // every 2 seconds
        secondsSurvived = 0;
        difficultyFrameCounter = 0;
        zombieSpeedMultiplier = 1.0;

        game.reset();
    }

    private void spawnZombie() {
        int edge = random.nextInt(4); // 0=top, 1=bottom, 2=left, 3=right
        int x = 0;
        int y = 0;

        switch (edge) {
            case 0:
                x = random.nextInt(PANEL_WIDTH);
                y = 0;
                break;
            case 1:
                x = random.nextInt(PANEL_WIDTH);
                y = PANEL_HEIGHT;
                break;
            case 2:
                x = 0;
                y = random.nextInt(PANEL_HEIGHT);
                break;
            case 3:
                x = PANEL_WIDTH;
                y = random.nextInt(PANEL_HEIGHT);
                break;
        }

        // A mix of slow/normal/fast zombies, scaled by current difficulty.
        double baseSpeed = 1.2 + random.nextDouble() * 1.6;
        zombies.add(new Zombie(x, y, baseSpeed * zombieSpeedMultiplier));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!game.gameOver && !game.paused) {
            update();
        }
        repaint();
    }

    /** One tick of the game loop: move player, move zombies, check collisions, spawn, score. */
    private void update() {
        frameCount++;

        boolean up = pressedKeys.contains(KeyEvent.VK_UP);
        boolean down = pressedKeys.contains(KeyEvent.VK_DOWN);
        boolean left = pressedKeys.contains(KeyEvent.VK_LEFT);
        boolean right = pressedKeys.contains(KeyEvent.VK_RIGHT);
        boolean sprint = pressedKeys.contains(KeyEvent.VK_SHIFT);

        player.move(up, down, left, right, sprint, PANEL_WIDTH, PANEL_HEIGHT);

        for (Zombie z : zombies) {
            z.moveTowardsPlayer(player);
        }

        checkCollisions();

        if (frameCount % spawnIntervalFrames == 0) {
            spawnZombie();
        }

        if (frameCount % FPS == 0) {
            secondsSurvived++;
            game.addScore(1);
        }

        increaseDifficultyOverTime();
    }

    private void checkCollisions() {
        Rectangle playerBounds = player.getBounds();
        ArrayList<Zombie> toRemove = new ArrayList<>();

        for (Zombie z : zombies) {
            if (playerBounds.intersects(z.getBounds())) {
                toRemove.add(z);
                game.loseHealth();
            }
        }
        zombies.removeAll(toRemove);
    }

    /** Every 20 seconds, zombies get a little faster and spawn a little more often. */
    private void increaseDifficultyOverTime() {
        difficultyFrameCounter++;
        if (difficultyFrameCounter >= FPS * 20) {
            difficultyFrameCounter = 0;
            zombieSpeedMultiplier += 0.2;
            spawnIntervalFrames = Math.max(FPS / 2, spawnIntervalFrames - (FPS / 4));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        player.draw(g);
        for (Zombie z : zombies) {
            z.draw(g);
        }

        drawHud(g);

        if (game.paused) {
            drawCenteredBanner(g, "PAUSED", Color.YELLOW, 40, 0);
        }

        if (game.gameOver) {
            drawGameOverScreen(g);
        }
    }

    private void drawHud(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + game.score, 20, 30);
        g.drawString("Health: " + game.health, 20, 55);
        g.drawString("Time Survived: " + secondsSurvived + "s", 20, 80);
        g.drawString("High Score: " + game.highScore, 20, 105);
    }

    private void drawGameOverScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        centerString(g, "GAME OVER", PANEL_HEIGHT / 2 - 40);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 24));
        centerString(g, "Score : " + game.score, PANEL_HEIGHT / 2 + 10);
        centerString(g, "Press R to Restart", PANEL_HEIGHT / 2 + 45);
    }

    private void drawCenteredBanner(Graphics g, String text, Color color, int fontSize, int yOffset) {
        g.setColor(color);
        g.setFont(new Font("Arial", Font.BOLD, fontSize));
        centerString(g, text, PANEL_HEIGHT / 2 + yOffset);
    }

    private void centerString(Graphics g, String text, int y) {
        int textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, (PANEL_WIDTH - textWidth) / 2, y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());

        if (e.getKeyCode() == KeyEvent.VK_R && game.gameOver) {
            initGame();
        }

        if (e.getKeyCode() == KeyEvent.VK_P && !game.gameOver) {
            game.paused = !game.paused;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not needed for this game.
    }
}
