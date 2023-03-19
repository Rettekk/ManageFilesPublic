import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.Properties;

public class database {

    public static String SQL_SELECT_User = "SELECT username, password, salt FROM public.user WHERE username = ?";
    public static String SQL_RIGHTS = "SELECT rights, dlfile FROM public.user WHERE username = ?";
    static String SQL_Register = "SELECT * FROM applicationkey WHERE keyid=?";
    static String SQL_InserUser = "INSERT INTO public.user (username, password, rights, dlfile, salt) VALUES (?, ?, ?, ?, ?)";
    static String SQL_DeleteToken = "DELETE FROM applicationkey WHERE keyid=?";
    static Connection connection;
    static String db_url, username, password;
    static String postgresDriver = "org.postgresql.Driver";
    static String cfgPath = "src/main/resources/config.properties";

    static void openDataBaseConnection() throws IOException, ClassNotFoundException, SQLException {
        Properties prop = new Properties();
        InputStream input = new FileInputStream(cfgPath);
        prop.load(input);
        db_url = prop.getProperty("db_url");
        username = prop.getProperty("db_username");
        password = prop.getProperty("db_password");

        Class.forName(postgresDriver);
        connection = DriverManager.getConnection(db_url, username, password);
    }

    static void closeDataBaseConnection() throws SQLException {
        connection.close();
    }

    public static boolean login(String username, String password) throws SQLException, IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
        openDataBaseConnection();
        PreparedStatement pStatement = connection.prepareStatement(SQL_SELECT_User);
        pStatement.setString(1, username);
        ResultSet rs = pStatement.executeQuery();
        boolean success = false;
        if (rs.next()) {
            byte[] salt = rs.getBytes("salt");
            String storedPasswordHexString = rs.getString("password");
            byte[] storedPasswordHash = hexToBytes(storedPasswordHexString);
            if (salt != null && storedPasswordHash != null) {
                success = hashPw.checkPassword(password, storedPasswordHash, salt);
            }
        }

        closeDataBaseConnection();
        return success;
    }

    static boolean[] getPermission(String username) throws SQLException, IOException, ClassNotFoundException {
        boolean[] permissions = new boolean[5];
        openDataBaseConnection();
        PreparedStatement pStatement = database.connection.prepareStatement(SQL_RIGHTS);
        pStatement.setString(1, username);
        ResultSet rs = pStatement.executeQuery();
        if (rs.next()) {
            String rights = rs.getString("rights");
            boolean dlFile = rs.getBoolean("dlFile");
            permissions[0] = rights.equals("admin");
            permissions[1] = true;
            permissions[2] = rights.equals("admin") || (rights.equals("user") && dlFile);
            permissions[3] = rights.equals("admin");
            permissions[4] = rights.equals("admin");
        }
        database.closeDataBaseConnection();
        return permissions;
    }

    public static int insertUser(String username, byte[] passwordHash, String rights, boolean dlfile, byte[] salt) throws SQLException, IOException, ClassNotFoundException {
        openDataBaseConnection();
        PreparedStatement stmt = database.connection.prepareStatement(SQL_InserUser);
        stmt.setString(1, username);
        String passwordHashHexString = bytesToHex(passwordHash);
        stmt.setString(2, passwordHashHexString);
        stmt.setString(3, rights);
        stmt.setBoolean(4, dlfile);
        stmt.setBytes(5, salt);
        int rowAffected = stmt.executeUpdate();
        stmt.close();
        closeDataBaseConnection();
        return rowAffected;
    }

    static void deleteToken(int token) {
        try {
            database.openDataBaseConnection();
            PreparedStatement stmt = database.connection.prepareStatement(SQL_DeleteToken);
            stmt.setInt(1, token);
            stmt.executeUpdate();
            stmt.close();
            database.closeDataBaseConnection();
        } catch (SQLException | ClassNotFoundException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
}
