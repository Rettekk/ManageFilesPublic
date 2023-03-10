import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import com.google.api.client.googleapis.services.CommonGoogleClientRequestInitializer;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.apache.pdfbox.pdmodel.PDDocument;

/* class to demonstarte use of Drive files list API */
public class gdrive {

    private static final String APPLICATION_NAME = "Google Drive File Manager";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/creds.json";
    static Drive drive;
    private static Credential credentials;
    static String apiKey = "AIzaSyCKhoaf7Fmkt93fjFcTcc8dI8Ht2J4W2ig";

    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = gdrive.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        //returns an authorized Credential object.
        return credential;
    }

    public static Drive getDriveService() throws IOException, GeneralSecurityException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
         drive = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .setGoogleClientRequestInitializer(new CommonGoogleClientRequestInitializer(apiKey))
                .build();
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return drive;
    }

    public static void revokeDriveConnection() throws IOException {
        java.io.File tokenDirectory = new java.io.File(TOKENS_DIRECTORY_PATH);
        if (tokenDirectory.exists()) {
            java.io.File[] files = tokenDirectory.listFiles();
            if (files != null) {
                for (java.io.File file : files) {
                    file.delete();
                }
            }
        }
    }

    public static String getFolderId(Drive service, String folderName) throws IOException {
        String folderId = null;
        String pageToken = null;
        do {
            // Abrufen des Ordners
            FileList folders = service.files().list()
                    .setQ("mimeType='application/vnd.google-apps.folder' and trashed=false and name='" + folderName + "'")
                    .setFields("nextPageToken, files(id)")
                    .setPageToken(pageToken)
                    .execute();
            for (File folder : folders.getFiles()) {
                folderId = folder.getId();
            }
            pageToken = folders.getNextPageToken();
        } while (pageToken != null);
        return folderId;
    }

    static String getOrCreateFolderId(Drive drive, String folderName, String parentFolderId) throws IOException {
        String query = "mimeType='application/vnd.google-apps.folder' and trashed=false and name='" + folderName + "'";
        if (parentFolderId != null) {
            query += " and '" + parentFolderId + "' in parents";
        }

        FileList files = drive.files().list().setQ(query).setFields("nextPageToken, files(id, name)").execute();

        if (!files.getFiles().isEmpty()) {
            return files.getFiles().get(0).getId();
        }

        File folderMetadata = new File();
        folderMetadata.setName(folderName);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");
        if (parentFolderId != null) {
            folderMetadata.setParents(Collections.singletonList(parentFolderId));
        }

        File folder = drive.files().create(folderMetadata).setFields("id").execute();

        return folder.getId();
    }

    public static void downloadFile(Drive driveService, String fileId) throws IOException {
        File file;
        try {
            file = driveService.files().get(fileId).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String downloadFolderPath = System.getProperty("user.home") + "/Downloads/";
        String filename = file.getName();
        java.io.File downloadFile = new java.io.File(downloadFolderPath + filename);
        FileOutputStream outputStream = new FileOutputStream(downloadFile);
        driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
        outputStream.close();
        PDDocument document = PDDocument.load(downloadFile);
        if (!document.isEncrypted()) {
            document.save(downloadFolderPath + filename + ".pdf");
        }
        document.close();
        downloadFile.delete();
    }
}