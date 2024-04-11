package websocket;


import chess.ChessGame;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import webSocketMessages.serverMessages.LoadGameMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String userName, Session session) {
        var connection = new Connection(userName, session);
        connections.put(userName, connection);
    }

    public void remove(String userName) {
        connections.remove(userName);
    }

    public void sendMessage(String player, String message) throws IOException {
        var conn = connections.get(player);
        if (conn.session.isOpen()) {
            conn.send(message);
        }
    }

    public void broadcastGame(LoadGameMessage gameMessage) throws IOException {
        var removeList = new ArrayList<Connection>();

        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (Objects.equals(c.userName, gameMessage.getGame().blackUsername())) {
                    gameMessage.setColor(ChessGame.TeamColor.BLACK);
                } else {
                    gameMessage.setColor(ChessGame.TeamColor.WHITE);
                }
                c.send(new Gson().toJson(gameMessage));
            } else {
                removeList.add(c);
            }
        }

        for (var c : removeList) {
            connections.remove(c.userName);
        }
    }

    public void broadcast(String excludePlayer, String message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if (!c.userName.equals(excludePlayer)) {
                    c.send(message);
                }
            } else {
                removeList.add(c);
            }
        }

        for (var c : removeList) {
            connections.remove(c.userName);
        }
    }
}
