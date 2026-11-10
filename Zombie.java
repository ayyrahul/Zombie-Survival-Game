import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** Represents a running zombie that continuously chases the player. */
public class Zombie {

    int x, y;
    int size;
    double speed;
    // The running zombie sprite faces right by default.
    private double facingAngle = 0.0;
    private int animationFrame = 0;
    // Rendered (scaled) sprite size in pixels. The PNG is 96x96; it is scaled down to this.
    private static final int SPRITE_SIZE = 32;
    private static final BufferedImage SPRITE = loadSprite("assets/zombie.png");

    public Zombie(int x, int y, double speed) {
        this.x = x;
        this.y = y;
        this.size = 22; // hitbox: roughly the zombie's torso, not the full sprite frame
        this.speed = speed;
    }

    private static BufferedImage loadSprite(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (Exception e) {
            System.out.println("Could not load zombie sprite: " + path);
            return null;
        }
    }

    /** Moves the zombie toward the player and turns it toward its chase direction. */
    public void moveTowardsPlayer(Player player) {
        double dx = player.x - x;
        double dy = player.y - y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0.001) {
            // The sprite faces right by default, so rotate from the positive X axis.
            facingAngle = Math.atan2(dy, dx);
            x += (int) Math.round((dx / distance) * speed);
            y += (int) Math.round((dy / distance) * speed);
            animationFrame++;
        }
    }

    public void draw(Graphics g) {
        if (SPRITE == null) {
            g.setColor(Color.GREEN);
            g.fillRect(x, y, size, size);
            return;
        }

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
