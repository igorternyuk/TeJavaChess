package com.techess.engine.player;

import com.techess.engine.Alliance;
import com.techess.engine.board.Board;
import com.techess.engine.moves.Move;
import com.techess.engine.pieces.Piece;

import java.util.Collection;

/**
 * Created by igor on 03.12.17.
 */

public class WhitePlayer extends Player{
    public WhitePlayer(final Board board, final Collection<Move> legalMovesWhitePieces,
                       final Collection<Move> legalMovesBlackPieces) {
        super(board, legalMovesWhitePieces, legalMovesBlackPieces);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.getBlackPlayer();
    }

    @Override
    public Collection<Piece> getOpponentActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getOpponentAlliance() {
        return Alliance.BLACK;
    }

    /*public Collection<Move> calculateCastles(final Collection<Move> thisPlayerLegalMoves,
                                                final Collection<Move> opponentLegalMoves) {
        return super.calculateCastles(thisPlayerLegalMoves, opponentLegalMoves, Alliance.WHITE);
    }*/
}
