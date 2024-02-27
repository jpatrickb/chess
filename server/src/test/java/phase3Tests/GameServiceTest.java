package phase3Tests;

import Service.GameService;
import dataAccess.memory.MemoryGameDAO;
import handlers.CreateGameRequest;
import model.GameID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameServiceTest {
    static final MemoryGameDAO gameDAO = new MemoryGameDAO();
    static final GameService service = new GameService(gameDAO);

    @BeforeEach
    void clear() {
        gameDAO.clear();
    }

    @Test
    void testCreateGameNotNull() {
        CreateGameRequest newGame = new CreateGameRequest("testGame");

        GameID gameID = service.createGame(newGame);

//        Positive test case (asserting that trying to get the game returns a valid game and not null)
        Assertions.assertNotNull(gameDAO.getGame(gameID.gameID()));
    }

    @Test
    void testCreateGameNull() {
//        Negative test
        Assertions.assertNull(gameDAO.getGame(1234));
    }
}
