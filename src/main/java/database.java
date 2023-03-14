import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class database {

    public static String SQL_SELECT = "SELECT username, password FROM public.user WHERE username = ? AND password = ?";
    public static String SQL_RIGHTS = "SELECT rights, dlfile FROM public.user WHERE username = ?";
    static String SQL_Register = "SELECT * FROM applicationkey WHERE keyid=?";
    static String SQL_InserUser = "INSERT INTO user (username, password, rights, dlfile) VALUES (?, ?, ?, ?)";
    static Connection connection;
    static String db_url;
    static String username;
    static String passwort;
    static String sqlDriver = "com.mysql.jdbc.Driver";
    static String postgresDriver = "org.postgresql.Driver";
    static String cfgPath = database.class.getClassLoader().getResource("config.properties").getPath();

    static void openDataBaseConnection() throws IOException, ClassNotFoundException, SQLException {
        Properties prop = new Properties();
        InputStream input = new FileInputStream(cfgPath);
        prop.load(input);
        db_url = prop.getProperty("db_url");
        username = prop.getProperty("db_username");
        passwort = prop.getProperty("db_password");

        Class.forName(postgresDriver);
        System.out.println("Opening database connection...");
        connection = DriverManager.getConnection(db_url, username, passwort);
        System.out.println("Connection valid: " + connection.isValid(0));
    }

    static void closeDataBaseConnection() throws SQLException {
        connection.close();
        System.out.println("Connection valid: " + connection.isValid(0));
    }

    public static boolean login(String username, String password) throws SQLException, IOException, ClassNotFoundException {
        openDataBaseConnection();
        PreparedStatement pStatement = connection.prepareStatement(SQL_SELECT);
        pStatement.setString(1, username);
        pStatement.setString(2, password);
        ResultSet rs = pStatement.executeQuery();
        boolean success = rs.next() && rs.getString("password").equals(password);
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
    static boolean checkToken(String token, String rights, String dlfiles) {
        try {
            openDataBaseConnection();
            PreparedStatement stmt = database.connection.prepareStatement(SQL_Register);
            stmt.setString(1, token);
            stmt.setString(2, rights);
            stmt.setString(3, dlfiles);
            ResultSet rs = stmt.executeQuery();
            boolean result = rs.next();
            rs.close();
            stmt.close();
            closeDataBaseConnection();
            return result;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void insertUser(String username, String password, String rights, boolean dlfile) throws SQLException {
        try {
            openDataBaseConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        PreparedStatement stmt = database.connection.prepareStatement(SQL_InserUser);
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.setString(3, rights);
        stmt.setBoolean(4, dlfile);
        stmt.executeUpdate();
        stmt.close();
        closeDataBaseConnection();
    }


}
