package webSocketMessages.serverMessages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import webSocketMessages.userCommands.UserGameCommand;

import java.io.IOException;

public class MessageAdapter extends TypeAdapter<ServerMessage> {
    public ServerMessage deserialize(String message) {
        JsonObject obj = JsonParser.parseString(message).getAsJsonObject();
        ServerMessage.ServerMessageType type = ServerMessage.ServerMessageType.valueOf(obj.get("serverMessageType").getAsString());

        return switch (type) {
            case LOAD_GAME -> new Gson().fromJson(message, LoadGameMessage.class);
            case ERROR -> new Gson().fromJson(message, ErrorMessage.class);
            case NOTIFICATION -> new Gson().fromJson(message, NotificationMessage.class);
        };
    }
    @Override
    public void write(JsonWriter jsonWriter, ServerMessage serverMessage) throws IOException {

    }

    @Override
    public ServerMessage read(JsonReader jsonReader) throws IOException {
        return null;
    }
}
