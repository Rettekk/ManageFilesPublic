import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class login extends JFrame {

    private JLabel userLabel, passwordLabel, statusLabel;
    private JTextField userTextField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private JMenuItem addFile, delFile, showCloud, authorize, cred, howTo, disc, exit;
    private JMenu startMenu, helpMenu;
    private static final String SQL_INSERT = "SELECT logins (username, password) VALUES (?,?)";


    public login() {
        setTitle("MangeFiles Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        userLabel = new JLabel("Benutzername:");
        passwordLabel = new JLabel("Passwort:");
        userTextField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Anmelden");
        statusLabel = new JLabel("");

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(userLabel);
        panel.add(userTextField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel());
        panel.add(loginButton);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(statusLabel, BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        startMenu = new JMenu("Start");
        addFile = new JMenuItem("Datei hochladen");
        delFile = new JMenuItem("Datei loeschen");
        showCloud = new JMenuItem("Cloud anzeigen");
        authorize = new JMenuItem("Mit der Cloud verbinden");
        disc = new JMenuItem("Verbindung trennen");
        exit = new JMenuItem("Exit");
        helpMenu = new JMenu("Hilfe");
        cred = new JMenuItem("Credits");
        howTo = new JMenuItem("Bedienung");
        startMenu.add(addFile).setEnabled(false);
        startMenu.add(delFile).setEnabled(false);
        startMenu.add(showCloud).setEnabled(false);
        startMenu.add(disc).setEnabled(false);
        startMenu.add(authorize).setEnabled(false);
        startMenu.add(disc);
        startMenu.add(exit);
        helpMenu.add(cred);
        helpMenu.add(howTo);
        menuBar.add(startMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
        setVisible(true);

        userTextField.setText("Test");
        passwordField.setText("Test123");

        setSize(300, 150);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(true);

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    database.openDataBaseConnection();


                    database.closeDataBaseConnection();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (ClassNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }

    public static void main(String[] args) {
        new login();
    }

    private boolean checkLoginInserts() {
        String passwordValue = String.valueOf(passwordField.getPassword());
        String usernameValue = userTextField.getText();
        if (userTextField.getText().isBlank()) {
            JOptionPane.showMessageDialog(null, "Benutzername ist leer!", "Fehler", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(null, "Passworteingabefeld ist leer!", "Fehler", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!(passwordValue.equals(passwordField) && usernameValue.equals(userTextField))) {
            JOptionPane.showMessageDialog(null, "Falsches Passwort oder Benutzername. Bitte versuchen Sie es erneut.",
                    "Fehler", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
