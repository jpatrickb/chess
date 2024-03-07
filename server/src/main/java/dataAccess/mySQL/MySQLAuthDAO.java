package dataAccess.mySQL;

import dataAccess.AuthDAO;
import dataAccess.DataAccess;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class MySQLAuthDAO implements AuthDAO {
    private final Connection conn;

    public MySQLAuthDAO() throws ResponseException {
        DataAccess.configureDatabase();
        try {
            conn = DatabaseManager.getConnection();
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
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

    @Override
    public AuthData createAuth(UserData userData) throws DataAccessException {
        var authToken = UUID.randomUUID().toString();
        try (var preparedStatement = conn.prepareStatement("INSERT INTO AUTH (NAME, TOKEN) VALUE (?, ?)")) {
            preparedStatement.setString(1, userData.username());
            preparedStatement.setString(2, authToken);

            preparedStatement.executeUpdate();

            return new AuthData(userData.username(), authToken);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public boolean authExists(String authToken) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("SELECT TOKEN FROM AUTH WHERE TOKEN=?")) {
            preparedStatement.setString(1, authToken);
            try (var rs = preparedStatement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("SELECT NAME, TOKEN from AUTH where TOKEN=?")) {
            preparedStatement.setString(1, authToken);
            try (var rs = preparedStatement.executeQuery()) {
                String username = "";
                while (rs.next()) {
                    username = rs.getString("NAME");
                }
                if (Objects.equals(username, "")) {
                    throw new DataAccessException("invalid authorization token");
                }
                return new AuthData(username, authToken);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public boolean deleteAuth(String authToken) throws DataAccessException {
        try (var preparedStatement = conn.prepareStatement("DELETE FROM AUTH WHERE TOKEN=?")) {
            preparedStatement.setString(1, authToken);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
