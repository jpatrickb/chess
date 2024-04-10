package websocket;


import org.eclipse.jetty.websocket.api.Session;
import server.Server;
import webSocketMessages.serverMessages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
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
            System.out.println("Found connection!");
            conn.send(message);
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
