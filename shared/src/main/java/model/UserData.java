package model;

/**
 * Contains data for the registered users
 * @param username the username of the user
 * @param password the password of the user
 * @param email the email of the user
 */
public record UserData(String username, String password, String email) {
}
