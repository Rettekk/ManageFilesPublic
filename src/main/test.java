/*import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class frame extends JFrame implements DropTargetListener {

    private JLabel label;

    public frame() {
        initComponents();
    }

    private void initComponents() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("File");
        JMenuItem menuItem = new JMenuItem("Add File");

        menu.add(menuItem);
        menuBar.add(menu);

        JPanel dropPanel = new JPanel();
        dropPanel.setPreferredSize(new Dimension(400, 300));

        label = new JLabel("Nichts");

        dropPanel.add(label);

        setJMenuBar(menuBar);
        getContentPane().add(dropPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("My GUI");
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        dropPanel.setDropTarget(new DropTarget(dropPanel, DnDConstants.ACTION_COPY, this));
        dropPanel.setEnabled(false);

        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                label.setText("reinziehen");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new frame().setVisible(true);
        });
    }

    @Override
    public void dragEnter(DropTargetDragEvent e) {
        if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            e.acceptDrag(DnDConstants.ACTION_COPY);
            label.setText("dragEnter");
        } else {
            e.rejectDrag();
        }
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        label.setText("dragExit");
    }

    @Override
    public void dragOver(DropTargetDragEvent e) {
        if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            e.acceptDrag(DnDConstants.ACTION_COPY);
            label.setText("DdragOver");
        } else {
            e.rejectDrag();
        }
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent) {

    }

    @Override
    public void drop(DropTargetDropEvent e) {
        e.acceptDrop(DnDConstants.ACTION_COPY);
        try {
            Transferable t = e.getTransferable();
            java.util.List<File> files = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
            for (File file : files) {
                // Hier können Sie den Code einfügen, um die Datei hochzuladen oder zu speichern
                String hallo = file.getAbsolutePath();
                System.out.println(file.getName() + hallo);
            }
            label.setText("drop");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        e.dropComplete(true);
    }

}
*/