package com.igorternyuk.tests.engine.board;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.moves.Move;
import com.igorternyuk.engine.moves.MoveTransition;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by igor on 08.08.18.
 */

public class TestCastling {
    @Test
    public void testWhiteKingSideCastling() {
        final Board board = Board.createStandardBoard();
        MoveTransition mt1 = board.getCurrentPlayer().makeMove(Move.MoveFactory.createMove(board, "e2", "e4"));
        assertThat(mt1.getMoveStatus().isDone(), is(true));
        final MoveTransition mt2 = mt1.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt1.getTransitedBoard(), "e7", "e5"));
        assertThat(mt2.getMoveStatus().isDone(), is(true));
        final MoveTransition mt3 = mt2.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt2.getTransitedBoard(), "g1", "f3"));
        assertThat(mt3.getMoveStatus().isDone(), is(true));
        final MoveTransition mt4 = mt3.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt3.getTransitedBoard(), "b8", "c6"));
        assertThat(mt4.getMoveStatus().isDone(), is(true));
        final MoveTransition mt5 = mt4.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt4.getTransitedBoard(), "f1", "b5"));
        assertThat(mt5.getMoveStatus().isDone(), is(true));
        final MoveTransition mt6 = mt5.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt5.getTransitedBoard(), "d7", "d6"));
        assertThat(mt6.getMoveStatus().isDone(), is(true));
        assertThat(mt6.getTransitedBoard().getCurrentPlayer().canCastle(), is(true));
        assertThat(mt6.getTransitedBoard().getCurrentPlayer().canCastleKingSide(), is(true));
        assertThat(mt6.getTransitedBoard().getCurrentPlayer().canCastleQueenSide(), is(false));
        assertThat(mt6.getTransitedBoard().getCurrentPlayer().isCastled(), is(false));
        final MoveTransition mt7 = mt6.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt6.getTransitedBoard(), "e1", "g1"));
        assertThat(mt7.getMoveStatus().isDone(), is(true));
        assertThat(mt7.getTransitedBoard().getCurrentPlayer().getOpponent().canCastle(), is(false));
        assertThat(mt7.getTransitedBoard().getCurrentPlayer().getOpponent().canCastleKingSide(), is(false));
        assertThat(mt7.getTransitedBoard().getCurrentPlayer().getOpponent().canCastleQueenSide(), is(false));
        assertThat(mt7.getTransitedBoard().getCurrentPlayer().getOpponent().isCastled(), is(true));
    }

    @Test
    public void testWhiteQueenSideCastling() {
        final Board board = Board.createStandardBoard();
        MoveTransition mt1 = board.getCurrentPlayer().makeMove(Move.MoveFactory.createMove(board, "e2", "e4"));
        assertThat(mt1.getMoveStatus().isDone(), is(true));
        final MoveTransition mt2 = mt1.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt1.getTransitedBoard(), "e7", "e5"));
        assertThat(mt2.getMoveStatus().isDone(), is(true));
        final MoveTransition mt3 = mt2.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt2.getTransitedBoard(), "d2", "d3"));
        assertThat(mt3.getMoveStatus().isDone(), is(true));
        final MoveTransition mt4 = mt3.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt3.getTransitedBoard(), "d7", "d6"));
        assertThat(mt4.getMoveStatus().isDone(), is(true));
        final MoveTransition mt5 = mt4.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt4.getTransitedBoard(), "b1", "c3"));
        assertThat(mt5.getMoveStatus().isDone(), is(true));
        final MoveTransition mt6 = mt5.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt5.getTransitedBoard(), "b8", "c6"));
        assertThat(mt6.getMoveStatus().isDone(), is(true));
        final MoveTransition mt7 = mt6.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt6.getTransitedBoard(), "c1", "e3"));
        assertThat(mt7.getMoveStatus().isDone(), is(true));
        final MoveTransition mt8 = mt7.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt7.getTransitedBoard(), "c8", "e6"));
        assertThat(mt8.getMoveStatus().isDone(), is(true));
        final MoveTransition mt9 = mt8.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt8.getTransitedBoard(), "d1", "d2"));
        assertThat(mt9.getMoveStatus().isDone(), is(true));
        final MoveTransition mt10 = mt9.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt9.getTransitedBoard(), "d8", "d7"));
        assertThat(mt10.getMoveStatus().isDone(), is(true));
        assertThat(mt10.getTransitedBoard().getCurrentPlayer().canCastle(), is(true));
        assertThat(mt10.getTransitedBoard().getCurrentPlayer().canCastleKingSide(), is(false));
        assertThat(mt10.getTransitedBoard().getCurrentPlayer().canCastleQueenSide(), is(true));
        assertThat(mt10.getTransitedBoard().getCurrentPlayer().isCastled(), is(false));
        final MoveTransition mt11 = mt10.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt10.getTransitedBoard(), "e1", "c1"));
        assertThat(mt11.getMoveStatus().isDone(), is(true));
        assertThat(mt11.getTransitedBoard().getCurrentPlayer().getOpponent().canCastle(), is(false));
        assertThat(mt11.getTransitedBoard().getCurrentPlayer().getOpponent().canCastleKingSide(), is(false));
        assertThat(mt11.getTransitedBoard().getCurrentPlayer().getOpponent().canCastleQueenSide(), is(false));
        assertThat(mt11.getTransitedBoard().getCurrentPlayer().getOpponent().isCastled(), is(true));
    }

    @Test
    public void testBlackKingSideCastling() {
        final Board board = Board.createStandardBoard();
        MoveTransition mt1 = board.getCurrentPlayer().makeMove(Move.MoveFactory.createMove(board, "d2", "d4"));
        assertThat(mt1.getMoveStatus().isDone(), is(true));
        final MoveTransition mt2 = mt1.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt1.getTransitedBoard(), "g8", "f6"));
        assertThat(mt2.getMoveStatus().isDone(), is(true));
        final MoveTransition mt3 = mt2.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt2.getTransitedBoard(), "c2", "c4"));
        assertThat(mt3.getMoveStatus().isDone(), is(true));
        final MoveTransition mt4 = mt3.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt3.getTransitedBoard(), "e7", "e6"));
        assertThat(mt4.getMoveStatus().isDone(), is(true));
        final MoveTransition mt5 = mt4.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt4.getTransitedBoard(), "b1", "c3"));
        assertThat(mt5.getMoveStatus().isDone(), is(true));
        final MoveTransition mt6 = mt5.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt5.getTransitedBoard(), "f8", "b4"));
        assertThat(mt6.getMoveStatus().isDone(), is(true));
        final MoveTransition mt7 = mt6.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt6.getTransitedBoard(), "e2", "e3"));
        assertThat(mt7.getMoveStatus().isDone(), is(true));

        assertThat(mt7.getMoveStatus().isDone(), is(true));
        assertThat(mt7.getMoveStatus().isDone(), is(true));
        assertThat(mt7.getTransitedBoard().getBlackPlayer().canCastle(), is(true));
        assertThat(mt7.getTransitedBoard().getBlackPlayer().canCastleKingSide(), is(true));
        assertThat(mt7.getTransitedBoard().getBlackPlayer().canCastleQueenSide(), is(false));
        assertThat(mt7.getTransitedBoard().getBlackPlayer().isCastled(), is(false));
        final MoveTransition mt8 = mt7.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt7.getTransitedBoard(), "e8", "g8"));
        assertThat(mt8.getMoveStatus().isDone(), is(true));
        assertThat(mt8.getTransitedBoard().getBlackPlayer().canCastle(), is(false));
        assertThat(mt8.getTransitedBoard().getBlackPlayer().canCastleKingSide(), is(false));
        assertThat(mt8.getTransitedBoard().getBlackPlayer().canCastleQueenSide(), is(false));
        assertThat(mt8.getTransitedBoard().getBlackPlayer().isCastled(), is(true));
    }

    @Test
    public void testBlackQueenSideCastling() {
        final Board board = Board.createStandardBoard();
        MoveTransition mt1 = board.getCurrentPlayer().makeMove(Move.MoveFactory.createMove(board, "d2", "d4"));
        assertThat(mt1.getMoveStatus().isDone(), is(true));
        final MoveTransition mt2 = mt1.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt1.getTransitedBoard(), "d7", "d5"));
        assertThat(mt2.getMoveStatus().isDone(), is(true));
        final MoveTransition mt3 = mt2.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt2.getTransitedBoard(), "b1", "c3"));
        assertThat(mt3.getMoveStatus().isDone(), is(true));
        final MoveTransition mt4 = mt3.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt3.getTransitedBoard(), "b8", "c6"));
        assertThat(mt4.getMoveStatus().isDone(), is(true));
        final MoveTransition mt5 = mt4.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt4.getTransitedBoard(), "c1", "f4"));
        assertThat(mt5.getMoveStatus().isDone(), is(true));
        final MoveTransition mt6 = mt5.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt5.getTransitedBoard(), "c8", "f5"));
        assertThat(mt6.getMoveStatus().isDone(), is(true));
        final MoveTransition mt7 = mt6.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt6.getTransitedBoard(), "e2", "e3"));
        assertThat(mt7.getMoveStatus().isDone(), is(true));
        final MoveTransition mt8 = mt7.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt7.getTransitedBoard(), "e7", "e6"));
        assertThat(mt8.getMoveStatus().isDone(), is(true));
        final MoveTransition mt9 = mt8.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt8.getTransitedBoard(), "d1", "e2"));
        assertThat(mt9.getMoveStatus().isDone(), is(true));
        final MoveTransition mt10 = mt9.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt9.getTransitedBoard(), "d8", "e7"));
        assertThat(mt10.getMoveStatus().isDone(), is(true));
        final MoveTransition mt11 = mt10.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt10.getTransitedBoard(), "g1", "f3"));
        assertThat(mt11.getMoveStatus().isDone(), is(true));
        assertThat(mt11.getTransitedBoard().getBlackPlayer().canCastle(), is(true));
        assertThat(mt11.getTransitedBoard().getBlackPlayer().canCastleKingSide(), is(false));
        assertThat(mt11.getTransitedBoard().getBlackPlayer().canCastleQueenSide(), is(true));
        assertThat(mt11.getTransitedBoard().getBlackPlayer().isCastled(), is(false));
        final MoveTransition mt12 = mt11.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt11.getTransitedBoard(), "e8", "c8"));
        assertThat(mt12.getMoveStatus().isDone(), is(true));
        assertThat(mt12.getTransitedBoard().getBlackPlayer().canCastle(), is(false));
        assertThat(mt12.getTransitedBoard().getBlackPlayer().canCastleKingSide(), is(false));
        assertThat(mt12.getTransitedBoard().getBlackPlayer().canCastleQueenSide(), is(false));
        assertThat(mt12.getTransitedBoard().getBlackPlayer().isCastled(), is(true));
    }

    @Test
    public void testNoCastlingOutOfCheck() {
        final Board board = Board.createStandardBoard();
        MoveTransition mt1 = board.getCurrentPlayer().makeMove(Move.MoveFactory.createMove(board, "d2", "d4"));
        assertThat(mt1.getMoveStatus().isDone(), is(true));
        final MoveTransition mt2 = mt1.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt1.getTransitedBoard(), "g8", "f6"));
        assertThat(mt2.getMoveStatus().isDone(), is(true));
        final MoveTransition mt3 = mt2.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt2.getTransitedBoard(), "c2", "c4"));
        assertThat(mt3.getMoveStatus().isDone(), is(true));
        final MoveTransition mt4 = mt3.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt3.getTransitedBoard(), "e7", "e6"));
        assertThat(mt4.getMoveStatus().isDone(), is(true));
        final MoveTransition mt5 = mt4.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt4.getTransitedBoard(), "g1", "f3"));
        assertThat(mt5.getMoveStatus().isDone(), is(true));
        final MoveTransition mt6 = mt5.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt5.getTransitedBoard(), "b7", "b6"));
        assertThat(mt6.getMoveStatus().isDone(), is(true));
        final MoveTransition mt7 = mt6.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt6.getTransitedBoard(), "g2", "g3"));
        assertThat(mt7.getMoveStatus().isDone(), is(true));
        final MoveTransition mt8 = mt7.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt7.getTransitedBoard(), "c8", "b7"));
        assertThat(mt8.getMoveStatus().isDone(), is(true));
        final MoveTransition mt9 = mt8.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt8.getTransitedBoard(), "f1", "g2"));
        assertThat(mt9.getMoveStatus().isDone(), is(true));
        final MoveTransition mt10 = mt9.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt9.getTransitedBoard(), "f8", "b4"));
        assertThat(mt10.getMoveStatus().isDone(), is(true));
        assertThat(mt10.getTransitedBoard().getWhitePlayer().canCastle(), is(false));
        assertThat(mt10.getTransitedBoard().getWhitePlayer().canCastleKingSide(), is(false));
        assertThat(mt10.getTransitedBoard().getWhitePlayer().canCastleQueenSide(), is(false));
        assertThat(mt10.getTransitedBoard().getWhitePlayer().isCastled(), is(false));
        assertThat(mt10.getTransitedBoard().getWhitePlayer().getLegalMoves().stream().noneMatch(Move::isCastlingMove),
                is(true));
        final MoveTransition mt11 = mt10.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt10.getTransitedBoard(), "e1", "g1"));
        assertThat(mt11.getMoveStatus().isDone(), is(false));
        assertThat(mt11.getTransitedBoard().getWhitePlayer().isUnderCheck(), is(true));
        assertThat(mt11.getTransitedBoard().getWhitePlayer().isCheckMate(), is(false));
        assertThat(mt11.getTransitedBoard().getWhitePlayer().isInStalemate(), is(false));
    }
}
