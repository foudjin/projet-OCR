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

    public TextGenerator(int paragraphCount, int sentenceCount) {
        // Générer le texte
        Language lang = new Language();
        this.text = lang.generateFrenchText(paragraphCount, sentenceCount);

        // Configuration de l'image
        this.config = new ImageConfig();

        // Chargement d'une police aléatoire
        TextStyle style = new TextStyle();
        this.textFont = style.loadRandomFont("fonts/latin");
    }

    public TextGenerator(int paragraphCount) {
        this(paragraphCount, 4); // par défaut : 4 phrases
    }

    public void saveAsImage(String directory, String fileName) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        BufferedImage image = new BufferedImage(config.width, config.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.rotate(config.rotationAngle, config.width / 2, config.height / 2);
        g2d.setColor(config.backgroundColor);
        g2d.fillRect(0, 0, config.width, config.height);

        drawStructuredText(g2d);
        alteration.applyTexture(g2d, config.width, config.height);
        alteration.applyInkEffect(g2d, config.width, config.height);
        g2d.dispose();

        try {
            File outputFile = new File(directory, fileName);
            ImageIO.write(image, "jpg", outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveAsHtml(String directory, String fileName) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html lang=\"fr\">\n<head>\n")
                .append("<meta charset=\"UTF-8\">\n")
                .append("<title>").append(fileName).append("</title>\n")
                .append("</head>\n<body style='font-family: sans-serif;'>\n");

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

    private void drawStructuredText(Graphics2D g2d) {
        float normalSize = 48f;
        float titleSize = 80f;
        boolean fits = false;

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
            fits = true;

            for (String paragraph : paragraphs) {
                boolean isTitle = paragraph.startsWith("##") && paragraph.endsWith("##");

                Font currentFont = isTitle ? titleFont : normalFont;
                g2d.setFont(currentFont);
                g2d.setColor(isTitle ? new Color(100, 0, 0) : config.textColor);

                FontMetrics metrics = g2d.getFontMetrics(currentFont);
                int currentLineHeight = isTitle ? titleHeight : lineHeight;

                String cleanParagraph = isTitle ? paragraph.replace("##", "").trim() : paragraph;
                String[] words = cleanParagraph.split(" ");
                StringBuilder line = new StringBuilder();

                for (String word : words) {
                    String testLine = line + " " + word;
                    int testWidth = metrics.stringWidth(testLine.trim());

                    if (testWidth > maxWidth) {
                        if (y + currentLineHeight > maxHeight) {
                            fits = false;
                            break;
                        }
                        alteration.drawWavyText(g2d, line.toString(), x, y, 0.1, 5);
                        y += currentLineHeight;
                        line = new StringBuilder(word);
                    } else {
                        line.append(" ").append(word);
                    }
                }

                if (!fits || y + currentLineHeight > maxHeight) {
                    fits = false;
                    break;
                }

                alteration.drawWavyText(g2d, line.toString(), x, y, 0.1, 5);
                y += currentLineHeight + (isTitle ? 40 : 20);
            }

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
