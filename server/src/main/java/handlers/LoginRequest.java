package handlers;

import com.google.gson.Gson;

/**
 * Record to accept information passed in upon requests to log in
 * @param username name of the user logging in
 * @param password password of the user loggin in
 */
public record LoginRequest(String username, String password) {

    public String toString() {
        return new Gson().toJson(this);
    }
}
