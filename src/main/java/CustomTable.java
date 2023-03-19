import javax.swing.*;

public class CustomTable extends JTable {
    public CustomTable(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
        setFillsViewportHeight(true);
    }
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}

