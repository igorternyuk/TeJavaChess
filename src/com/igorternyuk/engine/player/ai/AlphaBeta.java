package com.igorternyuk.engine.player.ai;

import com.igorternyuk.engine.Alliance;
import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.moves.Move;
import com.igorternyuk.engine.moves.MoveTransition;
import com.igorternyuk.engine.player.Player;

import java.util.Collection;

/**
 * Created by igor on 09.12.18.
 */
public class AlphaBeta implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    private long boardsEvaluated;
    private long cutsOffProduced;
    private int quiescenceCount;
    private static final int MAX_QUIESCENCE = 5000;

    public AlphaBeta(int depth) {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = depth;
        this.boardsEvaluated = 0;
        this.cutsOffProduced = 0;
        this.quiescenceCount = 0;
    }

    /*@Override
    public Move execute(final Board board) {
        final long startTime = System.nanoTime();
        final Player currentPlayer = board.getCurrentPlayer();
        Move bestMove = Move.MoveFactory.NULL_MOVE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int currentValue;
        System.out.println("AI starts thinking with depth = " + this.searchDepth);

        Collection<Move> sortedMoves = MoveSorter.EXPENSIVE.sort(currentPlayer.getLegalMoves());

        System.out.println("All possible moves were sorted");

        for(final Move move: sortedMoves){
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                currentValue = alphaBeta(moveTransition.getTransitedBoard()
                        , this.searchDepth, alpha, beta,board.getCurrentPlayer().getAlliance() );
                System.out.println("currentMove = " + move + " => value = " + currentValue);
                if(currentPlayer.getAlliance().isWhite()){
                    alpha = currentValue;
                    bestMove = move;
                    if(moveTransition.getTransitedBoard().getBlackPlayer().isCheckMate()){
                        break;
                    }
                } else {
                    beta = currentValue;
                    bestMove = move;
                    if(moveTransition.getTransitedBoard().getWhitePlayer().isCheckMate()){
                        break;
                    }
                }
            }
        }
        System.out.println("Board evaluated = "+ this.boardsEvaluated);
        System.out.println("this.cutsOffProduced = "+ this.cutsOffProduced);
        System.out.println("Best move = " + bestMove);
        System.out.println("Move time = " + calculateTimeTaken(startTime, System.nanoTime()));
        return bestMove;
    }*/


    private int alphaBeta(final Board board, int depth, int alpha, int beta, Alliance alliance) {
        System.out.println(" alpha = " + alpha + " beta = " + beta + " depth = " + depth);
        if (depth == 0 || board.isEndGameScenario()) {
            ++this.boardsEvaluated;
            return this.boardEvaluator.evaluate(board, depth);
        }

        Collection<Move> sortedMoves = MoveSorter.STANDARD.sort(board.getCurrentPlayer().getLegalMoves());
        int current;


        if (alliance.isWhite()) {
            current = alpha;
            for (final Move move : sortedMoves) {
                final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
                if (moveTransition.getMoveStatus().isDone()) {
                    current = Math.max(current, alphaBeta(moveTransition.getTransitedBoard(),
                            calculateQuiescenceDepth(moveTransition, depth), current, beta, Alliance.BLACK));
                    if (current >= beta) {
                        ++this.cutsOffProduced;
                        System.out.println("//////////////////////////////////////////////////////////");
                        return beta;
                        /*calculateQuiescenceDepth(moveTransition, depth), current*/
                    }
                }
            }
        } else {
            current = beta;
            for (final Move move : sortedMoves) {
                final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
                if (moveTransition.getMoveStatus().isDone()) {
                    current = Math.min(current, alphaBeta(moveTransition.getTransitedBoard(),
                            calculateQuiescenceDepth(moveTransition, depth), alpha, current, Alliance.WHITE));
                    if (current <= alpha) {
                        ++this.cutsOffProduced;
                        System.out.println("//////////////////////////////////////////////////////////");
                        return alpha;
                    }
                }
            }
        }
        return current;
    }

    private int calculateQuiescenceDepth(final MoveTransition moveTransition, int depth) {
        if (depth == 1 && this.quiescenceCount < MAX_QUIESCENCE) {
            int activityMeasure = 0;
            if (moveTransition.getTransitedBoard().getCurrentPlayer().isUnderCheck()) {
                activityMeasure += 2;
            }
            final Collection<Move> history = BoardUtils.getMoveHistory(moveTransition.getTransitedBoard(), 4);

            for (final Move move : history) {
                if (move.isCapturingMove()) {
                    ++activityMeasure;
                }
            }

            if (activityMeasure > 3) {
                ++this.quiescenceCount;
                return 2;
            }
        }
        return depth - 1;
    }

    private static String calculateTimeTaken(final long start, final long end) {
        final long timeTaken = (end - start) / 1000000;
        return timeTaken + " ms";
    }


    @Override
    public Move execute(final Board board) {
        final long startTime = System.currentTimeMillis();
        final Player currentPlayer = board.getCurrentPlayer();
        Move bestMove = Move.MoveFactory.NULL_MOVE;
        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        System.out.println(board.getCurrentPlayer() + " THINKING with depth = " + this.searchDepth);

        for (final Move move : MoveSorter.SMART.sort((board.getCurrentPlayer().getLegalMoves()))) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            this.quiescenceCount = 0;
            if (moveTransition.getMoveStatus().isDone()) {
                //final long candidateMoveStartTime = System.nanoTime();
                currentValue = currentPlayer.getAlliance().isWhite() ?
                        min(moveTransition.getTransitedBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue) :
                        max(moveTransition.getTransitedBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue);
                if (currentPlayer.getAlliance().isWhite() && currentValue > highestSeenValue) {
                    highestSeenValue = currentValue;
                    bestMove = move;
                    if (moveTransition.getTransitedBoard().getBlackPlayer().isCheckMate()) {
                        break;
                    }
                } else if (currentPlayer.getAlliance().isBlack() && currentValue < lowestSeenValue) {
                    lowestSeenValue = currentValue;
                    bestMove = move;
                    if (moveTransition.getTransitedBoard().getWhitePlayer().isCheckMate()) {
                        break;
                    }
                }
            }
        }
        System.out.println("Board evaluated = " + this.boardsEvaluated);
        System.out.println("this.cutsOffProduced = " + this.cutsOffProduced);
        System.out.println("Best move = " + bestMove);
        System.out.println("Move time = " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
        return bestMove;
    }


    private int max(final Board board,
                    final int depth,
                    final int highest,
                    final int lowest) {
        if (depth == 0 || board.isEndGameScenario()) {
            this.boardsEvaluated++;
            return this.boardEvaluator.evaluate(board, depth);
        }
        int currentHighest = highest;
        for (final Move move : MoveSorter.SMART.sort((board.getCurrentPlayer().getLegalMoves()))) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentHighest = Math.max(currentHighest, min(moveTransition.getTransitedBoard(),
                        calculateQuiescenceDepth(moveTransition, depth), currentHighest, lowest));
                if (currentHighest >= lowest) {
                    ++this.cutsOffProduced;
                    //return lowest;
                    break;
                }
            }
        }
        return currentHighest;
    }


    private int min(final Board board,
                    final int depth,
                    final int highest,
                    final int lowest) {
        if (depth == 0 || board.isEndGameScenario()) {
            this.boardsEvaluated++;
            return this.boardEvaluator.evaluate(board, depth);
        }
        int currentLowest = lowest;
        for (final Move move : MoveSorter.SMART.sort((board.getCurrentPlayer().getLegalMoves()))) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentLowest = Math.min(currentLowest, max(moveTransition.getTransitedBoard(),
                        calculateQuiescenceDepth(moveTransition, depth), highest, currentLowest));
                if (currentLowest <= highest) {
                    ++this.cutsOffProduced;
                    //return highest;
                    break;
                }
            }
        }
        return currentLowest;
    }

}
