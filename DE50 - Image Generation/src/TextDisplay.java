public class TextDisplay {
    public static void main(String[] args) {
        int numberOfImages = 20;
        String outputDirectory = "images";

        for (int i = 1; i <= numberOfImages; i++) {
            TextGenerator generator = new TextGenerator();
            generator.saveAsImage(outputDirectory, "reverse_ocr_" + i + ".jpg");
        }

        System.out.println(numberOfImages + " images générées avec succès dans le dossier '" + outputDirectory + "'.");
    }
}
