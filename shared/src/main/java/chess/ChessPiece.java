package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor color;
    private final PieceType type;
    public static final String WHITE_KING = " ♔ ";
    public static final String WHITE_QUEEN = " ♕ ";
    public static final String WHITE_BISHOP = " ♗ ";
    public static final String WHITE_KNIGHT = " ♘ ";
    public static final String WHITE_ROOK = " ♖ ";
    public static final String WHITE_PAWN = " ♙ ";
    public static final String BLACK_KING = " ♚ ";
    public static final String BLACK_QUEEN = " ♛ ";
    public static final String BLACK_BISHOP = " ♝ ";
    public static final String BLACK_KNIGHT = " ♞ ";
    public static final String BLACK_ROOK = " ♜ ";
    public static final String BLACK_PAWN = " ♟ ";


    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.color = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    private boolean inBounds(ChessPosition myPosition) {
        return (myPosition.getRow() >= 1 && myPosition.getRow() <= 8 && myPosition.getColumn() >= 1 && myPosition.getColumn() <= 8);
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
//        Initialize a collection of the moves
        Collection<ChessMove> validMoves = new HashSet<>(0);

//        Determine the type of piece to know how to move
        ChessPiece piece = board.getPiece(myPosition);

//        Store the row and column
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

//        PAWN
        if (piece.getPieceType() == PieceType.PAWN) {
            pawnMoves(piece, myPosition, board, validMoves, row, col);
        }

//        BISHOP
        if (piece.getPieceType() == PieceType.BISHOP || piece.getPieceType() == PieceType.QUEEN) {
            bishopOrQueen(board, myPosition, row, col, validMoves, piece);
        }

//        KING
        if (piece.getPieceType() == PieceType.KING) {
            kingMoves(board, myPosition, row, col, piece, validMoves);
        }

//        ROOK
        if (piece.getPieceType() == PieceType.ROOK || piece.getPieceType() == PieceType.QUEEN) {
            rookOrQueen(board, myPosition, row, col, validMoves, piece);
        }


//        KNIGHT
        if (piece.getPieceType() == PieceType.KNIGHT) {
            knightMoves(board, myPosition, row, col, piece, validMoves);

        }


        return validMoves;
    }

    private static void knightMoves(ChessBoard board, ChessPosition myPosition, int row, int col, ChessPiece piece, Collection<ChessMove> validMoves) {
        ChessPosition newPosition;
        int endRow;
        int endCol;
        int[] dirs = {-1, 1};

        for (var dirR : dirs) {
            for (var dirC : dirs) {
                endRow = row + 2 * dirR;
                endCol = col + dirC;

                for (var i = 0; i < 2; i++) {
                    if (!(endRow > 8 || endRow < 1 || endCol > 8 || endCol < 1)) {
                        newPosition = new ChessPosition(endRow, endCol);

                        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                            validMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }

                    endRow = row + dirC;
                    endCol = col + 2 * dirR;
                }

            }
        }
    }

    private static void rookOrQueen(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves, ChessPiece piece) {
        int[] dirs = {-1, 1};
        for (var dir : dirs) {
//                Check vertical direction
            for (int i = 1; i < 8; i++) {
                if (rookLookUp(board, myPosition, row, col, validMoves, piece, dir, i)) break;
            }

//                Check horizontal direction
            for (int i = 1; i < 8; i++) {
                if (rookLookSide(board, myPosition, row, col, validMoves, piece, dir, i)) break;
            }
        }
    }

    private static boolean rookLookSide(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves, ChessPiece piece, int dir, int i) {
        int endCol;
        ChessPosition newPosition;
        endCol = i * dir + col;

        newPosition = new ChessPosition(row, endCol);
        if (endCol > 8 || endCol < 1) {
            return true;
        }

        return addedPiece(board, myPosition, validMoves, piece, newPosition);
    }

    private static boolean rookLookUp(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves, ChessPiece piece, int dir, int i) {
        ChessPosition newPosition;
        int endRow;
        endRow = row + i * dir;
        if (endRow > 8 || endRow < 1) {
            return true;
        }
        newPosition = new ChessPosition(endRow, col);
        return addedPiece(board, myPosition, validMoves, piece, newPosition);
    }

    private static boolean addedPiece(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves, ChessPiece piece, ChessPosition newPosition) {
        if (board.getPiece(newPosition) == null) {
            validMoves.add(new ChessMove(myPosition, newPosition, null));
        } else if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
            validMoves.add(new ChessMove(myPosition, newPosition, null));
            return true;
        } else {
            return true;
        }
        return false;
    }

    private static void kingMoves(ChessBoard board, ChessPosition myPosition, int row, int col, ChessPiece piece, Collection<ChessMove> validMoves) {
        ChessPosition newPosition;
        int endCol;
        int endRow;
        int[] dirs = {-1, 0, 1};
        for (var up : dirs) {
            for (var side : dirs) {
                endRow = row + up;
                endCol = col + side;
                newPosition = new ChessPosition(endRow, endCol);

                if (endRow > 8 || endRow < 1 || endCol > 8 || endCol < 1) {
                    break;
                }

                if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
    }

    private static void bishopOrQueen(ChessBoard board, ChessPosition myPosition, int row, int col, Collection<ChessMove> validMoves, ChessPiece piece) {
        ChessPosition newPosition;
        int[] dirs = {-1, 1};
        for (var up : dirs) {
            for (var side : dirs) {
                for (int i = 1; i < 8; i++) {
                    newPosition = new ChessPosition(row + up * i, col + side * i);

//                        In bounds?
                    if (row + up * i > 8 || row + up * i < 1 || col + side * i > 8 || col + side * i < 1) {
                        break;
                    }

//                        Empty?
                    if (board.getPiece(newPosition) == null) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    }

//                        Can steal?
                    else if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                        break;
                    }

//                        Can't go there?
                    else {
                        break;
                    }
                }

            }
        }
    }


    private void pawnMoves(ChessPiece piece, ChessPosition myPosition, ChessBoard board, Collection<ChessMove> validMoves, int row, int col) {
        ChessPosition newPosition;
        PieceType[] types = {PieceType.QUEEN, PieceType.KNIGHT, PieceType.ROOK, PieceType.BISHOP};

//            Get the color to find direction of movement
        int dir = 1;
        int dist = 1;
        boolean prom = false;
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            dir = -1;
            if (myPosition.getRow() == 7) {
                dist = 2;
            } else if (myPosition.getRow() == 2) {
                prom = true;
            }
        } else {
            if (myPosition.getRow() == 2) {
                dist = 2;
            } else if (myPosition.getRow() == 7) {
                prom = true;
            }
        }

