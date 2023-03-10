import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

public class guiView extends JTable implements DropTargetListener, MouseListener {

    public JFrame gui;
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
    private creditsView creditsView = new creditsView();
    private howToView howToView = new howToView();


    public guiView() {
        gui = new JFrame();
        gui.setTitle("ManageMyFiles - IHK - Prüfungsunterlegen sortieren");
        gui.setName("ManageMyFiles");
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

        TextField searchField = new TextField();
        searchField.setPreferredSize(new Dimension(150, 20));
        JButton searchButton = new JButton("Suchen");
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        startMenu.add(addFile).setEnabled(false);
        startMenu.add(showCloud).setEnabled(false);
        startMenu.add(logOff).setEnabled(false);
        startMenu.add(authorize);
        helpMenu.add(cred);
        helpMenu.add(howTo);
        menuBar.add(startMenu);
        menuBar.add(helpMenu);
        menuBar.add(searchPanel);
        searchPanel.setVisible(false);
        startMenu.add(exit);
        startMenu.add(disc).setEnabled(false);
        gui.setJMenuBar(menuBar);
        gui.setVisible(true);
        dropPanel = new JPanel(new BorderLayout());
        dropLabel = new JLabel("Datei bitte hier ablegen");
        dropLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dropLabel.setFont(new Font("Arial", Font.BOLD, 20));
        addFileDetails = new JPanel(new BorderLayout());
        tabpane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        gui.add(tabpane);


        logOff.addActionListener(e -> {
            functions.logOut(gui);
        });

        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText();
            if (!searchTerm.isEmpty()) {
                searchInTree(tree, searchTerm);
            }
        });

        tabpane.addChangeListener(e -> {
            int index = tabpane.getSelectedIndex();
            if (index != 1) {
                searchButton.setEnabled(false);
                searchField.setEnabled(false);
            } else {
                searchButton.setEnabled(true);
                searchField.setEnabled(true);
            }
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
            functions.closeAllTabbedPanes(gui.getContentPane());
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

        showCloud.addActionListener(e -> {
            listTreeFiles();
            searchPanel.setVisible(true);
        });
        addFile.addActionListener(e -> {
            dropPanel.setDropTarget(new DropTarget(dropPanel, DnDConstants.ACTION_COPY, this));
            dropPanel.setPreferredSize(new Dimension(500, 300));
            dropPanel.add(dropLabel, BorderLayout.NORTH);
            gui.add(dropPanel);
            tabpane.addTab("Datei hinzufügen", dropPanel);
            tabpane.setSelectedIndex(tabpane.getTabCount() - 1);
        });

        exit.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        guiView gui = new guiView();
        gui.setVisible(true);
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
        renameFileItem.addActionListener(actionEvent -> renameSelectedFile(selectedNode));

        downloadFile.addActionListener(actionEvent -> {downloadSelectedFile();});

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
            renameFolder(selectedNode);
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

    public void renameFolder(DefaultMutableTreeNode selectedNode) {
        Drive service;
        try {
            service = gdrive.getDriveService();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        File selectedFile = (File) selectedNode.getUserObject();
        String newFolderName = JOptionPane.showInputDialog("Wie soll der Ordner umbenannt werden?");
        if (newFolderName != null) {
            File fileMetadata = new File();
            fileMetadata.setName(newFolderName);
            File updatedFile;
            try {
                updatedFile = service.files().update(selectedFile.getId(), fileMetadata).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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

    public void downloadSelectedFile() {
        try {
            selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode == null) {
                return;
            }
            Object downloadObject = selectedNode.getUserObject();
            if (downloadObject instanceof File) {
                File fileToDownload = (File) downloadObject;
                gdrive.getDriveService();
                gdrive.downloadFile(gdrive.drive, fileToDownload.getId());
                errorHandling.successDownloadFile();
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
            JScrollPane scrollPane = new JScrollPane(tree);
            scrollPane.setPreferredSize(new Dimension(800, 300));
            tabpane.addTab("Cloud Overview", scrollPane);
            tabpane.setSelectedIndex(tabpane.getTabCount() - 1);
            tree.addMouseListener(this);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
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
        try {
            Transferable t = e.getTransferable();
            List<java.io.File> files = (List<java.io.File>) t.getTransferData(DataFlavor.javaFileListFlavor);
            for (java.io.File file : files) {

                long totalBytes = file.length();
                long bytesUploaded = 0;
                String progressMessage = "Hochladen... ";
                JProgressBar progressBar = new JProgressBar(0, 100);
                progressBar.setString(progressMessage);
                progressBar.setStringPainted(true);
                JPanel progressPanel = new JPanel(new BorderLayout());
                progressPanel.add(progressBar, BorderLayout.NORTH);
                JDialog progressDialog = new JDialog(gui, "Hochladen", true);
                progressDialog.getContentPane().add(progressPanel);
                progressDialog.pack();
                progressDialog.setLocationRelativeTo(gui);
                progressDialog.setVisible(true);


                String fileName = file.getName();
                String fileSize = String.valueOf(file.length() / 1024);
                Drive drive = gdrive.getDriveService();
                if (fileName.toLowerCase().endsWith(allowedExtensions)) {
                    long lastModified = file.lastModified();
                    Date date = new Date(lastModified);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                    String formattedDate = dateFormat.format(date);
                    JDialog confirmDialog = new JDialog(gui, "Datei hochladen?", true);
                    String message = "<html>Möchten Sie die Datei hochladen?<br><br>"
                            + "Weitere Informationen zur Datei:<br><br>"
                            + "Dateiname: " + fileName + "<br>"
                            + "Dateigröße: " + fileSize + " MB<br>"
                            + "Zuletzt bearbeitet am: " + formattedDate + "<br>"
                            + "Dateipfad: " + file.getAbsolutePath() + "<br>"
                            + "<br><br>"
                            + "Die Datei wird umbenannt und in den jeweiligen Ordner gelegt.</html>";

                    JLabel messageLabel = new JLabel(message);
                    JButton yesButton = new JButton("Ja");
                    JButton noButton = new JButton("Nein");
                    yesButton.addActionListener(ev -> {
                        try {
                            PDDocument document = PDDocument.load(file);
                            for (int i = 0; i < document.getNumberOfPages(); i++) {
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
                                    confirmDialog.dispose();
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
                                    if (foundExamTitle.equals("WISO") || foundExamTitle.equals("Wirtschaftskunde")) {
                                        foundExamTitle = "WISO";
                                    } else if (foundExamTitle.equals("AP1") || foundExamTitle.equals("AP 1") || foundExamTitle.equals("Abschlussprüfung Teil 1") || foundExamTitle.equals("Abschlussprüfung Teil1")) {
                                        foundExamTitle = "AP1";
                                    } else if (foundExamTitle.equals("AP2") || foundExamTitle.equals("AP 2") || foundExamTitle.equals("Abschlusspüfung Teil 2") || foundExamTitle.equals("Abschlussprüfung Teil2")) {
                                        foundExamTitle = "AP2";
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
                                    } else if (foundSemesterTitle.equals("Winter2324") || foundSemesterTitle.equals("Winter 23/24") || foundSemesterTitle.equals("Winter23/24") || foundSemesterTitle.equals("Winter 2324")
                                            || foundSemesterTitle.equals("Winter 23-24") || foundSemesterTitle.equals("Winter 23 24") || foundSemesterTitle.equals("Winter 23-24")) {
                                        foundSemesterTitle = "Winter 23/24";
                                    }
                                } else {
                                    errorHandling.semesterNotFound(file);
                                    return;
                                }
                                document.close();
                                if (foundJob && foundExam && foundSemester) {
                                    String jobFolderId = gdrive.getOrCreateFolderId(drive, foundJobTitle, "root");
                                    String examFolderId = gdrive.getOrCreateFolderId(drive, foundExamTitle, jobFolderId);
                                    String semesterFolderId = gdrive.getOrCreateFolderId(drive, foundSemesterTitle, examFolderId);
                                    //rename file
                                    String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
                                    String newFileName = foundJobTitle + "_" + foundExamTitle + "_" + foundSemesterTitle + "_" + fileName.substring(0, fileName.lastIndexOf(".")) + "_" + timestamp;
                                    //Upload
                                    File fileMetadata = new File();
                                    fileMetadata.setName(newFileName);
                                    fileMetadata.setParents(Collections.singletonList(semesterFolderId));
                                    File newFile = drive.files().create(fileMetadata).execute();
                                    String fileId = newFile.getId();
                                    java.io.File fileContent = new java.io.File(file.getAbsolutePath());
                                    ByteArrayContent content = new ByteArrayContent("application/octet-stream", Files.readAllBytes(fileContent.toPath()));
                                    Drive.Files.Update update = drive.files().update(fileId, null, content);
                                    update.execute();
                                    confirmDialog.dispose();
                                } else {
                                    errorHandling.lessInfos(file);
                                    return;
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });

                    noButton.addActionListener(ev -> {
                        confirmDialog.dispose();
                    });

                    //UploadPanel
                    JPanel buttonPanel = new JPanel();
                    buttonPanel.add(yesButton);
                    buttonPanel.add(noButton);
                    confirmDialog.getContentPane().setLayout(new BorderLayout());
                    confirmDialog.getContentPane().add(messageLabel, BorderLayout.CENTER);
                    confirmDialog.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
                    confirmDialog.pack();
                    confirmDialog.setLocationRelativeTo(gui);
                    confirmDialog.setVisible(true);

                } else {
                    errorHandling.notSupportFileType();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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

}
