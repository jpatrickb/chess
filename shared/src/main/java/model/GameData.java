package model;

import chess.ChessGame;
import com.google.gson.Gson;

public record GameData(String gameName) {
    public String toString() {
        return new Gson().toJson(this);
    }
}
