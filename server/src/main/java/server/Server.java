package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import Service.ClearService;
import Service.RegistrationService;
import spark.*;

import java.nio.file.Paths;

public class Server {

    public Server() {

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        var webDir = Paths.get(Server.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "web");
        Spark.externalStaticFileLocation(webDir.toString());


        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registrationHandler);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::getGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clearApp);
        Spark.exception(ResponseException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void exceptionHandler(ResponseException e, Request request, Response response) {
        System.out.println("Handling exception");
        response.status(e.StatusCode());
    }


    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }


    private Object registrationHandler(Request request, Response response) throws ResponseException {
        response.type("application/json");
        var user = new Gson().fromJson(request.body(), UserData.class);
        AuthData authData = RegistrationService.registerUser(user);
        response.status(200);
        return new Gson().toJson(authData);
    }

    private Object loginUser(Request request, Response response) {
        response.type("application/json");
        return null;
    }

    private Object logoutUser(Request request, Response response) {
        return null;
    }

    private Object getGames(Request request, Response response) {
        return null;
    }

    private Object joinGame(Request request, Response response) {
        return null;
    }

    private Object createGame(Request request, Response response) {
        return null;
    }

    private Object clearApp(Request request, Response response) {
        ClearService.clearDatabase();
        response.status(200);
        return "";
    }
}
