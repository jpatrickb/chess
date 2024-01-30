package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor turn;
    private ChessBoard board;

    public ChessGame() {
        this.turn = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        // Return the team whose turn it is
        return this.turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        // Sets the team's turn
        this.turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // Verify that there is a piece at this position
        if (this.board.getPiece(startPosition) == null) {
            return null;
        } else {
            // Once we have verified there is a piece, we can get a Collection of the valid ChessMoves
            return this.board.getPiece(startPosition).pieceMoves(this.board, startPosition);
            // TODO: Still need to figure out how to check whether a move will leave the king in check
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Get the color for the team whose turn it is
        TeamColor turn = getTeamTurn();
        // Get the actual piece trying to move
        ChessPiece piece = this.board.getPiece(move.getStartPosition());
        // Get the color for the piece trying to move
        TeamColor color = piece.getTeamColor();
        Collection<ChessMove> possibleMoves = piece.pieceMoves(this.board, move.getStartPosition());
        if (color != turn) {
            // Check that the color of the piece moving is the same as the team's turn
            throw new InvalidMoveException("Not this team's turn to move");
        } else if (this.board.getPiece(move.getEndPosition()) != null) {
            if ((this.board.getPiece(move.getEndPosition()).getTeamColor() == color)) {
                // Ensure that the move doesn't land on a piece from the same team
                throw new InvalidMoveException("Can't move to a position with a piece of the same team");
            }
        } else if (!possibleMoves.contains(move)){
            // Ensures that the move is actually a possible move
            throw new InvalidMoveException("Not a possible move for that piece");
        } else if (this.isInCheck(color)) {
            // Ensures that this move doesn't leave the king in check
            throw new InvalidMoveException("This move will leave the king in check");
        } else {
            // Now that the move has been verified, it is made!
            this.board.addPiece(move.getEndPosition(), piece);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPiece piece;
        ChessPosition kingPosition = null;
        ChessPosition currPosition;
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                currPosition = new ChessPosition(row, col);
                if (this.board.getPiece(currPosition) != null) {
                    piece = this.board.getPiece(currPosition);
                    if (piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                        kingPosition = currPosition;
                        break;
                    }
                }
            }
        }
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                currPosition = new ChessPosition(row, col);
                if (this.board.getPiece(currPosition) != null) {

                    piece = this.board.getPiece(currPosition);
                    if (piece.getTeamColor() != teamColor) {
                        if (piece.pieceMoves(this.board, currPosition).contains(new ChessMove(currPosition, kingPosition, null))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // TODO: Figure out how to actually determine if the check is mated
        return isInCheck(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition myPosition;
        // Check that it is this team's turn
        if (this.getTeamTurn() == teamColor) {
            // Iterate through each position on the board
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    myPosition = new ChessPosition(row, col);
                    // If there are any valid moves, it's not stalemate
                    if (!this.validMoves(myPosition).isEmpty()) {
                        return false;
                    }
                }
            }
            // You've gotten all the way through? Well, it must be stalemate.
            return true;
        } else {
            // It's not our turn, so it can't possibly be stalemate
            return false;
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }
}
