package com.igorternyuk.engine.moves;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.pieces.Piece;
import com.igorternyuk.engine.pieces.Rook;

import java.util.Objects;

/**
 * Created by igor on 08.08.18.
 */
public abstract class Castling extends Move {
    protected final Rook castledRook;
    protected final Location castledRookStartLocation;
    protected final Location castledRookEndLocation;

    public Castling(final Board board, final Piece movedPiece, final Location destination, final Rook castledRook,
                    final Location castledRookStartLocation, final Location castledRookEndLocation) {
        super(board, movedPiece, destination);
        this.castledRook = castledRook;
        this.castledRookStartLocation = castledRookStartLocation;
        this.castledRookEndLocation = castledRookEndLocation;
    }

    public Rook getCastledRook() {
        return this.castledRook;
    }

    public Location getCastledRookStartLocation() {
        return this.castledRookStartLocation;
    }

    public Location getCastledRookEndLocation() {
        return this.castledRookEndLocation;
    }

    @Override
    public boolean isCapturingMove() {
        return false;
    }

    @Override
    public boolean isCastlingMove() {
        return true;
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
        //Current player's pieces except castled rook and king
        this.board.getCurrentPlayer().getActivePieces().stream()
                .filter(piece -> !piece.equals(this.movedPiece) && !piece.equals(this.castledRook))
                .forEach(builder::setPiece);
        //Opponent's pieces
        this.board.getCurrentPlayer().getOpponentActivePieces().forEach(builder::setPiece);
        builder.setPiece(this.movedPiece.move(this));
        builder.setPiece(Rook.createRook(this.castledRookEndLocation, this.board.getCurrentPlayer().getAlliance(),
                false));
        builder.setGameType(this.board.getGameType());
        builder.setKingsRookStartCoordinateX(this.board.getKingsRookStartCoordinateX());
        builder.setQueensRookStartCoordinateX(this.board.getQueensRookStartCoordinateX());
        builder.setMoveMaker(this.board.getCurrentPlayer().getOpponentAlliance());
        return builder.build();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + this.castledRook.hashCode();
        result = prime * result + this.castledRookEndLocation.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof Castling)) return false;
        final Castling otherCastling = (Castling) other;
        return Objects.equals(this.castledRook, otherCastling.getCastledRook()) &&
                Objects.equals(this.castledRookEndLocation, ((Castling) other).getCastledRookEndLocation()) &&
                super.equals(other);
    }
}