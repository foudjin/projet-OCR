import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class TextGenerator {
    private final ImageConfig config;
    private final String text;
    private final Font textFont;
    private final Alteration alteration;

    public TextGenerator() {
        config = new ImageConfig();
        Language lang = new Language();
        TextStyle style = new TextStyle();
        alteration = new Alteration();

        this.text = lang.generateArabicText(8);
        this.textFont = style.loadRandomFont("fonts/");
    }

    private void drawStructuredText(Graphics2D g2d) {
        Font normalFont = textFont.deriveFont(48f);
        Font titleFont = textFont.deriveFont(80f);

        g2d.setFont(normalFont);
        g2d.setColor(config.textColor);

        FontMetrics normalMetrics = g2d.getFontMetrics(normalFont);
        FontMetrics titleMetrics = g2d.getFontMetrics(titleFont);

        int lineHeight = normalMetrics.getHeight();
        int titleHeight = titleMetrics.getHeight();

        int x = ImageConfig.MARGIN;
        int y = ImageConfig.MARGIN + lineHeight;
        int maxWidth = config.width - 2 * ImageConfig.MARGIN;
        int maxHeight = config.height - ImageConfig.MARGIN;

        String[] paragraphs = text.split("\n\n");

        for (String paragraph : paragraphs) {
            boolean isTitle = paragraph.startsWith("##") && paragraph.endsWith("##");

            if (isTitle) {
                g2d.setFont(titleFont);
                g2d.setColor(new Color(100, 0, 0));
                paragraph = paragraph.replace("##", "").trim();
            } else {
                g2d.setFont(normalFont);
                g2d.setColor(config.textColor);
            }

            FontMetrics metrics = g2d.getFontMetrics();
            int currentLineHeight = isTitle ? titleHeight : lineHeight;

            String[] words = paragraph.split(" ");
            StringBuilder line = new StringBuilder();

            for (String word : words) {
                String testLine = line + " " + word;
                int testWidth = metrics.stringWidth(testLine.trim());

                if (testWidth > maxWidth) {
                    if (y + currentLineHeight > maxHeight) return;
                    alteration.drawWavyText(g2d, line.toString(), x, y, 0.1, 5);
                    y += currentLineHeight;
                    line = new StringBuilder(word);
                } else {
                    line.append(" ").append(word);
                }
            }

            if (y + currentLineHeight <= maxHeight) {
                alteration.drawWavyText(g2d, line.toString(), x, y, 0.1, 5);
                y += currentLineHeight + (isTitle ? 20 : 10);
            }
        }
    }

    public void saveAsImage(String directory, String fileName) {
        File dir = new File(directory);
        if (!dir.exists()) dir.mkdirs();

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
            File outputFile = new File(directory + File.separator + fileName);
            ImageIO.write(image, "jpg", outputFile);
            System.out.println("Image sauvegardée: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
