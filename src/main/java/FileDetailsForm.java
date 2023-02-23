import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FileDetailsForm extends JFrame {
    private JLabel fileNameLabel;
    private JTextField fileNameTextField;
    private JTable fileDetailsTable;

    public FileDetailsForm() {
        setTitle("Datei Details");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        fileNameLabel = new JLabel("Dateiname: ");
        add(fileNameLabel);

        fileNameTextField = new JTextField();
        add(fileNameTextField);

        fileDetailsTable = new JTable();
        fileDetailsTable.setModel(new DefaultTableModel(new Object[][]{
                {"Name", ""},
                {"Größe", ""},
                {"Typ", ""}
        }, new String[]{"Eigenschaft", "Wert"}));
        add(fileDetailsTable);

        setVisible(true);
    }

    public static void main(String[] args) {
        FileDetailsForm form = new FileDetailsForm();
    }
}
