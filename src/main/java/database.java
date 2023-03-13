import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class database {

    public static String SQL_SELECT = "SELECT username, password FROM users WHERE username = ? AND password = ?";
    public static String SQL_RIGHTS = "SELECT rights, dlFile FROM users WHERE username = ?";
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

        // Class.forName(postgresDriver);
        Class.forName(sqlDriver);
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
}
