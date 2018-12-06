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
    private int searchDepth;

    public MiniMax(int searchDepth) {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    @Override
    public Move execute(Board board) {
        int lowestDetectedValue = Integer.MAX_VALUE;
        int highestDetectedValue = Integer.MIN_VALUE;
        int currentValue = 0;
        Move bestMove = Move.NULL_MOVE;

        final long startTime = System.currentTimeMillis();
        System.out.println(board.getCurrentPlayer() + " starts thinking with searchDepth " + searchDepth);
        Collection<Move> legalMoves = board.getCurrentPlayer().getLegalMoves();
        for (final Move move : legalMoves) {
            MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                if (board.getCurrentPlayer().getAlliance().isWhite()) {
                    currentValue = min(moveTransition.getTransitedBoard(), searchDepth - 1);
                } else {
                    currentValue = max(moveTransition.getTransitedBoard(), searchDepth - 1);
                }
            }

            if (board.getCurrentPlayer().getAlliance().isWhite()) {
                if (highestDetectedValue < currentValue) {
                    highestDetectedValue = currentValue;
                    bestMove = move;
                }
            } else {
                if (lowestDetectedValue > currentValue) {
                    lowestDetectedValue = currentValue;
                    bestMove = move;
                }
            }
        }
        final long moveTime = System.currentTimeMillis() - startTime;
        System.out.println("Move time: " + moveTime / 1000 + " seconds.");
        return bestMove;
    }

    public int min(final Board board, int depth) {

        //If we are on the leaf level of the tree we can evaluate the current position
        if (depth == 0 || isGameOver(board)) {
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

        //If we are on the leaf level of the tree we can evaluate the current position
        if (depth == 0 || isGameOver(board)) {
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

    private static boolean isGameOver(final Board board) {
        return board.getCurrentPlayer().isCheckMate()
                || board.getCurrentPlayer().isInStalemate()
                || board.isInsufficientMaterial();
    }

    @Override
    public String toString() {
        return "MiniMax";
    }
}
