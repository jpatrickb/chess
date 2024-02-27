package handlers;

/**
 * Record to accept information passed in upon requests to log in
 * @param username name of the user logging in
 * @param password password of the user loggin in
 */
public record LoginRequest(String username, String password) {
}
