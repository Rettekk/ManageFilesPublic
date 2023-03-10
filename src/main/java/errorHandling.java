import javax.swing.*;
import java.io.File;

public class errorHandling {

    public static void notSupportFileType() {
        JOptionPane.showMessageDialog(null, "Der Dateityp wird nicht unterstützt. Bitte als .pdf-Datei hochladen.");
    }

    public static void lessInfos(File file) {
        JOptionPane.showMessageDialog(null, "Die Datei " + file.getName() + "  hat nicht die nötigen Informationen, um diese in die Cloud zu laden.");
    }

    public static void renameError() {
        JOptionPane.showMessageDialog(null, "Die Datei wurde nicht umbenannt.\nBitte geben Sie einen neuen Namen ein.");
    }

    public static void jobNotFound(File file) {
        JOptionPane.showMessageDialog(null, "Der Ausbildungsberuf wurde in der Datei " + file.getName() + " nicht gefunden. Die Datei wird nicht hochgeladen.");
    }

    public static void examNotFound(File file) {
        JOptionPane.showMessageDialog(null, "Die jeweilige Prüfung wurde in der Datei  " + file.getName() + "  nicht gefunden. Die Datei wird nicht hochgeladen.");
    }

    public static void semesterNotFound(File file) {
        JOptionPane.showMessageDialog(null, "Das Semester wurde in der Datei  " + file.getName() + " nicht gefunden. Die Datei wird nicht hochgeladen.");
    }

    public static void fileWrongPw() {
        JOptionPane.showMessageDialog(null, "Das Passwort ist falsch! Bitte wenden Sie sich an Ihren Administrator.");
    }

    public static void errorDownloadFile(Exception e) {
        JOptionPane.showMessageDialog(null, "Fehler beim Herunterladen der Datei: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    public static void maxAttemptsWrongPw() {
        JOptionPane.showMessageDialog(null, "Sie haben 3 falsche Passwörter eingegeben. Sie werden nun ausgeloggt.");
    }

    public static void successDownloadFile() {
        JOptionPane.showMessageDialog(null, "Die Datei wurde erfolgreich heruntergeladen und befindet sich im Download-Ordner.");
    }
}
