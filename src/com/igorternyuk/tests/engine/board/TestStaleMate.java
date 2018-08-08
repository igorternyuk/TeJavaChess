package com.igorternyuk.tests.engine.board;

import com.igorternyuk.engine.Alliance;
import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.moves.Move;
import com.igorternyuk.engine.moves.MoveTransition;
import com.igorternyuk.engine.pieces.*;
import org.junit.Test;

import static com.igorternyuk.engine.board.Board.Builder;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by igor on 08.08.18.
 */
public class TestStaleMate {
    @Test
    public void testPolgarTruongStaleMate() {
        final Builder builder = new Builder();
        builder.setPiece(King.createKing("h8", Alliance.BLACK, false));
        builder.setPiece(King.createKing("f7", Alliance.WHITE, false));
        builder.setPiece(Queen.createQueen("g2", Alliance.WHITE, false));
        builder.setMoveMaker(Alliance.WHITE);
        final Board board = builder.build();
        final MoveTransition mt = board.getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.getLocation("g2"),
                        BoardUtils.getLocation("g6")));
        assertThat("Move was not executed", mt.getMoveStatus().isDone(), is(true));
        assertThat("Black player is not in stalemate", mt.getTransitedBoard().getCurrentPlayer().isInStalemate(),
                is(true));
        assertThat("Black player is under check", mt.getTransitedBoard().getCurrentPlayer().isUnderCheck(), is(false));
        assertThat("Black player is checkmated", mt.getTransitedBoard().getCurrentPlayer().isCheckMate(), is(false));

    }

    @Test
    public void testKorchnoiKarpovStaleMate() {
        final Builder builder = new Builder();
        builder.setPiece(King.createKing("f7", Alliance.WHITE, false));
        builder.setPiece(Bishop.createBishop("c3", Alliance.WHITE, false));
        builder.setPiece(Pawn.createPawn("a3", Alliance.WHITE, false));
        builder.setPiece(King.createKing("h7", Alliance.BLACK, false));
        builder.setPiece(Pawn.createPawn("a4", Alliance.BLACK, false));
        builder.setMoveMaker(Alliance.WHITE);
        final Board board = builder.build();
        final MoveTransition mt = board.getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.getLocation("c3"),
                        BoardUtils.getLocation("g7")));
        assertThat("Move was not executed", mt.getMoveStatus().isDone(), is(true));
        assertThat("Black player is not in stalemate", mt.getTransitedBoard().getCurrentPlayer().isInStalemate(),
                is(true));
        assertThat("Black player is under check", mt.getTransitedBoard().getCurrentPlayer().isUnderCheck(), is(false));
        assertThat("Black player is checkmated", mt.getTransitedBoard().getCurrentPlayer().isCheckMate(), is(false));

    }

    @Test
    public void testBernsteinSmyslovStaleMate() {
        final Builder builder = new Builder();
        builder.setPiece(King.createKing("e2", Alliance.WHITE, false));
        builder.setPiece(Rook.createRook("b8", Alliance.WHITE, false));
        builder.setPiece(King.createKing("f5", Alliance.BLACK, false));
        builder.setPiece(Rook.createRook("h3", Alliance.BLACK, false));
        builder.setPiece(Pawn.createPawn("b3", Alliance.BLACK, false));
        builder.setPiece(Pawn.createPawn("f4", Alliance.BLACK, false));
        builder.setMoveMaker(Alliance.BLACK);
        final Board board = builder.build();
        final MoveTransition mt1 = board.getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(board, BoardUtils.getLocation("b3"),
                        BoardUtils.getLocation("b2")));
        assertThat("Move was not executed", mt1.getMoveStatus().isDone(), is(true));
        assertThat("White player is not in stalemate", mt1.getTransitedBoard().getCurrentPlayer().isInStalemate(),
                is(false));

        final MoveTransition mt2 = mt1.getTransitedBoard().getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt1.getTransitedBoard(), BoardUtils.getLocation("b8"),
                        BoardUtils.getLocation("b2")));
        assertThat(mt2.getMoveStatus().isDone(), is(true));
        final MoveTransition mt3 = mt2.getTransitedBoard().getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt2.getTransitedBoard(), BoardUtils.getLocation("h3"),
                        BoardUtils.getLocation("h2")));
        assertThat(mt3.getMoveStatus().isDone(), is(true));
        assertThat("White king is not under check", mt3.getTransitedBoard().getCurrentPlayer().isUnderCheck(),
                is(true));
        final MoveTransition mt4 = mt3.getTransitedBoard().getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt3.getTransitedBoard(), BoardUtils.getLocation("e2"),
                        BoardUtils.getLocation("f3")));
        assertThat(mt4.getMoveStatus().isDone(), is(true));
        final MoveTransition mt5 = mt4.getTransitedBoard().getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt4.getTransitedBoard(), BoardUtils.getLocation("h2"),
                        BoardUtils.getLocation("b2")));
        assertThat(mt5.getMoveStatus().isDone(), is(true));
        assertThat(mt5.getLastMove().isCapturingMove(), is(true));
        assertThat("White player is not in stalemate", mt5.getTransitedBoard().getCurrentPlayer().isInStalemate(),
                is(true));
        assertThat("White player is under check", mt5.getTransitedBoard().getCurrentPlayer().isUnderCheck(), is(false));
        assertThat("White player is checkmated", mt5.getTransitedBoard().getCurrentPlayer().isCheckMate(), is(false));

    }

}
