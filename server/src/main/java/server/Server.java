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



    public Server() {
        try {
//            This line is to use the memory data access
//            DataAccess dataAccess = new DataAccess(DataAccess.DataLocation.MEMORY);

//            This line is to use the SQL data access
            DataAccess dataAccess = new DataAccess(DataAccess.DataLocation.SQL);
            AuthDAO authDAO = dataAccess.getAuthDAO();
            UserDAO userDAO = dataAccess.getUserDAO();
            GameDAO gameDAO = dataAccess.getGameDAO();

            registrationService = new RegistrationService(userDAO, authDAO);
            loginService = new LoginService(userDAO, authDAO);
            logoutService = new LogoutService(authDAO);
            listService = new ListService(gameDAO);
            joinService = new JoinService(gameDAO);
            gameService = new GameService(gameDAO);
            clearService = new ClearService(userDAO, authDAO, gameDAO);
            authService = new AuthenticationService(authDAO);
        } catch (ResponseException ex) {
            System.out.printf("Unable to connect to database: %s%n", ex.getMessage());
        }
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
        response.status(e.getStatusCode());
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
    private Object loginUser(Request request, Response response) throws ResponseException {
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
    private Object logoutUser(Request request, Response response) throws ResponseException {
        response.type("application/json");
        var authToken = new LogoutRequest(request.headers("authorization"));

        logoutService.logoutUser(authToken);
        response.status(200);
        return "";
    }

    /**
     * Lists all the games for the user to see
     * @param request HTTP request - header is probed for authorization
     * @param response HTTP response
     * @return JSON containing a collection of the games
     * @throws ResponseException If user is unauthorized
     */
    private Object getGames(Request request, Response response) throws ResponseException {
        var authToken = request.headers("authorization");
        authService.authenticate(authToken);

        Collection<GameResponseData> allGames = listService.getGames();

        response.status(200);
        response.body(new Gson().toJson(new ListGamesResponse(allGames)));
        return new Gson().toJson(new ListGamesResponse(allGames));
    }

    /**
     * Joins a new game
     * @param request HTTP request - body containing gameID, header containing authorization
     * @param response HTTP response
     * @return Nothing
     * @throws ResponseException If the user is unauthorized
     */
    private Object joinGame(Request request, Response response) throws ResponseException {
        var authToken = request.headers("authorization");
        authService.authenticate(authToken);

        AuthData authData = authService.getAuthData(authToken);

        var joinInfo = new Gson().fromJson(request.body(), JoinGameRequest.class);

        joinService.joinGame(joinInfo, authData);
        response.status(200);
        return "";
    }

    /**
     * Creates a new game
     * @param request HTTP request - body contains gameName, header contains authorization
     * @param response HTTP response
     * @return JSON containing gameID of the game created
     * @throws ResponseException If the user is unauthorized
     */
    private Object createGame(Request request, Response response) throws ResponseException {
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
    private Object clearApp(Request request, Response response) throws ResponseException {
        clearService.clearDatabase();
        response.status(200);
        return "";
    }
}
