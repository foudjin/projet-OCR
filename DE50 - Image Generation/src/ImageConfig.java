import java.awt.*;
import java.util.Random;

public class ImageConfig {
    // Constants for image dimensions
    public static final int MAX_WIDTH = 2480;
    public static final int MAX_HEIGHT = 3508;
    public static final int MIN_WIDTH = 1240;
    public static final int MIN_HEIGHT = 1754;

    // Margin around the text
    public static final int MARGIN = 100;

    // Image attributes
    public int width;
    public int height;
    public Color backgroundColor;
    public Color textColor;
    public double rotationAngle;

    // Constructor that randomly generates image parameters
    public ImageConfig() {
        Random rand = new Random();

        // Randomize image orientation: portrait or landscape
        if (rand.nextBoolean()) {
            width = rand.nextInt((MAX_WIDTH - MIN_WIDTH) + 1) + MIN_WIDTH;
            height = rand.nextInt((MAX_HEIGHT - MIN_HEIGHT) + 1) + MIN_HEIGHT;
        } else {
            width = rand.nextInt((MAX_HEIGHT - MIN_HEIGHT) + 1) + MIN_HEIGHT;
            height = rand.nextInt((MAX_WIDTH - MIN_WIDTH) + 1) + MIN_WIDTH;
        }

        // Generate a light background color (close to beige/yellow)
        int r = rand.nextInt(50) + 200;
        int g = rand.nextInt(30) + 180;
        int b = rand.nextInt(20) + 140;
        backgroundColor = new Color(r, g, b);

        // Generate a dark text color (close to black/brown)
        int textR = rand.nextInt(50) + 50;
        int textG = rand.nextInt(30) + 30;
        int textB = rand.nextInt(30) + 30;
        textColor = new Color(textR, textG, textB);

        // Apply a small random rotation between -5 and +5 degrees
        rotationAngle = Math.toRadians(rand.nextInt(11) - 5);
    }
}
