import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class gui extends JTable implements DropTargetListener{

    private JFrame frame;
    private JMenuItem addFile, delFile, showCloud, authorize, cred, howTo, disc, exit;
    private JMenuBar menuBar;
    private JMenu startMenu, helpMenu;
    String[] columnNames = {"Name", "Size", "Type", "Date"};
    Object[][] data = {};
    DefaultTableModel tableModel;
    JTable table;
    JScrollPane scrollPane;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    gui window = new gui();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public gui() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JMenuBar menuBar = new JMenuBar();
        startMenu = new JMenu("Start");
        addFile = new JMenuItem("Datei hochladen");
        delFile = new JMenuItem("Datei loeschen");
        showCloud = new JMenuItem("Cloud anzeigen");
        authorize = new JMenuItem("Mit der Cloud verbinden");
        disc = new JMenuItem("Verbindung trennen");
        exit = new JMenuItem("Exit");
        helpMenu = new JMenu("Hilfe");
        cred = new JMenuItem("Credits");
        howTo = new JMenuItem("Bedienung");
        startMenu.add(addFile).setEnabled(false);
        startMenu.add(delFile).setEnabled(false);
        startMenu.add(showCloud).setEnabled(false);
        startMenu.add(disc).setEnabled(false);
        startMenu.add(authorize);
        startMenu.add(disc);
        startMenu.add(exit);
        helpMenu.add(cred);
        helpMenu.add(howTo);
        menuBar.add(startMenu);
        menuBar.add(helpMenu);
        frame.setJMenuBar(menuBar);

        JPanel panel = new JPanel();
        frame.getContentPane().add(panel, BorderLayout.SOUTH);

        authorize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gdriveAuthorize();
                addFile.setEnabled(true);
                delFile.setEnabled(true);
                showCloud.setEnabled(true);
                authorize.setEnabled(false);
                disc.setEnabled(true);
            }
        });
             //     dropPanel.setDropTarget(new DropTarget(dropPanel, DnDConstants.ACTION_COPY, this));
        showCloud.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listFiles();
            }
        });

        addFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);

            }
        });

        final JPanel dropPanel = new JPanel();
        frame.getContentPane().add(dropPanel, BorderLayout.CENTER);

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(data, columnNames);
        scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        table.setModel(tableModel);
    }

    public void gdriveAuthorize() {
        try {
            gdrive.main();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public void listFiles() {
        try {
            Drive service = gdrive.getDriveService();
            FileList result = service.files().list()
                    .setFields("nextPageToken, files(name, size, mimeType, createdTime)")
                    .execute();
            List<File> files = result.getFiles();

            DefaultTableModel newModel = new DefaultTableModel(columnNames, 0);
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                Object[] rowData = {file.getName(), file.getSize(), file.getMimeType(), file.getCreatedTime()};
                newModel.addRow(rowData);
            }
            table.setModel(newModel);

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dragEnter(DropTargetDragEvent e) {
        if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            e.acceptDrag(DnDConstants.ACTION_COPY);
           // label.setText("dragEnter");
        } else {
            e.rejectDrag();
        }
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
       // label.setText("dragExit");
    }

    @Override
    public void dragOver(DropTargetDragEvent e) {
        if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            e.acceptDrag(DnDConstants.ACTION_COPY);
           // label.setText("DdragOver");
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
              //  String hallo = file.getAbsolutePath();
              //  System.out.println(file.getName() + hallo);
            }
          //  label.setText("drop");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        e.dropComplete(true);
    }

}
