package com.igorternyuk.engine.moves;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.pieces.Piece;

/**
 * Created by igor on 08.08.18.
 */
public class PawnCapturingMove extends CapturingMove {
    public PawnCapturingMove(final Board board, final Piece movedPiece, final Location destination,
                             final Piece capturedPiece) {
        super(board, movedPiece, destination, capturedPiece);
    }

    public String toString() {
        return movedPiece.toString() + "x" + BoardUtils.getAlgebraicNotationFromPosition(destination);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || ((other instanceof PawnCapturingMove) && super.equals(other));
    }
}