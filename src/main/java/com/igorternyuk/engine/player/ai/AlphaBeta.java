package com.igorternyuk.engine.player.ai;

import com.igorternyuk.engine.Alliance;
import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.moves.Move;
import com.igorternyuk.engine.moves.MoveTransition;
import com.igorternyuk.engine.player.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by igor on 09.12.18.
 */
public class AlphaBeta implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    private long boardsEvaluated;
    private long cutsOffProduced;
    private int quiescenceCount;
    //private Map<String, Integer> tt = new HashMap<>();
    private static final int MAX_QUIESCENCE = 5000;

    //private Map<Board, Integer> tt = new HashMap<>();
    public AlphaBeta(int depth) {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = depth;
        this.boardsEvaluated = 0;
        this.cutsOffProduced = 0;
        this.quiescenceCount = 0;
    }

    private double alphaBeta(final Board board, int depth, double alpha, double beta, Alliance alliance) {
        System.out.println(" alpha = " + alpha + " beta = " + beta + " depth = " + depth);
        if (depth == 0 || board.isEndGameScenario()) {
            ++this.boardsEvaluated;
            return this.boardEvaluator.evaluate(board, depth);
        }

        Collection<Move> sortedMoves = MoveSorter.STANDARD.sort(board.getCurrentPlayer().getLegalMoves());
        double current;

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

    private static class Evaluation {
        private final double score;
        private final String bestLine;
        private final String evalBoard;

        public Evaluation(double score, String bestLine, String evalBoard) {
            this.score = score;
            this.bestLine = bestLine;
            this.evalBoard = evalBoard;
        }

        public double GetScore() {
            return this.score;
        }

        public String GetBestLine() {
            return this.bestLine;
        }

        public String GetEvalBoard() {
            return this.evalBoard;
        }
    }

    private static class ScoredMove {
        private final Move move;
        private final double score;

        private ScoredMove(Move move, double score) {
            this.move = move;
            this.score = score;
        }

        Move getMove() {
            return this.move;
        }

        double getScore() {
            return this.score;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || !(other instanceof ScoredMove)) return false;
            ScoredMove otherMove = (ScoredMove) other;
            if (this.score != otherMove.getScore())
                return false;
            return Objects.equals(this.move, otherMove.getMove());
        }

        @Override
        public int hashCode() {
            int result = Double.hashCode(this.score);
            result = 31 * result + this.move.hashCode();
            return result;
        }
    }

    @Override
    public Move execute(final Board board) {
        final long startTime = System.currentTimeMillis();
        final Player currentPlayer = board.getCurrentPlayer();
        Move bestMove = Move.MoveFactory.NULL_MOVE;

        double alpha = -1e20;
        double beta = +1e20;

        final Collection<Move> legalMoves = MoveSorter.SMART.sort((board.getCurrentPlayer().getLegalMoves()));
        final int numMoves = legalMoves.size();
        System.out.println(board.getCurrentPlayer() + " THINKING with depth = " + this.searchDepth);

        List<ScoredMove> listOfBestMoves = new ArrayList<>();
        double evalBest = 0;
        if (currentPlayer.getAlliance().isBlack()) {
            final Move mateMove = Move.MoveFactory.createMove(board, "d5", "h1");
            int moveCounter = 0;
            double minVal = +1e20;
            for (final Move move : legalMoves) {
                final long candidateMoveStartTime = System.nanoTime();
                ++moveCounter;
                final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
                this.quiescenceCount = 0;
                if (moveTransition.getMoveStatus().isDone()) {
                    String currLine = move.toString();
                    Evaluation currEval = max(moveTransition.getTransitedBoard(), calculateQuiescenceDepth(moveTransition, this.searchDepth), alpha, beta, currLine);
                    if (currEval.score < minVal) {
                        bestMove = move;
                        ScoredMove bestScoredMove = new ScoredMove(bestMove, currEval.score);
                        listOfBestMoves.add(bestScoredMove);
                        minVal = currEval.score;
                        evalBest = currEval.score;
                        if (board.isEndGameScenario())
                            break;
                    }
                    StringBuilder builder = new StringBuilder();
                    String strMove = move.toString();
                    builder.append(String.format("Analyzed move %s (%d / %d):  with depth %d q: %d\n", move, moveCounter, numMoves, this.searchDepth, this.quiescenceCount));
                    builder.append(String.format("best move: %s score: %.3f best line: %s", strMove, currEval.GetScore(), currEval.GetBestLine()));
                    builder.append(String.format("\nEvaluated board: %s\n", currEval.GetEvalBoard()));
                    builder.append(String.format("Time taken: %s", calculateTimeTaken(candidateMoveStartTime, System.nanoTime())));
                    final String strInfo = builder.toString();
                    System.out.println(strInfo);
                }
            }
        } else if (currentPlayer.getAlliance().isWhite()) {
            int moveCounter = 0;
            double maxVal = -1e20;
            for (final Move move : legalMoves) {
                final long candidateMoveStartTime = System.nanoTime();
                ++moveCounter;
                final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
                this.quiescenceCount = 0;
                if (moveTransition.getMoveStatus().isDone()) {
                    String currLine = move.toString();
                    Evaluation currEval = min(moveTransition.getTransitedBoard(), calculateQuiescenceDepth(moveTransition, this.searchDepth), alpha, beta, currLine);
                    if (currEval.score > maxVal) {
                        bestMove = move;
                        ScoredMove bestScoredMove = new ScoredMove(bestMove, currEval.score);
                        listOfBestMoves.add(bestScoredMove);
                        maxVal = Math.max(currEval.score, maxVal);
                        evalBest = currEval.score;
                        if (board.isEndGameScenario())
                            break;
                    }
                    StringBuilder builder = new StringBuilder();
                    String strMove = move.toString();
                    builder.append(String.format("Analyzed move %s (%d / %d):  with depth %d q: %d\n", move, moveCounter, numMoves, this.searchDepth, this.quiescenceCount));
                    builder.append(String.format("best move: %s score: %.3f\nbest line: %s\n", strMove, currEval.GetScore(), currEval.GetBestLine()));
                    builder.append(String.format("Evaluated board: %s\n", currEval.GetEvalBoard()));
                    builder.append(String.format("Time taken: %s", calculateTimeTaken(candidateMoveStartTime, System.nanoTime())));
                    final String strInfo = builder.toString();
                    System.out.println(strInfo);
                }
            }
        }

        System.out.println("Board evaluated = " + this.boardsEvaluated);
        System.out.println("this.cutsOffProduced = " + this.cutsOffProduced);
        System.out.println(String.format("Best move = %s eval = %.3f", bestMove, evalBest));
        System.out.println("Move time = " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
        return bestMove;
    }

    private static String score(final Player currentPlayer,
                                final double highestSeenValue,
                                final double lowestSeenValue) {

        if (currentPlayer.getAlliance().isWhite()) {
            return "[score: " + highestSeenValue + "]";
        } else if (currentPlayer.getAlliance().isBlack()) {
            return "[score: " + lowestSeenValue + "]";
        }
        throw new RuntimeException("Eso tiene mala pinta hijo mio!");
    }


    private Evaluation max(final Board board,
                           final int depth,
                           double alpha,
                           double beta, String currLine) {
        if (depth == 0 || board.isEndGameScenario()) {
            this.boardsEvaluated++;
            double score = this.boardEvaluator.evaluate(board, depth);
            return new Evaluation(score, currLine.toString(), board.toDecoratedString());
        }
        double value = -1e20;
        Evaluation evalBest = null;
        for (final Move move : MoveSorter.SMART.sort((board.getCurrentPlayer().getLegalMoves()))) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                Evaluation evaluation = min(moveTransition.getTransitedBoard(), depth - 1, alpha, beta, String.format("%s %s ", currLine, move));
                if (evaluation != null) {
                    double score = evaluation.GetScore();
                    value = Math.max(score, value);
                    if (score == value) {
                        evalBest = evaluation;
                    }
                    alpha = Math.max(alpha, value);
                    if (value >= beta) {
                        ++this.cutsOffProduced;
                        break;
                    }
                }
            }
        }
        return evalBest;
    }


    private Evaluation min(final Board board,
                           final int depth,
                           double alpha,
                           double beta, String currLine) {
        if (depth == 0 || board.isEndGameScenario()) {
            this.boardsEvaluated++;
            double score = this.boardEvaluator.evaluate(board, depth);
            return new Evaluation(score, currLine.toString(), board.toDecoratedString());
        }
        double value = 1000000;
        Evaluation evalBest = null;
        for (final Move move : MoveSorter.SMART.sort((board.getCurrentPlayer().getLegalMoves()))) {
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            //calculateQuiescenceDepth(moveTransition, depth)
            if (moveTransition.getMoveStatus().isDone()) {
                Evaluation evaluation = max(moveTransition.getTransitedBoard(), depth - 1, alpha, beta, String.format("%s %s ", currLine, move));
                if (evaluation != null) {
                    double score = evaluation.GetScore();
                    value = Math.min(score, value);
                    if (score == value) {
                        value = score;
                        evalBest = evaluation;
                    }
                    beta = Math.min(beta, value);
                    if (value <= alpha) {
                        ++this.cutsOffProduced;
                        break;
                    }
                }
            }
        }
        return evalBest;
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
    public String toString() {
        return "AlphaBeta";
    }

}
