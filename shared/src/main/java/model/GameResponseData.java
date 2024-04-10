package model;

import chess.ChessGame;

/**
 * Contains only the data to be returned when a user requests to list the games
 * @param gameID the ID associated with the game
 * @param whiteUsername String - username of the player playing WHITE
 * @param blackUsername String - username of the player playing BLACK
 * @param gameName String - name of the current game
 */
public record GameResponseData(Integer gameID, String whiteUsername, String blackUsername, String gameName) {

    public boolean isOccupied(ChessGame.TeamColor color) {
        return (color == ChessGame.TeamColor.WHITE && whiteUsername() != null) ||
                (color == ChessGame.TeamColor.BLACK && blackUsername() != null);
    }
}
