import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class database {

    static Connection connection;
    static String db_url = "jdbc:mysql://localhost:3306/managefiles";
    static String username = "root";
    static String passwort = "";
    static String driver = "com.mysql.jdbc.Driver";
    public static String SQL_SELECT = "SELECT username, password FROM users WHERE username = ? AND password = ?";

    static void openDataBaseConnection() throws SQLException, ClassNotFoundException {
        Class.forName(driver);
        System.out.println("Opening database connection...");
        connection = DriverManager.getConnection(db_url, username, passwort);
        System.out.println("Connection valid: " + connection.isValid(0));
    }

    static void closeDataBaseConnection() throws SQLException {
        connection.close();
        System.out.println("Connection valid: " + connection.isValid(0));
    }
}

/*
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
 */