package websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;


public class Connection {
    public String userName;
    public Session session;

    public Connection(String userName, Session session) {
        this.userName = userName;
        this.session = session;
    }

    public void send(String message) throws IOException {
        session.getRemote().sendString(message);
    }
}
