package server;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
import dataAccess.memory.MemoryAuthDAO;
import dataAccess.memory.MemoryGameDAO;
import dataAccess.memory.MemoryUserDAO;
import exception.ErrorMessage;
import exception.ResponseException;
import handlers.*;
import model.AuthData;
import model.GameID;
import Service.*;
import model.GameResponseData;
import spark.*;

import java.util.Collection;

public class Server {

    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final UserDAO userDAO = new MemoryUserDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    private final RegistrationService registrationService = new RegistrationService(userDAO, authDAO);
    private final LoginService loginService = new LoginService(userDAO, authDAO);
    private final LogoutService logoutService = new LogoutService(authDAO);
    private final ListService listService = new ListService(authDAO, gameDAO);
    private final JoinService joinService = new JoinService();
    private final GameService gameService = new GameService(gameDAO);
    private final ClearService clearService = new ClearService(userDAO, authDAO, gameDAO);
    private final AuthenticationService authService = new AuthenticationService(authDAO);



    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        Spark.init();


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
        response.status(e.StatusCode());
    }


    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }

    /** Registers new users
     *
     * @param request - the HTTP request
     * @param response - the HTTP response
     * @return JSON object containing the response body
     */
    private Object registrationHandler(Request request, Response response) {
//        Complete

        response.type("application/json");
        var user = new Gson().fromJson(request.body(), RegistrationRequest.class);
        try {
            AuthData authData = registrationService.registerUser(user);
            response.status(200);
            response.body(new Gson().toJson(authData));
            return new Gson().toJson(authData);
        } catch (ResponseException e) {
            response.status(e.StatusCode());
            response.body(new Gson().toJson(new ErrorMessage(e.getMessage())));
            return new Gson().toJson(new ErrorMessage(e.getMessage()));
        }
    }

    private Object loginUser(Request request, Response response) {
        response.type("application/json");
        var user = new Gson().fromJson(request.body(), LoginRequest.class);

        try {
            AuthData authData = loginService.login(user);
            response.status(200);
            response.body(new Gson().toJson(authData));
            return new Gson().toJson(authData);
        } catch (ResponseException e) {
            response.status(e.StatusCode());
            response.body(new Gson().toJson(new ErrorMessage(e.getMessage())));
            return new Gson().toJson(new ErrorMessage(e.getMessage()));
        }
    }

    private Object logoutUser(Request request, Response response) {
        response.type("application/json");
        var authToken = new LogoutRequest(request.headers("authorization"));

        try {
            logoutService.logoutUser(authToken);
            response.status(200);
            return "";
        } catch (ResponseException e) {
            response.status(e.StatusCode());
            response.body(new Gson().toJson(new ErrorMessage(e.getMessage())));
            return new Gson().toJson(new ErrorMessage(e.getMessage()));
        }
    }

    private Object getGames(Request request, Response response) {
//        TODO
        var authToken = request.headers("authorization");
        try {
            authService.authenticate(authToken);
            Collection<GameResponseData> allGames = listService.getGames();
            response.status(200);
            response.body(new Gson().toJson(new ListGamesResponse(allGames)));
            return new Gson().toJson(new ListGamesResponse(allGames));
        } catch (ResponseException e) {
            response.status(e.StatusCode());
            response.body(new Gson().toJson(new ErrorMessage(e.getMessage())));
            return new Gson().toJson(new ErrorMessage(e.getMessage()));
        }
    }

    private Object joinGame(Request request, Response response) {
//        TODO
        var authToken = request.headers("authorization");
        try {
            authService.authenticate(authToken);
            var playerColor = request.queryParams("playerColor");
            var gameID = request.queryParams("gameID");

            return "";
        } catch (ResponseException e) {
            response.status(e.StatusCode());
            response.body(new Gson().toJson(new ErrorMessage(e.getMessage())));
            return new Gson().toJson(new ErrorMessage(e.getMessage()));
        }
    }

    private Object createGame(Request request, Response response) {
//        TODO
        var gameName = request.queryParams("gameName");
        var authToken = request.headers("authorization");
        var newGame = new CreateGameRequest(authToken, gameName);

        try {
            authService.authenticate(authToken);
            GameID gameID = gameService.createGame(newGame);
            response.status(200);
            response.body(new Gson().toJson(gameID));
            return new Gson().toJson(gameID);
        } catch (ResponseException e) {
            response.status(e.StatusCode());
            response.body(new Gson().toJson(new ErrorMessage(e.getMessage())));
            return new Gson().toJson(new ErrorMessage(e.getMessage()));
        }


    }

    private Object clearApp(Request request, Response response) {
//        TODO
        clearService.clearDatabase();
        response.status(200);
        return "";
    }
}
