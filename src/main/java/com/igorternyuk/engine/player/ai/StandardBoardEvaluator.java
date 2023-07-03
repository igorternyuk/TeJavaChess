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
    private static final long CHECK_BONUS = 30;
    private static final long CASTLE_BONUS = 100;
    private static final long CASTLE_CAPABLE_BONUS = 50;
    private static final long CHECKMATE_BONUS = 10000000;
    private static final long DEPTH_BONUS = 100;
    private static final long MOBILITY_MULTIPLIER = 2;
    private static final long ATTACK_MULTIPLIER = 4;

    @Override
    public long evaluate(Board board, int depth) {
        long whiteScore = scorePlayer(board.getWhitePlayer(), depth);
        long blackScore = scorePlayer(board.getBlackPlayer(), depth);
        return whiteScore - blackScore;
    }

    @VisibleForTesting
    private long scorePlayer(final Player player, int depth) {
        final PawnStructureAnalyzer pawnStructureAnalyzer = new PawnStructureAnalyzer(player);
        final RookPositionAnalyzer rookPositionAnalyzer = new RookPositionAnalyzer(player);
        final BishopsEvaluator bishopsEvaluator = new BishopsEvaluator(player);
        final KingSafetyAnalyzer kingSafetyAnalyzer = new KingSafetyAnalyzer(player);
        final long material = materialValue(player);
        final long mobility_ = mobility(player);
        final long kingThreat = kingThreats(player, depth);
        final long attack = attacks(player);
        final long pawns = pawnStructureAnalyzer.pawnStructureScore();
        final long bishops = bishopsEvaluator.scoreBishops();
        final long rooks = rookPositionAnalyzer.rookPositionScore();
        final long kingSafety = kingSafetyAnalyzer.scoreKingSafety();

        return 200 * material
                + castleCapable(player)
                + castled(player)
                + mobility_
                + 2 * kingThreat
                + 150 * attack
                + 2 * pawns
                + 10 * bishops
                + 3 * rooks
                + 50 * kingSafety
                ;
    }

    private static long materialValue(final Player player) {
        Collection<Piece> pieces = player.getActivePieces();
        long value = 0;
        for (final Piece piece : pieces) {
            value += piece.getValue();
            if (!piece.isFirstMove()) {
                value += 20;
            }
        }
        return value;
    }

    private static long attacks(final Player player) {
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

    private static long mobility(final Player player) {
        return (long) (mobilityRatio(player) * MOBILITY_MULTIPLIER);
    }

    private static double mobilityRatio(final Player player) {
        return (100.f * player.getLegalMoves().size() / player.getOpponentLegalMoves().size());
    }

    private static long check(final Player player) {
        return player.getOpponent().isUnderCheck() ? CHECK_BONUS : 0;
    }

    private static long kingThreats(final Player player, int depth) {
        return player.getOpponent().isCheckMate() ? depthBonus(depth) * CHECKMATE_BONUS : check(player);
    }

    private static long depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private static long castled(final Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    private static long castleCapable(final Player player) {
        return player.canCastle() ? CASTLE_CAPABLE_BONUS : 0;
    }
}
