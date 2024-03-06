package dataAccess;

import dataAccess.memory.MemoryAuthDAO;
import dataAccess.memory.MemoryGameDAO;
import dataAccess.memory.MemoryUserDAO;
import dataAccess.mySQL.MySQLAuthDAO;
import dataAccess.mySQL.MySQLGameDAO;
import dataAccess.mySQL.MySQLUserDAO;
import exception.ResponseException;

import java.sql.SQLException;

/**
 * DataAccess class to provide access to the correct data access objects
 */
public class DataAccess {
    private AuthDAO authDAO;
    private UserDAO userDAO;
    private GameDAO gameDAO;

    /**
     * Upon initialization, determines whether to use SQL server or memory
     * @param dataLocation DataLocation object indicating where to look for data
     */
    public DataAccess(DataLocation dataLocation) throws ResponseException {
        if (dataLocation == DataLocation.SQL) {
            configureDatabase();
            try {
                this.authDAO = new MySQLAuthDAO();
                this.userDAO = new MySQLUserDAO();
                this.gameDAO = new MySQLGameDAO();
            } catch (SQLException ex) {
                throw new ResponseException(500, String.format("Unable to connect to database: %s%n", ex.getMessage()));
            }
        } else if (dataLocation == DataLocation.MEMORY) {
            this.authDAO = new MemoryAuthDAO();
            this.userDAO = new MemoryUserDAO();
            this.gameDAO = new MemoryGameDAO();
        }
    }

    /**
     * Getter function for the AuthDAO object
     * @return AuthDAO object providing access to authorization data
     */
    public AuthDAO getAuthDAO() {
        return authDAO;
    }

    /**
     * Getter function for the UserDAO object
     * @return AuthDAO object providing access to authorization data
     */
    public UserDAO getUserDAO() {
        return userDAO;
    }

    /**
     * Getter function for the GameDAO object
     * @return AuthDAO object providing access to authorization data
     */
    public GameDAO getGameDAO() {
        return gameDAO;
    }

    /**
     * Objects to indicate the data location to use for the server
     */
    public enum DataLocation {
        SQL,
        MEMORY
    }

    private final String[] createStatements = {
        """
        CREATE TABLE IF NOT EXISTS USERS (
            `ID` int NOT NULL AUTO_INCREMENT,
            `NAME` varchar(255) NOT NULL,
            `PASSWORD` varchar(255) NOT NULL,
            `EMAIL` varchar(255) NOT NULL,
            PRIMARY KEY (`ID`),
            INDEX(ID)
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS AUTH (
            `NAME` varchar(255) NOT NULL,
            `TOKEN` varchar(255) NOT NULL
        )
        """,
        """
        CREATE TABLE IF NOT EXISTS GAME (
            `ID` int NOT NULL AUTO_INCREMENT,
            `WHITENAME` varchar(255) NOT NULL,
            `BLACKNAME` varchar(255) NOT NULL,
            `GAMENAME` varchar(255) NOT NULL,
            `JSON` TEXT NOT NULL,
            PRIMARY KEY (`ID`),
            INDEX(ID)
        )
        """
    };

    private void configureDatabase() throws ResponseException {
        try {
            DatabaseManager.createDatabase();
            try (var conn = DatabaseManager.getConnection()) {
                for (var statement : createStatements) {
                    try (var preparedStatement = conn.prepareStatement(statement)) {
                        preparedStatement.executeUpdate();
                    }
                }
            } catch (SQLException ex) {
                throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
            }
        } catch (DataAccessException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
