import javax.swing.*;

public class errorHandling {

    public static void notSupportFileType() {
        JOptionPane.showMessageDialog(null, "Der Dateityp wird nicht unterstützt. Bitte als .pdf-Datei hochladen.");
    }

    public static void lessInfos() {
        JOptionPane.showMessageDialog(null, "Die Datei hat nicht die nötigen Informationen, um diese in die Cloud zu laden.");
    }

}
