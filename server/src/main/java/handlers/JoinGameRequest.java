package handlers;

/**
 * Record storing information passed in upon requests to join a game
 * @param playerColor String indicating the color to join as
 * @param gameID Integer indicating the ID of the game to join
 */
public record JoinGameRequest(String playerColor, Integer gameID) {
}
