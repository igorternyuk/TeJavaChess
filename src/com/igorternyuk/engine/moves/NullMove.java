package com.igorternyuk.engine.moves;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;

/**
 * Created by igor on 09.12.18.
 */
public final class NullMove extends Move {
    NullMove() {
        super(null, BoardUtils.NULL_LOCATION);
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
        throw new RuntimeException("Could not execute the null move");
    }
}
