package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand {
    private final int gameID;
    private final String username;

    public LeaveCommand(String authToken, String username, int gameID) {
        super(authToken);
        this.username = username;
        this.gameID = gameID;
        this.commandType = CommandType.LEAVE;
    }

    public int getGameID() {
        return gameID;
    }

    public String getUsername() {
        return username;
    }
}
