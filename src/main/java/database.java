import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class database {

    static Connection connection;
    static String db_url;
    static String username;
    static String passwort;
    static String sqlDriver = "com.mysql.jdbc.Driver";
    static String postgresDriver = "org.postgresql.Driver";
    public static String SQL_SELECT = "SELECT username, password FROM users WHERE username = ? AND password = ?";
    public static String SQL_DLFIELD = "SELECT dlField FROM users WHERE username = ?";

    static void openDataBaseConnection() throws IOException, ClassNotFoundException, SQLException {
        Properties prop = new Properties();
        InputStream input = new FileInputStream("config.properties");
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
}
