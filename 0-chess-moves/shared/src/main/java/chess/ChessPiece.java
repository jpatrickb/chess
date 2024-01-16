package chess;

import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor color;
    private PieceType type;

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

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
//        Initialize a collection of the moves
        Collection<ChessMove> valid_moves = null;

//        Determine the type of piece to know how to move
        ChessPiece piece = board.getPiece(myPosition);

//        Store the row and column
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

//        PAWN
        if (piece.getPieceType() == PieceType.PAWN) {

//            Get the color to find direction of movement
            int dir = 1;
            if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                dir = -1;
            }

//            Check if the pawn can move forwards (must be empty)
            ChessPosition newPosition = new ChessPosition(row+dir, col);
            if (board.getPiece(newPosition) == null) {
                valid_moves.add(new ChessMove(myPosition, newPosition, null));
            }

//            Check if the pawn can KO a piece diagonal to it on one direction
            newPosition = new ChessPosition(row+dir, col+1);
            if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                valid_moves.add(new ChessMove(myPosition, newPosition, null));
            }

//            Check if the pawn can KO a piece diagonal to it on the other direction
            newPosition = new ChessPosition(row+dir, col-1);
            if (board.getPiece(newPosition).getTeamColor() != piece.getTeamColor()) {
                valid_moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }

//        BISHOP
        if (piece.getPieceType() == PieceType.BISHOP) {
            int endRow = row;
            int endCol = col;



        }

        return valid_moves;
    }
}
