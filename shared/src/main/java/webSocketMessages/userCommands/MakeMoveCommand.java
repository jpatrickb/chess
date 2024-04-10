package webSocketMessages.userCommands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private final int gameID;
    private final ChessMove move;
    private final String username;

    public MakeMoveCommand(String authToken, String username, int gameID, ChessMove move) {
        super(authToken);
        this.username = username;
        this.gameID = gameID;
        this.move = move;
        this.commandType = CommandType.MAKE_MOVE;
    }

    public ChessMove getMove() {
        return move;
    }

    public int getGameID() {
        return gameID;
    }

    public String getUsername() {
        return username;
    }
}
