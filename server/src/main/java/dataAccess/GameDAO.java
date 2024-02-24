package dataAccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    public void clear();

    public void addGame(GameData gameData);

    public GameData getGame(int gameID);

    public Collection<GameData> listGames();

    public void updateGame();
}
