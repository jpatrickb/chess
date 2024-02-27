package phase3Tests;

import Service.GameService;
import Service.JoinService;
import dataAccess.memory.MemoryGameDAO;
import exception.ResponseException;
import handlers.CreateGameRequest;
import handlers.JoinGameRequest;
import model.AuthData;
import model.GameID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JoinServiceTest {
    static final MemoryGameDAO gameDAO = new MemoryGameDAO();
    static final JoinService service = new JoinService(gameDAO);
    static final GameService gameService = new GameService(gameDAO);

    @BeforeEach
    void clear() {
        gameDAO.clear();
    }

    @Test
    void testJoinGameBad() {
        GameID gameID = gameService.createGame(new CreateGameRequest("testGame"));

        AuthData authData = new AuthData("testUser", "12345");

//        Test handling invalid game passed in
        Assertions.assertThrows(ResponseException.class, () -> service.joinGame(new JoinGameRequest("BLACK", 5), authData));

//        Test bad color
        Assertions.assertThrows(ResponseException.class, () -> service.joinGame(new JoinGameRequest("green", gameID.gameID()), authData));
    }

    @Test
    void testJoinGameGood() {
        GameID gameID = gameService.createGame(new CreateGameRequest("testGame"));
        JoinGameRequest req = new JoinGameRequest("WHITE", gameID.gameID());

        AuthData authData = new AuthData("testUser", "12345");

//        Test properly add player
        Assertions.assertDoesNotThrow(() -> service.joinGame(req, authData));

//        Test username already taken
        Assertions.assertThrows(ResponseException.class, () -> service.joinGame(req, authData));
    }
}
