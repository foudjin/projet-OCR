import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class TextGenerator {
    private final String text;
    private final Font textFont;
    private final ImageConfig config;
    private final Alteration alteration = new Alteration();

    // Constructor with control over paragraph count and character count per paragraph
    public TextGenerator(int paragraphCount, int charPerParagraph) {
        Language lang = new Language();
        this.text = lang.generateFrenchText(paragraphCount, charPerParagraph);  // generate structured French text
        this.config = new ImageConfig();  // initialize image configuration (size, colors, etc.)
        this.textFont = new TextStyle().loadRandomFont("fonts/latin");  // randomly select a font
    }

    // Constructor with default character count
    public TextGenerator(int paragraphCount) {
        this(paragraphCount, 400);
    }

    // Save the document as an image (JPEG) to a specified directory
    public void saveAsImage(String directory, String fileName) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();  // create folder if it doesn't exist

        // Create a blank image with RGB color space
        BufferedImage image = new BufferedImage(config.width, config.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Set background and apply rotation
        g2d.rotate(config.rotationAngle, config.width / 2, config.height / 2);
        g2d.setColor(config.backgroundColor);
        g2d.fillRect(0, 0, config.width, config.height);

        // Draw the text
        drawStructuredText(g2d);

        // Apply visual effects (texture + ink blots)
        alteration.applyTexture(g2d, config.width, config.height);
        alteration.applyInkEffect(g2d, config.width, config.height);

        g2d.dispose();  // release resources

        try {
            ImageIO.write(image, "jpg", new File(directory, fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Save the same text content as a structured HTML document
    public void saveAsHtml(String directory, String fileName) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();  // ensure directory exists

        StringBuilder html = new StringBuilder();

        // Start HTML boilerplate
        html.append("<!DOCTYPE html>\n<html lang=\"fr\">\n<head>\n")
                .append("<meta charset=\"UTF-8\">\n")
                .append("<title>").append(fileName).append("</title>\n")
                .append("</head>\n<body style='font-family: sans-serif;'>\n");

        // Convert each paragraph or title into a proper HTML tag
        String[] blocks = text.split("\n\n");
        for (String block : blocks) {
            if (block.startsWith("##") && block.endsWith("##")) {
                String title = block.replace("##", "").trim();
                html.append("<h2>").append(title).append("</h2>\n");
            } else {
                html.append("<p>").append(block.trim()).append("</p>\n");
            }
        }

        html.append("</body>\n</html>");

        try (FileWriter writer = new FileWriter(new File(dir, fileName))) {
            writer.write(html.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Draw the structured text (titles and paragraphs) into the image canvas
    private void drawStructuredText(Graphics2D g2d) {
        float normalSize = 48f;  // base size for paragraph text
        float titleSize = 80f;   // base size for titles
        boolean fits = false;

        // Try to adjust font size until the full text fits in the image
        while (!fits && normalSize > 6f) {
            Font normalFont = textFont.deriveFont(normalSize);
            Font titleFont = textFont.deriveFont(titleSize);
            g2d.setFont(normalFont);

            FontMetrics normalMetrics = g2d.getFontMetrics(normalFont);
            FontMetrics titleMetrics = g2d.getFontMetrics(titleFont);

            int lineHeight = normalMetrics.getHeight();
            int titleHeight = titleMetrics.getHeight();

            int x = ImageConfig.MARGIN;
            int y = ImageConfig.MARGIN + lineHeight;
            int maxWidth = config.width - 2 * ImageConfig.MARGIN;
            int maxHeight = config.height - ImageConfig.MARGIN;

            String[] paragraphs = text.split("\n\n");
            fits = true;  // assume it fits until proven otherwise

            for (String paragraph : paragraphs) {
                boolean isTitle = paragraph.startsWith("##") && paragraph.endsWith("##");

                // Select font and color based on whether it's a title or not
                Font currentFont = isTitle ? titleFont : normalFont;
                g2d.setFont(currentFont);
                g2d.setColor(isTitle ? new Color(100, 0, 0) : config.textColor);

                FontMetrics metrics = g2d.getFontMetrics(currentFont);
                int currentLineHeight = isTitle ? titleHeight : lineHeight;

                String cleanParagraph = isTitle ? paragraph.replace("##", "").trim() : paragraph;
                String[] words = cleanParagraph.split(" ");
                StringBuilder line = new StringBuilder();

                // Line-wrapping logic
                for (String word : words) {
                    String testLine = line + " " + word;
                    int testWidth = metrics.stringWidth(testLine.trim());

                    if (testWidth > maxWidth) {
                        if (y + currentLineHeight > maxHeight) {
                            fits = false;
                            break;
                        }
                        // Draw the current line with a wavy effect
                        alteration.drawWavyText(g2d, line.toString(), x, y, 0.1, 2);
                        y += currentLineHeight;
                        line = new StringBuilder(word);
                    } else {
                        line.append(" ").append(word);
                    }
                }

                // Last line of the paragraph
                if (!fits || y + currentLineHeight > maxHeight) {
                    fits = false;
                    break;
                }

                alteration.drawWavyText(g2d, line.toString(), x, y, 0.1, 5);
                y += currentLineHeight + (isTitle ? 40 : 20);
            }

            // If text didn't fit, reduce font size and try again
            if (!fits) {
                g2d.setTransform(AffineTransform.getRotateInstance(config.rotationAngle, config.width / 2, config.height / 2));
                g2d.setColor(config.backgroundColor);
                g2d.fillRect(0, 0, config.width, config.height);
                normalSize -= 2f;
                titleSize -= 3f;
            }
        }
    }
}
