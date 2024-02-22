package dataAccess.memory;

import chess.ChessGame;
import dataAccess.GameDAO;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, HashMap<String, String>> gameList = new HashMap<>();
    private final HashMap<Integer, ChessGame> gameObjects = new HashMap<>();
    public void clear() {
        gameList.clear();
        gameObjects.clear();
    }

    @Override
    public GameData createGame() {
        return null;
    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return null;
    }

    @Override
    public void updateGame() {

    }
}
