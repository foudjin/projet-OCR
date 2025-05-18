import javax.swing.*;
import java.awt.*;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createGUI);
    }

    public static void createGUI() {
        JFrame frame = new JFrame("DE50 - Project OCR");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 320);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        //Language
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Second language(unavailable yet) :"), gbc);

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

        //drop-down choices
        Integer[] docOptions = IntStream.rangeClosed(1, 20).boxed().toArray(Integer[]::new);
        Integer[] paraOptions = IntStream.rangeClosed(1, 10).boxed().toArray(Integer[]::new);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Number of documents :"), gbc);
        JComboBox<Integer> docBox = new JComboBox<>(docOptions);
        gbc.gridx = 1;
        panel.add(docBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Minimum paragraphes :"), gbc);
        JComboBox<Integer> minBox = new JComboBox<>(paraOptions);
        minBox.setSelectedItem(3);
        gbc.gridx = 1;
        panel.add(minBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Maximum paragraphes :"), gbc);
        JComboBox<Integer> maxBox = new JComboBox<>(paraOptions);
        maxBox.setSelectedItem(6);
        gbc.gridx = 1;
        panel.add(maxBox, gbc);

        //validate and progress
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

        //Action bouton
        generateButton.addActionListener(e -> {
            int docCount = (Integer) docBox.getSelectedItem();
            int minP = (Integer) minBox.getSelectedItem();
            int maxP = (Integer) maxBox.getSelectedItem();

            if (minP > maxP) {
                JOptionPane.showMessageDialog(frame, "Minimum should be inferior or equal to maximum.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            generateButton.setEnabled(false);
            progressBar.setValue(0);

            SwingWorker<Void, Integer> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    for (int i = 1; i <= docCount; i++) {
                        int actualParagraphCount = minP + (int) (Math.random() * (maxP - minP + 1));
                        TextGenerator generator = new TextGenerator(actualParagraphCount);
                        generator.saveAsImage("images", "document_" + i + ".jpg");
                        setProgress((int) ((i / (float) docCount) * 100));
                    }
                    return null;
                }

                @Override
                protected void done() {
                    setProgress(100);
                    generateButton.setEnabled(true);
                    JOptionPane.showMessageDialog(frame, docCount + " documents generated !");
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
