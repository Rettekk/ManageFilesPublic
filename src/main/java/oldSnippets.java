import com.google.api.client.googleapis.media.MediaHttpUploader;

import java.io.IOException;

public class oldSnippets {

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


    public class UploadFile {
        private static final int UPLOAD_CHUNK_SIZE = MediaHttpUploader.MINIMUM_CHUNK_SIZE * 2;

        public static void upload(Drive drive, java.io.File file, String parentId) throws IOException {
            File fileMetadata = new File();
            fileMetadata.setName(file.getName());
            fileMetadata.setParents(Collections.singletonList(parentId));

            FileContent mediaContent = new FileContent("application/octet-stream", file);

            Drive.Files.Create create = drive.files().create(fileMetadata, mediaContent)
                    .setFields("id, name, size")
                    .setChunkSize(UPLOAD_CHUNK_SIZE)
                    .setMediaHttpUploader(new MediaHttpUploader(mediaContent, null)
                            .setProgressListener(new MediaHttpUploaderProgressListener() {
                                @Override
                                public void progressChanged(MediaHttpUploader uploader) throws IOException {
                                    switch (uploader.getUploadState()) {
                                        case INITIATION_STARTED:
                                            System.out.println("Upload started.");
                                            break;
                                        case INITIATION_COMPLETE:
                                            System.out.printf("Upload complete. File size: %d bytes\n", file.length());
                                            break;
                                        case MEDIA_IN_PROGRESS:
                                            System.out.printf("Upload in progress. Bytes uploaded: %d\r", uploader.getNumBytesUploaded());
                                            break;
                                        case MEDIA_COMPLETE:
                                            System.out.println("\nUpload completed successfully.");
                                            break;
                                        case NOT_STARTED:
                                            System.out.println("Upload has not started yet.");
                                            break;
                                    }
                                }
                            }));

            File uploadedFile = create.execute();
            System.out.printf("File uploaded: %s (%d bytes)\n", uploadedFile.getName(), uploadedFile.getSize());
        }
    }*/

}
