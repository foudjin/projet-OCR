import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

public class Alteration {
    private final String[] textureFiles = {
            "1.jpg", "2.jpg", "3.jpg", "4.jpg", "5.jpg", "6.jpg", "7.jpg", "8.jpg", "9.jpg",
            "10.jpg", "11.jpg", "12.jpg", "13.jpg", "14.jpg", "15.jpg"
    };

    public void applyTexture(Graphics2D g2d, int width, int height) {
        try {
            Random rand = new Random();
            String textureFile = "textures/" + textureFiles[rand.nextInt(textureFiles.length)];
            BufferedImage texture = ImageIO.read(new File(textureFile));
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.drawImage(texture, 0, 0, width, height, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void applyInkEffect(Graphics2D g2d, int width, int height) {
        Random rand = new Random();
        for (int i = 0; i < width; i += rand.nextInt(200) + 50) {
            for (int j = 0; j < height; j += rand.nextInt(100) + 20) {
                int alpha = rand.nextInt(100) + 50;
                g2d.setColor(new Color(0, 0, 0, alpha));
                g2d.fillOval(i, j, rand.nextInt(5) + 2, rand.nextInt(5) + 2);
            }
        }
    }

    public void drawWavyText(Graphics2D g2d, String text, int x, int baseY, double frequency, int amplitude) {
        FontMetrics metrics = g2d.getFontMetrics();
        int currentX = x;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int charWidth = metrics.charWidth(c);
            int yOffset = (int) (Math.sin((currentX + i * 10) * frequency) * amplitude);
            g2d.drawString(String.valueOf(c), currentX, baseY + yOffset);
            currentX += charWidth;
        }
    }
}
