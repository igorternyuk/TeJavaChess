package com.igorternyuk.engine.moves;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.pieces.Piece;

/**
 * Created by igor on 08.08.18.
 */
public class RegularMove extends Move {

    public RegularMove(final Board board, final Piece movedPiece, final Location destinationCoordinate) {
        super(board, movedPiece, destinationCoordinate);
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
        final Board.Builder builder = new Board.Builder();
        this.board.getCurrentPlayer().getActivePieces().stream().filter(piece -> !this.movedPiece.equals(piece))
                .forEach(builder::setPiece);
        this.board.getCurrentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);
        builder.setPiece(this.movedPiece.move(this));
        builder.setGameType(this.board.getGameType());
        builder.setKingsRookStartCoordinateX(this.board.getKingsRookStartCoordinateX());
        builder.setQueensRookStartCoordinateX(this.board.getQueensRookStartCoordinateX());
        builder.setMoveMaker(this.board.getCurrentPlayer().getOpponentAlliance());
        builder.setTransitionMove(this);
        return builder.build();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || ((other instanceof RegularMove) && super.equals(other));
    }

    @Override
    public String toString() {
        return movedPiece.toString() + BoardUtils.getAlgebraicNotationFromPosition(destination);
    }
}