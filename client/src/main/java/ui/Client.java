package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.*;
import server.ServerFacade;
import webSocketMessages.userCommands.MakeMoveCommand;
import websocket.NotificationHandler;
import websocket.WebSocketFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;

/**
 * The Client class represents the interface for interacting with the server and managing the chess game.
 */
public class Client {
    public static State state = State.LOGGED_OUT;
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler repl;
    private ArrayList<GameResponseData> allGames;
    private final WebSocketFacade ws;
    private AuthData authData;

    /**
     * Constructs a Client object with the specified server URL and REPL interface.
     *
     * @param serverUrl The URL of the server.
     * @param repl      The REPL interface.
     */
    public Client(String serverUrl, NotificationHandler repl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.repl = repl;
        this.ws = new WebSocketFacade(this.serverUrl, this.repl);
    }

    /**
     * Evaluates the input command and executes the corresponding action.
     *
     * @param input The input command.
     * @return The result of the evaluation.
     */
    public String eval(String input) {
        try {
            var tokens = input.split(" ");
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
                case "move" -> makeMove(params);
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

    private String makeMove(String[] params) throws ResponseException {
        var start = params[0];
        var end = params[1];
        ChessPosition startPosition = parsePosition(start.toLowerCase());
        ChessPosition endPosition = parsePosition(end.toLowerCase());

        ChessPiece.PieceType promotionPiece = null;
        if (params.length == 3) {
            promotionPiece = ChessPiece.PieceType.valueOf(params[2].toUpperCase());
        }

        ChessMove move = new ChessMove(startPosition, endPosition, promotionPiece);
//        ws.makeMove(authData.authToken(), );

        return "";
    }

    private ChessPosition parsePosition(String pos) throws ResponseException {
        if (pos.length() != 2) {
            throw new ResponseException(400, "Invalid position: " + pos);
        }
        var col = pos.charAt(0) - 96;
        var row = pos.charAt(1) - 48;
        return new ChessPosition(row, col);
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
     */
    private String joinGame(String[] params) {
        if (state == State.LOGGED_OUT) {
            return "Must login first";
        }

        try {
            updateGames();
            int idx = Integer.parseInt(params[0]);
            var game = allGames.get(idx - 1);

            ChessGame.TeamColor color = parseColor(params.length == 2 ? params[1].toLowerCase() : null);
            if (color == null) {
                return "Invalid color.";
            }

            if (game.isOccupied(color)) {
                return String.format("Can't join as %s", color.name().toLowerCase());
            }

            server.joinGame(new JoinRequest(game.gameID(), color));
            ws.joinPlayer(authData.authToken(), game.gameID(), color);
//            BoardDisplay.main(new ChessGame(), color);
            return "";
        } catch (IndexOutOfBoundsException e) {
            return "Requested game doesn't exist";
        } catch (ResponseException e) {
            return "Failed to observe game, try later.";
        } catch (NumberFormatException e) {
            return "Invalid input";
        }
    }

    private ChessGame.TeamColor parseColor(String color) {
        if (color != null && (color.equals("white") || color.equals("black"))) {
            return ChessGame.TeamColor.valueOf(color.toUpperCase());
        }
        return null;
    }


    /**
     * Retrieves a collection of available games from the server.
     *
     * @return A string representation of the list of available games.
     */
    private String listGames() {
        if (state == State.LOGGED_OUT) {
            return "Must login first";
        }
        updateGames();
        return buildGameList();
    }

    /**
     * Creates a new game with the specified name.
     *
     * @param params The parameters for creating the game, including the name of the game.
     * @return A message indicating the successful creation of the game.
     */
    private String createGame(String[] params) {
        if (state == State.LOGGED_OUT) {
            return "Must login first";
        }
        String name = String.join(" ", params);
        GameID gameID;
        try {
            gameID = server.createGame(new GameName(name));
            updateGames();
            for (int idx = 0; idx < allGames.size(); idx ++) {
                if (allGames.get(idx).gameID() == gameID.gameID()) {
                    return "Game " + name + " created with ID " + (idx + 1);
                }
            }
            return "Error creating game, please try again";
        } catch (ResponseException e) {
            return "Couldn't create game with that name, try again.";
        }
    }

    /**
     * Logs out the currently authenticated user.
     *
     * @return A message indicating successful logout.
     */
    private String logout() {
        if (state == State.LOGGED_OUT) {
            return "Must login first";
        }
        state = State.LOGGED_OUT;
        try {
            server.logoutUser();
        } catch (ResponseException e) {
            return "Failed to log out";
        }
        return "Logged out user";
    }

    /**
     * Registers a new user with the specified user data.
     *
     * @param params The parameters for registering a user, including username, password, and email.
     * @return A message indicating successful registration and login.
     */
    private String register(String[] params) {
        if (state == State.LOGGED_IN) {
            return "Must logout first";
        }
        if (params.length == 3) {
            UserData userData = new UserData(params[0], params[1], params[2]);
            try {
                authData = server.registerUser(userData);
            } catch (ResponseException e) {
                return "Invalid credentials";
            }
            state = State.LOGGED_IN;
            return "Logged in as " + authData.username();
        }
        return "Invalid credentials";
    }

    /**
     * Logs in a user with the specified user data.
     *
     * @param params The parameters for logging in a user, including username and password.
     * @return A message indicating successful login.
     */
    private String login(String[] params) {
        if (state == State.LOGGED_IN) {
            return "Must logout first";
        }
        if (params.length == 2) {
            UserData userData = new UserData(params[0], params[1], null);
            try {
                authData = server.loginUser(userData);
            } catch (ResponseException e) {
                return "Invalid login";
            }
            state = State.LOGGED_IN;
            return "Logged in as " + authData.username();
        }
        return "Invalid credentials";
    }

    /**
     * Builds a formatted string containing information about all available games.
     *
     * @return A formatted string containing information about all available games, including game ID, usernames of white and black players,
     *         and the name of the game.
     */
    private String buildGameList() {
        StringBuilder response = new StringBuilder();
        response.append(LINE);
        response.append(String.format("| ID  | %-14s| %-14s| %-12s|\n", "White Player", "Black Player", "Game Name"));
        response.append(LINE);
        for (int idx = 0; idx < allGames.size(); idx++) {
            var game = allGames.get(idx);
            response.append(String.format("| %-4d| %-14s| %-14s| %-12s|\n", idx+1, game.whiteUsername(), game.blackUsername(), game.gameName()));
            response.append(LINE);
        }

        return String.valueOf(response);
    }

    private void updateGames() {
        try {
            var newGames = server.listGames();
            ArrayList<GameResponseData> tempGames = new ArrayList<>();

            if (allGames != null) {
                for (var currGame : allGames) {
                    for (var newGame : newGames) {
                        if (Objects.equals(newGame.gameID(), currGame.gameID())) {
                            tempGames.add(newGame);
                        }
                    }
                }
                for (var newGame : newGames) {
                    boolean found = false;
                    for (var currGame : allGames) {
                        if (Objects.equals(newGame.gameID(), currGame.gameID())) {
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        tempGames.add(newGame);
                    }
                }
            } else {
                tempGames = newGames;
            }
            allGames = tempGames;
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
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
