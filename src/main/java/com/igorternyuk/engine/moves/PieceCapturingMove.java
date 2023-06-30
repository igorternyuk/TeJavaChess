package com.igorternyuk.engine.moves;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.pieces.Piece;

/**
 * Created by igor on 08.08.18.
 */
public class PieceCapturingMove extends CapturingMove {
    public PieceCapturingMove(final Board board, final Piece movedPiece, final Location destination,
                              final Piece capturedPiece) {
        super(board, movedPiece, destination, capturedPiece);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || ((other instanceof PieceCapturingMove) && super.equals(other));
    }

    @Override
    public String toString() {
        return movedPiece.toString() + "x" + BoardUtils.getAlgebraicNotationFromLocation(destination);
    }
}