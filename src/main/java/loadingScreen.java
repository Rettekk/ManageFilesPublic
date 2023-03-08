import javax.swing.*;

public class loadingScreen extends JDialog {
    private final JProgressBar progressBar;

    public loadingScreen(guiView owner) {
        super();
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        JPanel panel = new JPanel();
        panel.add(progressBar);
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(owner);
    }

    public static void setValue(int percent) {
    }

    public void setProgress(int value) {
        progressBar.setValue(value);
    }
}
