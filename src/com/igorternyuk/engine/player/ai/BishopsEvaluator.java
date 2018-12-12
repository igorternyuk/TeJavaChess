package com.igorternyuk.engine.player.ai;

import com.igorternyuk.engine.board.Tile;
import com.igorternyuk.engine.pieces.Piece;
import com.igorternyuk.engine.player.Player;

import java.util.Collection;

/**
 * Created by igor on 11.12.18.
 */
public class BishopsEvaluator {
    private static final int BISHOP_PAIR_BONUS = 50;
    private Player player;

    public BishopsEvaluator(final Player player) {
        this.player = player;
    }

    public int scoreBishops() {
        Collection<Piece> pieces = this.player.getActivePieces();
        int value = 0;
        int numPawnsOnLightSquares = 0;
        int numPawnsOnDarkSquares = 0;
        boolean hasLightSquareBishop = false;
        boolean hasDarkSquareBishop = false;
        for (final Piece piece : pieces) {
            final Tile currentTile = this.player.getBoard().getTile(piece.getLocation());
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
}
