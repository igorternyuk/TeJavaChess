package com.igorternyuk.engine.player.ai;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.moves.Move;

import java.util.Collection;
import java.util.Comparator;

/**
 * Created by igor on 09.12.18.
 */
public enum MoveSorter {
    STANDARD {
        @Override
        public Collection<Move> sort(Collection<Move> moves) {
            return Ordering.from((Comparator<Move>) (first, second) -> ComparisonChain.start()
                    .compareTrueFirst(first.isCastlingMove(), second.isCastlingMove())
                    .compare(BoardUtils.mvvlva(second), BoardUtils.mvvlva(first))
                    .result()).immutableSortedCopy(moves);
        }
    },
    EXPENSIVE {
        @Override
        public Collection<Move> sort(Collection<Move> moves) {
            return Ordering.from((Comparator<Move>) (first, second) -> ComparisonChain.start()
                    .compareTrueFirst(BoardUtils.kingThreat(first), BoardUtils.kingThreat(second))
                    .compareTrueFirst(first.isCastlingMove(), second.isCastlingMove())
                    .compare(BoardUtils.mvvlva(second), BoardUtils.mvvlva(first))
                    .result()).immutableSortedCopy(moves);
        }
    },
    SMART {
        @Override
        public Collection<Move> sort(Collection<Move> moves) {
            return Ordering.from((Comparator<Move>) (first, second) -> ComparisonChain.start()
                    .compareTrueFirst(BoardUtils.isThreatenedBoardImmediate(first.getBoard()),
                            BoardUtils.isThreatenedBoardImmediate(second.getBoard()))
                    .compareTrueFirst(first.isCapturingMove(), second.isCapturingMove())
                    .compareTrueFirst(first.isCastlingMove(), second.isCastlingMove())
                    .compare(BoardUtils.mvvlva(second), BoardUtils.mvvlva(first))
                    .compare(first.getMovedPiece().getValue(), second.getMovedPiece().getValue())
                    .result()).immutableSortedCopy(moves);
        }
    };

    public abstract Collection<Move> sort(final Collection<Move> moves);
}
