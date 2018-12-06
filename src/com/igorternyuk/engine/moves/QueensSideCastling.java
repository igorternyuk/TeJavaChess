package com.igorternyuk.engine.moves;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.pieces.Piece;
import com.igorternyuk.engine.pieces.Rook;

/**
 * Created by igor on 08.08.18.
 */
public class QueensSideCastling extends Castling {
    public QueensSideCastling(final Board board, final Piece movedPiece, final Location kingsDestination,
                              final Rook castleRook, final Location castleRookStartLocation,
                              final Location castleRookEndLocation) {
        super(board, movedPiece, kingsDestination, castleRook, castleRookStartLocation, castleRookEndLocation);
    }

    @Override
    public boolean isKingSideCastling() {
        return false;
    }

    @Override
    public boolean isQueenSideCastling() {
        return true;
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || ((other instanceof QueensSideCastling) && super.equals(other));
    }

    @Override
    public String toString() {
        return "0-0-0";
    }
}