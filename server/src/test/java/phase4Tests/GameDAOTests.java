package phase4Tests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.mySQL.MySQLGameDAO;
import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class GameDAOTests {

    private static final GameDAO dao;

    static {
        try {
            dao = new MySQLGameDAO();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void init() {
        GameData gameData = new GameData(
                1,
                "patrick",
                "ben",
                "coolestGame",
                new ChessGame()
        );
        try {
            dao.addGame(gameData);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    void testAddGame() {
        GameData gameData = new GameData(
                1,
                "patrick",
                "ben",
                "coolestGame",
                new ChessGame()
        );

        Assertions.assertDoesNotThrow(() -> dao.addGame(gameData));
    }

    @Test
    void acceptsNull() {
        GameData gameData = new GameData(
                2,
                null,
                null,
                "emptyGame",
                new ChessGame()
        );

        Assertions.assertDoesNotThrow(() -> dao.addGame(gameData));
    }

    @Test
    void testGetGoodGame() {
        GameData gameData = new GameData(
                1,
                "patrick",
                "ben",
                "coolestGame",
                new ChessGame()
        );

        try {
            var gotGame = dao.getGame(1);
            Assertions.assertEquals(gameData, gotGame);
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testGetBadGame() {
        try {
            Assertions.assertNull(dao.getGame(200));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testListGames() {
        Assertions.assertDoesNotThrow(dao::listGames);
    }

    @Test
    void testCorrectlyListsGames() {
        GameData gameData = new GameData(
                1,
                "patrick",
                "ben",
                "coolestGame",
                new ChessGame()
        );
        try {
            Assertions.assertTrue(dao.listGames().contains(gameData));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testUpdate() {
        GameData gameData = new GameData(
                1,
                "patrick",
                "ethan",
                "coolestGame",
                new ChessGame()
        );
        Assertions.assertDoesNotThrow(() -> dao.updateGame(gameData));
    }

    @Test
    void updatingWorked() {
        GameData gameData = new GameData(
                1,
                "patrick",
                "ethan",
                "coolestGame",
                new ChessGame()
        );
        try {
            dao.updateGame(gameData);
            Assertions.assertEquals(gameData, dao.getGame(1));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void testClear() {
        Assertions.assertDoesNotThrow(dao::clear);
    }

    @Test
    void testClearWorks() {
        try {
            dao.clear();

            Assertions.assertNull(dao.getGame(1));
            Assertions.assertNull(dao.getGame(2));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }
}
