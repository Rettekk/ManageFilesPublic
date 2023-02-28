import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class howToView {
    public JPanel createPanel() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("<html>\n" +
                "<head>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <h1>Benutzeranleitung</h1>\n" , SwingConstants.CENTER);
        panel.add(label);
        return panel;
    }
}
