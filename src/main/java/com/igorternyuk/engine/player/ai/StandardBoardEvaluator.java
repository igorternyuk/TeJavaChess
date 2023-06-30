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
    private static final int CHECK_BONUS = 30;
    private static final int CASTLE_BONUS = 100;
    private static final int CASTLE_CAPABLE_BONUS = 500;
    private static final int CHECKMATE_BONUS = 20000;
    private static final int DEPTH_BONUS = 100;
    private static final double MOBILITY_MULTIPLIER = 2;
    private static final int ATTACK_MULTIPLIER = 2;

    @Override
    public int evaluate(Board board, int depth) {
        return scorePlayer(board.getWhitePlayer(), depth)
                - scorePlayer(board.getBlackPlayer(), depth);
    }

    @VisibleForTesting
    private int scorePlayer(final Player player, int depth) {
        final PawnStructureAnalyzer pawnStructureAnalyzer = new PawnStructureAnalyzer(player);
        final RookPositionAnalyzer rookPositionAnalyzer = new RookPositionAnalyzer(player);
        final BishopsEvaluator bishopsEvaluator = new BishopsEvaluator(player);
        final KingSafetyAnalyzer kingSafetyAnalyzer = new KingSafetyAnalyzer(player);
        return materialValue(player)
                + castleCapable(player) + castled(player)
                + mobility(player) + kingThreats(player, depth)
                + attacks(player)
                + pawnStructureAnalyzer.pawnStructureScore()
                + bishopsEvaluator.scoreBishops()
                + rookPositionAnalyzer.rookPositionScore()
                + kingSafetyAnalyzer.scoreKingSafety();
    }

    private static int materialValue(final Player player) {
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

    private static int attacks(final Player player) {
        long attackScore = player.getLegalMoves().stream().filter(move -> {
            if (move.isCapturingMove()) {
                final CapturingMove capturingMove = (CapturingMove) move;
                final Piece capturingPiece = capturingMove.getMovedPiece();
                final Piece capturedPiece = capturingMove.getCapturedPiece();
                return capturingPiece.getValue() <= capturedPiece.getValue();
            }
            return false;
        }).count();
        return (int) attackScore * ATTACK_MULTIPLIER;
    }

    private static int mobility(final Player player) {
        return (int) (mobilityRatio(player) * MOBILITY_MULTIPLIER);
    }

    private static int mobilityRatio(final Player player) {
        return (int) (100.f * player.getLegalMoves().size() / player.getOpponentLegalMoves().size());
    }

    private static int check(final Player player) {
        return player.getOpponent().isUnderCheck() ? CHECK_BONUS : 0;
    }

    private static int kingThreats(final Player player, int depth) {
        return player.getOpponent().isCheckMate() ? CHECKMATE_BONUS * depthBonus(depth) : check(player);
    }

    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private static int castled(final Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }

    private static int castleCapable(final Player player) {
        return player.canCastle() ? CASTLE_CAPABLE_BONUS : 0;
    }
}
