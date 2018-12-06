package com.igorternyuk.engine.player.ai;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.moves.Move;
import com.igorternyuk.engine.moves.MoveTransition;

import java.util.Collection;

/**
 * Created by igor on 06.12.18.
 */
public class MiniMax implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;

    public MiniMax() {
        this.boardEvaluator = null;
    }

    @Override
    public Move execute(Board board, int depth) {
        return null;
    }

    public int min(final Board board, int depth) {
        //If we are on the leaf level of the tree we can evaluate the current position
        if (depth == 0) {
            return this.boardEvaluator.evaluate(board, depth);
        }
        int lowestDetectedValue = Integer.MAX_VALUE;
        Collection<Move> legalMoves = board.getCurrentPlayer().getLegalMoves();
        for (final Move move : legalMoves) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = max(moveTransition.getTransitedBoard(), depth - 1);
                if (currentValue < lowestDetectedValue) {
                    lowestDetectedValue = currentValue;
                }
            }
        }
        return lowestDetectedValue;
    }

    public int max(final Board board, int depth) {
        if (depth == 0) {
            return this.boardEvaluator.evaluate(board, depth);
        }

        int highestDetectedValue = Integer.MIN_VALUE;
        Collection<Move> legalMoves = board.getCurrentPlayer().getLegalMoves();
        for (final Move move : legalMoves) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                final int currentValue = min(moveTransition.getTransitedBoard(), depth - 1);
                if (currentValue > highestDetectedValue) {
                    highestDetectedValue = currentValue;
                }
            }
        }
        return highestDetectedValue;
    }

    @Override
    public String toString() {
        return "MiniMax";
    }
}
