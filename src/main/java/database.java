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
    static String driver = "com.mysql.jdbc.Driver";
    public static String SQL_SELECT = "SELECT username, password FROM users WHERE username = ? AND password = ?";

    static void openDataBaseConnection() throws SQLException, ClassNotFoundException, IOException {
        Properties prop = new Properties();
        InputStream input = new FileInputStream("config.properties");
        prop.load(input);
        db_url = prop.getProperty("db_url");
        username = prop.getProperty("db_username");
        passwort = prop.getProperty("db_password");

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
