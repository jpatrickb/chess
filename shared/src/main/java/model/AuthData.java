package model;

import com.google.gson.Gson;

/**
 * Record to accept store Authorization data for a given user
 * @param username name of the user
 * @param authToken authToken associated with this user's current session
 */
public record AuthData(String username, String authToken) {


    public String toString() {
        return new Gson().toJson(this);
    }
}
