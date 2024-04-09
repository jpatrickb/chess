package webSocketMessages.serverMessages;

public class ErrorMessage extends ServerMessage {
    private final String errorMessage;

    public ErrorMessage(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;
        this.serverMessageType = ServerMessageType.ERROR;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
