package com.igorternyuk.engine.player.ai;

import com.google.common.annotations.VisibleForTesting;
import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.Tile;
import com.igorternyuk.engine.moves.CapturingMove;
import com.igorternyuk.engine.pieces.Piece;
import com.igorternyuk.engine.player.Player;

import java.util.Collection;

/**
 * Created by igor on 06.12.18.
 */
public final class StandardBoardEvaluator implements BoardEvaluator {
    private static final int CHECK_BONUS = 30;
    private static final int CASTLE_BONUS = 60;
    private static final int CASTLE_CAPABLE_BONUS = 30;
    private static final int BISHOP_PAIR_BONUS = 50;
    private static final int CHECKMATE_BONUS = 20000;
    private static final int DEPTH_BONUS = 100;
    private static final int MOBILITY_MULTIPLIER = 2;
    private static final int ATTACK_MULTIPLIER = 2;

    @Override
    public int evaluate(Board board, int depth) {
        return scorePlayer(board.getWhitePlayer(), depth)
                - scorePlayer(board.getBlackPlayer(), depth);
    }

    @VisibleForTesting
    private int scorePlayer(final Player player, int depth) {
        return materialValue(player) + evaluateBishops(player)
                + castleCapable(player) + castled(player)
                + mobility(player) + kingThreats(player, depth)
                + attacks(player);
    }

    private static int materialValue(final Player player) {
        Collection<Piece> pieces = player.getActivePieces();
        int value = 0;
        for (final Piece piece : pieces) {
            value += piece.getValue();
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

    private static int evaluateBishops(final Player player) {
        Collection<Piece> pieces = player.getActivePieces();
        int value = 0;
        int numPawnsOnLightSquares = 0;
        int numPawnsOnDarkSquares = 0;
        boolean hasLightSquareBishop = false;
        boolean hasDarkSquareBishop = false;
        for (final Piece piece : pieces) {
            final Tile currentTile = player.getBoard().getTile(piece.getLocation());
            if (currentTile.isTileLight()) {
                if (piece.getPieceType().isPawn()) {
                    ++numPawnsOnLightSquares;
                } else if (piece.getPieceType().isBishop()) {
                    hasLightSquareBishop = true;
                }
            } else if (currentTile.isTileDark()) {
                if (piece.getPieceType().isPawn()) {
                    ++numPawnsOnDarkSquares;
                } else if (piece.getPieceType().isBishop()) {
                    hasDarkSquareBishop = true;
                }
            }
        }

        if (hasDarkSquareBishop) {
            value += 5 * numPawnsOnLightSquares;
        }
        if (hasLightSquareBishop) {
            value += 5 * numPawnsOnDarkSquares;
        }
        if (hasLightSquareBishop && hasDarkSquareBishop) {
            value += BISHOP_PAIR_BONUS;
        }
        return value;
    }

    private static int mobility(final Player player) {
        return mobilityRatio(player) * MOBILITY_MULTIPLIER;
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
