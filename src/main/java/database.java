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

/*	public void actionPerformed(ActionEvent e) {
				int id = 1;
				String passwordValue = String.valueOf(passwordFieldPW.getPassword());
				String passwordValueRepeat = String.valueOf(passwordFieldPwRepeat.getPassword());
				String userName = textFieldUsername.getText();
				if(!(passwordValue.equals(passwordValueRepeat)) ) {
					repeatLabelInfo.setText("The passwords must be equal!");
				} else {
					repeatLabelInfo.setText(null);
					try {
						database.openDataBaseConnection();
						Statement st = database.connection.createStatement();
			            PreparedStatement preparedStatement = database.connection.prepareStatement(SQL_INSERT);
			            preparedStatement.setString(1, userName);
			            preparedStatement.setString(2, passwordValueRepeat);
			            preparedStatement.executeUpdate();
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			*/