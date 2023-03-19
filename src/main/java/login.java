import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

public class login extends JFrame {

    private JLabel userLabel, passwordLabel, statusLabel;
    private JTextField userTextField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel registerLabel;

    public login() {
        setTitle("ManageMyFiles - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(false);
        userLabel = new JLabel("Benutzername:");
        passwordLabel = new JLabel("Passwort:");
        userTextField = new JTextField();
        passwordField = new JPasswordField();
        FontIcon iconRegMatchPw = FontIcon.of(MaterialDesign.MDI_LOGIN_VARIANT, 25, Color.DARK_GRAY);
        loginButton = new JButton();
        loginButton.setIcon(iconRegMatchPw);
        statusLabel = new JLabel("");
        FontIcon iconReg = FontIcon.of(MaterialDesign.MDI_INFORMATION, 25, Color.DARK_GRAY);
        JLabel iconLabel = new JLabel(iconReg);
        registerLabel = new JLabel("Kein Konto?");
        registerLabel.setForeground(Color.BLUE);

        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(Color.LIGHT_GRAY);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.setBackground(UIManager.getColor("control"));
            }
        });


        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5); // Abstand zwischen den Komponenten
        c.fill = GridBagConstraints.HORIZONTAL; // Horizontale Ausrichtung der Komponenten
        c.gridx = 0; // Spalte 0
        c.gridy = 0; // Zeile 0

        // Benutzername-Komponenten
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(userLabel, c);

        c.gridx = 1; // Spalte 1
        c.anchor = GridBagConstraints.LINE_END;
        c.gridwidth = 2; // Breite von 2 Spalten
        userTextField.setColumns(10);
        panel.add(userTextField, c);

        // Passwort-Komponenten
        c.gridx = 0; // Spalte 0
        c.gridy = 1; // Zeile 1
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(passwordLabel, c);

        c.gridx = 1; // Spalte 1
        c.anchor = GridBagConstraints.LINE_END;
        c.gridwidth = 2; // Breite von 2 Spalten
        panel.add(passwordField, c);

        // Anmelden-Button-Komponenten
        c.gridx = 0; // Spalte 0
        c.gridy = 2; // Zeile 2
        c.gridwidth = 3; // Breite von 3 Spalten
        c.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, c);

        // Kein Konto?-Label- und Icon-Komponenten
        c.gridx = 0; // Spalte 0
        c.gridy = 3; // Zeile 3
        c.gridheight = 1; // Spannt über 1 Zeile
        c.gridwidth = 1; // Spannt über 1 Spalte
        c.insets = new Insets(0, 0, 0, 0); // Erhöht den links Abstand auf 10 Pixel
        c.anchor = GridBagConstraints.LINE_END;
        panel.add(registerLabel, c);

        c.gridx = 1; // Spalte 1
        c.anchor = GridBagConstraints.LINE_START;
        panel.add(iconLabel, c);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(statusLabel, BorderLayout.SOUTH);
        menuBar menu = new menuBar();
        setJMenuBar(menu.menuBar());

        JToolTip toolTip = new JToolTip();
        toolTip.setTipText("<html><body style='width: 200px;'>" + "<h3>Registrierungsinformation</h3>" + "<p>Für die Registrierung benötigen Sie ein Token, den Sie von Ihrem Admin bekommen. " + "Ohne diesen Token können Sie sich nicht registrieren.</p></body></html>");

        iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        iconLabel.setToolTipText(toolTip.getTipText());
        ToolTipManager.sharedInstance().setInitialDelay(100);


        registerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                registerView register = new registerView();
                register.setVisible(true);
            }
        });


        loginButton.addActionListener(e -> {
            String userName = userTextField.getText();
            String passWord = String.valueOf(passwordField.getPassword());
            if (!functions.checkLoginFields(userTextField, passwordField)) return;
            try {
                if (database.login(userName, passWord)) {
                    errorHandling.forward();
                    guiView gui = new guiView(userName);
                    IconManager iconManager = new IconManager();
                    iconManager.setIcon(guiView.gui);
                    gui.setVisible(true);
                    dispose();
                } else {
                    errorHandling.wrongInput();
                }
            } catch (SQLException | ClassNotFoundException | IOException | NoSuchAlgorithmException |
                     InvalidKeySpecException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}