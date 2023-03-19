import javax.swing.*;

public class CustomPanel extends JPanel {
    public CustomPanel(Object[][] data, Object[] columnNames) {
        CustomTable table = new CustomTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
    }
}
