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
                "  <h1>Willkommen zum Benutzerhandbuch von ManageMyFiles.</h1>\n" +
                "Diese Anwendung ermöglicht es Ihnen, auf Ihre Google Drive-Dateien zuzugreifen und diese zu organisieren.<br>" +
                "<br>" +
                "Um Dateien von Ihrem Computer in Google Drive hochzuladen,<br>" +
                "ziehen Sie die Dateien einfach in die Drag&Drop-Schaltfläche.<br>" +
                "Die Datei wird dann umbenannt und in den jeweiligen Ordner gelegt." +
                "<br>" +
                "Dateien und Ordner löschen kann nur der Admin. Bitte wenden Sie sich an ihn." +
                "<br>" +
                "Um eine Datei oder einen Ordner zu suchen,<br>" +
                "geben Sie den Namen der Datei oder des Ordners in das Suchfeld ein und drücken Sie die den Suchen Button.<br>" +
                "<br>" +
                "<br>" +
                "Das Programm funktioniert derzeit nur auf Windows Betriebssystemen ohne Probleme." +
                "<br>" +
                "<br>" +
                "Vielen Dank, dass Sie ManageMyFiles verwenden!", SwingConstants.CENTER);
        panel.add(label);
        return panel;
    }
}
