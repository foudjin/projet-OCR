import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

public class TextStyle {
    public Font loadRandomFont(String fontDirectory) {
        File folder = new File(fontDirectory);
        File[] fontFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".ttf"));

        if (fontFiles != null && fontFiles.length > 0) {
            try {
                Random rand = new Random();
                File chosenFont = fontFiles[rand.nextInt(fontFiles.length)];
                return Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(chosenFont)).deriveFont(72f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return new Font("Arabic Typesetting", Font.BOLD, 72);
    }
}
