import com.github.javafaker.Faker;
import java.util.Locale;
import java.util.Random;

public class Language {

    // Faker instance (API who generate the text) with French locale
    private final Faker faker = new Faker(new Locale("fr"));
    private final Random rand = new Random();

    /**
     * Generates multiple French paragraphs with a title and a fixed number of characters per paragraph
     * @param nbParagraphs number of paragraphs to generate
     * @param charPerParagraph number of characters per paragraph
     * @return a structured French text with titles and paragraphs
     */
    public String generateFrenchText(int nbParagraphs, int charPerParagraph) {
        StringBuilder fullText = new StringBuilder();

        for (int i = 0; i < nbParagraphs; i++) {
            // Generate a fake book title for the paragraph title
            String title = faker.book().title();
            fullText.append("## ").append(title).append(" ##\n\n");

            // Build paragraph until the required character count is reached
            StringBuilder paragraph = new StringBuilder();
            while (paragraph.length() < charPerParagraph) {
                String sentence = faker.book().title() + ". ";
                paragraph.append(sentence);
            }

            // Trim paragraph to the exact size if it exceeds the limit
            String truncated = paragraph.substring(0, Math.min(paragraph.length(), charPerParagraph)).trim();
            fullText.append(truncated).append("\n\n");
        }

        return fullText.toString().trim();
    }

    // Overloaded method with default paragraph length (400 characters)
    public String generateFrenchText(int paragraphCount) {
        return generateFrenchText(paragraphCount, 400);
    }

    // Placeholder method for Arabic text generation (currently returns French text)
    public String generateArabicText(int paragraphCount) {
        return generateFrenchText(paragraphCount);
    }
}
