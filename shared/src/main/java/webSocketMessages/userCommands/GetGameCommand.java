package webSocketMessages.userCommands;

public class GetGameCommand extends UserGameCommand {
    private final int gameID;

    public GetGameCommand(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.GET_GAME;
    }

    public int getGameID() {
        return gameID;
    }
}
