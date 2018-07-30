package com.igorternyuk.engine.player;

import com.igorternyuk.engine.Alliance;
import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.moves.Move;
import com.igorternyuk.engine.pieces.Piece;

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

    @Override
    public String toString(){
        return "White";
    }

    /*public Collection<Move> calculateCastles(final Collection<Move> thisPlayerLegalMoves,
                                                final Collection<Move> opponentLegalMoves) {
        return super.calculateCastles(thisPlayerLegalMoves, opponentLegalMoves, Alliance.WHITE);
    }*/
}
