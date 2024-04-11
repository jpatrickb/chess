package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import exception.ResponseException;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String s) {
                    JsonObject obj = JsonParser.parseString(s).getAsJsonObject();
                    ServerMessage.ServerMessageType type = ServerMessage.ServerMessageType.valueOf(obj.get("serverMessageType").getAsString());
                    switch (type) {
                        case LOAD_GAME -> notificationHandler.loadGame(new Gson().fromJson(s, LoadGameMessage.class));
                        case ERROR -> notificationHandler.error(new Gson().fromJson(s, ErrorMessage.class));
                        case NOTIFICATION -> notificationHandler.notify(new Gson().fromJson(s, NotificationMessage.class));
                    }
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinPlayer(String authToken, int gameID, ChessGame.TeamColor color) throws ResponseException {
        try {
            var command = new JoinPlayerCommand(authToken, gameID, color);
            send(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ResponseException {
        try {
            var command = new MakeMoveCommand(authToken, gameID, move);
            send(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void send(String msg) throws IOException {
        this.session.getBasicRemote().sendText(msg);
    }
}
