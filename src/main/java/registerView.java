import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
        titleLabel = new JLabel("Registrierung - ManageMyFiles");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        usernameLabel = new JLabel("Benutzername:");
        passwordLabel = new JLabel("Passwort:");
        tokenLabel = new JLabel("Registrierungstoken:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        tokenField = new JTextField(8);
        registerButton = new JButton("Registrieren");

        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(tokenLabel);
        panel.add(tokenField);
        panel.add(new JLabel());
        panel.add(registerButton);

        ((AbstractDocument) tokenField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if ((fb.getDocument().getLength() + string.length()) <= 8 && string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                if ((fb.getDocument().getLength() + text.length() - length) <= 8 && text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });


        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setTitle("Registrierung - ManageMyFiles");
        setResizable(false);

        registerButton.addActionListener(e -> {
            String userName = usernameField.getText();
            String passWord = String.valueOf(passwordField.getPassword());
            byte[] salt = hashPw.generateSalt();
            int token = Integer.parseInt(tokenField.getText());
            if (functions.checkData(passWord, userName)) {
                try {
                    byte[] hashedPassword = hashPw.hashPassword(passWord, salt);
                    checkTokenAndRights tokenResult = checkTokenAndRights.checkToken(token);
                    boolean validToken = tokenResult.valid;
                    String rights = tokenResult.rights;
                    boolean dlfile = tokenResult.dlfile;
                    if (validToken) {
                        int insertValid = database.insertUser(userName, hashedPassword, rights, dlfile, salt);
                        if (insertValid == 1) {
                            errorHandling.confirmregister();
                            database.deleteToken(token);
                            dispose();
                        } else {
                            errorHandling.errorRegister();
                        }
                    } else {
                        errorHandling.errorRegister();
                    }
                } catch (NoSuchAlgorithmException | InvalidKeySpecException | SQLException | ClassNotFoundException |
                         IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }
}
