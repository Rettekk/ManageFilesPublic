import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
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
import java.net.URLConnection;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;

public class guiView extends JTable implements DropTargetListener, MouseListener {

    public JFrame gui;
    private JMenuItem addFile, showCloud, authorize, cred, howTo, disc, exit, logOff;
    private JMenuBar menuBar;
    private JMenu startMenu, helpMenu;
    JLabel label, dropLabel;
    private JTabbedPane tabpane;
    private JTree tree;
    private DefaultMutableTreeNode rootNode;
    JPopupMenu menu;
    JPanel dropPanel, addFileTab, addFileDetails;
    Object[][] data = {};
    DefaultTableModel tableModel;
    JTable table;
    JScrollPane scrollPane;
    JMenuItem deleteItem, changeItem, renameFileItem, createFolderItem;
    DefaultMutableTreeNode selectedNode;
    File selectedFile, fileToDelete;
    TreePath path;
    Object userObject;

    public guiView() {
        gui = new JFrame();
        gui.setBounds(100, 100, 800, 600);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setLocationRelativeTo(null);
        gui.setResizable(false);
        JMenuBar menuBar = new JMenuBar();
        startMenu = new JMenu("Start");
        addFile = new JMenuItem("Datei hochladen");
        showCloud = new JMenuItem("Cloud anzeigen");
        authorize = new JMenuItem("Mit der Cloud verbinden");
        logOff = new JMenuItem("Abmelden");
        exit = new JMenuItem("Exit");
        disc = new JMenuItem("Verbindung zu Google Drive entfernen");
        helpMenu = new JMenu("Hilfe");
        cred = new JMenuItem("Credits");
        howTo = new JMenuItem("Bedienung");
        startMenu.add(addFile).setEnabled(false);
        startMenu.add(showCloud).setEnabled(false);
        startMenu.add(logOff).setEnabled(false);
        startMenu.add(authorize);
        helpMenu.add(cred);
        helpMenu.add(howTo);
        menuBar.add(startMenu);
        menuBar.add(helpMenu);
        startMenu.add(exit);
        startMenu.add(disc).setEnabled(false);
        gui.setJMenuBar(menuBar);
        gui.setVisible(true);
        dropPanel = new JPanel(new BorderLayout());
        dropLabel = new JLabel("Datei bitte hier ablegen");
        dropLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dropLabel.setFont(new Font("Arial", Font.BOLD, 20));

        logOff.addActionListener(e -> {
            addFile.setEnabled(false);
            showCloud.setEnabled(false);
            disc.setEnabled(false);
            logOff.setEnabled(false);
            authorize.setEnabled(true);
            closeAllTabbedPanes(gui.getContentPane());
        });

        disc.addActionListener(e -> {
            try {
                gdrive.revokeDriveConnection();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            addFile.setEnabled(false);
            showCloud.setEnabled(false);
            disc.setEnabled(false);
            logOff.setEnabled(false);
            authorize.setEnabled(true);
            closeAllTabbedPanes(gui.getContentPane());
        });

        authorize.addActionListener(e -> {
            gdriveAuthorize();
            addFile.setEnabled(true);
            showCloud.setEnabled(true);
            authorize.setEnabled(false);
            disc.setEnabled(true);
            logOff.setEnabled(true);
            addFileTab = new JPanel(new BorderLayout());
            addFileTab.setPreferredSize(new Dimension(500, 300));
            tabpane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
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
            tabpane.setSelectedIndex(tabpane.getTabCount() - 1);
        });


        exit.addActionListener(e -> System.exit(0));
    }

    public void showContextMenu(int x, int y) {
        menu = new JPopupMenu();
        deleteItem = new JMenuItem("Loeschen");
        renameFileItem = new JMenuItem("Umbenennen");
        createFolderItem = new JMenuItem("Ordner hinzufuegen");

        renameFileItem.addActionListener(actionEvent -> {
        });

        deleteItem.addActionListener(e -> {
            path = tree.getSelectionPath();
            if (path != null) {
                deleteSelectedFile();
            }
            listTreeFiles();
        });


        menu.add(deleteItem);
        menu.add(renameFileItem);
        menu.add(createFolderItem);
        menu.show(tree, x, y);
    }

    public void deleteSelectedFile() {
        selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode == null) {
            return;
        }
        selectedNode.getUserObjectPath();
         userObject = selectedNode.getUserObject();
        System.out.println(userObject.getClass());
        if (userObject instanceof File) {
            System.out.println(userObject.getClass());
            try {
                Drive service = gdrive.getDriveService();
                fileToDelete = (File) userObject;
                service.files().delete(fileToDelete.getId()).execute();
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
                parent.remove(selectedNode);
                ((DefaultTreeModel) tree.getModel()).reload(parent);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void deleteFile2(DefaultMutableTreeNode nodeToDelete) {
        try {
            if (nodeToDelete != null) {
                userObject = nodeToDelete.getUserObject();
                System.out.println(selectedNode);
                System.out.println(path);
                System.out.println(userObject.getClass());
                if (userObject instanceof File) {
                    Drive service = gdrive.getDriveService();
                    File fileToDelete = (File) userObject;
                    System.out.println(fileToDelete.getId());
                    service.files().delete(fileToDelete.getId()).execute();
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) nodeToDelete.getParent();
                    parent.remove(nodeToDelete);
                    ((DefaultTreeModel) tree.getModel()).reload(parent);
                }
            }
        } catch (IOException | GeneralSecurityException ex) {
            ex.printStackTrace();
        }
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
        removeCloudOverviewTab();
        try {
            Drive service = gdrive.getDriveService();
            FileList result = service.files().list()
                    .setFields("nextPageToken, files(name, size, mimeType, createdTime, id, parents)")
                    .execute();
            List<File> files = result.getFiles();

            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Home");
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

    private int getCloudOverviewTabIndex() {
        for (int i = 0; i < tabpane.getTabCount(); i++) {
            if (tabpane.getTitleAt(i).equals("Cloud Overview")) {
                return i;
            }
        }
        return -1;
    }

    private void removeCloudOverviewTab() {
        int index = getCloudOverviewTabIndex();
        if (index >= 0) {
            tabpane.removeTabAt(index);
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
                fileNode.setUserObject(file);
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
        String[] allowedExtensions = {".pdf", ".docx", ".doc", ".txt", ".odt"};
        e.acceptDrop(DnDConstants.ACTION_COPY);
        try {
            Transferable t = e.getTransferable();
            java.util.List<java.io.File> files = (java.util.List<java.io.File>) t.getTransferData(DataFlavor.javaFileListFlavor);
            for (java.io.File file : files) {
                String getFileName = file.getName();
                String compareFileName = "name='" + getFileName + "' and trashed=false";
                Drive drive = gdrive.getDriveService();
                //  prüfen ob endung passt
                if (Arrays.stream(allowedExtensions).anyMatch(getFileName::endsWith)) {
                    FileList result = drive.files().list().setQ(compareFileName).execute();
                    if (!result.getFiles().isEmpty()) {
                        // prüfen datei vorhanden
                        JOptionPane.showMessageDialog(null, "Die Datei " + getFileName + " ist bereits vorhanden.");
                    } else {
                        // upload
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
                } else {
                    JOptionPane.showMessageDialog(null, "Der Dateityp wird nicht unterstützt. Bitte als .pdf-, .docx- oder .txt-Datei hochladen.");
                }
            }
            dropLabel.setText("drop");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        e.dropComplete(true);
    }

    public static void closeAllTabbedPanes(Container container) {
        for (Component component : container.getComponents()) {
            if (component instanceof JTabbedPane) {
                JTabbedPane tabbedPane = (JTabbedPane) component;
                int count = tabbedPane.getTabCount();
                for (int i = count - 1; i >= 0; i--) {
                    tabbedPane.removeTabAt(i);
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int x = e.getX();
            int y = e.getY();
            path = tree.getPathForLocation(x, y);
            if (path != null) {
                tree.setSelectionPath(path);
                selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
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

