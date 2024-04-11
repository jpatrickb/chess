package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static ui.EscapeSequences.*;

/**
 * The BoardDisplay class provides methods for displaying the chess board and pieces on the console.
 */
public class BoardDisplay {

    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int LINE_WIDTH_IN_CHARS = 1;
    private static final String[] HEADERS = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
    private static final String[] WHITE_PIECES = {WHITE_ROOK, WHITE_KNIGHT, WHITE_BISHOP, WHITE_QUEEN, WHITE_KING, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK};
    private static final String[] BLACK_PIECES = {BLACK_ROOK, BLACK_KNIGHT, BLACK_BISHOP, BLACK_QUEEN, BLACK_KING, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK};


    /**
     * Displays the chess board with pieces on the console.
     *
     * @param game ChessGame object to print game from
     * @param color The color whose perspective to print the game from
     */
    public static void main(ChessGame game, ChessGame.TeamColor color) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        var board = game.getBoard();

        out.println(ERASE_SCREEN);

        if (color == ChessGame.TeamColor.WHITE) {
            drawHeadersForward(out);

            drawChessBoardForward(out, board);

            drawHeadersForward(out);
        } else {
            drawHeadersBackward(out);

            drawChessBoardBackward(out, board);

            drawHeadersBackward(out);
        }

        out.print(SET_BG_COLOR_WHITE);

        out.print(SET_TEXT_COLOR_WHITE);
    }

    /**
     * Draws the column headers for the chess board in backward order.
     *
     * @param out The output stream to print to.
     */
    private static void drawHeadersBackward(PrintStream out) {
        setBlack(out);

        out.print(SET_BG_COLOR_LIGHT_GREY);

        out.print(EMPTY.repeat(LINE_WIDTH_IN_CHARS));
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            printHeaderText(out, HEADERS[BOARD_SIZE_IN_SQUARES - boardCol - 1]);

        }
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(EMPTY.repeat(LINE_WIDTH_IN_CHARS));

        out.println(SET_BG_COLOR_WHITE);
    }

    /**
     * Draws the column headers for the chess board in forward order.
     *
     * @param out The output stream to print to.
     */
    private static void drawHeadersForward(PrintStream out) {

        setBlack(out);

        out.print(SET_BG_COLOR_LIGHT_GREY);

        out.print(EMPTY.repeat(LINE_WIDTH_IN_CHARS));
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            printHeaderText(out, HEADERS[boardCol]);

        }
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(EMPTY.repeat(LINE_WIDTH_IN_CHARS));

        out.println(SET_BG_COLOR_WHITE);

    }

    /**
     * Prints a header text on the console.
     *
     * @param out    The output stream to print to.
     * @param player The header text to print.
     */
    private static void printHeaderText(PrintStream out, String player) {
        out.print(SET_BG_COLOR_LIGHT_GREY);

        out.print(player);

        setBlack(out);
    }

    /**
     * Draws the chess board with pieces in forward order.
     *
     * @param out   The output stream to print to.
     * @param board The board to print
     */
    private static void drawChessBoardForward(PrintStream out, ChessBoard board) {
        for (int row = 0; row < 8; row++) {
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(" ");
            out.print(BOARD_SIZE_IN_SQUARES - row);
            out.print(" ");
            for (int col = 0; col < 8; col++) {
                if ((col + row) % 2 == 0) {
                    out.print(SET_BG_COLOR_WHITE);
                } else {
                    out.print(SET_BG_COLOR_BLACK);
                }

                ChessPiece piece = board.getPiece(new ChessPosition(8 - row, col + 1));
                if (piece == null) {
                    out.print(EMPTY.repeat(LINE_WIDTH_IN_CHARS));
                } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    out.print(SET_TEXT_COLOR_BLUE);
                    out.print(piece);
                } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    out.print(SET_TEXT_COLOR_RED);
                    out.print(piece);
                }
            }
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(" ");
            out.print(BOARD_SIZE_IN_SQUARES - row);
            out.print(" ");

            out.println(SET_BG_COLOR_WHITE);
        }
    }

    /**
     * Draws the chess board with pieces in backward order.
     *
     * @param out   The output stream to print to.
     * @param board The board to print
     */
    private static void drawChessBoardBackward(PrintStream out, ChessBoard board) {
        for (int row = 0; row < 8; row++) {
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(" ");
            out.print(row + 1);
            out.print(" ");
            for (int col = 0; col < 8; col++) {
                if ((col + row) % 2 == 0) {
                    out.print(SET_BG_COLOR_WHITE);
                } else {
                    out.print(SET_BG_COLOR_BLACK);
                }

                ChessPiece piece = board.getPiece(new ChessPosition(row + 1, 8 - col));
                if (piece == null) {
                    out.print(EMPTY.repeat(LINE_WIDTH_IN_CHARS));
                } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    out.print(SET_TEXT_COLOR_BLUE);
                    out.print(piece);
                } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                    out.print(SET_TEXT_COLOR_RED);
                    out.print(piece);
                }
            }
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(" ");
            out.print(row + 1);
            out.print(" ");

            out.print(SET_BG_COLOR_WHITE);
            out.println();
        }
    }

    /**
     * Sets the text and background color to black on the console.
     *
     * @param out The output stream to print to.
     */
    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }
}
