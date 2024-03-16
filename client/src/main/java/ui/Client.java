package ui;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.*;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class Client {
    private final Repl repl;
    public static State state = State.LOGGED_OUT;
    private UserData userData;
    private final ServerFacade server;
    private final String serverUrl;

    public Client(String serverUrl, Repl repl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.repl = repl;
    }

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
                case "join" -> joinGame(params);
                case "observe" -> joinGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException e) {
            return e.getMessage();
        }
    }

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

    private String observeGame(String[] params) {
        return null;
    }

    private String listGames() throws ResponseException {
        Collection<GameResponseData> allGames = server.listGames();
        return buildGameList(allGames);
    }

    private String createGame(String[] params) throws ResponseException {
        if (params.length == 1) {
            GameID gameID = server.createGame(new GameName(params[0]));
            return "Game " + params[0] + " created with ID " + Integer.toString(gameID.gameID());
        }
        throw new ResponseException(400, "error: bad request");
    }

    private String logout() throws ResponseException {
        state = State.LOGGED_OUT;
        server.logoutUser();
        return "Logged out user";
    }

    private String register(String[] params) throws ResponseException {
        if (params.length == 3) {
            UserData userData = new UserData(params[0], params[1], params[2]);
            AuthData authData = server.registerUser(userData);
            state = State.LOGGED_IN;
            return "Logged in as " + authData.username();
        }
        throw new ResponseException(400, "error: bad request");
    }

    private String login(String[] params) throws ResponseException {
        if (params.length == 2) {
            userData = new UserData(params[0], params[1], null);
            AuthData authData = server.loginUser(userData);
            state = State.LOGGED_IN;
            return "Logged in as " + authData.username();
        }
        throw new ResponseException(401, "error: unauthorized");
    }

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
}
