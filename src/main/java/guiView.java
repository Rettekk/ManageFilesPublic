import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

public class guiView extends JTable implements DropTargetListener, MouseListener{

    public JFrame gui;
    private JMenuItem addFile, delFile, showCloud, authorize, cred, howTo, disc, exit;
    private JMenuBar menuBar;
    private JMenu startMenu, helpMenu;
    JLabel label, dropLabel;
    private JTabbedPane tabpane;
    private JTree tree;
    private DefaultMutableTreeNode rootNode;
    JPopupMenu menu;
    JPanel dropPanel;
    Object[][] data = {};
    DefaultTableModel tableModel;
    JTable table;
    JScrollPane scrollPane;
    JMenuItem deleteItem, changeItem;
    DefaultMutableTreeNode selectedNode;
    File selectedFile;

    TreePath path;
    public guiView() {
        gui = new JFrame();
        gui.setBounds(100, 100, 800, 600);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setLocationRelativeTo(null);
        gui.setResizable(false);
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
        gui.setJMenuBar(menuBar);
        gui.setVisible(true);
        dropPanel = new JPanel(new BorderLayout());
        dropLabel = new JLabel("Drop files here");
        dropLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dropLabel.setFont(new Font("Arial", Font.BOLD, 20));
        authorize.addActionListener(e -> {
            gdriveAuthorize();
            addFile.setEnabled(true);
            delFile.setEnabled(true);
            showCloud.setEnabled(true);
            authorize.setEnabled(false);
            disc.setEnabled(true);

            JPanel addFileTab = new JPanel(new BorderLayout());
            addFileTab.setPreferredSize(new Dimension(500, 300));
            tabpane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT );
            gui.add(tabpane);
            scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(800, 300));
            addFileTab.add(scrollPane, BorderLayout.CENTER);
        });

        showCloud.addActionListener(e -> listTreeFiles());
        addFile.addActionListener(e -> {
            dropPanel.setDropTarget(new DropTarget(dropPanel, DnDConstants.ACTION_COPY, this));
            dropPanel.setPreferredSize(new Dimension(500, 300));
            dropPanel.add(dropLabel, BorderLayout.NORTH);
            gui.add(dropPanel);
            tabpane.addTab("Datei hinzufuegen", dropPanel);
            tabpane.setSelectedIndex(tabpane.getTabCount()-1);
        });

        delFile.addActionListener(e -> {
            TreePath selectedPath = tree.getSelectionPath();
            if (selectedPath != null) {
                 selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                if (selectedNode.getUserObject() instanceof File) {
                    File selectedFile = (File) selectedNode.getUserObject();
                    try {
                        Drive service = gdrive.getDriveService();
                        service.files().delete(selectedFile.getId()).execute();
                        listTreeFiles();
                    } catch (IOException | GeneralSecurityException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        exit.addActionListener(e -> System.exit(0));
    }

    public void showContextMenu(int x, int y) {
        menu = new JPopupMenu();
        deleteItem = new JMenuItem("Loeschen");
        changeItem = new JMenuItem("Umbenennen");
        changeItem.addActionListener(actionEvent -> {
            TreePath selectedPath = tree.getSelectionPath();
            if (selectedPath != null) {
                selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                if (selectedNode.getUserObject() instanceof File) {
                    File selectedFile = (File) selectedNode.getUserObject();
                    String newName = JOptionPane.showInputDialog("222");
                    if (newName != null && !newName.equals(selectedFile.getName())) {
                        try {
                            Drive service = gdrive.getDriveService();
                            File fileMetadata = new File();
                            fileMetadata.setName(newName);
                            File updatedFile = service.files().create(fileMetadata).execute();
                            service.files().delete(selectedFile.getId()).execute();
                            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
                            parent.remove(selectedNode);
                            DefaultMutableTreeNode newFileNode = new DefaultMutableTreeNode(updatedFile);
                            parent.add(newFileNode);
                            ((DefaultTreeModel)tree.getModel()).reload(parent);
                        } catch (IOException | GeneralSecurityException ex) {
                            ex.printStackTrace();
                        }
                    }

                }
            }
        });

        deleteItem.addActionListener(e -> {
            TreePath selectedPath = tree.getSelectionPath();
            if (selectedPath != null) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                if (selectedNode.getUserObject() instanceof File) {
                    File selectedFile = (File) selectedNode.getUserObject();
                    try {
                        Drive service = gdrive.getDriveService();
                        service.files().delete(selectedFile.getId()).execute();
                        listTreeFiles();
                    } catch (IOException | GeneralSecurityException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        menu.add(deleteItem);
        menu.add(changeItem);
        menu.show(tree, x, y);
    }

    public void gdriveAuthorize() {
        try {
            gdrive.getDriveService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public void listTreeFiles() {
        try {
            Drive service = gdrive.getDriveService();
            FileList result = service.files().list()
                    .setFields("nextPageToken, files(name, size, mimeType, createdTime, id, parents)")
                    .execute();
            List<File> files = result.getFiles();

            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
            DefaultTreeModel model = new DefaultTreeModel(root);
            for (File file : files) {
                if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
                    DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(file.getName());
                    root.add(folderNode);
                    addFilesToNode(folderNode, file, service);
                } else {
                    DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file.getName());
                    root.add(fileNode);
                }
            }
            tree = new JTree(model);
            JScrollPane scrollPane = new JScrollPane(tree);
            scrollPane.setPreferredSize(new Dimension(800, 300));
            tabpane.add("Cloud Overview", scrollPane);
            tabpane.setSelectedIndex(tabpane.getTabCount() - 1);
            tree.addMouseListener(this);

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    }



    public void addFilesToNode(DefaultMutableTreeNode folderNode, File folder, Drive service) throws IOException {
        FileList result = service.files().list()
                .setQ("'" + folder.getId() + "' in parents")
                .setFields("nextPageToken, files(name, size, mimeType, createdTime, id, parents)")
                .execute();
        List<File> files = result.getFiles();
        for (File file : files) {
            if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
                DefaultMutableTreeNode subFolderNode = new DefaultMutableTreeNode(file.getName());
                folderNode.add(subFolderNode);
                addFilesToNode(subFolderNode, file, service);
            } else {
                DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file.getName());
                folderNode.add(fileNode);
            }
        }
    }

    @Override
    public void dragEnter(DropTargetDragEvent e) {
        if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            e.acceptDrag(DnDConstants.ACTION_COPY);
            dropLabel.setText("dragEnter");
        } else {
            e.rejectDrag();
        }
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
        dropLabel.setText("dragExit");
    }

    @Override
    public void dragOver(DropTargetDragEvent e) {
        if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            e.acceptDrag(DnDConstants.ACTION_COPY);
            dropLabel.setText("DdragOver");
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
            java.util.List<java.io.File> files = (java.util.List<java.io.File>) t.getTransferData(DataFlavor.javaFileListFlavor);
            for (java.io.File file : files) {
                Drive drive = gdrive.getDriveService();
                File fileMetadata = new File();
                fileMetadata.setName(file.getName());

                File newFile = drive.files().create(fileMetadata).execute();
                String fileId = newFile.getId();

                java.io.File fileContent = new java.io.File(file.getAbsolutePath());
                ByteArrayContent content = new ByteArrayContent("application/octet-stream", Files.readAllBytes(fileContent.toPath()));

                Drive.Files.Update update = drive.files().update(fileId, null, content);
                update.execute();

                listTreeFiles();
            }
            dropLabel.setText("drop");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        e.dropComplete(true);
    }
    @Override
    public void mouseReleased (MouseEvent e){
        if (e.isPopupTrigger()) {
            int x = e.getX();
            int y = e.getY();
             path = tree.getPathForLocation(x, y);
            if (path != null) {
                tree.setSelectionPath(path);
                showContextMenu(x, y);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    public static void main(String[] args) {
        guiView gui = new guiView();
        gui.setVisible(true);
    }
}

