package model;

import chess.ChessGame;

/**
 * Contains the data for a game object stored in the database
 * @param gameID Integer containing the gameID created for the game
 * @param whiteUsername String - username of the player playing WHITE
 * @param blackUsername String - username of the player playing BLACK
 * @param gameName String - name of the current game
 * @param game ChessGame - contains the current game state
 */
public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
}
