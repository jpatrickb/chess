package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;
    private String authToken;
    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData registerUser(UserData userData) throws ResponseException {
        var path = "/user";
        AuthData authData = this.makeRequest("POST", path, userData, AuthData.class);
        if (authData != null) {
            authToken = authData.authToken();
        }
        return authData;
    }

    public AuthData loginUser(UserData userData) throws ResponseException {
        var path = "/session";
        AuthData authData = this.makeRequest("POST", path, userData, AuthData.class);
        if (authData != null) {
            authToken = authData.authToken();
        }
        return authData;
    }

    public void logoutUser() throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null);
        authToken = null;
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
