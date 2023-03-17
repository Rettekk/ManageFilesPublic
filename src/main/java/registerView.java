import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Arrays;

public class registerView extends JFrame {

    private final JLabel usernameLabel, passwordLabel, tokenLabel, repeatPasswordLabel, passwordMismatchLabel, iconRegLabel;
    private final JTextField usernameField, tokenField;
    private final JPasswordField passwordField, repeatPasswordField;
    private final JButton registerButton;
    JPanel panel;
    FontIcon iconRegMatchPw;
    JToolTip toolTipMatchPw;
    public registerView() {

        usernameLabel = new JLabel("Benutzername:");
        passwordLabel = new JLabel("Passwort:");
        repeatPasswordLabel = new JLabel("Passwort repeat:");
        tokenLabel = new JLabel("Registrierungstoken:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        tokenField = new JTextField(8);
        registerButton = new JButton("Registrieren");
        passwordMismatchLabel = new JLabel("Passwörter stimmen nicht überein");
        repeatPasswordField = new JPasswordField();

        panel = new JPanel(new GridLayout(5, 3, 2, 2));

        //Erste Zeile
        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(new JLabel());
        //2.
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        //3.
        panel.add(repeatPasswordLabel);
        panel.add(repeatPasswordField);
        iconRegMatchPw = FontIcon.of(MaterialDesign.MDI_ALERT, 25, Color.RED.darker());
        iconRegLabel = new JLabel(iconRegMatchPw);
        iconRegLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 90)); // rechter Abstand
        iconRegLabel.setVisible(false);
        panel.add(iconRegLabel);
        //4.
        panel.add(tokenLabel);
        panel.add(tokenField);
        panel.add(new JLabel());
        //5.
        panel.add(new JLabel());
        panel.add(registerButton);
        panel.add(new JLabel());

        //ToolTip
        toolTipMatchPw = new JToolTip();
        toolTipMatchPw.setTipText("<html><body style='width: 200px;'>" + "Beide Passwörter stimmen nicht überein! Bitte prüfen!</body></html>");
        iconRegLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        iconRegLabel.setToolTipText(toolTipMatchPw.getTipText());
        ToolTipManager.sharedInstance().setInitialDelay(100);

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkPasswordMatch();
            }
        });

        repeatPasswordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                checkPasswordMatch();
            }
        });


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
            if (checkFieldsInputs()) {
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
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException | SQLException |
                             ClassNotFoundException |
                             IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
    }
    private void checkPasswordMatch() {
        char[] password = passwordField.getPassword();
        char[] repeatPassword = repeatPasswordField.getPassword();
        if (!Arrays.equals(password, repeatPassword)) {
            iconRegLabel.setVisible(true);
        } else {
            iconRegLabel.setVisible(false);
        }
    }
    public boolean checkFieldsInputs() {
        if (usernameField.getText().isEmpty() || passwordField.getPassword().length == 0 || tokenField.getText().isEmpty() || repeatPasswordField.getPassword().length == 0) {
            errorHandling.errorEmptyFields();
            return false;
        } else {
            return true;
        }
    }
}