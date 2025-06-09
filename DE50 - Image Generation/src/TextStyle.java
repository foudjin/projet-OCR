import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

public class TextStyle {

    // Loads a random font from the given directory
    public Font loadRandomFont(String fontDirectory) {
        File folder = new File(fontDirectory);

        // Filter .ttf font files
        File[] fontFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".ttf"));

        if (fontFiles != null && fontFiles.length > 0) {
            try {
                Random rand = new Random();

                // Pick a random font file
                File chosenFont = fontFiles[rand.nextInt(fontFiles.length)];

                // Load and return it with default size 72
                return Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(chosenFont)).deriveFont(72f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // If no font was found or an error occurred, return a fallback font
        return new Font("Arabic Typesetting", Font.BOLD, 72);
    }
}
