package dataAccessTests;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.mySQL.MySQLAuthDAO;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AuthDAOTests {
    private static final AuthDAO authDAO;

    static {
        try {
            authDAO = new MySQLAuthDAO();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCreateValidAuth() {
        UserData userData = new UserData("name", "pass", "email");
        Assertions.assertDoesNotThrow(() -> authDAO.createAuth(userData));
    }

    @Test
    void testSuccessfullyCreated() {
        UserData userData = new UserData("name", "pass", "email");
        try {
            var authData = authDAO.createAuth(userData);
            Assertions.assertTrue(authDAO.authExists(authData.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testAuthExists() {
        UserData userData = new UserData("name", "pass", "email");
        try {
            var authData = authDAO.createAuth(userData);
            Assertions.assertTrue(authDAO.authExists(authData.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testAuthNotExists() {
        try {
            Assertions.assertFalse(authDAO.authExists("randomToken"));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testGetAuthValid() {
        UserData userData = new UserData("name", "pass", "email");
        try {
            var authData = authDAO.createAuth(userData);

            Assertions.assertEquals(authData, authDAO.getAuth(authData.authToken()));

        } catch (DataAccessException e) {
            Assertions.fail();
        }

    }

    @Test
    void testGetInvalidAuth() {
        UserData userData = new UserData("name", "pass", "email");
        try {
            authDAO.createAuth(userData);
            Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth("random"));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testValidDelete() {
        UserData userData = new UserData("name", "pass", "email");
        try {
            var authData = authDAO.createAuth(userData);
            Assertions.assertTrue(authDAO.deleteAuth(authData.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testInvalidDelete() {
        Assertions.assertDoesNotThrow(() -> authDAO.deleteAuth("random"));
    }

    @Test
    void testClearRuns() {
        Assertions.assertDoesNotThrow(authDAO::clear);
    }

    @Test
    void testClearActuallyClears() {
        UserData userData = new UserData("name", "pass", "email");
        try {
            var authData = authDAO.createAuth(userData);
            Assertions.assertTrue(authDAO.deleteAuth(authData.authToken()));

            Assertions.assertDoesNotThrow(authDAO::clear);

            Assertions.assertFalse(authDAO.authExists(authData.authToken()));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }
}
