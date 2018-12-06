package com.igorternyuk.engine.moves;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.pieces.Piece;
import com.igorternyuk.engine.pieces.Rook;

import java.util.Collection;
import java.util.Objects;

import static com.igorternyuk.engine.pieces.Piece.NULL_PIECE;

/**
 * Created by igor on 01.12.17.
 */

public abstract class Move {
    protected final Board board;
    protected final Piece movedPiece;
    protected final Location destination;
    private final boolean isFirstMove;
    public static final Move NULL_MOVE;

    static {
        NULL_MOVE = new NullMove();
    }

    protected Move(final Board board, final Piece movingPiece, final Location destination) {
        this.board = board;
        this.movedPiece = movingPiece;
        this.destination = destination;
        this.isFirstMove = this.movedPiece.isFirstMove();
    }

    protected Move(final Board board, final Location destination) {
        this.board = board;
        this.destination = destination;
        this.movedPiece = NULL_PIECE;
        this.isFirstMove = false;
    }

    public Location getDestination() {
        return this.destination;
    }

    public Piece getMovedPiece(){
        return this.movedPiece;
    }

    public abstract boolean isCapturingMove();

    public abstract boolean isCastlingMove();

    public abstract boolean isKingSideCastling();

    public abstract boolean isQueenSideCastling();

    public abstract boolean isPawnPromotionMove();

    public abstract boolean isEnPassantCapture();

    public boolean isPawnMove() { return this.movedPiece.getPieceType().isPawn(); }

    public Piece getCapturedPiece(){
        return null;
    }

    public abstract Board execute();

    public Board getBoard() {
        return this.board;
    }


    public static final class NullMove extends Move {
        private NullMove() {
            super(null, BoardUtils.NULL_LOCATION);
        }

        @Override
        public boolean isCapturingMove() {
            return false;
        }

        @Override
        public boolean isCastlingMove() {
            return false;
        }

        @Override
        public boolean isKingSideCastling() {
            return false;
        }

        @Override
        public boolean isQueenSideCastling() {
            return false;
        }

        @Override
        public boolean isPawnPromotionMove() {
            return false;
        }

        @Override
        public boolean isEnPassantCapture() {
            return false;
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Could not execute the null move");
        }
    }

    public static class MoveFactory {

        public static Move createMove(final Board board, final String from, final String to) {
            return createMove(board, BoardUtils.getLocation(from), BoardUtils.getLocation(to));
        }

        public static Move createMove(final Board board, final Location currentLocation, final Location destination) {
            final Collection<Move> legalMoves = board.getCurrentPlayer().getLegalMoves();
            for(final Move move: legalMoves){
                if (move.getMovedPiece().getLocation().equals(currentLocation) &&
                   move.getDestination().equals(destination)){
                    return move;
                }
            }
            return NULL_MOVE;
        }

        public static Move createPawnPromotionMove(final Board board, final Location currentLocation,
                                                   final Location destination, final Piece promotedPiece) {
             final Collection<Move> legalMoves = board.getCurrentPlayer().getLegalMoves();
             for(final Move move: legalMoves){
                 if(move.isPawnPromotionMove()) {
                     final PawnPromotion pawnPromotion = (PawnPromotion)move;
                     if (pawnPromotion.getMovedPiece().getLocation().equals(currentLocation) &&
                         pawnPromotion.getDestination().equals(destination) &&
                         pawnPromotion.getPromotedPiece().equals(promotedPiece)){
                         return pawnPromotion;
                     }
                 }
             }
             return NULL_MOVE;
        }

        public static Move createRandomFisherChessCastling(final Board board, final Location currentLocation,
                                                           final Location destination, final Rook castlingRook,
                                                           final Location castlingRookTargetLocation) {
            final Collection<Move> legalMoves = board.getCurrentPlayer().getLegalMoves();
            for(final Move move: legalMoves){
                if(move.isCastlingMove()){
                    Castling castling = (Castling)move;
                    if (castling.getMovedPiece().getLocation().equals(currentLocation) &&
                       castling.getDestination().equals(destination) &&
                       castling.getCastledRook().equals(castlingRook) &&
                            castling.getCastledRookEndLocation().equals(castlingRookTargetLocation)) {
                       return castling;
                    }
                }
            }
            return NULL_MOVE;
        }
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof Move)) return false;

        final Move otherMove = (Move) other;
        return Objects.equals(this.getMovedPiece(), otherMove.getMovedPiece()) &&
                Objects.equals(this.getDestination(), otherMove.getDestination());
    }

    @Override
    public int hashCode() {
        final int hashPrime = 31;
        int result = getMovedPiece().hashCode();
        result = hashPrime * result + getDestination().hashCode();
        return result;
    }
}
