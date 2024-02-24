package server;

import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccess;
import dataAccess.GameDAO;
import dataAccess.UserDAO;
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
//    This line is to use the memory data access
    private final DataAccess dataAccess = new DataAccess(DataAccess.DataLocation.MEMORY);

//    This line is to use the SQL data access
//    private final DataAccess dataAccess = new DataAccess(DataAccess.DataLocation.SQL);
    private final AuthDAO authDAO = dataAccess.getAuthDAO();
    private final UserDAO userDAO = dataAccess.getUserDAO();
    private final GameDAO gameDAO = dataAccess.getGameDAO();

    private final RegistrationService registrationService = new RegistrationService(userDAO, authDAO);
    private final LoginService loginService = new LoginService(userDAO, authDAO);
    private final LogoutService logoutService = new LogoutService(authDAO);
    private final ListService listService = new ListService(gameDAO);
    private final JoinService joinService = new JoinService(gameDAO);
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
        response.body(new Gson().toJson(new ErrorMessage(e.getMessage())));
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
    private Object registrationHandler(Request request, Response response) throws ResponseException {
        response.type("application/json");
        var user = new Gson().fromJson(request.body(), RegistrationRequest.class);

        AuthData authData = registrationService.registerUser(user);

        response.status(200);
        response.body(new Gson().toJson(authData));
        return new Gson().toJson(authData);
    }

    private Object loginUser(Request request, Response response) throws ResponseException {
        response.type("application/json");

        var user = new Gson().fromJson(request.body(), LoginRequest.class);
        AuthData authData = loginService.login(user);

        response.status(200);
        response.body(new Gson().toJson(authData));
        return new Gson().toJson(authData);
    }

    private Object logoutUser(Request request, Response response) throws ResponseException {
        response.type("application/json");
        var authToken = new LogoutRequest(request.headers("authorization"));

        logoutService.logoutUser(authToken);
        response.status(200);
        return "";
    }

    private Object getGames(Request request, Response response) throws ResponseException {
        var authToken = request.headers("authorization");
        authService.authenticate(authToken);

        Collection<GameResponseData> allGames = listService.getGames();

        response.status(200);
        response.body(new Gson().toJson(new ListGamesResponse(allGames)));
        return new Gson().toJson(new ListGamesResponse(allGames));
    }

    private Object joinGame(Request request, Response response) throws ResponseException {
        var authToken = request.headers("authorization");
        authService.authenticate(authToken);

        AuthData authData = authService.getAuthData(authToken);

        var joinInfo = new Gson().fromJson(request.body(), JoinGameRequest.class);

        joinService.joinGame(joinInfo, authData);
        response.status(200);
        return "";
    }

    private Object createGame(Request request, Response response) throws ResponseException {
        var authToken = request.headers("authorization");
        authService.authenticate(authToken);

        var newGame = new Gson().fromJson(request.body(), CreateGameRequest.class);
        GameID gameID = gameService.createGame(newGame);

        response.status(200);
        response.body(new Gson().toJson(gameID));
        return new Gson().toJson(gameID);


    }

    private Object clearApp(Request request, Response response) {
//        TODO
        clearService.clearDatabase();
        response.status(200);
        return "";
    }
}
