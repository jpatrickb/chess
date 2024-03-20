package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * Facilitates communication with the server through various methods such as joining a game,
 * creating a game, registering and logging in users, listing available games, clearing the database, etc.
 */
public class ServerFacade {
    private final String serverUrl;
    private String authToken;

    /**
     * Constructs a ServerFacade object with the specified server URL.
     *
     * @param url The URL of the server.
     */
    public ServerFacade(String url) {
        serverUrl = url;
    }

    /**
     * Sends a request to join a game.
     *
     * @param joinRequest The request to join a game.
     * @throws ResponseException if the server responds with an error.
     */
    public void joinGame(JoinRequest joinRequest) throws ResponseException {
        var path = "/game";
        this.makeRequest("PUT", path, joinRequest, null);
    }

    /**
     * Creates a new game with the provided game name.
     *
     * @param gameName The name of the game to be created.
     * @return GameID object with the ID of the created game.
     * @throws ResponseException if the server responds with an error.
     */
    public GameID createGame(GameName gameName) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, gameName, GameID.class);
    }

    /**
     * Registers a new user with the provided user data.
     *
     * @param userData The data of the user to be registered.
     * @return Authentication data for the registered user.
     * @throws ResponseException if the server responds with an error.
     */
    public AuthData registerUser(UserData userData) throws ResponseException {
        var path = "/user";
        AuthData authData = this.makeRequest("POST", path, userData, AuthData.class);
        if (authData != null) {
            authToken = authData.authToken();
        }
        return authData;
    }

    /**
     * Logs in a user with the provided user data.
     *
     * @param userData The data of the user to be logged in.
     * @return Authentication data for the logged-in user.
     * @throws ResponseException if the server responds with an error.
     */
    public AuthData loginUser(UserData userData) throws ResponseException {
        var path = "/session";
        AuthData authData = this.makeRequest("POST", path, userData, AuthData.class);
        if (authData != null) {
            authToken = authData.authToken();
        }
        return authData;
    }

    /**
     * Logs out the currently authenticated user.
     *
     * @throws ResponseException if the server responds with an error.
     */
    public void logoutUser() throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null);
        authToken = null;
    }

    /**
     * Retrieves a collection of available games from the server.
     *
     * @return A collection of game response data.
     * @throws ResponseException if the server responds with an error.
     */
    public ArrayList<GameResponseData> listGames() throws ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, null, GameList.class).games();
    }

    /**
     * Clears the database on the server.
     *
     * @throws ResponseException if the server responds with an error.
     */
    public void clear() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null);
    }

    private <T> T makeRequest (String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.setRequestProperty("authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    private void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }


    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
