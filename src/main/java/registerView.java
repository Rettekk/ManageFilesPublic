import javax.swing.*;
import java.awt.*;
import java.io.IOException;
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
        tokenField = new JTextField();
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

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        setTitle("Registrierung - ManageMyFiles");
        setResizable(false);

        registerButton.addActionListener(e -> {
            System.out.println("Register Button clicked");
            String userName = usernameField.getText();
            String passWord = String.valueOf(passwordField.getPassword());
            String token = tokenField.getText();
            if (functions.checkData(passWord, userName)) {
                checkTokenAndRights tokenResult = checkTokenAndRights.checkToken(token);
                boolean validToken = tokenResult.valid;
                String rights = tokenResult.rights;
                boolean dlfile = tokenResult.dlfile;
                if (validToken) {
                    try {
                        int insertValid = database.insertUser(userName, passWord, rights, dlfile);
                        if (insertValid == 1) {
                            errorHandling.confirmregister();
                            database.deleteToken(token);
                            dispose();
                        } else {
                            errorHandling.errorRegister();
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    } catch (ClassNotFoundException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    errorHandling.errorRegister();
                }
            }
        });

        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }
}
