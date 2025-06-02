import com.github.javafaker.Faker;
import java.util.Locale;
import java.util.Random;

public class Language {
    private final Faker faker = new Faker(new Locale("fr"));
    private final Random rand = new Random();


    public String generateFrenchText(int nbParagraphs, int charPerParagraph) {
        StringBuilder fullText = new StringBuilder();

        for (int i = 0; i < nbParagraphs; i++) {
            // Titre en rouge foncé encadré par ##
            String title = faker.book().title();
            fullText.append("## ").append(title).append(" ##\n\n");

            StringBuilder paragraph = new StringBuilder();
            while (paragraph.length() < charPerParagraph) {
                String sentence = faker.book().title() + ". ";
                paragraph.append(sentence);
            }

            // Tronque si ça dépasse la taille max
            String truncated = paragraph.substring(0, Math.min(paragraph.length(), charPerParagraph)).trim();
            fullText.append(truncated).append("\n\n");
        }

        return fullText.toString().trim();
    }


    public String generateFrenchText(int paragraphCount) {
        return generateFrenchText(paragraphCount, 400); // valeur par défaut
    }

    public String generateArabicText(int paragraphCount) {
        return generateFrenchText(paragraphCount);
    }
}
