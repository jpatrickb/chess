package dataAccess;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class TestJDBC {

    public static void main(String[] args) throws Exception {
        (new TestJDBC()).example();
        System.out.println("Hello world!");
    }

    public void example() throws Exception {
        try (var conn = getConnection()) {
            try (var preparedStatement = conn.prepareStatement("SELECT 1+1")) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                System.out.println(rs.getInt(1));
            }
        }
    }

    Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306", "root", "Ced@rR1dg3");
    }

}
