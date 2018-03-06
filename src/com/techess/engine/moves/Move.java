package com.techess.engine.moves;

import com.techess.engine.board.Board;
import com.techess.engine.board.Position;
import com.techess.engine.pieces.Pawn;
import com.techess.engine.pieces.Piece;
import com.techess.engine.pieces.Rook;

import static com.techess.engine.board.Board.Builder;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by igor on 01.12.17.
 */

public abstract class Move {
    protected final Board board;
    protected final Piece movedPiece;
    protected final Position destination;
    protected final boolean isFirstMove;
    private static final Move NULL_MOVE = new NullMove();

    private Move(final Board board, final Piece movingPiece, final Position destination) {
        this.board = board;
        this.movedPiece = movingPiece;
        this.destination = destination;
        this.isFirstMove = this.movedPiece.isFirstMove();
    }

    private Move(final Board board, final Position destination){
        this.board = board;
        this.destination = destination;
        this.movedPiece = null;
        this.isFirstMove = false;
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

    public boolean isPawnPromotionMove() { return false; }

    public Piece getCapturedPiece(){
        return null;
    }

    public abstract Board execute();

    public Board getBoard() {
        return this.board;
    }

    public static class RegularMove extends Move {

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
            builder.setGameType(this.board.getGameType());
            builder.setKingsRookStartCoordinateX(this.board.getKingsRookStartCoordinateX());
            builder.setQueensRookStartCoordinateX(this.board.getQueensRookStartCoordinateX());
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponentAlliance());
            return builder.build();
        }

        @Override
        public boolean equals(final Object other){
            return this == other || ((other instanceof RegularMove) && super.equals(other));
        }

        @Override
        public String toString(){
            return movedPiece.toString() + Board.getAlgebraicNotationFromPosition(destination);
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
            builder.setGameType(this.board.getGameType());
            builder.setKingsRookStartCoordinateX(this.board.getKingsRookStartCoordinateX());
            builder.setQueensRookStartCoordinateX(this.board.getQueensRookStartCoordinateX());
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
            return super.equals(other) && Objects.equals(this.getCapturedPiece(), otherMove.getCapturedPiece());
        }

