import java.awt.*;
import java.util.Random;

public class ImageConfig {
    public static final int MAX_WIDTH = 2480;
    public static final int MAX_HEIGHT = 3508;
    public static final int MIN_WIDTH = 1240;
    public static final int MIN_HEIGHT = 1754;
    public static final int MARGIN = 100;

    public int width;
    public int height;
    public Color backgroundColor;
    public Color textColor;
    public double rotationAngle;

    public ImageConfig() {
        Random rand = new Random();

        if (rand.nextBoolean()) {
            width = rand.nextInt((MAX_WIDTH - MIN_WIDTH) + 1) + MIN_WIDTH;
            height = rand.nextInt((MAX_HEIGHT - MIN_HEIGHT) + 1) + MIN_HEIGHT;
        } else {
            width = rand.nextInt((MAX_HEIGHT - MIN_HEIGHT) + 1) + MIN_HEIGHT;
            height = rand.nextInt((MAX_WIDTH - MIN_WIDTH) + 1) + MIN_WIDTH;
        }

        int r = rand.nextInt(50) + 200;
        int g = rand.nextInt(30) + 180;
        int b = rand.nextInt(20) + 140;
        backgroundColor = new Color(r, g, b);

        int textR = rand.nextInt(50) + 50;
        int textG = rand.nextInt(30) + 30;
        int textB = rand.nextInt(30) + 30;
        textColor = new Color(textR, textG, textB);

        rotationAngle = Math.toRadians(rand.nextInt(11) - 5);
    }
}
