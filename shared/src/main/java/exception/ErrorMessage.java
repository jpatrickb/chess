package exception;

import com.google.gson.Gson;

/**
 * Record storing a message to be passed upon throwing exceptions
 * @param message the message to be passed through the error
 */
public record ErrorMessage(String message) {

    public String toString() {
        return new Gson().toJson(this);
    }

}
