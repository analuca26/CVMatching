

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class resumeMatcher extends JFrame implements ActionListener {
    // GUI components
    private JTextArea guidelineArea, resultArea;
    private JButton uploadButton, matchButton;
    private JLabel fileLabel;
    private File resumeFile;
    
    public resumeMatcher() {
        // Set up the main window (JFrame)
        setTitle("Resume Matcher");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Top panel: file upload and guideline input
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1));
        
        // File upload panel
        JPanel filePanel = new JPanel();
        uploadButton = new JButton("Upload Resume");
        uploadButton.addActionListener(this);
        fileLabel = new JLabel("No file selected");
        filePanel.add(uploadButton);
        filePanel.add(fileLabel);
        topPanel.add(filePanel);
        
        // Guideline input panel
        JPanel guidelinePanel = new JPanel(new BorderLayout());
        guidelinePanel.add(new JLabel("Enter required skills (comma separated):"), BorderLayout.NORTH);
        guidelineArea = new JTextArea(3, 40);
        guidelinePanel.add(new JScrollPane(guidelineArea), BorderLayout.CENTER);
        topPanel.add(guidelinePanel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Button to trigger matching
        matchButton = new JButton("Match Resume");
        matchButton.addActionListener(this);
        add(matchButton, BorderLayout.CENTER);
        
        // Result display area
        resultArea = new JTextArea(10, 50);
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.SOUTH);
        
        setVisible(true);
    }
    
    public static void main(String[] args) {
        new resumeMatcher();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == uploadButton) {
            // Open file chooser to select a resume file
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                resumeFile = chooser.getSelectedFile();
                fileLabel.setText(resumeFile.getName());
            }
        } else if (e.getSource() == matchButton) {
            // Check if resume file and guideline have been provided
            if (resumeFile == null) {
                JOptionPane.showMessageDialog(this, "Please upload a resume file first.");
                return;
            }
            String guidelines = guidelineArea.getText();
            if (guidelines.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the required skills guidelines.");
                return;
            }
            try {
                // Read and process the resume file
                String resumeText = readFile(resumeFile);
                // Convert text to lower case for case-insensitive matching
                resumeText = resumeText.toLowerCase();
                // Tokenize the resume text into a set of words (ignoring punctuation)
                Set<String> resumeWords = new HashSet<>(Arrays.asList(resumeText.split("\\W+")));
                
                // Process the guidelines: assume comma-separated values
                String[] requiredSkills = guidelines.toLowerCase().split(",");
                int totalSkills = requiredSkills.length;
                int matchedSkills = 0;
                List<String> missingSkills = new ArrayList<>();
                for (String skill : requiredSkills) {
                    skill = skill.trim();
                    if (skill.isEmpty()) continue;
                    if (resumeWords.contains(skill)) {
                        matchedSkills++;
                    } else {
                        missingSkills.add(skill);
                    }
                }
                int percentage = (int)((matchedSkills / (double) totalSkills) * 100);
                StringBuilder result = new StringBuilder();
                result.append("Matching Percentage: " + percentage + "%\n");
                result.append("Matched Skills: " + matchedSkills + " out of " + totalSkills + "\n");
                if (!missingSkills.isEmpty()) {
                    result.append("Missing Skills: " + missingSkills + "\n");
                } else {
                    result.append("All required skills found!");
                }
                resultArea.setText(result.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
            }
        }
    }
    
    // Utility method to read file content.
    // For TXT files, we read the contents directly.
    // For DOCX and ODT files, you need to add and use appropriate libraries.
    private String readFile(File file) throws IOException {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".txt")) {
            return new String(Files.readAllBytes(file.toPath()));
        } else if (fileName.endsWith(".docx")) {
            // DOCX files require Apache POI. (See: https://poi.apache.org/)
            return "Reading DOCX files requires Apache POI. Please implement file reading accordingly.";
        } else if (fileName.endsWith(".odt")) {
            // ODT files can be handled using an appropriate library.
            return "Reading ODT files requires an appropriate library. Please implement file reading accordingly.";
        } else {
            throw new IOException("Unsupported file format");
        }
    }
}
