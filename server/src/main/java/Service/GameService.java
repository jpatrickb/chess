package Service;

import chess.ChessGame;
import dataAccess.GameDAO;
import handlers.CreateGameRequest;
import model.GameData;
import model.GameID;

import java.util.Random;

public class GameService {
    private final GameDAO gameDAO;

    public GameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public GameID createGame(CreateGameRequest newGame) {
//        Generate the game ID
        Random random = new Random();
        int gameID = random.nextInt(1000000);

//        Initialize a new game
        ChessGame game = new ChessGame();
        GameData gameData = new GameData(
                gameID,
                null,
                null,
                newGame.gameName(),
                game
        );
//        Add the game to the database
        this.gameDAO.addGame(gameData);

        return new GameID(gameID);
    }
}
