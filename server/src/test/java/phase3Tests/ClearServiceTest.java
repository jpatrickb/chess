package phase3Tests;

import Service.ClearService;
import Service.GameService;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import dataAccess.memory.MemoryAuthDAO;
import dataAccess.memory.MemoryGameDAO;
import dataAccess.memory.MemoryUserDAO;
import handlers.CreateGameRequest;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClearServiceTest {
    static final UserDAO userDAO = new MemoryUserDAO();
    static final AuthDAO authDAO = new MemoryAuthDAO();
    static final GameDAO gameDAO = new MemoryGameDAO();
    static final GameService gameService = new GameService(gameDAO);
    static final ClearService service = new ClearService(userDAO, authDAO, gameDAO);

    @Test
    void testClear() {
        UserData userData = new UserData("user", "pass", "email");

        userDAO.createUser(userData);
        AuthData authData = authDAO.createAuth(userData);
        gameService.createGame(new CreateGameRequest("game"));

//        First show that everything is nonempty
        Assertions.assertTrue(userDAO.isUser(userData));

        Assertions.assertEquals(authData, authDAO.getAuth(authData.authToken()));

//        Clear everything
        service.clearDatabase();

//        Make sure everything was cleared
        Assertions.assertFalse(userDAO.isUser(userData));
        Assertions.assertNull(authDAO.getAuth(authData.authToken()));
    }
}
