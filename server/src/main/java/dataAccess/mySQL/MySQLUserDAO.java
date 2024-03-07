package dataAccess.mySQL;

import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.UserDAO;
import exception.ResponseException;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class MySQLUserDAO implements UserDAO {
    private final Connection conn;

    public MySQLUserDAO() throws ResponseException {
        DataAccess.configureDatabase();
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }


    @Override
    public boolean isUser(UserData userData) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("SELECT NAME FROM USERS WHERE NAME=?")) {
            preparedStatement.setString(1, userData.username());
            try (var rs = preparedStatement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("SELECT PASSWORD, EMAIL from USERS where NAME=?")) {
            preparedStatement.setString(1, username);
            try (var rs = preparedStatement.executeQuery()) {
                String password = "";
                String email = "";
                while (rs.next()) {
                    password = rs.getString("PASSWORD");
                    email = rs.getString("EMAIL");
                }
                if (Objects.equals(password, "") && Objects.equals(email, "")) {
                    throw new DataAccessException("invalid name");
                }
                return new UserData(username, password, email);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
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
        try (var preparedStatement = conn.prepareStatement("DELETE FROM USERS WHERE NAME!=''")) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
