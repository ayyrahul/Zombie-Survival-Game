import javax.swing.JFrame;

/**
 * Entry point. Creates the game window and starts the game.
 */
public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Zombie Survival");
        GamePanel gamePanel = new GamePanel();

        frame.add(gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);

        gamePanel.requestFocusInWindow();
    }
}
