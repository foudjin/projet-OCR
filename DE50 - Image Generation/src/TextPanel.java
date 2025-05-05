import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.Random;
import javax.imageio.ImageIO;

class TextGenerator {
    private String text;
    private static final int MAX_WIDTH = 2480;
    private static final int MAX_HEIGHT = 3508;
    private static final int MIN_WIDTH = 1240;
    private static final int MIN_HEIGHT = 1754;
    private static final int MARGIN = 100;
    private int width;
    private int height;
    private Color backgroundColor;
    private Color textColor;
    private double rotationAngle;
    private Font textFont;
    private String[] textureFiles = {"1.jpg", "2.jpg", "3.jpg", "4.jpg", "5.jpg", "6.jpg", "7.jpg", "8.jpg", "9.jpg", "10.jpg", "11.jpg", "12.jpg", "13.jpg", "14.jpg", "15.jpg"};

    public TextGenerator() {
        this.text = generateRandomText(200);
        Random rand = new Random();

        if (rand.nextBoolean()) {
            this.width = rand.nextInt((MAX_WIDTH - MIN_WIDTH) + 1) + MIN_WIDTH;
            this.height = rand.nextInt((MAX_HEIGHT - MIN_HEIGHT) + 1) + MIN_HEIGHT;
        } else {
            this.width = rand.nextInt((MAX_HEIGHT - MIN_HEIGHT) + 1) + MIN_HEIGHT;
            this.height = rand.nextInt((MAX_WIDTH - MIN_WIDTH) + 1) + MIN_WIDTH;
        }

        int r = rand.nextInt(50) + 200;
        int g = rand.nextInt(30) + 180;
        int b = rand.nextInt(20) + 140;
        this.backgroundColor = new Color(r, g, b);

        int textR = rand.nextInt(50) + 50;
        int textG = rand.nextInt(30) + 30;
        int textB = rand.nextInt(30) + 30;
        this.textColor = new Color(textR, textG, textB);

        this.rotationAngle = Math.toRadians(rand.nextInt(11) - 5);
        this.textFont = loadRandomFont("fonts/");
    }

