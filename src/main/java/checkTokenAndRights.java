import java.io.IOException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class checkTokenAndRights {

    boolean valid;
    boolean dlfile;
    String rights;


    public checkTokenAndRights(boolean valid, boolean dlfile, String rights) {
        this.valid = valid;
        this.dlfile = dlfile;
        this.rights = rights;
    }


    static checkTokenAndRights checkToken(String token) {
        try {
            database.openDataBaseConnection();
            PreparedStatement stmt = database.connection.prepareStatement(database.SQL_Register);
            BigDecimal tokenNumeric = new BigDecimal(token);
            stmt.setString(1, String.valueOf(tokenNumeric));
            ResultSet rs = stmt.executeQuery();
            boolean result = rs.next();
            boolean dlfile = false;
            String rights = null;

            if (result) {
                dlfile = rs.getBoolean("dlfile");
                rights = rs.getString("rights");
            }

            rs.close();
            stmt.close();
            database.closeDataBaseConnection();
            return new checkTokenAndRights(result, dlfile, rights);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
