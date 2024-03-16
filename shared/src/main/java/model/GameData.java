package model;

import chess.ChessGame;

import java.util.Objects;

/**
 * Contains the data for a game object stored in the database
 * @param gameID Integer containing the gameID created for the game
 * @param whiteUsername String - username of the player playing WHITE
 * @param blackUsername String - username of the player playing BLACK
 * @param gameName String - name of the current game
 * @param game ChessGame - contains the current game state
 */
public record GameData(Integer gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameData gameData = (GameData) o;
        return gameID == gameData.gameID && Objects.equals(whiteUsername, gameData.whiteUsername) && Objects.equals(blackUsername, gameData.blackUsername) && Objects.equals(gameName, gameData.gameName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameID, whiteUsername, blackUsername, gameName);
    }
}
