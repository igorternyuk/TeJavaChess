package com.techess.engine.board;

import com.techess.engine.pieces.Pawn;
import com.techess.engine.pieces.Piece;
import com.techess.engine.pieces.Rook;
import static com.techess.engine.board.Board.Builder;
import static com.techess.engine.board.Board.fileMap;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by igor on 01.12.17.
 */

public abstract class Move {
    protected final Board board;
    protected final Piece movedPiece;
    protected final Position destination;
    private static final Move NULL_MOVE = new NullMove();

    private Move(final Board board, final Piece movingPiece, final Position destination) {
        this.board = board;
        this.movedPiece = movingPiece;
        this.destination = destination;
    }

    public Position getDestination(){
        return this.destination;
    }

    public Piece getMovedPiece(){
        return this.movedPiece;
    }

    public boolean isCapturingMove(){
        return false;
    }

    public boolean isCastlingMove(){
        return false;
    }

    public Piece getCapturedPiece(){
        return null;
    }

    public abstract Board execute();

    public static final class RegularMove extends Move {

        public RegularMove(final Board board, final Piece movedPiece, final Position destinationCoordinate) {
            super(board, movedPiece, destinationCoordinate);
        }

        @Override
        public Board execute() {
            final Builder builder = new Board.Builder();
            this.board.getCurrentPlayer().getActivePieces().stream().filter(piece -> !this.movedPiece.equals(piece))
                    .forEach(piece -> builder.setPiece(piece));
            this.board.getCurrentPlayer().getOpponent().getActivePieces().forEach(piece -> builder.setPiece(piece));
            builder.setPiece(this.movedPiece.move(this));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponentAlliance());
            return builder.build();
        }
    }

    public static class CapturingMove extends Move{
        private Piece capturedPiece;
        public CapturingMove(final Board board, final Piece movedPiece, final Position destination,
                             final Piece capturedPiece) {
            super(board, movedPiece, destination);
            this.capturedPiece = capturedPiece;
        }

        @Override
        public Board execute() {

            final Builder builder = new Builder();
            this.board.getCurrentPlayer().getActivePieces().stream().filter(piece -> !this.movedPiece.equals(piece))
                    .forEach(piece -> builder.setPiece(piece));
            this.board.getCurrentPlayer().getOpponentActivePieces().stream()
                    .filter(piece -> !this.capturedPiece.equals(piece)).forEach(piece -> builder.setPiece(piece));
            builder.setPiece(this.movedPiece.move(this));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponentAlliance());
            return builder.build();
        }

        @Override
        public boolean isCapturingMove(){
            return true;
        }

        @Override
        public Piece getCapturedPiece(){
            return this.capturedPiece;
        }

        @Override
        public int hashCode(){
            return this.capturedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            if(this == other) return true;
            if(other == null || !(other instanceof CapturingMove)) return false;
            CapturingMove otherMove = (CapturingMove)other;
            return super.equals(other) && Objects.equals(this.getCapturedPiece(), otherMove.getCapturedPiece());        }
    }

    public static final class PawnMove extends Move{
        private Piece capturedPiece;
        public PawnMove(final Board board, final Piece movedPiece, final Position destination,
                        final Piece capturedPiece) {
            super(board, movedPiece, destination);
            this.capturedPiece = capturedPiece;
        }

        @Override
        public Board execute() {
            return null;
        }
    }

    public static class PawnCapturingMove extends CapturingMove{
        private Piece capturedPiece;
        public PawnCapturingMove(final Board board, final Piece movedPiece, final Position destination,
                                 final Piece capturedPiece) {
            super(board, movedPiece, destination, capturedPiece);
            this.capturedPiece = capturedPiece;
        }

        @Override
        public Board execute() {
            return null;
        }
    }

    public static class PawnJump extends Move {
        public PawnJump(final Board board, final Piece movedPiece, final Position destination) {
            super(board, movedPiece, destination);
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for(final Piece piece: this.board.getCurrentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece))
                    builder.setPiece(piece);
            }

            for(final Piece piece: this.board.getCurrentPlayer().getOpponentActivePieces()){
                builder.setPiece(piece);
            }

            Pawn jumpedPawn = (Pawn) this.movedPiece.move(this);
            builder.setEnPassantPawn(jumpedPawn);
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponentAlliance());
            return builder.build();
        }
    }

    public static final class PawnEnPassantCapture extends PawnCapturingMove {
        private Piece capturedPiece;
        public PawnEnPassantCapture(final Board board, final Piece movedPiece, final Position destination,
                                    final Piece capturedPiece) {
            super(board, movedPiece, destination, capturedPiece);
            this.capturedPiece = capturedPiece;
        }

        @Override
        public Board execute() {
            return null;
        }
    }

    private static class Castling extends Move{
        protected final Rook castledRook;
        protected final Position castledRookStartPosition;
        protected final Position castledRookEndPosition;
        public Castling(final Board board, final Piece movedPiece, final Position destination, final Rook castledRook,
                        final Position castledRookStartPosition, final Position castledRookEndPosition) {
            super(board, movedPiece, destination);
            this.castledRook = castledRook;
            this.castledRookStartPosition = castledRookStartPosition;
            this.castledRookEndPosition = castledRookEndPosition;
        }

        public Rook getCastledRook() {
            return this.castledRook;
        }

        public Position getCastledRookStartPosition() {
            return this.castledRookStartPosition;
        }

        public Position getCastledRookEndPosition() {
            return this.castledRookEndPosition;
        }

        @Override
        public boolean isCastlingMove(){
            return true;
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            //Current player's pieces except castled rook and king
            this.board.getCurrentPlayer().getActivePieces().stream().filter(piece -> {
                return !piece.equals(this.movedPiece) && !piece.equals(this.castledRook);
            }).forEach(piece -> builder.setPiece(piece));
            //Opponent's pieces
            this.board.getCurrentPlayer().getOpponentActivePieces().forEach( piece -> builder.setPiece(piece));
            builder.setPiece(this.movedPiece.move(this));
            builder.setPiece(Rook.createRook(this.castledRookEndPosition, this.board.getCurrentPlayer().getAlliance(),
                    false));
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponentAlliance());
            return builder.build();
        }
    }

    public static final class KingsSideCastling extends Castling {
        public KingsSideCastling(final Board board, final Piece movedPiece, final Position kingsDestination,
                                 final Rook castleRook, final Position castleRookStartPosition,
                                 final Position castleRookEndPosition) {
            super(board, movedPiece, kingsDestination, castleRook, castleRookStartPosition, castleRookEndPosition);
        }

        @Override
        public Board execute() {
            return super.execute();
        }

        @Override
        public String toString(){
            return "0-0";
        }
    }

    public static final class QueensSideCastling extends Castling {
        public QueensSideCastling(final Board board, final Piece movedPiece, final Position kingsDestination,
                                  final Rook castleRook, final Position castleRookStartPosition,
                                  final Position castleRookEndPosition) {
            super(board, movedPiece, kingsDestination, castleRook, castleRookStartPosition, castleRookEndPosition);
        }

        @Override
        public Board execute() {
            return super.execute();
        }

        @Override
        public String toString(){
            return "0-0-0";
        }
    }

    public static final class NullMove extends Move {
        public NullMove() {
            super(null, null, Board.NULL_POSITION);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Could not execute the null move");
        }
    }

    public static class MoveFactory {
         public static Move createMove(final Board board, final Position currentPosition, final Position destination){
            final Collection<Move> legalMoves = board.getCurrentPlayer().getLegalMoves();
            for(final Move move: legalMoves){
                if(move.getMovedPiece().getPosition().equals(currentPosition) &&
                   move.getDestination().equals(destination)){
                    return move;
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
