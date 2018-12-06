package com.igorternyuk.engine.player.ai;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.pieces.Piece;
import com.igorternyuk.engine.player.Player;

import java.util.Collection;

/**
 * Created by igor on 06.12.18.
 */
public final class StandardBoardEvaluator implements BoardEvaluator {
    private static final int CHECK_BONUS = 20;
    private static final int CHECKMATE_BONUS = 20000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLE_BONUS = 60;

    @Override
    public int evaluate(Board board, int depth) {
        return scorePlayer(board, board.getWhitePlayer(), depth)
                - scorePlayer(board, board.getBlackPlayer(), depth);
    }

    private int scorePlayer(final Board board, final Player player, int depth) {
        return materialValue(player) + castled(player) + check(player)
                + mobility(player) + checkMate(player, depth);
    }

    private static int materialValue(final Player player) {
        Collection<Piece> pieces = player.getActivePieces();
        int value = 0;
        for (final Piece piece : pieces) {
            value += piece.getValue();
        }
        return value;
    }

    private static int mobility(final Player player) {
        return player.getLegalMoves().size();
    }

    private static int check(final Player player) {
        return player.getOpponent().isUnderCheck() ? CHECK_BONUS : 0;
    }

    private static int checkMate(final Player player, int depth) {
        return player.getOpponent().isCheckMate() ? CHECKMATE_BONUS * depthBonus(depth) : 0;
    }

    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private static int castled(final Player player) {
        return player.isCastled() ? CASTLE_BONUS : 0;
    }
}
