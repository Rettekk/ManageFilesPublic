import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.ImageIcon;

public class login extends JFrame {

    private JLabel userLabel, passwordLabel, statusLabel;
    private JTextField userTextField;
    private JPasswordField passwordField;
    private JButton loginButton;

    private JMenuItem addFile, delFile, showCloud, authorize, cred, howTo, disc, exit, logOff;
    private JMenu startMenu, helpMenu;
    private JLabel registerLabel;

    public login() {
        setTitle("ManageMyFiles - Login");
        setName("ManageMyFiles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);
        setResizable(false);
        setLocationRelativeTo(null);


        registerLabel = new JLabel("Kein Konto?");
        registerLabel.setForeground(Color.BLUE);
        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                registerView register = new registerView();
                register.setVisible(true);
            }
        });


        userLabel = new JLabel("Benutzername:");
        passwordLabel = new JLabel("Passwort:");
        userTextField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Anmelden");
        statusLabel = new JLabel("");

        JPanel panel = new JPanel(new GridLayout(4, 4));
        panel.add(userLabel);
        panel.add(userTextField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(registerLabel);
        panel.add(new JLabel());
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
        logOff = new JMenuItem("Abmelden");
        exit = new JMenuItem("Exit");
        helpMenu = new JMenu("Hilfe");
        cred = new JMenuItem("Credits");
        howTo = new JMenuItem("Bedienung");
        JMenuItem showTrash = new JMenuItem("Papierkorb anzeigen");
        startMenu.add(addFile).setEnabled(false);
        startMenu.add(delFile).setEnabled(false);
        startMenu.add(showCloud).setEnabled(false);
        startMenu.add(disc).setEnabled(false);
        startMenu.add(authorize).setEnabled(false);
        startMenu.add(logOff).setEnabled(false);
        startMenu.add(disc);
        startMenu.add(exit);
        helpMenu.add(cred);
        helpMenu.add(howTo);
        helpMenu.add(showTrash).setEnabled(false);
        menuBar.add(startMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
        setVisible(true);

        exit.addActionListener(e -> System.exit(0));

        userTextField.setText("lehrer");
        passwordField.setText("lehrer123");

        loginButton.addActionListener(e -> {
            String userName = userTextField.getText();
            String passWord = String.valueOf(passwordField.getPassword());
            try {
                if (database.login(userName, passWord)) {
                    errorHandling.forward();
                    guiView gui = new guiView(userName);
                    gui.setVisible(true);
                    dispose();
                } else {
                    errorHandling.wrongInput();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }


        });
    }
}