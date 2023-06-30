package com.igorternyuk.engine.moves;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.pieces.Piece;

/**
 * Created by igor on 08.08.18.
 */
public class PawnMove extends RegularMove {
    public PawnMove(final Board board, final Piece movedPiece, final Location destination) {
        super(board, movedPiece, destination);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || ((other instanceof PawnMove) && super.equals(other));
    }

    @Override
    public String toString() {
        return movedPiece.toString() + BoardUtils.getAlgebraicNotationForCoordinateY(destination.getY());
    }
}