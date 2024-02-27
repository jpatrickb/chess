package handlers;

import com.google.gson.Gson;

/**
 * Record accepting information provided upon a request to register a new user
 * @param username the username of the new user
 * @param password the password of the new user
 * @param email the email of the new user
 */
public record RegistrationRequest(String username, String password, String email) {

    public String toString() {
        return new Gson().toJson(this);
    }
}
