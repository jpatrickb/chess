package phase3Tests;

import Service.LogoutService;
import dataAccess.memory.MemoryAuthDAO;
import exception.ResponseException;
import handlers.LogoutRequest;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LogoutServiceTest {
    static final MemoryAuthDAO authDAO = new MemoryAuthDAO();
    static final LogoutService service = new LogoutService(authDAO);

    @BeforeEach
    void clear() {
        authDAO.clear();
    }

    @Test
    void testLogoutUser() {
//        Test that a user that is not logged in cannot log out
        Assertions.assertThrows(ResponseException.class, () -> service.logoutUser(new LogoutRequest("1234")));

        UserData userData = new UserData("realUser", "realPassword", "email@email.com");

        AuthData authData = authDAO.createAuth(userData);

//        Test that a user currently logged in actually can log in
        Assertions.assertDoesNotThrow(() -> service.logoutUser(new LogoutRequest(authData.authToken())));
    }
}
