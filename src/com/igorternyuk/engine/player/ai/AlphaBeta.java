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

    //private Map<Board, Integer> tt = new HashMap<>();
    public AlphaBeta(int depth) {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = depth;
        this.boardsEvaluated = 0;
        this.cutsOffProduced = 0;
        this.quiescenceCount = 0;
    }

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
                    if (current > beta) {
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
                    if (current < alpha) {
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
        int moveCounter = 0;
        final Collection<Move> legalMoves = MoveSorter.SMART.sort((board.getCurrentPlayer().getLegalMoves()));
        final int numMoves = legalMoves.size();
        System.out.println(board.getCurrentPlayer() + " THINKING with depth = " + this.searchDepth);
        String s = "";
        for (final Move move : legalMoves) {
            final long candidateMoveStartTime = System.nanoTime();
            ++moveCounter;
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

                final String quiescenceInfo = " " + score(currentPlayer, highestSeenValue, lowestSeenValue) + " q: " + this.quiescenceCount;
                s = "\t" + toString() + "(" + this.searchDepth + "), m: (" + moveCounter + "/" + numMoves + ") " + move + ", best:  " + bestMove

                        + quiescenceInfo + ", t: " + calculateTimeTaken(candidateMoveStartTime, System.nanoTime());
            }
            System.out.println(s);
        }

        System.out.println("Board evaluated = " + this.boardsEvaluated);
        System.out.println("this.cutsOffProduced = " + this.cutsOffProduced);
        System.out.println("Best move = " + bestMove);
        System.out.println("Move time = " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
        return bestMove;
    }

    private static String score(final Player currentPlayer,
                                final int highestSeenValue,
                                final int lowestSeenValue) {

        if (currentPlayer.getAlliance().isWhite()) {
            return "[score: " + highestSeenValue + "]";
        } else if (currentPlayer.getAlliance().isBlack()) {
            return "[score: " + lowestSeenValue + "]";
        }
        throw new RuntimeException("Eso tiene mala pinta hijo mio!");
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

    @Override
    public String toString() {
        return "AlphaBeta";
    }

}
