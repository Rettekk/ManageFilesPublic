import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class creditsView {
    public JPanel createPanel() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("<html>\n" +
                "<head>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <h1>Vielen Dank fuer die Benutzung meines Dokumentenmanagers</h1>\n" +
                "  <p>Die folgenden Personen haben an diesem Projekt mitgearbeitet:</p>\n" +
                "  <ul>\n" +
                "    <li><strong>Benny</strong> - Entwickler</li>\n" +
                "  </ul>\n" +
                "  <p>Verwendete Schnittstellen:</p>\n" +
                "  <ul>\n" +
                "    <li><strong>Google Drive API</strong> - Google</li>\n" +
                "    <li><strong>PDFBox - Apache</strong></li>\n" +
                "  </ul>\n" +
                "</body>\n" +
                "</html>\n", SwingConstants.CENTER);
        panel.add(label);
        return panel;
    }
}
