import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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
    private JLabel registerLabel;

    public login() {
        setTitle("ManageMyFiles - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);
        setResizable(false);
        setLocationRelativeTo(null);
        setVisible(false);
        userLabel = new JLabel("Benutzername:");
        passwordLabel = new JLabel("Passwort:");
        userTextField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Anmelden");
        statusLabel = new JLabel("");
        FontIcon iconReg = FontIcon.of(MaterialDesign.MDI_INFORMATION, 25, Color.DARK_GRAY);
        JLabel iconLabel = new JLabel(iconReg);
        registerLabel = new JLabel("Kein Konto?");
        registerLabel.setForeground(Color.BLUE);

        JPanel panel = new JPanel(new GridLayout(4,4));
        panel.add(userLabel);
        panel.add(userTextField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(registerLabel);
        panel.add(new JLabel());
        panel.add(iconLabel);
        panel.add(loginButton);

        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(statusLabel, BorderLayout.SOUTH);
        menuBar menu = new menuBar();
        setJMenuBar(menu.menuBar());

        JToolTip toolTip = new JToolTip();
        toolTip.setTipText("<html><body style='width: 200px;'>" +
                "<h3>Registrierungsinformation</h3>" +
                "<p>Für die Registrierung benötigen Sie ein Token, den Sie von Ihrem Admin bekommen. " +
                "Ohne diesen Token können Sie sich nicht registrieren.</p></body></html>");

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
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            } catch (InvalidKeySpecException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}