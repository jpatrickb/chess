package webSocketMessages.serverMessages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage {
    private final ChessGame game;

    public LoadGameMessage(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
        this.serverMessageType = ServerMessageType.LOAD_GAME;
    }

    public ChessGame getGame() {
        return game;
    }
}
