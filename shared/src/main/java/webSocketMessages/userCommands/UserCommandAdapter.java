package webSocketMessages.userCommands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class UserCommandAdapter extends TypeAdapter<UserGameCommand> {
    public UserGameCommand deserialize(String message) throws IOException {
        JsonObject obj = JsonParser.parseString(message).getAsJsonObject();
        UserGameCommand.CommandType type = UserGameCommand.CommandType.valueOf(obj.get("commandType").getAsString());

        return switch (type) {
            case JOIN_PLAYER -> new Gson().fromJson(message, JoinPlayerCommand.class);
            case JOIN_OBSERVER -> new Gson().fromJson(message, JoinObserverCommand.class);
            case MAKE_MOVE -> new Gson().fromJson(message, MakeMoveCommand.class);
            case LEAVE -> new Gson().fromJson(message, LeaveCommand.class);
            case RESIGN -> new Gson().fromJson(message, ResignCommand.class);
        };
    }

    @Override
    public void write(JsonWriter jsonWriter, UserGameCommand userGameCommand) throws IOException {

    }

    @Override
    public UserGameCommand read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