    private Font loadRandomFont(String fontDirectory) {
        File folder = new File(fontDirectory);
        File[] fontFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".ttf"));

        if (fontFiles != null && fontFiles.length > 0) {
            try {
                Random rand = new Random();
                File chosenFont = fontFiles[rand.nextInt(fontFiles.length)]; // Sélection aléatoire
                return Font.createFont(Font.TRUETYPE_FONT, new FileInputStream(chosenFont)).deriveFont(72f);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Si aucune police arabe n'est trouvée, utiliser une police de secours
        return new Font("Arabic Typesetting", Font.BOLD, 72);
    }


    private String generateRandomText(int paragraphCount) {
        String[] words = {
                "كتاب", "مدرسة", "مدينة", "مكتب", "جامعة", "شاشة", "لوحة", "قلم", "هاتف", "كمبيوتر",
                "برنامج", "معلومات", "بيانات", "تكنولوجيا", "شبكة", "حاسوب", "ذاكرة", "إنترنت", "كود"
        };

        String[] titles = {
                "## التعليم والتكنولوجيا ##",
                "## أهمية القراءة ##",
                "## تأثير الإنترنت ##",
                "## تطور البرمجيات ##"
        };

        Random rand = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < paragraphCount; i++) {
            if (i % 2 == 0) {
                sb.append(titles[rand.nextInt(titles.length)]).append("\n\n"); // Ajoute un titre
            }

            int wordCount = rand.nextInt(30) + 20; // Entre 20 et 50 mots par paragraphe
            for (int j = 0; j < wordCount; j++) {
                sb.append(words[rand.nextInt(words.length)]).append(" ");
            }
            sb.append("\n\n"); // Ajoute un saut de ligne entre les paragraphes
        }

        return sb.toString().trim();
    }


    private void drawStructuredText(Graphics2D g2d) {
        Font normalFont = textFont.deriveFont(48f);
        Font titleFont = textFont.deriveFont(80f);

        g2d.setFont(normalFont);
        g2d.setColor(textColor);

        FontMetrics normalMetrics = g2d.getFontMetrics(normalFont);
        FontMetrics titleMetrics = g2d.getFontMetrics(titleFont);

        int lineHeight = normalMetrics.getHeight();
        int titleHeight = titleMetrics.getHeight();

        int x = MARGIN;
        int y = MARGIN + lineHeight;
        int maxWidth = width - 2 * MARGIN;
        int maxHeight = height - MARGIN;

        String[] paragraphs = text.split("\n\n"); // Sépare en paragraphes
        Random rand = new Random();

        for (String paragraph : paragraphs) {
            boolean isTitle = paragraph.startsWith("##") && paragraph.endsWith("##");

            if (isTitle) {
                g2d.setFont(titleFont);
                g2d.setColor(new Color(100, 0, 0)); // Titre en rouge foncé
                paragraph = paragraph.replace("##", "").trim();
            } else {
                g2d.setFont(normalFont);
                g2d.setColor(textColor);
            }

            FontMetrics metrics = g2d.getFontMetrics();
            int currentLineHeight = isTitle ? titleHeight : lineHeight;

            String[] words = paragraph.split(" ");
            StringBuilder line = new StringBuilder();

            for (String word : words) {
                String testLine = line + " " + word;
                int testWidth = metrics.stringWidth(testLine.trim());

                if (testWidth > maxWidth) {
                    if (y + currentLineHeight > maxHeight) return; // Stop si on dépasse la page

                    drawWavyText(g2d, line.toString(), x, y, 0.1, 5);
                    y += currentLineHeight;
                    line = new StringBuilder(word);
                } else {
                    line.append(" ").append(word);
                }
            }

            if (y + currentLineHeight <= maxHeight) {
                drawWavyText(g2d, line.toString(), x, y, 0.1, 5);
                y += currentLineHeight + (isTitle ? 20 : 10); // Ajout d’espace entre paragraphes
            }
        }
    }




    private void drawText(Graphics2D g2d) {
        g2d.setFont(textFont);
        g2d.setColor(textColor);

        FontMetrics metrics = g2d.getFontMetrics(textFont);
        int lineHeight = metrics.getHeight();
        int x = MARGIN;
        int y = MARGIN + lineHeight;
        int maxWidth = width - 2 * MARGIN;
        int maxHeight = height - MARGIN;

        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        Random rand = new Random();
        double frequency = 0.1;  // Contrôle la fréquence des ondulations
        int amplitude = 5;       // Amplitude de l'onde en pixels

        for (String word : words) {
            String testLine = line + " " + word;
            int testWidth = metrics.stringWidth(testLine.trim());

            if (testWidth > maxWidth) {
                if (y + lineHeight > maxHeight) break;
                drawWavyText(g2d, line.toString(), x, y, frequency, amplitude);
                y += lineHeight;
                line = new StringBuilder(word);
            } else {
                line.append(" ").append(word);
            }
        }
        if (y + lineHeight <= maxHeight) {
            drawWavyText(g2d, line.toString(), x, y, frequency, amplitude);
        }
    }

    // Méthode qui applique l'ondulation sur le texte
    private void drawWavyText(Graphics2D g2d, String text, int x, int baseY, double frequency, int amplitude) {
        FontMetrics metrics = g2d.getFontMetrics();
        int currentX = x;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int charWidth = metrics.charWidth(c);

            // Calcul de l'offset Y pour l'ondulation
            int yOffset = (int) (Math.sin((currentX + i * 10) * frequency) * amplitude);

            g2d.drawString(String.valueOf(c), currentX, baseY + yOffset);
            currentX += charWidth;
        }
    }


    private void applyTexture(Graphics2D g2d) {
        try {
            Random rand = new Random();
            String textureFile = "textures/" + textureFiles[rand.nextInt(textureFiles.length)];
            BufferedImage texture = ImageIO.read(new File(textureFile));
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.drawImage(texture, 0, 0, width, height, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void applyInkEffect(Graphics2D g2d) {
        Random rand = new Random();

        for (int i = 0; i < width; i += rand.nextInt(200) + 50) {
            for (int j = 0; j < height; j += rand.nextInt(100) + 20) {
                int alpha = rand.nextInt(100) + 50;
                g2d.setColor(new Color(0, 0, 0, alpha));
                g2d.fillOval(i, j, rand.nextInt(5) + 2, rand.nextInt(5) + 2);
            }
        }
    }

    public void saveAsImage(String directory, String fileName) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.rotate(rotationAngle, width / 2, height / 2);
        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, width, height);

        drawStructuredText(g2d);
        applyTexture(g2d);
        applyInkEffect(g2d);
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
