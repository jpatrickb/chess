package Service;

import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import model.GameResponseData;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles requests to list all the games in the database
 */
public class ListService {
    private final GameDAO gameDAO;

    /**
     * Receives a GameDAO object to provide access to the game data
     * @param gameDAO GameDAO object providing access to the game data
     */
    public ListService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    /**
     * Returns a HashSet of all the GameResponseData
     * Contains only the gameID, player usernames, and gameName (not the ChessGame object)
     * @return Collection of GameResponseData containing data for all the games
     */
    public ArrayList<GameResponseData> getGames() throws DataAccessException {
        ArrayList<GameData> allGames = gameDAO.listGames();
        ArrayList<GameResponseData> gameResponseData = new ArrayList<>();
        for (var game : allGames) {
            gameResponseData.add(new GameResponseData(
                            game.gameID(), game.whiteUsername(),
                            game.blackUsername(),
                            game.gameName()));
        }

        return gameResponseData;
    }

    public ConcurrentHashMap<Integer, GameData> getGameObjects() throws DataAccessException {
        ArrayList<GameData> allGames = gameDAO.listGames();
        ConcurrentHashMap<Integer, GameData> gameData = new ConcurrentHashMap<>();
        for (var game : allGames) {
            gameData.put(game.gameID(), game);
        }

        return gameData;
    }
}
