package ui;

import chess.ChessBoard;
import chess.ChessGame;
import exception.ResponseException;
import model.*;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static ui.EscapeSequences.*;

/**
 * The Client class represents the interface for interacting with the server and managing the chess game.
 */
public class Client {
    public static State state = State.LOGGED_OUT;
    private final ServerFacade server;
    private final Repl repl;

    /**
     * Constructs a Client object with the specified server URL and REPL interface.
     *
     * @param serverUrl The URL of the server.
     * @param repl      The REPL interface.
     */
    public Client(String serverUrl, Repl repl) {
        server = new ServerFacade(serverUrl);
        this.repl = repl;
    }

    /**
     * Evaluates the input command and executes the corresponding action.
     *
     * @param input The input command.
     * @return The result of the evaluation.
     */
    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join", "observe" -> joinGame(params);
                case "cleardb" -> clearDataBase();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    /**
     * Provides help information about available commands.
     *
     * @return Help information about available commands.
     */
    public String help() {
        String[] commands = {"create <NAME>", "list", "join <ID> [WHITE|BLACK|<empty>]", "observe <ID>", "logout", "quit", "help"};
        String[] description = {"create a game with specified name",
                "list all games",
                "joins a game to play or watch",
                "watch a game",
                "logs you out",
                "finished playing",
                "list possible commands (if you're seeing this message, you don't need to use this command)"};
        if (state == State.LOGGED_OUT) {
            commands = new String[]{"register <USERNAME> <PASSWORD> <EMAIL>", "login <USERNAME> <PASSWORD>", "quit", "help"};
            description = new String[]{"create an account", "login and play", "stop playing", "list possible commands"};
        }
        StringBuilder response = new StringBuilder();
        for (int i = 0; i < commands.length; i++) {
            response.append(SET_TEXT_COLOR_BLUE);
            response.append(" - ");
            response.append(commands[i]);
            response.append(SET_TEXT_COLOR_MAGENTA);
            response.append(" - ");
            response.append(description[i]).append("\n");
        }
        return response.toString();
    }

    /**
     * Joins a game with the specified parameters, either as a player or an observer.
     *
     * @param params The parameters for joining the game, including the game ID and optionally the desired color (WHITE or BLACK).
     * @return An empty string if the operation is successful.
     * @throws ResponseException if the server responds with an error.
     */
    private String joinGame(String[] params) throws ResponseException {
        if (params.length == 1) {
            server.joinGame(new JoinRequest(Integer.parseInt(params[0]), null));
            BoardDisplay.main(new String[]{new ChessBoard().toString()});
            return "";
        } else if (params.length == 2) {
            if (Objects.equals(params[1], "white")) {
                ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;
                server.joinGame(new JoinRequest(Integer.parseInt(params[0]), color));
            } else if (Objects.equals(params[1], "black")) {
                ChessGame.TeamColor color = ChessGame.TeamColor.BLACK;
                server.joinGame(new JoinRequest(Integer.parseInt(params[0]), color));
            } else {
                return "Invalid color.";
            }
            BoardDisplay.main(new String[]{""});
            return "";
        }
        throw new ResponseException(400, "error: bad request");
    }

    /**
     * Retrieves a collection of available games from the server.
     *
     * @return A string representation of the list of available games.
     * @throws ResponseException if the server responds with an error.
     */
    private String listGames() throws ResponseException {
        Collection<GameResponseData> allGames = server.listGames();
        return buildGameList(allGames);
    }

    /**
     * Creates a new game with the specified name.
     *
     * @param params The parameters for creating the game, including the name of the game.
     * @return A message indicating the successful creation of the game.
     * @throws ResponseException if the server responds with an error.
     */
    private String createGame(String[] params) throws ResponseException {
        if (params.length == 1) {
            GameID gameID = server.createGame(new GameName(params[0]));
            return "Game " + params[0] + " created with ID " + gameID.gameID();
        }
        throw new ResponseException(400, "error: bad request");
    }

    /**
     * Logs out the currently authenticated user.
     *
     * @return A message indicating successful logout.
     * @throws ResponseException if the server responds with an error.
     */
    private String logout() throws ResponseException {
        state = State.LOGGED_OUT;
        server.logoutUser();
        return "Logged out user";
    }

    /**
     * Registers a new user with the specified user data.
     *
     * @param params The parameters for registering a user, including username, password, and email.
     * @return A message indicating successful registration and login.
     * @throws ResponseException if the server responds with an error.
     */
    private String register(String[] params) throws ResponseException {
        if (params.length == 3) {
            UserData userData = new UserData(params[0], params[1], params[2]);
            AuthData authData = server.registerUser(userData);
            state = State.LOGGED_IN;
            return "Logged in as " + authData.username();
        }
        throw new ResponseException(400, "error: bad request");
    }

    /**
     * Logs in a user with the specified user data.
     *
     * @param params The parameters for logging in a user, including username and password.
     * @return A message indicating successful login.
     * @throws ResponseException if the server responds with an error.
     */
    private String login(String[] params) throws ResponseException {
        if (params.length == 2) {
            UserData userData = new UserData(params[0], params[1], null);
            AuthData authData = server.loginUser(userData);
            state = State.LOGGED_IN;
            return "Logged in as " + authData.username();
        }
        throw new ResponseException(401, "error: unauthorized");
    }

    /**
     * Builds a formatted string containing information about all available games.
     *
     * @param allGames The collection of GameResponseData objects representing all available games.
     * @return A formatted string containing information about all available games, including game ID, usernames of white and black players,
     *         and the name of the game.
     */
    private static String buildGameList(Collection<GameResponseData> allGames) {
        StringBuilder response = new StringBuilder("All games:\n");
        for (var game : allGames) {
            response.append("gameID:\t\t");
            response.append(game.gameID());
            response.append("\n");

            response.append("whiteUsername:\t");
            response.append(game.whiteUsername());
            response.append("\n");

            response.append("blackUsername:\t");
            response.append(game.blackUsername());
            response.append("\n");

            response.append("gameName:\t\t");
            response.append(game.gameName());
            response.append("\n\n");
        }

        return String.valueOf(response);
    }

    /**
     * Clears the database on the server.
     *
     * @return A message indicating successful clearing of the database.
     * @throws ResponseException if the server responds with an error.
     */
    private String clearDataBase() throws ResponseException {
        server.clear();
        return "Database cleared.";
    }
}
