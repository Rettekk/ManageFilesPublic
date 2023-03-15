import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class registerView extends JFrame {

    private final JLabel titleLabel;
    private final JLabel usernameLabel;
    private final JLabel passwordLabel;
    private final JLabel tokenLabel;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JTextField tokenField;
    private final JButton registerButton;

    public registerView() {
        // initialize UI components
        titleLabel = new JLabel("Registrierung - ManageMyFiles");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        usernameLabel = new JLabel("Benutzername:");
        passwordLabel = new JLabel("Passwort:");
        tokenLabel = new JLabel("Registrierungstoken:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        tokenField = new JTextField();
        registerButton = new JButton("Registrieren");

        // set layout
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(tokenLabel);
        panel.add(tokenField);
        panel.add(new JLabel());
        panel.add(registerButton);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setTitle("Registrierung - ManageMyFiles");
        setResizable(false);


        registerButton.addActionListener(e -> {
            String userName = usernameField.getText();
            String passWord = String.valueOf(passwordField.getPassword());
            String token = tokenField.getText();
            if (userName.length() < 5) {
            JOptionPane.showMessageDialog(null, "Der Benutzername muss mindestens 5 Zeichen haben.");
            } else if (passWord.length() < 8) {
                JOptionPane.showMessageDialog(null, "Das Passwort muss mindestens 8 Zeichen haben.");
            } else if (!database.checkToken(token)) {
                JOptionPane.showMessageDialog(null, "Der Registrierungstoken ist ungültig.");
            } else if (database.checkToken(String.valueOf(token.equals(token)))) {
                database.insertUser(userName, passWord,
            }
        });
        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

}
