package webSocketMessages.serverMessages;

public class NotificationMessage extends ServerMessage {
    private final String message;

    public NotificationMessage(ServerMessageType type, String message) {
        super(type);
        this.message = message;
        this.serverMessageType = ServerMessageType.NOTIFICATION;
    }

    public String getMessage() {
        return message;
    }
}
