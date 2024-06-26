package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand{
    private final int gameID;

    public ResignCommand(String authToken, int gameID) {
        super(authToken);
        this.gameID = gameID;
        this.commandType = CommandType.RESIGN;
    }

    public int getGameID() {
        return gameID;
    }
}
