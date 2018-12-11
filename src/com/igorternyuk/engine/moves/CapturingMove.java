package com.igorternyuk.engine.moves;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.pieces.Piece;

import java.util.Objects;

/**
 * Created by igor on 08.08.18.
 */
public abstract class CapturingMove extends Move {
    private Piece capturedPiece;

    public CapturingMove(final Board board, final Piece movedPiece, final Location destination,
                         final Piece capturedPiece) {
        super(board, movedPiece, destination);
        this.capturedPiece = capturedPiece;
    }

    @Override
    public Board execute() {
        final Board.Builder builder = new Board.Builder();
        this.board.getCurrentPlayer().getActivePieces().stream().filter(piece -> !this.movedPiece.equals(piece))
                .forEach(builder::setPiece);
        this.board.getCurrentPlayer().getOpponentActivePieces().stream()
                .filter(piece -> !this.capturedPiece.equals(piece)).forEach(builder::setPiece);
        builder.setPiece(this.movedPiece.move(this));
        builder.setGameType(this.board.getGameType());
        builder.setKingsRookStartCoordinateX(this.board.getKingsRookStartCoordinateX());
        builder.setQueensRookStartCoordinateX(this.board.getQueensRookStartCoordinateX());
        builder.setMoveMaker(this.board.getCurrentPlayer().getOpponentAlliance());
        builder.setTransitionMove(this);
        builder.setCheckEndGamePhase(true);
        return builder.build();
    }

    @Override
    public boolean isCapturingMove() {
        return true;
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
    public Piece getCapturedPiece() {
        return this.capturedPiece;
    }

    @Override
    public int hashCode() {
        return this.capturedPiece.hashCode() + super.hashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof CapturingMove)) return false;
        CapturingMove otherMove = (CapturingMove) other;
        return super.equals(other) && Objects.equals(this.getCapturedPiece(), otherMove.getCapturedPiece());
    }

    @Override
    public String toString() {
        return movedPiece.toString() + "x" + BoardUtils.getAlgebraicNotationFromPosition(destination);
    }
}