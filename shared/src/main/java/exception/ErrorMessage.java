package exception;

import com.google.gson.Gson;

public record ErrorMessage(String message) {

    public String toString() {
        return new Gson().toJson(this);
    }

}
