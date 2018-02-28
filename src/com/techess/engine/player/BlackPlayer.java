package com.techess.engine.player;

import com.techess.engine.Alliance;
import com.techess.engine.board.Board;
import com.techess.engine.board.Move;
import com.techess.engine.board.Tile;
import com.techess.engine.pieces.Piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by igor on 03.12.17.
 */
public class BlackPlayer extends Player {
    public BlackPlayer(final Board board, final Collection<Move> legalMovesWhitePieces,
                       final Collection<Move> legalMovesBlackPieces) {
        super(board, legalMovesBlackPieces, legalMovesWhitePieces);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.getWhitePlayer();
    }

    @Override
    public Collection<Piece> getOpponentActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getOpponentAlliance() {
        return Alliance.WHITE;
    }

    /*public Collection<Move> calculateCastles(final Collection<Move> thisPlayerLegalMoves,
                                             final Collection<Move> opponentLegalMoves) {
        return super.calculateCastles(thisPlayerLegalMoves, opponentLegalMoves, Alliance.BLACK);
    }*/
}
