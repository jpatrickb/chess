package clientTests;

import chess.ChessGame;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Collection;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    static GameID gameID;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var url = "http://localhost:" + port;
        facade = new ServerFacade(url);
    }

    @BeforeEach
    public void createUsers() {
        try {
            facade.registerUser(new UserData("patrick", "beal", "email"));
            gameID = facade.createGame(new GameName("myGame"));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void clear() {
        try {
            facade.clear();
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void register() throws ResponseException {
        var authData = facade.registerUser(new UserData("test1", "password", "fakeemail@gmail.com"));
        Assertions.assertTrue(authData.authToken().length() > 10);
    }

    @Test
    public void doubleRegister() throws ResponseException {
        facade.registerUser(new UserData("test1", "password", "fakeemail@gmail.com"));
        Assertions.assertThrows(ResponseException.class, () -> facade.registerUser(
                new UserData("test1",
                "password",
                "fakeemail@gmail.com")));

    }

    @Test
    public void loginSuccess() {
        Assertions.assertDoesNotThrow(() -> facade.loginUser(new UserData("patrick", "beal", null)));
    }

    @Test
    public void loginFail() {
        Assertions.assertThrows(ResponseException.class, () -> facade.loginUser(
                new UserData("fakeName", "badPass", null)
        ));
    }

    @Test
    public void logoutSuccess() {
        Assertions.assertDoesNotThrow(() -> facade.logoutUser());
    }

    @Test
    public void logoutFail() {
        Assertions.assertDoesNotThrow(() -> facade.logoutUser());
        Assertions.assertThrows(ResponseException.class, () -> facade.logoutUser());
    }

    @Test
    public void createGameSuccess() {
        Assertions.assertNotNull(gameID);
        Assertions.assertDoesNotThrow(() -> facade.createGame(new GameName("testCreate")));
    }

    @Test
    public void createGameFail() {
        try {
            facade.logoutUser();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertThrows(ResponseException.class, () -> facade.createGame(new GameName("test")));
    }

    @Test
    public void testList() {
        Collection<GameResponseData> games = null;
        try {
            games = facade.listGames();
        } catch (ResponseException e) {
            Assertions.fail();
        }
        Collection<GameResponseData> expected = new ArrayList<>();
        expected.add(new GameResponseData(gameID.gameID(), null, null, "myGame"));
        Assertions.assertEquals(expected, games);
    }

    @Test
    public void testListFails() {
        try {
            facade.logoutUser();
        } catch (ResponseException e) {
            Assertions.fail();
        }
        Assertions.assertThrows(ResponseException.class, () -> facade.listGames());
    }

    @Test
    public void joinGameSuccess() {
        Assertions.assertDoesNotThrow(() -> facade.joinGame(new JoinRequest(gameID.gameID(), ChessGame.TeamColor.WHITE)));
        try {
            Collection<GameResponseData> games = facade.listGames();
            Collection<GameResponseData> expected = new ArrayList<>();
            expected.add(new GameResponseData(
                    gameID.gameID(),
                    "patrick",
                    null,
                    "myGame"
            ));
            Assertions.assertEquals(expected, games);
        } catch (ResponseException e) {
            Assertions.fail();
        }
    }

    @Test
    public void joinGameFail() {
        Assertions.assertDoesNotThrow(() -> facade.joinGame(new JoinRequest(
                gameID.gameID(),
                ChessGame.TeamColor.WHITE
        )));
        Assertions.assertThrows(ResponseException.class,
                () -> facade.joinGame(new JoinRequest(
                        gameID.gameID(),
                        ChessGame.TeamColor.WHITE
                )));
    }
}
