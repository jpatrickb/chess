package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand{
    private final int gameID;
    private final String username;

    public ResignCommand(String authToken, String username, int gameID) {
        super(authToken);
        this.username = username;
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }

    public String getUsername() {
        return username;
    }
}
