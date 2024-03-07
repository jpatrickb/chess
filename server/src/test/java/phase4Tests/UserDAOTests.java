package phase4Tests;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import dataAccess.mySQL.MySQLUserDAO;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;



public class UserDAOTests {

    private static final UserDAO userDAO;

    static {
        try {
            userDAO = new MySQLUserDAO();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    public UserDAOTests() {
    }

    @AfterAll
    static void clearAll() {
        try {
            userDAO.clear();
        } catch (DataAccessException ignored) {

        }
    }

    @Test
    void testCreateUserValid() {
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(new UserData(
                "name",
                "pass",
                "email")));
    }

    @Test
    void testInvalidCreateUser() {
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(new UserData(
                null,
                null,
                null
        )));
    }

    @Test
    void testIsUser() {
        try {
            userDAO.createUser(new UserData(
                    "name",
                    "pass",
                    "email"
            ));
        } catch (DataAccessException ignored) {

        }
        Assertions.assertDoesNotThrow(() -> userDAO.isUser(new UserData(
                "name",
                "pass",
                "email")));
        try {
            Assertions.assertTrue(userDAO.isUser(new UserData(
                    "name",
                    "pass",
                    "email")));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testIsNotUser() {
        Assertions.assertDoesNotThrow(() -> userDAO.isUser(new UserData(
                "fake",
                "bad",
                "why?"
        )));
        try {
            Assertions.assertFalse(userDAO.isUser(new UserData(
                    "badName",
                    "badPass",
                    "badEmail")));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testGetUser() {
        try {
            userDAO.createUser(new UserData(
                    "name",
                    "pass",
                    "email"
            ));
        } catch (DataAccessException ignored) {

        }
        try {
            Assertions.assertEquals(new UserData(
                    "name",
                    "pass",
                    "email"
            ), userDAO.getUser("name"));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testGetBadUser() {
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser("fakeName"));
    }

    @Test
    void testClearSafe() {
        Assertions.assertDoesNotThrow(userDAO::clear);
    }

    @Test
    void testClearDeletes() {
        Assertions.assertDoesNotThrow(userDAO::clear);
        try {
            Assertions.assertFalse(userDAO.isUser(new UserData(
                    "name",
                    "pass",
                    "email"
            )));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

}
