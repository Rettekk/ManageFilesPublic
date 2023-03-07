import javax.swing.*;
import java.awt.*;

public class loadingScreen {
    private static JWindow window;
    private JLabel label;

    public loadingScreen() {
        window = new JWindow();
        JPanel panel = new JPanel(new BorderLayout());
        label = new JLabel("Loading...", SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        window.add(panel);
        window.pack();
        window.setLocationRelativeTo(null);
    }

    public static void showLoadingScreen() {
        window.setVisible(true);
    }

    public static void hideLoadingScreen() {
        window.dispose();
    }

    public void setLoadingText(String text) {
        label.setText(text);
    }
}
