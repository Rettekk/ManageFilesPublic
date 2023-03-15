import javax.swing.*;
import java.awt.*;

public class functions {

    public static void closeAllTabbedPanes(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JTabbedPane) {
                JTabbedPane tabbedPane = (JTabbedPane) component;
                int count = tabbedPane.getTabCount();
                for (int i = count - 1; i >= 0; i--) {
                    tabbedPane.removeTabAt(i);
                }
            }
        }
    }
    public static String normalizeString(String str) {
        return str.trim().toLowerCase();
    }

    public static void logOut(JFrame gui) {
        functions.closeAllTabbedPanes(gui.getContentPane());
        JOptionPane.showMessageDialog(null, "Sie werden nun abgemeldet.");
        login login = new login();
        login.setVisible(true);
        gui.setVisible(false);
    }
    static boolean checkData(String password, String username) {
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(null, "Das Passwort muss mindestens 8 Zeichen haben.");
            return false;
        }
        if (username.length() < 5) {
            JOptionPane.showMessageDialog(null, "Der Benutzername muss mindestens 5 Zeichen haben.");
            return false;
        }
        return true;
    }
}
