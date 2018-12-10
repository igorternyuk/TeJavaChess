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
    }
    /*
    * SORT {
            @Override
            Collection<Move> sort(final Collection<Move> moves) {
                return Ordering.from(SMART_SORT).immutableSortedCopy(moves);
            }
        };

        public static Comparator<Move> SMART_SORT = new Comparator<Move>() {
            @Override
            public int compare(final Move move1, final Move move2) {
                return ComparisonChain.start()
                        .compareTrueFirst(BoardUtils.isThreatenedBoardImmediate(move1.getBoard()), BoardUtils.isThreatenedBoardImmediate(move2.getBoard()))
                        .compareTrueFirst(move1.isAttack(), move2.isAttack())
                        .compareTrueFirst(move1.isCastlingMove(), move2.isCastlingMove())
                        .compare(move2.getMovedPiece().getPieceValue(), move1.getMovedPiece().getPieceValue())
                        .result();
            }
        };
    * */;

    public abstract Collection<Move> sort(final Collection<Move> moves);
}
