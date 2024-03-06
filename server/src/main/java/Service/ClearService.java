package Service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import exception.ResponseException;

/**
 * Handles requests to clear the game
 */
public class ClearService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    /**
     * Accepts objects providing data access
     * @param userDAO UserDAO object providing access to the user database
     * @param authDAO AuthDAO object providing access to the authorization database
     * @param gameDAO GameDAO object providing access to the game database
     */
    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    /**
     * Clears the databases by calling each DAO's clear function
     */
    public void clearDatabase() throws ResponseException {
        try {
            userDAO.clear();
        } catch (DataAccessException e) {
            throw new ResponseException(500, e.getMessage());
        }
        authDAO.clear();
        gameDAO.clear();
    }
}
