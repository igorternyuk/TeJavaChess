package com.igorternyuk.engine.player.ai;

import com.google.common.annotations.VisibleForTesting;
import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.moves.CapturingMove;
import com.igorternyuk.engine.pieces.Piece;
import com.igorternyuk.engine.player.Player;

import java.util.Collection;

/**
 * Created by igor on 06.12.18.
 */
public final class StandardBoardEvaluator implements BoardEvaluator {
    private static final double CHECK_BONUS = 30;
    private static final double CASTLE_BONUS = 80;
    private static final double CASTLE_CAPABLE_BONUS = 50;
    private static final double CHECKMATE_BONUS = 100000000;
    private static final double DEPTH_BONUS = 100;
    private static final double MOBILITY_MULTIPLIER = 2;
    private static final double ATTACK_MULTIPLIER = 3;

    @Override
    public double evaluate(Board board, int depth) {
        double whiteScore = scorePlayer(board.getWhitePlayer(), depth);
        double blackScore = scorePlayer(board.getBlackPlayer(), depth);
        return whiteScore - blackScore;
    }

    @VisibleForTesting
    private double scorePlayer(final Player player, int depth) {
        final PawnStructureAnalyzer pawnStructureAnalyzer = new PawnStructureAnalyzer(player);
        final RookPositionAnalyzer rookPositionAnalyzer = new RookPositionAnalyzer(player);
        final BishopsEvaluator bishopsEvaluator = new BishopsEvaluator(player);
        final KingSafetyAnalyzer kingSafetyAnalyzer = new KingSafetyAnalyzer(player);
        final double material = materialValue(player);
        final double mobility_ = mobility(player);
        final double kingThreat = kingThreats(player, depth);
        final double attack = attacks(player);
        final double pawns = pawnStructureAnalyzer.pawnStructureScore();
        final double bishops = bishopsEvaluator.scoreBishops();
        final double rooks = rookPositionAnalyzer.rookPositionScore();
        final double kingSafety = kingSafetyAnalyzer.scoreKingSafety();

        return 50 * material
                + castleCapable(player)
                + castled(player)
                + 1.2 * mobility_
                + 1.5 * kingThreat
                + 3 * attack
                + 2 * pawns
                + 2.5 * bishops
                + 3 * rooks
                + 7 * kingSafety
                ;
    }

    private static double materialValue(final Player player) {
        Collection<Piece> pieces = player.getActivePieces();
        int value = 0;
        for (final Piece piece : pieces) {
            value += piece.getValue();
            if (!piece.isFirstMove()) {
                value += 20;
            }
        }
        return value;
    }

    private static double attacks(final Player player) {
        long attackScore = player.getLegalMoves().stream().filter(move -> {
            if (move.isCapturingMove()) {
                final CapturingMove capturingMove = (CapturingMove) move;
                final Piece capturingPiece = capturingMove.getMovedPiece();
                final Piece capturedPiece = capturingMove.getCapturedPiece();
                return capturingPiece.getValue() < capturedPiece.getValue();
            }
            return false;
        }).count();
        return attackScore * ATTACK_MULTIPLIER;
    }

    private static double mobility(final Player player) {
        return (mobilityRatio(player) * MOBILITY_MULTIPLIER);
    }

    private static double mobilityRatio(final Player player) {
        return (100.f * player.getLegalMoves().size() / player.getOpponentLegalMoves().size());
    }

    private static double check(final Player player) {
        return player.getOpponent().isUnderCheck() ? CHECK_BONUS : 0;
    }

    private static double kingThreats(final Player player, int depth) {
        return player.getOpponent().isCheckMate() ? depthBonus(depth) * CHECKMATE_BONUS : check(player);
    }

    private static double depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private static double castled(final Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    private static double castleCapable(final Player player) {
        return player.canCastle() ? CASTLE_CAPABLE_BONUS : 0;
    }

    private static double nonCastledPenalty(final Player player) {
        return !player.canCastle() && !player.isCastled() ? -1000 : 0;
    }
}
