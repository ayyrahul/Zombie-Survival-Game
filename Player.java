import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** Represents the player-controlled character. */
public class Player {

    int x, y;
    int size;
    int speed;
    // The running sprite naturally faces right, so 0 radians is its forward direction.
    private double facingAngle = 0.0;
    private int animationFrame = 0;
    // Rendered (scaled) sprite size in pixels. The PNG is 96x96; it is scaled down to this.
    private static final int SPRITE_SIZE = 36;
    private static final BufferedImage SPRITE = loadSprite("assets/player.png");

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = 24; // hitbox: roughly the character's torso, not the full sprite frame
        this.speed = 4;
    }

    private static BufferedImage loadSprite(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (Exception e) {
            System.out.println("Could not load player sprite: " + path);
            return null;
        }
    }

    public void move(boolean up, boolean down, boolean left, boolean right,
                     boolean sprint, int panelWidth, int panelHeight) {
        int currentSpeed = sprint ? speed * 2 : speed;

        if (up) y -= currentSpeed;
        if (down) y += currentSpeed;
        if (left) x -= currentSpeed;
        if (right) x += currentSpeed;

        int directionX = (right ? 1 : 0) - (left ? 1 : 0);
        int directionY = (down ? 1 : 0) - (up ? 1 : 0);
        if (directionX != 0 || directionY != 0) {
            // This sprite faces right by default.
            facingAngle = Math.atan2(directionY, directionX);
            animationFrame++;
        }

        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x > panelWidth - size) x = panelWidth - size;
        if (y > panelHeight - size) y = panelHeight - size;
    }

    public void draw(Graphics g) {
        if (SPRITE == null) {
            g.setColor(Color.BLUE);
            g.fillOval(x, y, size, size);
            return;
        }

        // A small alternating vertical bob makes the running pose feel animated.
        int bob = (animationFrame / 5) % 2 == 0 ? -1 : 1;
        Graphics2D g2 = (Graphics2D) g.create();
        double centerX = x + size / 2.0;
        double centerY = y + size / 2.0 + bob;
        AffineTransform transform = new AffineTransform();
        transform.translate(centerX, centerY);
        transform.rotate(facingAngle);
        transform.translate(-SPRITE_SIZE / 2.0, -SPRITE_SIZE / 2.0);
        // Scale the 96x96 source image down to SPRITE_SIZE (previously drawn unscaled).
        transform.scale((double) SPRITE_SIZE / SPRITE.getWidth(), (double) SPRITE_SIZE / SPRITE.getHeight());
        g2.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(SPRITE, transform, null);
        g2.dispose();
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}
