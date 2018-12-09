package com.igorternyuk.engine.moves;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.pieces.Pawn;
import com.igorternyuk.engine.pieces.Piece;

import java.util.Objects;

/**
 * Created by igor on 08.08.18.
 */
public class PawnPromotion extends PawnMove {
    private final PawnMove pawnMove;
    private Pawn promotedPawn;
    private Piece promotedPiece;

    public PawnPromotion(final PawnMove decoratedPawnMove, final Piece promotedPiece) {
        super(decoratedPawnMove.getBoard(), decoratedPawnMove.getMovedPiece(), decoratedPawnMove.getDestination());
        this.pawnMove = decoratedPawnMove;
        this.promotedPawn = (Pawn) decoratedPawnMove.getMovedPiece();
        this.promotedPiece = promotedPiece;
    }

    public PawnMove getPawnMove() {
        return this.pawnMove;
    }

    public Pawn getPromotedPawn() {
        return promotedPawn;
    }

    public Piece getPromotedPiece() {
        return promotedPiece;
    }

    @Override
    public boolean isPawnPromotionMove() {
        return true;
    }

    @Override
    public Board execute() {
        final Board promotedPawnBoard = pawnMove.execute();
        final Board.Builder builder = new Board.Builder();
        promotedPawnBoard.getCurrentPlayer().getActivePieces().stream()
                .filter(piece -> !this.promotedPawn.equals(piece)).forEach(builder::setPiece);
        promotedPawnBoard.getCurrentPlayer().getOpponent().getActivePieces()
                .forEach(builder::setPiece);
        builder.setPiece(promotedPiece);
        builder.setGameType(this.board.getGameType());
        builder.setKingsRookStartCoordinateX(this.board.getKingsRookStartCoordinateX());
        builder.setQueensRookStartCoordinateX(this.board.getQueensRookStartCoordinateX());
        builder.setMoveMaker(this.board.getCurrentPlayer().getOpponentAlliance());
        builder.setTransitionMove(this);
        return builder.build();
    }

    @Override
    public int hashCode() {
        final int prime = 37;
        int result = super.hashCode();
        result += prime * this.pawnMove.hashCode();
        result += prime * this.promotedPawn.hashCode();
        result += prime * this.promotedPiece.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof PawnPromotion)) return false;
        PawnPromotion otherPawnPromotion = (PawnPromotion) other;
        return Objects.equals(this.pawnMove, otherPawnPromotion.getPawnMove()) &&
                Objects.equals(this.promotedPawn, otherPawnPromotion.getPromotedPawn()) &&
                Objects.equals(this.promotedPiece, otherPawnPromotion.getPromotedPiece()) &&
                super.equals(other);
    }

    @Override
    public String toString() {
        return pawnMove.toString() + promotedPiece.getPieceType().getName().toUpperCase();
    }
}