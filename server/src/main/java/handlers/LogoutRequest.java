package handlers;

/**
 * Record accepting the authToken of a user attempting to log out
 * @param authToken the HTTP header authorization of the user logging out
 */
public record LogoutRequest(String authToken) {
}
