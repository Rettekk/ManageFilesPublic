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
