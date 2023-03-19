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

    public static void errorDownloadFile(Exception e) {
        JOptionPane.showMessageDialog(null, "Fehler beim Herunterladen der Datei: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    public static void successDownloadFile() {
        JOptionPane.showMessageDialog(null, "Die Datei wurde erfolgreich heruntergeladen und befindet sich im Download-Ordner.");
    }

    public static void forward() {
        JOptionPane.showMessageDialog(null, "Sie werden nun weitergeleitet.", "Erfolgreich", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void wrongInput() {
        JOptionPane.showMessageDialog(null, "Benutzername oder Passwort falsch!", "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    public static void deleteOnlyOneFolder() {
        System.out.println("Es kann nur ein Ordner gelöscht werden.");
    }

    public static void noPermission() {
        JOptionPane.showMessageDialog(null, "Sie haben keine ausreichenden Rechte, um diese Datei herunterzuladen.", "Keine Berechtigung", JOptionPane.ERROR_MESSAGE);

    }

    public static void trashErrorRevokeFile(Exception e) {
        JOptionPane.showMessageDialog(null, "Fehler beim Wiederherstellen der Datei: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    public static void trashError(Exception e) {
        JOptionPane.showMessageDialog(null, "Fehler beim Laden des Papierkorbs: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    public static void noPermissionRevoke() {
        JOptionPane.showMessageDialog(null, "Sie haben keine ausreichenden Rechte, um diese Datei wiederherzustellen.", "Keine Berechtigung", JOptionPane.ERROR_MESSAGE);
    }

    public static void errorRegister() {
        JOptionPane.showMessageDialog(null, "Der Registrierungstoken ist falsch.");
    }

    public static void confirmregister() {
        JOptionPane.showMessageDialog(null, "Registrierung erfolgreich!");
    }

    public static void emptyFields() {
        JOptionPane.showMessageDialog(null, "Bitte füllen Sie alle Felder aus.");
    }

    public static void errorEmptyFields() {
        JOptionPane.showMessageDialog(null, "Bitte füllen Sie alle Felder aus.");
    }
}
