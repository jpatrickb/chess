package chess;

import java.util.Collection;
import java.util.HashSet;

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
        // White starts
        this.turn = TeamColor.WHITE;
        // Instantiate the board
        this.board = new ChessBoard();
        // Set it up correctly
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
        BLACK,
        NONE
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> allMoves = new HashSet<>(0);
        // Verify that there is a piece at this position
        if (this.board.getPiece(startPosition) == null) {
            return allMoves;
        } else {
            // Once we have verified there is a piece, we can get a Collection of the valid ChessMoves
            allMoves = this.board.getPiece(startPosition).pieceMoves(this.board, startPosition);
            Collection<ChessMove> onlyValid = new HashSet<>(0);
            for (var move : allMoves) {
                // Find the starting and ending pieces
                ChessPiece piece = this.board.getPiece(move.getStartPosition());
                ChessPiece targetPiece = this.board.getPiece(move.getEndPosition());

                // Try making the move
                this.board.addPiece(move.getEndPosition(), piece);
                this.board.removePiece(move.getStartPosition());

                // Check if the king will be in check; if not, adds to valid moves
                if (!this.isInCheck(piece.getTeamColor())) {
                    onlyValid.add(move);
                }

                // Now resets the board
                this.board.addPiece(move.getStartPosition(), piece);
                this.board.addPiece(move.getEndPosition(), targetPiece);
            }
            return onlyValid;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
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
        } if (this.board.getPiece(move.getEndPosition()) != null) {
            if ((this.board.getPiece(move.getEndPosition()).getTeamColor() == color)) {
                // Ensure that the move doesn't land on a piece from the same team
                throw new InvalidMoveException("Can't move to a position with a piece of the same team");
            }
        } if (!possibleMoves.contains(move)){
            // Ensures that the move is actually a possible move
            throw new InvalidMoveException("Not a possible move for that piece");
        } else {
            // Now that the move has been verified, it is made!
            ChessPiece newPiece = piece;
            if (move.getPromotionPiece() != null) {
                newPiece = new ChessPiece(color, move.getPromotionPiece());
            }
            ChessPiece takenPiece = this.board.getPiece(move.getEndPosition());
            this.board.addPiece(move.getEndPosition(), newPiece);
            this.board.removePiece(move.getStartPosition());

            // Ensures that this move doesn't leave the king in check
            if (this.isInCheck(color)) {
                this.board.addPiece(move.getEndPosition(), takenPiece);
                this.board.addPiece(move.getStartPosition(), piece);
                throw new InvalidMoveException("This move will leave the king in check");
            }

            // Set the new team's turn
            if (this.getTeamTurn() == TeamColor.WHITE) {
                this.setTeamTurn(TeamColor.BLACK);
            } else {
                this.setTeamTurn(TeamColor.WHITE);
            }

            if (isInCheckmate(getTeamTurn())) {
                setTeamTurn(TeamColor.NONE);
                throw new InvalidMoveException("Checkmate!");
            }
            if (isInStalemate(getTeamTurn())) {
                setTeamTurn(TeamColor.NONE);
                throw new InvalidMoveException("Stalemate!");
            }
            if (isInCheck(getTeamTurn())) {
                throw new InvalidMoveException("Check!");
            }
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        ChessPiece.PieceType[] types = {ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP, null};
        ChessPiece piece;
        ChessPosition kingPosition = null;
        ChessPosition currPosition;
        // First we need to find the king...
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                currPosition = new ChessPosition(row, col);
                if (this.board.getPiece(currPosition) != null) {
                    piece = this.board.getPiece(currPosition);
                    if (piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                        // Found the king! Saving its position...
                        kingPosition = currPosition;
                        break;
                    }
                }
            }
        }
        // Now we check the moves for all other pieces
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                currPosition = new ChessPosition(row, col);
                // Clearly null pieces can't have any valid moves
                if (this.board.getPiece(currPosition) != null) {

                    piece = this.board.getPiece(currPosition);
                    // Also don't care if the piece is the same color, since it can't attack its own king
                    if (piece.getTeamColor() != teamColor) {
                        // Check whether this piece has a valid move from its position to the king's position
                        for (var type : types) {
                            if (piece.pieceMoves(this.board, currPosition).contains(new ChessMove(currPosition, kingPosition, type))) {
                                // All we need to find is one piece to know the king is in check...
                                return true;
                            }
                        }
                    }
                }
            }
        }
        // Looks like nothing was found, king is safe!
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // If it's not in check, it obviously can't be in checkmate
        if (!isInCheck(teamColor)) {
            return false;
        }

        // Iterate through all possible moves, determine if they leave the king in check
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                // Initialize the starting position
                ChessPosition position = new ChessPosition(row, col);

                // Ensure it's not an empty position
                if (this.board.getPiece(position) != null) {
                    // Ensure the piece is on the team whose turn it is
                    if (this.board.getPiece(position).getTeamColor() == teamColor) {
                        // If the valid moves isn't empty, then there are possible moves (not checkmate)
                        if (!this.validMoves(position).isEmpty()) {
                            return false;
                        }
                    }
                }
            }
        }

        // If none had valid moves that wouldn't leave the king in check, then game over
        return true;
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
                    // Ensure not an empty spot
                    if (this.board.getPiece(myPosition) != null) {
                        // Ensure the piece is the right color
                        if (this.board.getPiece(myPosition).getTeamColor() == teamColor) {
                            // If there are any valid moves, it's not stalemate
                            if (!this.validMoves(myPosition).isEmpty()) {
                                return false;
                            }
                        }
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
