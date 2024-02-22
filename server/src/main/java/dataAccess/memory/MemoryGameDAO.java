package dataAccess.memory;

import chess.ChessGame;
import dataAccess.GameDAO;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private final HashMap<Integer, HashMap<String, String>> gameList = new HashMap<>();
    private final HashMap<Integer, ChessGame> gameObjects = new HashMap<>();
    public void clear() {
        gameList.clear();
        gameObjects.clear();
    }
}
