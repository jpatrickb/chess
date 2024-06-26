package server;

import com.google.gson.Gson;
import dataAccess.*;
import dataAccess.mySQL.MySQLAuthDAO;
import dataAccess.mySQL.MySQLGameDAO;
import dataAccess.mySQL.MySQLUserDAO;
import exception.ErrorMessage;
import exception.ResponseException;
import handlers.*;
import model.*;
import Service.*;
import spark.*;
import websocket.WebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Initializes a server to run the chess games on
 */
public class Server {

    private RegistrationService registrationService;
    private LoginService loginService;
    private LogoutService logoutService;
    private ListService listService;
    private JoinService joinService;
    private GameService gameService;
    private ClearService clearService;
    private AuthenticationService authService;
    private WebSocketHandler webSocketHandler;



    public Server() {
        try {
            AuthDAO authDAO = new MySQLAuthDAO();
            UserDAO userDAO = new MySQLUserDAO();
            GameDAO gameDAO = new MySQLGameDAO();

            registrationService = new RegistrationService(userDAO, authDAO);
            loginService = new LoginService(userDAO, authDAO);
            logoutService = new LogoutService(authDAO);
            listService = new ListService(gameDAO);
            joinService = new JoinService(gameDAO);
            gameService = new GameService(gameDAO);
            clearService = new ClearService(userDAO, authDAO, gameDAO);
            authService = new AuthenticationService(authDAO);

            webSocketHandler = new WebSocketHandler(gameService, loginService);
        } catch (ResponseException ex) {
            System.out.printf("Unable to connect to database: %s%n", ex.getMessage());
        }
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
//        Spark.init();


        // Register your endpoints and handle exceptions here.
        Spark.webSocket("/connect", webSocketHandler);

        Spark.post("/user", this::registrationHandler);

        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);

        Spark.get("/game", this::getGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.get("/objects", this::gameObjects);

        Spark.delete("/db", this::clearApp);

        Spark.exception(ResponseException.class, this::responseExceptionHandler);
        Spark.exception(DataAccessException.class, this::dataExceptionHandler);
        Spark.exception(IOException.class, this::ioExceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private void ioExceptionHandler(IOException e, Request request, Response response) {
        response.status(500);
        response.body(new Gson().toJson(new ErrorMessage(e.getMessage())));
    }

    private void responseExceptionHandler(ResponseException e, Request request, Response response) {
        response.status(e.getStatusCode());
        response.body(new Gson().toJson(new ErrorMessage(e.getMessage())));
    }

    private void dataExceptionHandler(DataAccessException e, Request request, Response response) {
        response.status(500);
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

    /**
     * Logs in new users
     * @param request HTTP request - body is probed for username and password
     * @param response HTTP response
     * @return JSON of the authorization data upon successful login
     * @throws ResponseException if unsuccessful log in, indicating incorrect password or other errors
     */
    private Object loginUser(Request request, Response response) throws ResponseException, DataAccessException {
        response.type("application/json");

        var user = new Gson().fromJson(request.body(), LoginRequest.class);
        AuthData authData = loginService.login(user);

        response.status(200);
        response.body(new Gson().toJson(authData));
        return new Gson().toJson(authData);
    }

    /**
     * Logs a user out
     * @param request HTTP request - headers are probed for authorization
     * @param response HTTP response
     * @return Nothing
     * @throws ResponseException If user is unauthorized to log out, or other errors
     */
    private Object logoutUser(Request request, Response response) throws ResponseException, DataAccessException {
        response.type("application/json");
        var authToken = new LogoutRequest(request.headers("authorization"));
        authService.authenticate(authToken.authToken());

        logoutService.logoutUser(authToken);
        response.status(200);
        return "{}";
    }

    /**
     * Lists all the games for the user to see
     * @param request HTTP request - header is probed for authorization
     * @param response HTTP response
     * @return JSON containing a collection of the games
     * @throws ResponseException If user is unauthorized
     * @throws DataAccessException If error occurs while communicating with database
     */
    private Object getGames(Request request, Response response) throws ResponseException, DataAccessException {
        var authToken = request.headers("authorization");
        authService.authenticate(authToken);

        ArrayList<GameResponseData> allGames = listService.getGames();

        response.status(200);
        response.body(new Gson().toJson(new ListGamesResponse(allGames)));
        return new Gson().toJson(new ListGamesResponse(allGames));
    }

    private Object gameObjects(Request request, Response response) throws ResponseException, DataAccessException {
        var authToken = request.headers("authorization");
        authService.authenticate(authToken);

        ConcurrentHashMap<Integer, GameData> games = listService.getGameObjects();

        response.status(200);
        response.body(new Gson().toJson(new GameObjects(games)));
        return new Gson().toJson(new GameObjects(games));
    }

    /**
     * Joins a new game
     * @param request HTTP request - body containing gameID, header containing authorization
     * @param response HTTP response
     * @return Nothing
     * @throws ResponseException If the user is unauthorized
     */
    private Object joinGame(Request request, Response response) throws ResponseException, DataAccessException, IOException {
        var authToken = request.headers("authorization");
        authService.authenticate(authToken);

        AuthData authData = authService.getAuthData(authToken);

        var joinInfo = new Gson().fromJson(request.body(), JoinGameRequest.class);

        joinService.joinGame(joinInfo, authData);

        response.status(200);
        return "{}";
    }

    /**
     * Creates a new game
     * @param request HTTP request - body contains gameName, header contains authorization
     * @param response HTTP response
     * @return JSON containing gameID of the game created
     * @throws ResponseException If the user is unauthorized
     */
    private Object createGame(Request request, Response response) throws ResponseException, DataAccessException {
        var authToken = request.headers("authorization");
        authService.authenticate(authToken);

        var newGame = new Gson().fromJson(request.body(), CreateGameRequest.class);
        GameID gameID = gameService.createGame(newGame);

        response.status(200);
        response.body(new Gson().toJson(gameID));
        return new Gson().toJson(gameID);
    }

    /**
     * Clears all databases
     * @param request HTTP Request
     * @param response HTTP Response
     * @return Nothing
     */
    private Object clearApp(Request request, Response response) throws ResponseException, DataAccessException {
        clearService.clearDatabase();
        response.status(200);
        return "{}";
    }
}
