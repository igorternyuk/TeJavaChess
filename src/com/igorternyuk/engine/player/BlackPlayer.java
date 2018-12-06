package com.igorternyuk.engine.player;

import com.igorternyuk.engine.Alliance;
import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.moves.Move;
import com.igorternyuk.engine.pieces.Piece;

import java.util.Collection;

/**
 * Created by igor on 03.12.17.
 */
public class BlackPlayer extends Player {
    public BlackPlayer(final Board board, final Collection<Move> legalMovesBlackPieces,
                       final Collection<Move> legalMovesWhitePieces) {
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

    @Override
    public String toString(){
        return "Black player";
    }
}
