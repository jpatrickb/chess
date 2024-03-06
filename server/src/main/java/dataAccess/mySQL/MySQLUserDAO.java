package dataAccess.mySQL;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import exception.ResponseException;
import model.UserData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO {
    private final Connection conn;

    public MySQLUserDAO() throws SQLException {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chess");
    }


    @Override
    public boolean isUser(UserData userData) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("SELECT NAME FROM USERS")) {
            try (var rs = preparedStatement.executeQuery()) {
                return rs.next();
            } catch (SQLException ex) {
                throw new DataAccessException(ex.getMessage());
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("INSERT INTO USERS (NAME, PASSWORD, EMAIL) VALUE(?, ?, ?)")) {
            preparedStatement.setString(1, userData.username());
            preparedStatement.setString(2, userData.password());
            preparedStatement.setString(3, userData.email());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("DELETE * FROM USERS")) {

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
