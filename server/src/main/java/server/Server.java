package server;

import com.google.gson.Gson;
import model.User;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import server.Service.RegistrationService;
import spark.*;

import java.nio.file.Paths;

public class Server {

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        var webDir = Paths.get(Server.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "web");
        Spark.externalStaticFileLocation(webDir.toString());


        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);

        Spark.awaitInitialization();
        return Spark.port();
    }


    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }


    private Object registerUser(Request request, Response response) {
        var user = new Gson().fromJson(request.body(), User.class);
        user = RegistrationService.registerUser(user);
        return new Gson().toJson(user);
    }
}
