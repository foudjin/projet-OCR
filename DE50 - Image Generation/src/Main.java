import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.util.stream.IntStream;

public class Main {
    private static SwingWorker<Void, Integer> worker;
    private static long startTime;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createGUI);
    }

    public static void createGUI() {
        JFrame frame = new JFrame("DE50 - Data Generation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 580); // Increased size for better layout
        frame.setLocationRelativeTo(null);

        // Main panel with layout and padding
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Language selection (disabled placeholder)
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

        JPanel langPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        langPanel.add(frButton);
        langPanel.add(enButton);

        gbc.gridx = 1;
        panel.add(langPanel, gbc);

        // Output folder selection (with titled border)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;

        JTextField folderField = new JTextField("Generation", 25);
        folderField.setEditable(false);
        folderField.setBackground(new Color(240, 240, 240));

        JButton chooseFolder = new JButton("Choose");

        JPanel folderPanel = new JPanel(new BorderLayout(8, 0));
        folderPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Output directory",
                TitledBorder.LEFT, TitledBorder.TOP));
        folderPanel.add(folderField, BorderLayout.CENTER);
        folderPanel.add(chooseFolder, BorderLayout.EAST);

        panel.add(folderPanel, gbc);

        // Number of documents input
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Number of documents:"), gbc);

        JTextField docField = new JTextField("5", 6);
        gbc.gridx = 1;
        panel.add(docField, gbc);

        // Paragraphs min / max selection
        Integer[] paraOptions = IntStream.rangeClosed(1, 10).boxed().toArray(Integer[]::new);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Paragraphs (min / max):"), gbc);

        JComboBox<Integer> minParaBox = new JComboBox<>(paraOptions);
        JComboBox<Integer> maxParaBox = new JComboBox<>(paraOptions);
        minParaBox.setSelectedItem(3);
        maxParaBox.setSelectedItem(6);

        JPanel paraPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        paraPanel.add(minParaBox);
        paraPanel.add(maxParaBox);
        gbc.gridx = 1;
        panel.add(paraPanel, gbc);

        // Characters per paragraph min / max selection
        Integer[] charOptions = IntStream.rangeClosed(100, 1000).filter(n -> n % 50 == 0).boxed().toArray(Integer[]::new);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Chars per paragraph (min / max):"), gbc);

        JComboBox<Integer> minCharBox = new JComboBox<>(charOptions);
        JComboBox<Integer> maxCharBox = new JComboBox<>(charOptions);
        minCharBox.setSelectedItem(300);
        maxCharBox.setSelectedItem(600);

        JPanel charPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        charPanel.add(minCharBox);
        charPanel.add(maxCharBox);
        gbc.gridx = 1;
        panel.add(charPanel, gbc);

        // Generate and Cancel buttons
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JButton generateButton = new JButton("Generate");
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setEnabled(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(generateButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

        // Progress bar
        gbc.gridy = 6;
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(400, 22));
        panel.add(progressBar, gbc);

        // Time estimate label
        gbc.gridy = 7;
        JLabel timeLabel = new JLabel("Time: --:--");
        timeLabel.setPreferredSize(new Dimension(400, 20));
        panel.add(timeLabel, gbc);

        // Add panel to frame
        frame.add(panel);
        frame.setVisible(true);

        // Folder chooser action
        chooseFolder.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                folderField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        // Action for Generate button
        generateButton.addActionListener(e -> {
            // Get and validate document count
            String docText = docField.getText().trim();
            int docCount;
            try {
                docCount = Integer.parseInt(docText);
                if (docCount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Enter a valid number of documents.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Read min/max settings
            int minP = (Integer) minParaBox.getSelectedItem();
            int maxP = (Integer) maxParaBox.getSelectedItem();
            int minC = (Integer) minCharBox.getSelectedItem();
            int maxC = (Integer) maxCharBox.getSelectedItem();

            if (minP > maxP || minC > maxC) {
                JOptionPane.showMessageDialog(frame, "Minimum must be <= maximum.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Prepare UI for generation
            generateButton.setEnabled(false);
            cancelButton.setEnabled(true);
            progressBar.setValue(0);
            timeLabel.setText("Time: --:--");
            startTime = System.currentTimeMillis();
            String outputDir = folderField.getText();

            // Background generation thread
            worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    for (int i = 1; i <= docCount && !isCancelled(); i++) {
                        int actualP = minP + (int) (Math.random() * (maxP - minP + 1));
                        int actualC = minC + (int) (Math.random() * (maxC - minC + 1));

                        // Generate the document
                        TextGenerator generator = new TextGenerator(actualP, actualC);
                        generator.saveAsImage(outputDir, "document_" + i + ".jpg");
                        generator.saveAsHtml(outputDir, "document_" + i + ".html");

                        // Update progress
                        int progress = (int) ((i / (float) docCount) * 100);
                        setProgress(progress);

                        // Display time since start
                        long elapsed = System.currentTimeMillis() - startTime;
                        int sec = (int) (elapsed / 1000) % 60;
                        int min = (int) (elapsed / 1000) / 60;
                        timeLabel.setText(String.format("Time: %02d:%02d", min, sec));
                    }
                    return null;
                }

                @Override
                protected void done() {
                    long total = System.currentTimeMillis() - startTime;
                    int seconds = (int) (total / 1000) % 60;
                    int minutes = (int) (total / 1000) / 60;

                    progressBar.setValue(100);
                    timeLabel.setText("Done in: " + minutes + " min " + seconds + " sec");
                    generateButton.setEnabled(true);
                    cancelButton.setEnabled(false);

                    if (!isCancelled()) {
                        JOptionPane.showMessageDialog(frame, docCount + " documents generated in '" + outputDir + "' in " + minutes + "m " + seconds + "s.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Generation cancelled.");
                    }
                }
            };

            // Update progress bar live
            worker.addPropertyChangeListener(evt -> {
                if ("progress".equals(evt.getPropertyName())) {
                    progressBar.setValue((Integer) evt.getNewValue());
                }
            });

            worker.execute();
        });

        // Cancel button logic
        cancelButton.addActionListener(e -> {
            if (worker != null && !worker.isDone()) {
                worker.cancel(true);
            }
        });
    }
}
