import javax.swing.*;
import java.awt.*;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createGUI);
    }

    public static void createGUI() {
        JFrame frame = new JFrame("DE50 - Data Generation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Langue (désactivée)
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Second language (unavailable yet):"), gbc);

        JRadioButton frButton = new JRadioButton("Français");
        JRadioButton enButton = new JRadioButton("English");
        frButton.setEnabled(false);
        enButton.setEnabled(false);
        frButton.setSelected(true);

        ButtonGroup langGroup = new ButtonGroup();
        langGroup.add(frButton);
        langGroup.add(enButton);

        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        langPanel.add(frButton);
        langPanel.add(Box.createHorizontalStrut(10));
        langPanel.add(enButton);

        gbc.gridx = 1;
        panel.add(langPanel, gbc);

        // Champs personnalisés
        Integer[] paraOptions = IntStream.rangeClosed(1, 10).boxed().toArray(Integer[]::new);
        Integer[] charOptions = IntStream.rangeClosed(100, 1000).filter(n -> n % 50 == 0).boxed().toArray(Integer[]::new);

        // Nombre de documents (champ de texte)
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Number of documents:"), gbc);
        JTextField docField = new JTextField("5", 5);
        gbc.gridx = 1;
        panel.add(docField, gbc);

        // Paragraphes min / max
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Paragraphs (min / max):"), gbc);
        JPanel paraPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JComboBox<Integer> minParaBox = new JComboBox<>(paraOptions);
        JComboBox<Integer> maxParaBox = new JComboBox<>(paraOptions);
        minParaBox.setSelectedItem(3);
        maxParaBox.setSelectedItem(6);
        paraPanel.add(minParaBox);
        paraPanel.add(maxParaBox);
        gbc.gridx = 1;
        panel.add(paraPanel, gbc);

        // Caractères min / max
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Chars per paragraph (min / max):"), gbc);
        JPanel charPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JComboBox<Integer> minCharBox = new JComboBox<>(charOptions);
        JComboBox<Integer> maxCharBox = new JComboBox<>(charOptions);
        minCharBox.setSelectedItem(300);
        maxCharBox.setSelectedItem(600);
        charPanel.add(minCharBox);
        charPanel.add(maxCharBox);
        gbc.gridx = 1;
        panel.add(charPanel, gbc);

        // Bouton + barre de progression
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton generateButton = new JButton("Generate");
        panel.add(generateButton, gbc);

        gbc.gridy = 5;
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(300, 20));
        panel.add(progressBar, gbc);

        frame.add(panel);
        frame.setVisible(true);

        generateButton.addActionListener(e -> {
            String docText = docField.getText().trim();
            int docCount;
            try {
                docCount = Integer.parseInt(docText);
                if (docCount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Enter a valid number of documents.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int minP = (Integer) minParaBox.getSelectedItem();
            int maxP = (Integer) maxParaBox.getSelectedItem();
            int minC = (Integer) minCharBox.getSelectedItem();
            int maxC = (Integer) maxCharBox.getSelectedItem();

            if (minP > maxP || minC > maxC) {
                JOptionPane.showMessageDialog(frame, "Minimum must be <= maximum.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            generateButton.setEnabled(false);
            progressBar.setValue(0);
            String outputDir = "Generation";

            SwingWorker<Void, Integer> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    for (int i = 1; i <= docCount; i++) {
                        int actualParagraphCount = minP + (int) (Math.random() * (maxP - minP + 1));
                        int actualCharCount = minC + (int) (Math.random() * (maxC - minC + 1));
                        TextGenerator generator = new TextGenerator(actualParagraphCount, actualCharCount);
                        generator.saveAsImage(outputDir, "document_" + i + ".jpg");
                        generator.saveAsHtml(outputDir, "document_" + i + ".html");
                        setProgress((int) ((i / (float) docCount) * 100));
                    }
                    return null;
                }

                @Override
                protected void done() {
                    setProgress(100);
                    generateButton.setEnabled(true);
                    JOptionPane.showMessageDialog(frame, docCount + " documents generated in '" + outputDir + "' !");
                }
            };

            worker.addPropertyChangeListener(evt -> {
                if ("progress".equals(evt.getPropertyName())) {
                    progressBar.setValue((Integer) evt.getNewValue());
                }
            });

            worker.execute();
        });
    }
}
