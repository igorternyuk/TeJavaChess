package com.igorternyuk.engine.moves;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.pieces.Piece;
import com.igorternyuk.engine.pieces.Rook;

/**
 * Created by igor on 08.08.18.
 */
public class KingsSideCastling extends Castling {
    public KingsSideCastling(final Board board, final Piece movedPiece, final Location kingsDestination,
                             final Rook castleRook, final Location castleRookStartLocation,
                             final Location castleRookEndLocation) {
        super(board, movedPiece, kingsDestination, castleRook, castleRookStartLocation, castleRookEndLocation);
    }

    @Override
    public boolean isKingSideCastling() {
        return true;
    }

    @Override
    public boolean isQueenSideCastling() {
        return false;
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || ((other instanceof KingsSideCastling) && super.equals(other));
    }

    @Override
    public String toString() {
        return "0-0";
    }
}