import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.datatransfer.Clipboard;
import java.security.SecureRandom;

public class PasswordGeneratorGUI extends JFrame {
    private final PasswordGenerator passwordGenerator;

    private JTextField lengthField;
    private JCheckBox uppercaseCheckBox, lowercaseCheckBox, digitsCheckBox, specialCharsCheckBox;
    private JButton generateButton, copyButton, saveButton;
    private JTextArea passwordArea;
    private JProgressBar strengthProgressBar;

    public PasswordGeneratorGUI() {
        super("Password Generator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        passwordGenerator = new PasswordGenerator();

        initializeComponents();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(createOptionsPanel(), BorderLayout.NORTH);
        add(passwordArea, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        add(createStrengthPanel(), BorderLayout.WEST);

        generateButton.addActionListener(e -> generatePassword());

        copyButton.addActionListener(e -> copyToClipboard());

        saveButton.addActionListener(e -> saveToFile());

        updateStrengthProgressBar();
    }

    private void initializeComponents() {
        lengthField = new JTextField(5);
        uppercaseCheckBox = new JCheckBox("Uppercase");
        lowercaseCheckBox = new JCheckBox("Lowercase");
        digitsCheckBox = new JCheckBox("Digits");
        specialCharsCheckBox = new JCheckBox("Special Characters");

        uppercaseCheckBox.setSelected(true);
        lowercaseCheckBox.setSelected(true);
        digitsCheckBox.setSelected(true);
        specialCharsCheckBox.setSelected(true);

        generateButton = new JButton("Generate Password");
        copyButton = new JButton("Copy to Clipboard");
        saveButton = new JButton("Save to File");

        passwordArea = new JTextArea(5, 30);
        passwordArea.setEditable(false);

        strengthProgressBar = new JProgressBar();
        strengthProgressBar.setStringPainted(true);
    }

    private JPanel createOptionsPanel() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setBackground(Color.WHITE);
        optionsPanel.add(new JLabel("Password Length:"));
        optionsPanel.add(lengthField);
        optionsPanel.add(uppercaseCheckBox);
        optionsPanel.add(lowercaseCheckBox);
        optionsPanel.add(digitsCheckBox);
        optionsPanel.add(specialCharsCheckBox);
        return optionsPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(generateButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(saveButton);
        return buttonPanel;
    }

    private JPanel createStrengthPanel() {
        JPanel strengthPanel = new JPanel();
        strengthPanel.setBackground(Color.WHITE);
        strengthPanel.setLayout(new BoxLayout(strengthPanel, BoxLayout.Y_AXIS));
        strengthPanel.add(new JLabel("Password Strength:"));
        strengthPanel.add(strengthProgressBar);
        return strengthPanel;
    }

    private void generatePassword() {
        try {
            int length = Integer.parseInt(lengthField.getText());
            boolean includeUppercase = uppercaseCheckBox.isSelected();
            boolean includeLowercase = lowercaseCheckBox.isSelected();
            boolean includeDigits = digitsCheckBox.isSelected();
            boolean includeSpecialChars = specialCharsCheckBox.isSelected();

            String generatedPassword = passwordGenerator.generatePassword(length, includeUppercase, includeLowercase, includeDigits, includeSpecialChars);
            passwordArea.setText(generatedPassword);
            updateStrengthProgressBar();
        } catch (NumberFormatException ex) {
            showErrorMessage("Please enter a valid password length.");
        } catch (IllegalArgumentException ex) {
            showErrorMessage(ex.getMessage());
        }
    }

    private void copyToClipboard() {
        StringSelection selection = new StringSelection(passwordArea.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, null);
        showMessageDialog("Password copied to clipboard.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveToFile() {
        try {
            String password = passwordArea.getText();
            if (password.isEmpty()) {
                throw new IllegalStateException("No password generated to save.");
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Password to File");
            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getPath();
                passwordGenerator.savePasswordToFile(password, filePath);
                showMessageDialog("Password saved to file: " + filePath, "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            showErrorMessage("Error saving password to file.");
        } catch (IllegalStateException ex) {
            showErrorMessage(ex.getMessage());
        }
    }

    private void updateStrengthProgressBar() {
        int strength = passwordGenerator.calculatePasswordStrength(passwordArea.getText());
        strengthProgressBar.setValue(strength);
        strengthProgressBar.setString(strength + "%");
        setStrengthProgressBarColor(strength);
    }

    private void setStrengthProgressBarColor(int strength) {
        if (strength >= 75) {
            strengthProgressBar.setForeground(new Color(0, 128, 0));
        } else if (strength >= 50) {
            strengthProgressBar.setForeground(new Color(255, 165, 0));
        } else {
            strengthProgressBar.setForeground(Color.RED);
        }
    }

    private void showMessageDialog(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private void showErrorMessage(String message) {
        showMessageDialog(message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            PasswordGeneratorGUI passwordGeneratorGUI = new PasswordGeneratorGUI();
            passwordGeneratorGUI.setSize(600, 300);
            passwordGeneratorGUI.setVisible(true);
        });
    }
}

class PasswordGenerator {
    private static final String UPPERCASE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_CHARS = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:'\",.<>?/";

    public String generatePassword(int length, boolean includeUppercase, boolean includeLowercase, boolean includeDigits, boolean includeSpecialChars) {
        String validChars = buildValidCharsString(includeUppercase, includeLowercase, includeDigits, includeSpecialChars);

        if (validChars.isEmpty()) {
            throw new IllegalArgumentException("At least one character type should be selected.");
        }

        return generateRandomPassword(length, validChars);
    }

    public void savePasswordToFile(String password, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(password);
        }
    }

    public int calculatePasswordStrength(String password) {
        int length = password.length();
        int uppercaseCount = countCharacterType(password, UPPERCASE_CHARS);
        int lowercaseCount = countCharacterType(password, LOWERCASE_CHARS);
        int digitsCount = countCharacterType(password, DIGITS);
        int specialCharsCount = countCharacterType(password, SPECIAL_CHARS);

        int strength = 0;

        if (length >= 8) {
            strength += 30;
        }
        if (length >= 12) {
            strength += 20;
        }
        strength += uppercaseCount + lowercaseCount + digitsCount + specialCharsCount;

        return Math.min(strength, 100);
    }

    private String generateRandomPassword(int length, String validChars) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(validChars.length());
            password.append(validChars.charAt(randomIndex));
        }

        return password.toString();
    }

    private String buildValidCharsString(boolean includeUppercase, boolean includeLowercase, boolean includeDigits, boolean includeSpecialChars) {
        StringBuilder validChars = new StringBuilder();
        if (includeUppercase) validChars.append(UPPERCASE_CHARS);
        if (includeLowercase) validChars.append(LOWERCASE_CHARS);
        if (includeDigits) validChars.append(DIGITS);
        if (includeSpecialChars) validChars.append(SPECIAL_CHARS);
        return validChars.toString();
    }

    private int countCharacterType(String password, String charType) {
        int count = 0;
        for (char c : password.toCharArray()) {
            if (charType.indexOf(c) != -1) {
                count++;
            }
        }
        return count;
    }
}
