# Password Generator GUI

This Java application provides a graphical user interface (GUI) for generating strong and secure passwords. It leverages Java's Swing library to create an intuitive and user-friendly interface, allowing users to customize their password preferences and generate passwords with varying levels of complexity.

![Password Generator GUI](https://raw.githubusercontent.com/localhost-sys/OOP/main/pic.png "Password Generator GUI")

## Features

- **Password Length Customization:** Users can specify the desired length of the generated password.
- **Character Type Selection:** Users can choose to include uppercase letters, lowercase letters, digits, and special characters in their passwords.
- **Password Strength Indicator:** The application evaluates the strength of the generated password and visually represents it with a progress bar. The strength calculation considers factors such as length, character types, and complexity.
- **Copy to Clipboard:** Users can easily copy the generated password to the system clipboard with the click of a button.
- **Save to File:** The application allows users to save the generated password to a text file for later use.

## Implementation Details

- **Object-Oriented Design:** The code follows object-oriented programming principles, with a clear separation of GUI-related components in the `PasswordGeneratorGUI` class and password generation logic in the `PasswordGenerator` class.
- **Secure Randomization:** The application uses `SecureRandom` for secure randomization during password generation.
- **Password Strength Calculation:** The strength of the generated password is calculated based on factors such as length, character types, and complexity.

## Dependencies

- Java Swing Library
