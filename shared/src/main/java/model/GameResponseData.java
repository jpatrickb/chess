package model;

import com.google.gson.Gson;

public record GameResponseData(Integer gameID, String whiteUsername, String blackUsername, String gameName) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
