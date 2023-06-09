import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.kordamp.ikonli.materialdesign.MaterialDesign;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;


public class guiView extends JTable implements DropTargetListener, MouseListener {

    public static JFrame gui;
    JLabel dropLabel;
    JPopupMenu menu;
    JPanel dropPanel, addFileTab, addFileDetails;
    JTable table;
    JScrollPane scrollPane;
    JMenuItem deleteItem, renameFileItem, createFolderItem;
    DefaultMutableTreeNode selectedNode;
    TreePath path;
    String foundJobTitle = "";
    String foundExamTitle = "";
    String foundSemesterTitle = "";
    boolean foundJob = false;
    boolean foundExam = false;
    boolean foundSemester = false;
    private JMenuItem addFile, showCloud, authorize, cred, howTo, disc, exit, logOff;
    private JMenuBar menuBar;
    private JMenu startMenu, helpMenu;
    private JTabbedPane tabpane;
    private JTree tree;
    private String loginName;
    private boolean[] permissions;


    public guiView(String loginName) throws SQLException, IOException, ClassNotFoundException {
        this.loginName = loginName;
        permissions = database.getPermission(loginName);
        gui = new JFrame();
        gui.setTitle("ManageMyFiles - IHK - Prüfungsunterlegen sortieren");
        gui.setBounds(100, 100, 800, 600);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gui.setLocationRelativeTo(null);
        gui.setResizable(false);
        menuBar = new JMenuBar();
        startMenu = new JMenu("Start");
        addFile = new JMenuItem("Dateien hochladen");
        showCloud = new JMenuItem("Cloud anzeigen");
        authorize = new JMenuItem("Mit der Cloud verbinden");
        logOff = new JMenuItem("Abmelden");
        exit = new JMenuItem("Exit");
        disc = new JMenuItem("Verbindung zu Google Drive entfernen");
        helpMenu = new JMenu("Hilfe");
        cred = new JMenuItem("Credits");
        howTo = new JMenuItem("Bedienung");
        JMenuItem showTrash = new JMenuItem("Papierkorb anzeigen");
        JLabel loggedInLabel = new JLabel("Angemeldet als " + loginName);
        loggedInLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        TextField searchField = new TextField();
        searchField.setPreferredSize(new Dimension(150, 20));
        JButton searchButton = new JButton("Suchen");
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        startMenu.add(addFile).setEnabled(false);
        startMenu.add(showCloud).setEnabled(false);
        startMenu.add(logOff).setEnabled(true);
        startMenu.add(authorize);
        helpMenu.add(cred);
        helpMenu.add(howTo);
        helpMenu.add(showTrash).setEnabled(false);
        menuBar.add(startMenu);
        menuBar.add(helpMenu);
        menuBar.add(searchPanel);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(loggedInLabel);
        searchPanel.setVisible(false);
        startMenu.add(exit);
        startMenu.add(disc).setEnabled(false);
        gui.setJMenuBar(menuBar);
        gui.setVisible(true);
        dropPanel = new JPanel(new BorderLayout());
        dropLabel = new JLabel("Datei bitte hier ablegen");
        dropLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dropLabel.setVerticalAlignment(SwingConstants.CENTER);
        dropLabel.setFont(new Font("Arial", Font.BOLD, 20));
        addFileDetails = new JPanel(new BorderLayout());
        tabpane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        gui.add(tabpane);

        tabpane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int tabIndex = tabpane.getSelectedIndex();
                    if (tabIndex >= 0) {
                        if (tabIndex == tabpane.getTabCount() - 1) {
                            tabpane.removeTabAt(tabIndex);
                        }
                    }
                }
            }
        });

        logOff.addActionListener(e -> functions.logOut(gui));

        showTrash.addActionListener(e -> showTrash());

        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText();
            if (!searchTerm.isEmpty()) {
                searchInTree(tree, searchTerm);
            }
        });

        tabpane.addChangeListener(e -> {
            int index = tabpane.getSelectedIndex();
            String tabName = tabpane.getTitleAt(index);
            if (!tabName.equals("Cloud Overview")) {
                searchButton.setEnabled(false);
                searchField.setEnabled(false);
            } else {
                searchButton.setEnabled(true);
                searchField.setEnabled(true);
            }
        });

        cred.addActionListener(e -> openCreditsTab());
        howTo.addActionListener(e -> openHowToUseTab());
        disc.addActionListener(e -> {
            try {
                gdrive.revokeDriveConnection();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            addFile.setEnabled(false);
            showCloud.setEnabled(false);
            disc.setEnabled(false);
            authorize.setEnabled(true);
            functions.closeAllTabbedPanes(gui.getContentPane());
        });

        authorize.addActionListener(e -> {
            try {
                if (gdrive.isTokenValid(gdrive.getCredentials(new NetHttpTransport()))) {
                    gdriveAuthorize();
                    addFile.setEnabled(true);
                    showCloud.setEnabled(true);
                    authorize.setEnabled(false);
                    disc.setEnabled(true);
                    logOff.setEnabled(true);
                    showTrash.setEnabled(permissions[0] || permissions[2]);
                    addFileTab = new JPanel(new BorderLayout());
                    addFileTab.setPreferredSize(new Dimension(500, 300));
                    scrollPane = new JScrollPane(table);
                    scrollPane.setPreferredSize(new Dimension(800, 300));
                    addFileTab.add(scrollPane, BorderLayout.CENTER);
                } else {
                    gdrive.revokeDriveConnection();
                    gdriveAuthorize();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        showCloud.addActionListener(e -> {
            showCloudOverview();
            searchPanel.setVisible(true);
        });

        addFile.addActionListener(e -> {
            FontIcon drophere = FontIcon.of(MaterialDesign.MDI_FOLDER_DOWNLOAD, 100, Color.decode("#A9A9A9"));
            JLabel iconLabel = new JLabel(drophere);
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            dropLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            Box verticalBox = Box.createVerticalBox();
            verticalBox.add(Box.createVerticalGlue());
            verticalBox.add(iconLabel);
            verticalBox.add(Box.createVerticalStrut(20));
            verticalBox.add(dropLabel);
            verticalBox.add(Box.createVerticalGlue());

            dropPanel.setDropTarget(new DropTarget(dropPanel, DnDConstants.ACTION_COPY, this));
            dropPanel.setPreferredSize(new Dimension(500, 300));
            dropPanel.setLayout(new BorderLayout());
            dropPanel.add(verticalBox, BorderLayout.CENTER);

            gui.add(dropPanel);
            tabpane.addTab("PDF-Datei hinzufügen", dropPanel);
            tabpane.setSelectedIndex(tabpane.getTabCount() - 1);
        });

        exit.addActionListener(e -> System.exit(0));
    }

    public void searchInTree(JTree tree, String searchTerm) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree.getModel().getRoot();
        Enumeration e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.toString().toLowerCase().contains(searchTerm.toLowerCase())) {
                TreeNode[] nodes = ((DefaultTreeModel) tree.getModel()).getPathToRoot(node);
                TreePath path = new TreePath(nodes);
                tree.setSelectionPath(path);
                return;
            }
        }
    }

    public void showContextMenu(int x, int y) {
        menu = new JPopupMenu();
        JMenuItem downloadFile = new JMenuItem("Datei herunterladen");
        deleteItem = new JMenuItem("Datei löschen");
        renameFileItem = new JMenuItem("Datei umbenennen");
        createFolderItem = new JMenuItem("Ordner hinzufügen");
        JMenuItem renameFolderItem = new JMenuItem("Ordner umbenennen");
        JMenuItem deleteFolderItem = new JMenuItem("Ordner löschen");

        downloadFile.setEnabled(permissions[2]);
        deleteItem.setEnabled(permissions[0]);
        renameFileItem.setEnabled(permissions[0]);
        createFolderItem.setEnabled(permissions[0]);
        renameFolderItem.setEnabled(permissions[4]);
        deleteFolderItem.setEnabled(permissions[3]);

        renameFileItem.addActionListener(actionEvent -> renameSelectedFile(selectedNode));
        downloadFile.addActionListener(actionEvent -> downloadSelectedFile());

        deleteItem.addActionListener(e -> {
            try {
                deleteSelectedFile();
            } catch (IOException | GeneralSecurityException ex) {
                throw new RuntimeException(ex);
            }
        });

        createFolderItem.addActionListener(actionEvent -> createFolder(selectedNode));

        renameFolderItem.addActionListener(actionEvent -> renameFolder(selectedNode));

        deleteFolderItem.addActionListener(e -> {
            try {
                deleteFolder(selectedNode);
            } catch (GeneralSecurityException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        menu.add(downloadFile);
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
            errorHandling.deleteOnlyOneFolder();
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

    public void renameFolder(DefaultMutableTreeNode selectedNode) {
        Drive service;
        try {
            service = gdrive.getDriveService();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        String oldFolderName = selectedNode.getUserObject().toString();
        String newFolderName = JOptionPane.showInputDialog("Wie soll der Ordner umbenannt werden?");
        if (newFolderName != null) {
            String folderId;
            try {
                folderId = gdrive.getFolderId(service, oldFolderName);
                if (folderId == null) {
                    JOptionPane.showMessageDialog(null, "Der Ordner konnte nicht gefunden werden.");
                    return;
                }
                File fileMetadata = new File();
                fileMetadata.setName(newFolderName);
                File updatedFolder = service.files().update(folderId, fileMetadata).execute();
                selectedNode.setUserObject(updatedFolder.getName());
                ((DefaultTreeModel) tree.getModel()).reload(selectedNode);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
        int result = JOptionPane.showConfirmDialog(null, "Möchten Sie diese Datei wirklich löschen?", "Warnung", JOptionPane.YES_NO_OPTION);
        if (result != JOptionPane.YES_OPTION) {
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

    public void downloadSelectedFile() {
        try {
            selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode == null) {
                return;
            }
            Object downloadObject = selectedNode.getUserObject();
            if (downloadObject instanceof File) {
                if (permissions[2]) {
                    File fileToDownload = (File) downloadObject;
                    gdrive.getDriveService();
                    gdrive.downloadFile(gdrive.drive, fileToDownload.getId());
                    errorHandling.successDownloadFile();
                } else {
                    errorHandling.noPermission();
                }
            }
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            errorHandling.errorDownloadFile(e);
        }
    }

    private void renameSelectedFile(DefaultMutableTreeNode nodeToRename) {
        try {
            if (nodeToRename != null) {
                Object userObject = nodeToRename.getUserObject();
                if (userObject instanceof File) {
                    File fileToRename = (File) userObject;
                    String currentName = fileToRename.getName();
                    String newName = JOptionPane.showInputDialog("Geben Sie den neuen Namen für die Datei ein:", currentName);
                    if (newName != null && !newName.equals(currentName)) {
                        File updatedFile = new File();
                        updatedFile.setName(newName);
                        Drive service = gdrive.getDriveService();
                        service.files().update(fileToRename.getId(), updatedFile).execute();
                        fileToRename.setName(newName);
                        nodeToRename.setUserObject(fileToRename);
                        ((DefaultTreeModel) tree.getModel()).nodeChanged(nodeToRename);
                    } else {
                        errorHandling.renameError();
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
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private void showTrash() {
        int trashTabIndex = tabpane.indexOfTab("Cloud Papierkorb");
        if (trashTabIndex != -1) {
            tabpane.setSelectedIndex(trashTabIndex);
        } else {
            DefaultListModel<File> model = new DefaultListModel<>();
            JList<File> list = new JList<>(model);
            JScrollPane scrollPane = new JScrollPane(list);
            scrollPane.setPreferredSize(new Dimension(800, 300));
            tabpane.addTab("Cloud Papierkorb", scrollPane);
            tabpane.setSelectedIndex(tabpane.getTabCount() - 1);
            try {
                Drive service = gdrive.getDriveService();
                String query = "trashed=true and mimeType!='application/vnd.google-apps.folder'";
                FileList result = service.files().list().setQ(query).setSpaces("drive").setFields("nextPageToken, files(id, name)").execute();
                List<File> files = result.getFiles();
                for (File file : files) {
                    model.addElement(file);
                }
                list.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent evt) {
                        if (!permissions[0]) {
                            errorHandling.noPermissionRevoke();
                            return;
                        }
                        if (evt.getClickCount() == 2) {
                            int index = list.locationToIndex(evt.getPoint());
                            File file = model.getElementAt(index);
                            if (JOptionPane.showConfirmDialog(null, "Möchten Sie diese Datei aus dem Papierkorb wiederherstellen?", "Datei wiederherstellen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                                try {
                                    service.files().update(file.getId(), new File().setTrashed(false)).execute();
                                    model.removeElementAt(index);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    errorHandling.trashErrorRevokeFile(e);
                                }
                            }
                        }
                    }
                });
            } catch (IOException | GeneralSecurityException e) {
                e.printStackTrace();
                errorHandling.trashError(e);
            }
        }
    }

    public void showCloudOverview() {
        int cloudOverviewTabIndex = tabpane.indexOfTab("Cloud Overview");
        if (cloudOverviewTabIndex != -1) {
            tabpane.setSelectedIndex(cloudOverviewTabIndex);
        } else {
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setPreferredSize(new Dimension(300, 15));
            JLabel progressLabel = new JLabel("Daten werden geladen. Dies kann bis zu einer Minute dauern");
            progressLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            progressLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 400, 0));

            JPanel progressBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            progressBarPanel.add(progressBar);

            Box verticalBox = Box.createVerticalBox();
            verticalBox.add(Box.createVerticalStrut(30));
            verticalBox.add(progressBarPanel);
            verticalBox.add(Box.createVerticalStrut(20));
            verticalBox.add(progressLabel);
            verticalBox.add(Box.createVerticalGlue());

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(verticalBox);

            tabpane.addTab("Cloud Overview", panel);
            tabpane.setSelectedIndex(tabpane.getTabCount() - 1);

            new Thread(() -> {
                try {
                    Drive service = gdrive.getDriveService();
                    String[] oberOrdner = infos.jobArray;
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode("Google Drive");
                    DefaultTreeModel model = new DefaultTreeModel(root);

                    for (String ober : oberOrdner) {
                        String folderId = gdrive.getFolderId(service, ober);
                        if (folderId == null) {
                            continue;
                        }
                        DefaultMutableTreeNode oberNode = new DefaultMutableTreeNode(ober);
                        root.add(oberNode);
                        addFilesToNode(oberNode, folderId, service);
                    }

                    tree = new JTree(model);
                    tree.setCellRenderer(new treeName());
                    scrollPane = new JScrollPane(tree);
                    scrollPane.setPreferredSize(new Dimension(800, 300));
                    tabpane.setComponentAt(tabpane.getSelectedIndex(), scrollPane);
                    tree.addMouseListener(this);
                } catch (IOException | GeneralSecurityException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Fehler beim Laden der Cloud-Übersicht: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
                } finally {
                    progressBar.setVisible(false);
                    progressLabel.setText("Loaded");
                }
            }).start();
        }
    }


    public void addFilesToNode(DefaultMutableTreeNode folderNode, String folderId, Drive service) throws IOException {
        FileList result = service.files().list()
                .setQ("'" + folderId + "' in parents and trashed=false")
                .setFields("nextPageToken, files(name, size, mimeType, createdTime, id, parents)")
                .execute();
        List<File> files = result.getFiles();
        for (File file : files) {
            if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
                DefaultMutableTreeNode subFolderNode = new DefaultMutableTreeNode(file.getName());
                folderNode.add(subFolderNode);
                // folderNode.setUserObject(file);
                addFilesToNode(subFolderNode, file.getId(), service);
            } else {
                DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file.getName());
                fileNode.setUserObject(file);
                folderNode.add(fileNode);
            }
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

    public void drop(DropTargetDropEvent e) {
        String allowedExtensions = ".pdf";
        e.acceptDrop(DnDConstants.ACTION_COPY);
        DataFlavor[] flavors = e.getCurrentDataFlavors();
        String fileName = null, fileSize, filePath, formattedDate;
        long lastModified;
        Date date;
        SimpleDateFormat dateFormat;
        try {
            Transferable t = e.getTransferable();
            List<java.io.File> files = (List<java.io.File>) t.getTransferData(DataFlavor.javaFileListFlavor);

            Object[][] data = new Object[files.size()][4];
            Object[] columnNames = {"Dateiname", "Größe", "Zuletzt bearbeitet", "Dateipfad"};

            int i = 0;
            for (java.io.File file : files) {
                fileName = file.getName();
                if (!fileName.toLowerCase().endsWith(allowedExtensions)) {
                    errorHandling.notSupportFileType();
                    return;
                }
                fileSize = String.valueOf(file.length() / 1024);
                filePath = file.getAbsolutePath();
                lastModified = file.lastModified();
                date = new Date(lastModified);
                dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                formattedDate = dateFormat.format(date);
                data[i][0] = fileName;
                data[i][1] = fileSize;
                data[i][2] = formattedDate;
                data[i][3] = filePath;
                i++;
            }

            CustomPanel panel = new CustomPanel(data, columnNames);
            int option = JOptionPane.showOptionDialog(null, panel, "Möchtest du diese Dateien hochladen?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Ja", "Nein"}, "Ja");
            if (option == JOptionPane.YES_OPTION) {
                for (java.io.File file : files) {
                    try {
                        PDDocument document = PDDocument.load(file);
                        for (int j = 0; j < document.getNumberOfPages(); j++) {
                            PDFTextStripper pdfStripper = new PDFTextStripper();
                            String result = pdfStripper.getText(document);
                            String[] jobs = infos.jobArray;
                            for (String jobInfo : jobs) {
                                if (result.toLowerCase().contains(functions.normalizeString(jobInfo))) {
                                    foundJobTitle = jobInfo;
                                    foundJob = true;
                                    break;
                                }
                            }
                            if (!foundJob) {
                                errorHandling.jobNotFound(file);
                                return;
                            }
                            String[] exams = infos.examArray;
                            for (String examInfo : exams) {
                                if (result.toLowerCase().contains(functions.normalizeString(examInfo))) {
                                    foundExamTitle = examInfo;
                                    foundExam = true;
                                    break;
                                }
                            }
                            if (foundExam) {
                                switch (foundExamTitle) {
                                    case "WISO":
                                    case "Wirtschaftskunde":
                                        foundExamTitle = "WISO";
                                        break;
                                    case "AP1":
                                    case "AP 1":
                                    case "Abschlussprüfung Teil 1":
                                    case "Abschlussprüfung Teil1":
                                        foundExamTitle = "AP1";
                                        break;
                                    case "AP2":
                                    case "AP 2":
                                    case "Abschlusspüfung Teil 2":
                                    case "Abschlussprüfung Teil2":
                                        foundExamTitle = "AP2";
                                        break;
                                }
                            } else {
                                errorHandling.examNotFound(file);
                                return;
                            }

                            String[] semesters = infos.semArray;
                            for (String semesterInfo : semesters) {
                                if (result.toLowerCase().contains(functions.normalizeString(semesterInfo))) {
                                    foundSemesterTitle = semesterInfo;
                                    foundSemester = true;
                                    break;
                                }
                            }
                            if (foundSemester) {
                                if (foundSemesterTitle.equals("Sommer23") || foundSemesterTitle.equals("Sommer 23")) {
                                    foundSemesterTitle = "Sommer 23";
                                } else if (foundSemesterTitle.equals("Winter2324") || foundSemesterTitle.equals("Winter 23/24") || foundSemesterTitle.equals("Winter23/24") || foundSemesterTitle.equals("Winter 2324") || foundSemesterTitle.equals("Winter 23-24") || foundSemesterTitle.equals("Winter 23 24")) {
                                    foundSemesterTitle = "Winter 23/24";
                                }
                            } else {
                                errorHandling.semesterNotFound(file);
                                return;
                            }
                            document.close();
                            if (foundJob && foundExam && foundSemester) {
                                gdrive.uploadToDrive(foundJobTitle, foundExamTitle, foundSemesterTitle, fileName, file);
                                break;
                            } else {
                                errorHandling.lessInfos(file);
                                return;
                            }
                        }
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                errorHandling.filesSuccessfullyUploaded();
            }
            if (option == JOptionPane.NO_OPTION) {
                errorHandling.filesNotUploaded();
            }
        } catch (IOException | UnsupportedFlavorException ex) {
            throw new RuntimeException(ex);
        }
        e.dropComplete(true);
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

   /* public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        guiView gui = new guiView(null);
        gui.setVisible(true);
    }*/
}
