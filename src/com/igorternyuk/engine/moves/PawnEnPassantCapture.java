package com.igorternyuk.engine.moves;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.pieces.Piece;

/**
 * Created by igor on 08.08.18.
 */
public final class PawnEnPassantCapture extends PawnCapturingMove {
    public PawnEnPassantCapture(final Board board, final Piece movedPiece, final Location destination,
                                final Piece capturedPiece) {
        super(board, movedPiece, destination, capturedPiece);
    }

    @Override
    public boolean isEnPassantCapture() {
        return true;
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || ((other instanceof PawnEnPassantCapture) && super.equals(other));
    }
}