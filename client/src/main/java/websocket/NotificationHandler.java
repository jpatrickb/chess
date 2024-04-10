package websocket;

import webSocketMessages.serverMessages.ErrorMessage;
import webSocketMessages.serverMessages.LoadGameMessage;
import webSocketMessages.serverMessages.NotificationMessage;
import webSocketMessages.serverMessages.ServerMessage;

public interface NotificationHandler {
    void notify(NotificationMessage message);

    void loadGame(LoadGameMessage message);

    void error(ErrorMessage message);
}
