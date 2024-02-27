package phase3Tests;

import Service.GameService;
import Service.ListService;
import dataAccess.memory.MemoryGameDAO;
import handlers.CreateGameRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ListServiceTest {
    static final MemoryGameDAO gameDAO = new MemoryGameDAO();
    static final ListService service = new ListService(gameDAO);
    static final GameService gameService = new GameService(gameDAO);

    @BeforeEach
    void clear() {
        gameDAO.clear();
    }

    @Test
    void testGetGames() {
//        Test that there are currently no games
        Assertions.assertTrue(service.getGames().isEmpty());

        gameService.createGame(new CreateGameRequest("game1"));
        gameService.createGame(new CreateGameRequest("game2"));
        gameService.createGame(new CreateGameRequest("game3"));

//        Test that there are the correct number of games
        Assertions.assertEquals(3, service.getGames().size());
    }
}
