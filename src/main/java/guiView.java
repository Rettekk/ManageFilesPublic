import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.sun.jdi.event.MonitorWaitEvent;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.tree.MutableTreeNode;

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
    private creditsView creditsView = new creditsView();
    private howToView howToView = new howToView();


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
        addFileDetails = new JPanel(new BorderLayout());
       // tabpane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabpane = new closableTabs();
        gui.add(tabpane);

        logOff.addActionListener(e -> {
            closeAllTabbedPanes(gui.getContentPane());
            JOptionPane.showMessageDialog(null, "Sie werden nun abgemeldet.");
            login login = new login();
            login.setVisible(true);
            gui.setVisible(false);
        });

        cred.addActionListener(e -> {
            openCreditsTab();
        });
        howTo.addActionListener(e -> {
            openHowToUseTab();
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
        deleteItem = new JMenuItem("Datei loeschen");
        renameFileItem = new JMenuItem("Datei umbenennen");
        createFolderItem = new JMenuItem("Ordner hinzufuegen");
        JMenuItem renameFolderItem = new JMenuItem("Ordner umbenennen");
        JMenuItem deleteFolderItem = new JMenuItem("Ordner loeschen");

        renameFileItem.addActionListener(actionEvent -> {
            renameSelectedFile(selectedNode);
        });

        deleteItem.addActionListener(e -> {
            try {
                deleteSelectedFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (GeneralSecurityException ex) {
                throw new RuntimeException(ex);
            }
        });

        createFolderItem.addActionListener(actionEvent -> {
            createFolder(selectedNode);
        });

        renameFolderItem.addActionListener(actionEvent -> {
            try {
                renameFolder(selectedNode);
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        deleteFolderItem.addActionListener(e -> {
            try {
                deleteFolder(selectedNode);
            } catch (GeneralSecurityException ex) {
                throw new RuntimeException(ex);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        menu.add(deleteItem);
        menu.add(renameFileItem);
        menu.add(createFolderItem);
        menu.add(renameFolderItem);
        menu.add(deleteFolderItem);
        menu.show(tree, x, y);
    }

    public void deleteFolder(DefaultMutableTreeNode selectedNode) throws IOException, GeneralSecurityException {
        Drive service = gdrive.getDriveService();
        File file = (File) selectedNode.getUserObject();

        if (!file.getMimeType().equals("application/vnd.google-apps.folder")) {
            System.out.println("Es kann nur ein Ordner gelöscht werden.");
            return;
        }

        FileList result = service.files().list()
                .setQ("'" + file.getId() + "' in parents")
                .setFields("nextPageToken, files(id)")
                .execute();
        List<File> files = result.getFiles();
        if (files.size() > 0) {
            int choice = JOptionPane.showConfirmDialog(null, "Der Ordner enthält " + files.size() + " Unterordner oder Dateien. Möchten Sie den Ordner und alle Unterordner und Dateien löschen?", "Bestätigung", JOptionPane.YES_NO_OPTION);
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }

        deleteFolderRecursive(service, file.getId());
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
        parent.remove(selectedNode);
        ((DefaultTreeModel) tree.getModel()).reload(parent);
    }

    private void deleteFolderRecursive(Drive service, String folderId) throws IOException {
        FileList result = service.files().list()
                .setQ("'" + folderId + "' in parents")
                .setFields("nextPageToken, files(id, mimeType)")
                .execute();
        List<File> files = result.getFiles();

        for (File file : files) {
            if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
                deleteFolderRecursive(service, file.getId());
            } else {
                service.files().delete(file.getId()).execute();
            }
        }

        int choice = JOptionPane.showConfirmDialog(null, "Wollen Sie den Ordner wirklich löschen?", "Bestätigung", JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            service.files().delete(folderId).execute();
        }
    }


    public void renameFolder(DefaultMutableTreeNode selectedNode) throws GeneralSecurityException, IOException {
        Drive service = gdrive.getDriveService();
        File selectedFile = (File) selectedNode.getUserObject();
        String newFolderName = JOptionPane.showInputDialog("Wie soll der Ordner umbenannt werden?");
        if (newFolderName != null) {
            File fileMetadata = new File();
            fileMetadata.setName(newFolderName);
            File updatedFile = service.files().update(selectedFile.getId(), fileMetadata).execute();
            selectedNode.setUserObject(updatedFile);
            ((DefaultTreeModel) tree.getModel()).reload(selectedNode);
        }
    }


    public void createFolder(DefaultMutableTreeNode selectedNode) {
        try {
            Drive service = gdrive.getDriveService();

            String newFolderName = JOptionPane.showInputDialog("Bitte einen Name für den Ordner eingeben:");
            File fileMetadata = new File();
            fileMetadata.setName(newFolderName);
            fileMetadata.setMimeType("application/vnd.google-apps.folder");

            if (newFolderName == null || newFolderName.isEmpty()) {
                return;
            }

            if (selectedNode != null && selectedNode.getUserObject() instanceof File) {
                File selectedFile = (File) selectedNode.getUserObject();
                if (selectedFile.getMimeType().equals("application/vnd.google-apps.folder")) {
                    String parentId = selectedFile.getId();
                    fileMetadata.setParents(Collections.singletonList(parentId));
                }
            }

            File newFolder = service.files().create(fileMetadata).execute();
            DefaultMutableTreeNode newFolderNode = new DefaultMutableTreeNode(newFolder.getName());
            newFolderNode.setUserObject(newFolder);

            if (selectedNode != null) {
                selectedNode.add(newFolderNode);
                ((DefaultTreeModel) tree.getModel()).reload(selectedNode);
            } else {
                DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
                model.insertNodeInto(newFolderNode, (MutableTreeNode) model.getRoot(), model.getChildCount(model.getRoot()));
                tree.scrollPathToVisible(new TreePath(newFolderNode.getPath()));
            }

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Fehler beim Erstellen des Ordners: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void deleteSelectedFile() throws IOException, GeneralSecurityException {
        selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
        if (selectedNode == null) {
            return;
        }
        Object deleteObject = selectedNode.getUserObject();
        if (deleteObject instanceof File) {
            Drive service = gdrive.getDriveService();
            File fileToDelete = (File) deleteObject;
            service.files().delete(fileToDelete.getId()).execute();
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selectedNode.getParent();
            parent.remove(selectedNode);
            ((DefaultTreeModel) tree.getModel()).reload(parent);
        }
    }

    private void renameSelectedFile(DefaultMutableTreeNode nodeToRename) {
        try {
            if (nodeToRename != null) {
                Object userObject = nodeToRename.getUserObject();
                if (userObject instanceof File) {
                    File fileToRename = (File) userObject;
                    String currentName = fileToRename.getName();
                    String newName = JOptionPane.showInputDialog("Geben Sie den neuen Namen fuer die Datei ein:", currentName);
                    if (newName != null && !newName.equals(currentName)) {
                        File updatedFile = new File();
                        updatedFile.setName(newName);
                        Drive service = gdrive.getDriveService();
                        service.files().update(fileToRename.getId(), updatedFile).execute();
                        fileToRename.setName(newName);
                        nodeToRename.setUserObject(fileToRename);
                        ((DefaultTreeModel) tree.getModel()).nodeChanged(nodeToRename);
                    } else {
                        JOptionPane.showMessageDialog(null, "Die Datei wurde nicht umbenannt.\nBitte geben Sie einen neuen Namen ein.");
                    }
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
                    .setFields("files(name, size, mimeType, id, parents)")
                    .execute();
            List<File> files = result.getFiles();

            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Home");
            DefaultTreeModel model = new DefaultTreeModel(root);
            for (File file : files) {
                if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
                    DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(file.getName());
                    folderNode.setUserObject(file);
                    root.add(folderNode);
                    addFilesToNode(folderNode, file, service);
                } else {
                    DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file.getName());
                    fileNode.setUserObject(file);
                    root.add(fileNode);

                }
            }
            tree = new JTree(model);
            tree.setCellRenderer(new treeName());
            JScrollPane scrollPane = new JScrollPane(tree);
            scrollPane.setPreferredSize(new Dimension(800, 300));
            tabpane.addTab("Cloud Overview", scrollPane);
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

    public void openCreditsTab() {
        String tabName = "Credits";
        boolean tabExists = false;
        int tabIndex = -1;

        for (int i = 0; i < tabpane.getTabCount(); i++) {
            if (tabpane.getTitleAt(i).equals(tabName)) {
                tabExists = true;
                tabIndex = i;
                break;
            }
        }

        if (!tabExists) {
            creditsView creditsView = new creditsView();
            scrollPane = new JScrollPane(creditsView.createPanel());
            tabpane.addTab(tabName, scrollPane);
            tabIndex = tabpane.getTabCount() - 1;
        }

        tabpane.setSelectedIndex(tabIndex);
    }

    public void openHowToUseTab() {
        String tabName = "Benutzeranleitung";
        boolean tabExists = false;
        int tabIndex = -1;

        for (int i = 0; i < tabpane.getTabCount(); i++) {
            if (tabpane.getTitleAt(i).equals(tabName)) {
                tabExists = true;
                tabIndex = i;
                break;
            }
        }

        if (!tabExists) {
            howToView howToView = new howToView();
            scrollPane = new JScrollPane(howToView.createPanel());
            tabpane.addTab(tabName, scrollPane);
            tabIndex = tabpane.getTabCount() - 1;
        }

        tabpane.setSelectedIndex(tabIndex);
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
                folderNode.setUserObject(file);
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
        } else {
            e.rejectDrag();
        }
    }

    @Override
    public void dragExit(DropTargetEvent dte) {
    }

    @Override
    public void dragOver(DropTargetDragEvent e) {
        if (e.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            e.acceptDrag(DnDConstants.ACTION_COPY);
        } else {
            e.rejectDrag();
        }
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dropTargetDragEvent) {
    }

    public void drop(DropTargetDropEvent e) {
        String[] allowedExtensions = {".pdf"};
        e.acceptDrop(DnDConstants.ACTION_COPY);
        try {
            Transferable t = e.getTransferable();
            java.util.List<java.io.File> files = (java.util.List<java.io.File>) t.getTransferData(DataFlavor.javaFileListFlavor);
            for (java.io.File file : files) {
                String getFileName = file.getName();
                String compareFileName = "name='" + getFileName + "' and trashed=false";
                Drive drive = gdrive.getDriveService();
                if (Arrays.stream(allowedExtensions).anyMatch(getFileName::endsWith)) {
                    FileList result = drive.files().list().setQ(compareFileName).execute();
                    if (!result.getFiles().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Die Datei " + getFileName + " ist bereits vorhanden.");
                    } else {
                        JDialog confirmDialog = new JDialog(gui, "Datei hochladen?", true);
                        JLabel messageLabel = new JLabel("Möchten Sie die Datei \"" + file.getName() + "\" hochladen (" + file.length() + " Bytes)?");
                        JButton yesButton = new JButton("Ja");
                        JButton noButton = new JButton("Nein");

                        yesButton.addActionListener(ev -> {
                            try {
                                File fileMetadata = new File();
                                fileMetadata.setName(file.getName());
                                File newFile = drive.files().create(fileMetadata).execute();
                                String fileId = newFile.getId();
                                java.io.File fileContent = new java.io.File(file.getAbsolutePath());
                                ByteArrayContent content = new ByteArrayContent("application/octet-stream", Files.readAllBytes(fileContent.toPath()));
                                Drive.Files.Update update = drive.files().update(fileId, null, content);
                                update.execute();
                                confirmDialog.dispose();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        });

                        noButton.addActionListener(ev -> {
                            confirmDialog.dispose();
                        });

                        JPanel buttonPanel = new JPanel();
                        buttonPanel.add(yesButton);
                        buttonPanel.add(noButton);
                        confirmDialog.getContentPane().setLayout(new BorderLayout());
                        confirmDialog.getContentPane().add(messageLabel, BorderLayout.CENTER);
                        confirmDialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
                        confirmDialog.pack();
                        confirmDialog.setLocationRelativeTo(gui);
                        confirmDialog.setVisible(true);
                        listTreeFiles();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Der Dateityp wird nicht unterstützt. Bitte als .pdf-Datei hochladen.");
                }
            }
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


    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            int x = e.getX();
            int y = e.getY();
            path = tree.getPathForLocation(x, y);
            if (path != null) {
                tree.setSelectionPath(path);
                selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
                showContextMenu(x, y);
            } else
                tree.clearSelection();
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {
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

