package websocket;

import com.google.gson.Gson;
import exception.ResponseException;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import javax.management.NotificationFilter;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class WebSocketFacade extends Endpoint {

    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/connect");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String s) {
                    ServerMessage serverMessage = new Gson().fromJson(s, ServerMessage.class);
                    notificationHandler.notify(serverMessage);
                }
            });
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinPlayer(Session session, EndpointConfig endpointConfig) {

    }
}