//            Check if the pawn can move forwards (must be empty)
        newPosition = new ChessPosition(row + dir, col);
        if (board.getPiece(newPosition) == null) {
            if (prom) {
                for (var type : types) {
                    validMoves.add(new ChessMove(myPosition, newPosition, type));
                }
            } else {
                validMoves.add(new ChessMove(myPosition, newPosition, null));
                if (dist == 2) {
                    newPosition = new ChessPosition(row + dist * dir, col);
                    if (board.getPiece(newPosition) == null) {
                        validMoves.add(new ChessMove(myPosition, newPosition, null));
                    }
                }
            }
        }

//            Check if the pawn can KO a piece diagonal to it on one direction
        int[] dirs = {1, -1};
        for (var dir2 : dirs) {
            newPosition = new ChessPosition(row + dir, col + dir2);
            if (inBounds(newPosition)) {
                if (board.getPiece(newPosition) != null) {
                    if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                        if (prom) {
                            for (var type : types) {
                                validMoves.add(new ChessMove(myPosition, newPosition, type));
                            }
                        } else {
                            validMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        if (color == ChessGame.TeamColor.WHITE) {
            return getString(WHITE_KING, WHITE_QUEEN, WHITE_BISHOP, WHITE_KNIGHT, WHITE_ROOK, WHITE_PAWN);
        } else {
            return getString(BLACK_KING, BLACK_QUEEN, BLACK_BISHOP, BLACK_KNIGHT, BLACK_ROOK, BLACK_PAWN);
        }
    }

    private String getString(String king, String queen, String bishop, String knight, String rook, String pawn) {
        return switch (type) {
            case KING -> king;
            case QUEEN -> queen;
            case BISHOP -> bishop;
            case KNIGHT -> knight;
            case ROOK -> rook;
            case PAWN -> pawn;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return color == that.color && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, type);
    }
}
