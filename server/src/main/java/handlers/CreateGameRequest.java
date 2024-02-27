package handlers;

/**
 * Record storing information passed in upon a request to create a new game
 * @param gameName String of the name passed in by the user
 */
public record CreateGameRequest(String gameName) {
}
