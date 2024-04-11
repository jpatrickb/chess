package websocket;

import Service.GameService;
import Service.LoginService;
import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dataAccess.DataAccessException;
import exception.ResponseException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    private final GameService gameService;
    private final LoginService loginService;

    public WebSocketHandler(GameService gameService, LoginService loginService) {
        this.gameService = gameService;
        this.loginService = loginService;
    }


    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            JsonObject obj = JsonParser.parseString(message).getAsJsonObject();
            UserGameCommand.CommandType type = UserGameCommand.CommandType.valueOf(obj.get("commandType").getAsString());
            switch (type) {
                case JOIN_PLAYER -> joinPlayer(new Gson().fromJson(message, JoinPlayerCommand.class), session);
                case JOIN_OBSERVER -> joinObserver(new Gson().fromJson(message, JoinObserverCommand.class), session);
                case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCommand.class));
                case LEAVE -> leaveGame(new Gson().fromJson(message, LeaveCommand.class));
                case RESIGN -> resignGame(new Gson().fromJson(message, ResignCommand.class));
                case GET_GAME -> getGame(new Gson().fromJson(message, GetGameCommand.class));
            }
        } catch (Exception e) {
            System.out.printf("Error occurred: %s%n", e.getMessage());
            var errorMessage = new ErrorMessage(e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
    }

    private void getGame(GetGameCommand getGameCommand) throws DataAccessException, IOException {
        var gameData = this.gameService.getGame(getGameCommand.getGameID());
        String username = loginService.getUser(getGameCommand.getAuthString());

        var loadGame = new LoadGameMessage(gameData);
        connections.sendMessage(username, new Gson().toJson(loadGame));
    }

    private void resignGame(ResignCommand command) throws DataAccessException, IOException, ResponseException {
        String username = loginService.getUser(command.getAuthString());
        var gameData = this.gameService.getGame(command.getGameID());

        if (!Objects.equals(gameData.whiteUsername(), username) && !Objects.equals(gameData.blackUsername(), username)) {
            throw new ResponseException(401, "Can't resign as an observer");
        }
        if (gameData.game().getTeamTurn() == ChessGame.TeamColor.NONE) {
            throw new ResponseException(400, "Already resigned");
        }

        gameData.game().setTeamTurn(ChessGame.TeamColor.NONE);
        gameService.updateGame(gameData);

        var message = new NotificationMessage(String.format("Player %s has resigned the game.", username));
        connections.broadcast(null, new Gson().toJson(message));
    }

    private void leaveGame(LeaveCommand leaveCommand) throws DataAccessException, IOException {
        String username = loginService.getUser(leaveCommand.getAuthString());
        connections.remove(username);
        var notification = new NotificationMessage(String.format("Player %s has left the game.", username));
        connections.broadcast(username, new Gson().toJson(notification));
    }

    private void makeMove(MakeMoveCommand command) throws DataAccessException, InvalidMoveException, IOException, ResponseException {
        String username = loginService.getUser(command.getAuthString());
        var gameData = this.gameService.getGame(command.getGameID());
        ChessGame.TeamColor color = gameData.game().getTeamTurn();
        String turn = switch (color) {
            case WHITE -> gameData.whiteUsername();
            case BLACK -> gameData.blackUsername();
            case NONE -> null;
        };

        if (!Objects.equals(turn, username)) {
            throw new ResponseException(400, "Not your turn");
        }

        gameData.game().makeMove(command.getMove());
        this.gameService.updateGame(gameData);

        connections.broadcastGame(new LoadGameMessage(gameData));
        var notification = new NotificationMessage("Move " + command.getMove().toString() + " made by " + username);
        connections.broadcast(username, new Gson().toJson(notification));
    }

    private void joinObserver(JoinObserverCommand command, Session session) throws DataAccessException, IOException, ResponseException {
        String username = loginService.getUser(command.getAuthString());
        connections.add(username, session);
        var gameData = this.gameService.getGame(command.getGameID());
        if (gameData == null) {
            throw new ResponseException(400, "Invalid game id");
        }

        var text = String.format("Player %s has joined as observer", username);
        var notification = new NotificationMessage(text);
        connections.broadcast(username, new Gson().toJson(notification));
        sendGame(gameData, ChessGame.TeamColor.WHITE, username);
    }

    public void joinPlayer(JoinPlayerCommand command, Session session) throws DataAccessException, IOException {
        String username = loginService.getUser(command.getAuthString());
        connections.add(username, session);
        var gameData = this.gameService.getGame(command.getGameID());
        if (!username.equals(getUsername(gameData, command.getPlayerColor()))) {
            var message = new ErrorMessage("Can't join as " + command.getPlayerColor().toString());
            connections.sendMessage(username, new Gson().toJson(message));
        } else {
            var text = String.format("Player %s has joined as %s%n", username, command.getPlayerColor());
            var notification = new NotificationMessage(text);
            connections.broadcast(username, new Gson().toJson(notification));
            sendGame(gameData, command.getPlayerColor(), username);
        }
    }

    public void sendGame(GameData game, ChessGame.TeamColor color, String player) throws IOException {
        var message = new LoadGameMessage(game);
        message.setColor(color);
        connections.sendMessage(player, new Gson().toJson(message));
    }

    private String getUsername(GameData gameData, ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            return gameData.whiteUsername();
        } else {
            return gameData.blackUsername();
        }
    }
}
