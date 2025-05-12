import java.util.Random;

public class Language {
    public String generateArabicText(int paragraphCount) {
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
                sb.append(titles[rand.nextInt(titles.length)]).append("\n\n");
            }

            int wordCount = rand.nextInt(30) + 20;
            for (int j = 0; j < wordCount; j++) {
                sb.append(words[rand.nextInt(words.length)]).append(" ");
            }
            sb.append("\n\n");
        }

        return sb.toString().trim();
    }
}
