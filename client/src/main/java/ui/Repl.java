package ui;


import webSocketMessages.serverMessages.ServerMessage;
import websocket.NotificationHandler;

import java.util.Scanner;

import static ui.EscapeSequences.*;

/**
 * The Repl class represents a Read-Eval-Print Loop (REPL) interface for interacting with the chess game.
 */
public class Repl implements NotificationHandler {
    private final Client client;

    /**
     * Constructs a Repl object with the specified server URL.
     *
     * @param serverUrl The URL of the server.
     */
    public Repl(String serverUrl) {
        client = new Client(serverUrl, this);
    }

    /**
     * Starts the REPL interface.
     */
    public void run() {
        System.out.print(SET_BG_COLOR_WHITE);
        System.out.println("👑 Welcome to 240 Chess. Type Help to get started. 👑");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                System.out.print(e.getMessage());
            }
        }

        System.out.println();
    }

    /**
     * Prints the REPL prompt.
     */
    private void printPrompt() {
        System.out.print(SET_TEXT_COLOR_BLACK + "\n" + "[" + Client.state + "] >>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void notify(ServerMessage message) {
        System.out.println(SET_TEXT_COLOR_RED);
    }
}
