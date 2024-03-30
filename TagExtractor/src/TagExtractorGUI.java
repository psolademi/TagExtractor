import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TagExtractorGUI extends JFrame {
    private JTextArea tagTextArea;
    private JFileChooser fileChooser;
    private File textFile;
    private File stopWordsFile;

    public TagExtractorGUI() {
        setTitle("Tag Extractor");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tagTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(tagTextArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton selectTextFileButton = new JButton("Select Text File");
        selectTextFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectTextFile();
            }
        });

        JButton selectStopWordsFileButton = new JButton("Select Stop Words File");
        selectStopWordsFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectStopWordsFile();
            }
        });

        JButton extractTagsButton = new JButton("Extract Tags");
        extractTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                extractTags();
            }
        });

        JButton saveTagsButton = new JButton("Save Tags");
        saveTagsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTagsToFile();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(selectTextFileButton);
        buttonPanel.add(selectStopWordsFileButton);
        buttonPanel.add(extractTagsButton);
        buttonPanel.add(saveTagsButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void selectTextFile() {
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            textFile = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(this, "Text file selected: " + textFile.getName());
        }
    }

    private void selectStopWordsFile() {
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files", "txt"));
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            stopWordsFile = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(this, "Stop words file selected: " + stopWordsFile.getName());
        }
    }

    private void extractTags() {
        if (textFile == null || stopWordsFile == null) {
            JOptionPane.showMessageDialog(this, "Please select both text file and stop words file.");
            return;
        }

        Map<String, Integer> tagFrequencyMap = new HashMap<>();
        Set<String> stopWords = loadStopWords(stopWordsFile);

        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
                for (String word : words) {
                    if (!stopWords.contains(word)) {
                        tagFrequencyMap.put(word, tagFrequencyMap.getOrDefault(word, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        displayTags(tagFrequencyMap);
    }

    private Set<String> loadStopWords(File stopWordsFile) {
        Set<String> stopWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(stopWordsFile))) {
            String word;
            while ((word = reader.readLine()) != null) {
                stopWords.add(word.trim().toLowerCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stopWords;
    }

    private void displayTags(Map<String, Integer> tagFrequencyMap) {
        StringBuilder tags = new StringBuilder();
        for (Map.Entry<String, Integer> entry : tagFrequencyMap.entrySet()) {
            tags.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        tagTextArea.setText(tags.toString());
    }

    private void saveTagsToFile() {
        if (tagTextArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tags to save.");
            return;
        }

        fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                writer.write(tagTextArea.getText());
                JOptionPane.showMessageDialog(this, "Tags saved to: " + outputFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TagExtractorGUI().setVisible(true);
            }
        });
    }
}