import javax.swing.*;

public class errorHandling {

    public void wrongFile() {
        JOptionPane.showMessageDialog(null, "Wrong file type. Please select a .txt file.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}