        @Override
        public String toString(){
            return movedPiece.toString() + "x" + Board.getAlgebraicNotationFromPosition(destination);
        }
    }

    public static class PieceCapturingMove extends CapturingMove{
        public PieceCapturingMove(final Board board, final Piece movedPiece, final Position destination,
                                 final Piece capturedPiece) {
            super(board, movedPiece, destination, capturedPiece);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || ((other instanceof PieceCapturingMove) && super.equals(other));
        }

        @Override
        public String toString(){
            return movedPiece.toString() + "x" + Board.getAlgebraicNotationFromPosition(destination);
        }
    }

    public static class PawnMove extends RegularMove{
        public PawnMove(final Board board, final Piece movedPiece, final Position destination) {
            super(board, movedPiece, destination);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || ((other instanceof PawnMove) && super.equals(other));
        }

        @Override
        public String toString(){
            return movedPiece.toString() + Board.getAlgebraicNotationForCoordinateY(destination.getY());
        }
    }

    public static class PawnCapturingMove extends CapturingMove{
        public PawnCapturingMove(final Board board, final Piece movedPiece, final Position destination,
                                 final Piece capturedPiece) {
            super(board, movedPiece, destination, capturedPiece);
        }

        public String toString(){
            return movedPiece.toString() + "x" + Board.getAlgebraicNotationFromPosition(destination);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || ((other instanceof PawnCapturingMove) && super.equals(other));
        }
    }

    public static class PawnJump extends PawnMove {
        public PawnJump(final Board board, final Piece movedPiece, final Position destination) {
            super(board, movedPiece, destination);
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            this.board.getCurrentPlayer().getActivePieces().stream().filter(piece -> !this.movedPiece.equals(piece))
                    .forEach(piece -> builder.setPiece(piece));
            this.board.getCurrentPlayer().getOpponentActivePieces().forEach(piece -> builder.setPiece(piece));
            Pawn jumpedPawn = (Pawn) this.movedPiece.move(this);
            System.out.println("Setting en passant pawn " +
                    Board.getAlgebraicNotationFromPosition(jumpedPawn.getPosition()));
            builder.setEnPassantPawn(jumpedPawn);
            builder.setPiece(jumpedPawn);
            builder.setGameType(this.board.getGameType());
            builder.setKingsRookStartCoordinateX(this.board.getKingsRookStartCoordinateX());
            builder.setQueensRookStartCoordinateX(this.board.getQueensRookStartCoordinateX());
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponentAlliance());
            return builder.build();
        }

        @Override
        public boolean equals(final Object other){
            return this == other || ((other instanceof PawnJump) && super.equals(other));
        }
    }

    public static class PawnPromotion extends PawnMove{
        private final PawnMove pawnMove;
        private Pawn promotedPawn;
        private Piece promotedPiece;

        public PawnPromotion(final PawnMove decoratedPawnMove, final Piece promotedPiece) {
            super(decoratedPawnMove.getBoard(), decoratedPawnMove.getMovedPiece(), decoratedPawnMove.getDestination());
            this.pawnMove = decoratedPawnMove;
            this.promotedPawn = (Pawn)decoratedPawnMove.getMovedPiece();
            this.promotedPiece = promotedPiece;
        }

        public PawnMove getPawnMove() {
            return pawnMove;
        }

        public Pawn getPromotedPawn() {
            return promotedPawn;
        }

        public Piece getPromotedPiece() {
            return promotedPiece;
        }

        @Override
        public boolean isPawnPromotionMove() { return true; }

        @Override
        public Board execute() {
            final Board promotedPawnBoard = pawnMove.execute();
            final Builder builder = new Board.Builder();
            promotedPawnBoard.getCurrentPlayer().getActivePieces().stream()
                    .filter(piece -> !this.promotedPawn.equals(piece)).forEach(piece -> builder.setPiece(piece));
            promotedPawnBoard.getCurrentPlayer().getOpponent().getActivePieces()
                    .forEach(piece -> builder.setPiece(piece));
            builder.setPiece(promotedPiece);
            builder.setGameType(this.board.getGameType());
            builder.setKingsRookStartCoordinateX(this.board.getKingsRookStartCoordinateX());
            builder.setQueensRookStartCoordinateX(this.board.getQueensRookStartCoordinateX());
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponentAlliance());
            return builder.build();
        }

        @Override
        public int hashCode(){
            final int prime = 37;
            int result = super.hashCode();
            result += prime * this.pawnMove.hashCode();
            result += prime * this.promotedPawn.hashCode();
            result += prime * this.promotedPiece.hashCode();
            return result;
        }

        @Override
        public boolean equals(final Object other){
            if(this == other) return true;
            if(other == null || !(other instanceof PawnPromotion)) return false;
            PawnPromotion otherPawnPromotion = (PawnPromotion)other;
            return Objects.equals(this.pawnMove, otherPawnPromotion.getPawnMove()) &&
                   Objects.equals(this.promotedPawn, otherPawnPromotion.getPromotedPawn()) &&
                   Objects.equals(this.promotedPiece, otherPawnPromotion.getPromotedPiece()) &&
                   super.equals(other);
        }

        @Override
        public String toString(){
            return pawnMove.toString() + promotedPiece.getPieceType().getName().toUpperCase();
        }
    }

    public static final class PawnEnPassantCapture extends PawnCapturingMove {
        public PawnEnPassantCapture(final Board board, final Piece movedPiece, final Position destination,
                                    final Piece capturedPiece) {
            super(board, movedPiece, destination, capturedPiece);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || ((other instanceof PawnEnPassantCapture) && super.equals(other));
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
            builder.setGameType(this.board.getGameType());
            builder.setKingsRookStartCoordinateX(this.board.getKingsRookStartCoordinateX());
            builder.setQueensRookStartCoordinateX(this.board.getQueensRookStartCoordinateX());
            builder.setMoveMaker(this.board.getCurrentPlayer().getOpponentAlliance());
            return builder.build();
        }

        @Override
        public int hashCode(){
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + this.castledRook.hashCode();
            result = prime * result + this.castledRookEndPosition.hashCode();
            return result;
        }

        @Override
        public boolean equals(Object other) {
            if(this == other) return true;
            if(other == null || !(other instanceof Castling)) return false;
            final Castling otherCastling = (Castling)other;
            return Objects.equals(this.castledRook, otherCastling.getCastledRook()) &&
                   Objects.equals(this.castledRookEndPosition, ((Castling) other).getCastledRookEndPosition()) &&
                   super.equals(other);
        }
    }

    public static final class KingsSideCastling extends Castling {
        public KingsSideCastling(final Board board, final Piece movedPiece, final Position kingsDestination,
                                 final Rook castleRook, final Position castleRookStartPosition,
                                 final Position castleRookEndPosition) {
            super(board, movedPiece, kingsDestination, castleRook, castleRookStartPosition, castleRookEndPosition);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || ((other instanceof KingsSideCastling) && super.equals(other));
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
        public boolean equals(final Object other){
            return this == other || ((other instanceof QueensSideCastling) && super.equals(other));
        }

        @Override
        public String toString(){
            return "0-0-0";
        }
    }

    public static final class NullMove extends Move {
        public NullMove() {
            super(null, Board.NULL_POSITION);
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

        public static Move createPawnPromotionMove(final Board board, final Position currentPosition,
                                                   final Position destination, final Piece promotedPiece){
             final Collection<Move> legalMoves = board.getCurrentPlayer().getLegalMoves();
             for(final Move move: legalMoves){
                 if(move.isPawnPromotionMove()) {
                     final PawnPromotion pawnPromotion = (PawnPromotion)move;
                     if (pawnPromotion.getMovedPiece().getPosition().equals(currentPosition) &&
                         pawnPromotion.getDestination().equals(destination) &&
                         pawnPromotion.getPromotedPiece().equals(promotedPiece)){
                         return pawnPromotion;
                     }
                 }
             }
             return NULL_MOVE;
        }

        public static Move createRandomFisherChessCastling(final Board board, final Position currentPosition,
                                                           final Position destination, final Rook castlingRook,
                                                           final Position castlingRookTargetPosition){
            final Collection<Move> legalMoves = board.getCurrentPlayer().getLegalMoves();
            for(final Move move: legalMoves){
                if(move.isCastlingMove()){
                    Castling castling = (Castling)move;
                    if(castling.getMovedPiece().getPosition().equals(currentPosition) &&
                       castling.getDestination().equals(destination) &&
                       castling.getCastledRook().equals(castlingRook) &&
                       castling.getCastledRookEndPosition().equals(castlingRookTargetPosition)){
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
