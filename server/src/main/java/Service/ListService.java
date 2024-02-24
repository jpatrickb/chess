package Service;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import model.GameData;
import model.GameResponseData;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ListService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public ListService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public Collection<GameResponseData> getGames() {
        Collection<GameData> allGames = gameDAO.listGames();
        Collection<GameResponseData> gameResponseData = new HashSet<>();
        for (var game : allGames) {
            gameResponseData.add(new GameResponseData(
                            game.gameID(), game.whiteUsername(),
                            game.blackUsername(),
                            game.gameName()));
        }

        return gameResponseData;
    }
}
