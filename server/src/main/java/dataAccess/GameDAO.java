package dataAccess;

import model.GameData;

import java.util.ArrayList;

public interface GameDAO {
    void clear() throws DataAccessException;

    void addGame(GameData gameData) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;

    void updateGame(GameData newGame) throws DataAccessException;
}
