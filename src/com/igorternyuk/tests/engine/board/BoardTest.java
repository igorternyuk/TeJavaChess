package com.igorternyuk.tests.engine.board;

import com.google.common.collect.Iterables;
import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.moves.Move;
import com.igorternyuk.engine.pieces.Piece;
import com.igorternyuk.engine.pieces.PieceType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by igor on 08.03.18.
 */
public class BoardTest {

    @Test
    public void initialStandardBoard(){
        Board standardBoard = Board.createStandardBoard();

        assertThat(standardBoard.getCurrentPlayer().getLegalMoves().size(), is(20));
        assertEquals(standardBoard.getCurrentPlayer().getOpponentLegalMoves().size(), 20);
        assertEquals(standardBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(move -> move.isPawnMove()).count(), 16);
        assertEquals(standardBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(move -> move.isPawnMove()).count(), 16);
        assertEquals(standardBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(move -> move.getMovedPiece().getPieceType().equals(PieceType.KNIGHT)).count(), 4);
        assertEquals(standardBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(move -> move.getMovedPiece().getPieceType().equals(PieceType.KNIGHT)).count(), 4);
        assertEquals(standardBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter( move->move.isCastlingMove()).count(), 0);
        assertEquals(standardBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(move->move.isCastlingMove()).count(), 0);
        assertEquals(standardBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(move -> move.isCapturingMove()).count(), 0);
        assertEquals(standardBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(move -> move.isCapturingMove()).count(), 0);
        assertEquals(standardBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(move -> move.isPawnPromotionMove()).count(), 0);
        assertEquals(standardBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(move -> move.isPawnPromotionMove()).count(), 0);

        assertFalse(standardBoard.getCurrentPlayer().isUnderCheck());
        assertFalse(standardBoard.getCurrentPlayer().isCheckMate());
        assertFalse(standardBoard.getCurrentPlayer().isInStalemate());
        assertFalse(standardBoard.getCurrentPlayer().isCastled());

        assertFalse(standardBoard.getCurrentPlayer().getOpponent().isUnderCheck());
        assertFalse(standardBoard.getCurrentPlayer().getOpponent().isCheckMate());
        assertFalse(standardBoard.getCurrentPlayer().getOpponent().isInStalemate());
        assertFalse(standardBoard.getCurrentPlayer().getOpponent().isCastled());

        assertEquals(standardBoard.getCurrentPlayer(), standardBoard.getWhitePlayer());
        assertEquals(standardBoard.getCurrentPlayer().getOpponent(), standardBoard.getBlackPlayer());

        assertEquals(standardBoard.getCurrentPlayer().toString(), "White");
        assertEquals(standardBoard.getCurrentPlayer().getOpponent().toString(), "Black");

        assertEquals(standardBoard.getWhitePieces().size(), 16);
        assertEquals(standardBoard.getWhitePieces().stream().filter(piece -> piece.getPieceType().isPawn())
                .count(), 8);
        assertEquals(standardBoard.getBlackPieces().size(), 16);
        assertEquals(standardBoard.getBlackPieces().stream().filter(piece -> piece.getPieceType().isPawn())
                .count(), 8);
        assertEquals(standardBoard.getAllActivePieces().size(), 32);
        assertTrue(standardBoard.getTile("a1").isOccupied());
        assertTrue(standardBoard.getTile("a1").isTileDark());
        assertTrue(standardBoard.getTile("a1").getPiece().getPieceType().isRook());
        assertTrue(standardBoard.getTile("a1").getPiece().getAlliance().isWhite());
        assertTrue(standardBoard.getTile("e1").isOccupied());
        assertTrue(standardBoard.getTile("e1").getPiece().getPieceType().isKing());
        assertTrue(standardBoard.getTile("e1").getPiece().getAlliance().isWhite());
        assertTrue(standardBoard.getTile("c1").isOccupied());
        assertTrue(standardBoard.getTile("c1").getPiece().getPieceType().equals(PieceType.BISHOP));
        assertTrue(standardBoard.getTile("c1").isTileDark());
        assertTrue(standardBoard.getTile("c1").getPiece().getAlliance().isWhite());
        assertTrue(standardBoard.getTile("d1").isOccupied());
        assertTrue(standardBoard.getTile("d1").getPiece().getPieceType().equals(PieceType.QUEEN));
        assertTrue(standardBoard.getTile("d1").isTileLight());
        assertTrue(standardBoard.getTile("d1").getPiece().getAlliance().isWhite());
        assertTrue(standardBoard.getTile("f1").isOccupied());
        assertTrue(standardBoard.getTile("f1").getPiece().getPieceType().equals(PieceType.BISHOP));
        assertTrue(standardBoard.getTile("f1").isTileLight());
        assertTrue(standardBoard.getTile("f1").getPiece().getAlliance().isWhite());

        assertTrue(standardBoard.getTile("a8").isOccupied());
        assertTrue(standardBoard.getTile("a8").isTileLight());
        assertTrue(standardBoard.getTile("a8").getPiece().getPieceType().isRook());
        assertTrue(standardBoard.getTile("a8").getPiece().getAlliance().isBlack());
        assertTrue(standardBoard.getTile("e8").isOccupied());
        assertTrue(standardBoard.getTile("e8").getPiece().getPieceType().isKing());
        assertTrue(standardBoard.getTile("e8").getPiece().getAlliance().isBlack());

        assertTrue(standardBoard.getTile("c8").isOccupied());
        assertTrue(standardBoard.getTile("c8").getPiece().getPieceType().equals(PieceType.BISHOP));
        assertTrue(standardBoard.getTile("c8").isTileLight());
        assertTrue(standardBoard.getTile("c8").getPiece().getAlliance().isBlack());
        assertTrue(standardBoard.getTile("d8").isOccupied());
        assertTrue(standardBoard.getTile("d8").getPiece().getPieceType().equals(PieceType.QUEEN));
        assertTrue(standardBoard.getTile("d8").isTileDark());
        assertTrue(standardBoard.getTile("d8").getPiece().getAlliance().isBlack());
        assertTrue(standardBoard.getTile("f8").isOccupied());
        assertTrue(standardBoard.getTile("f8").getPiece().getPieceType().equals(PieceType.BISHOP));
        assertTrue(standardBoard.getTile("f8").isTileDark());
        assertTrue(standardBoard.getTile("f8").getPiece().getAlliance().isBlack());

        final Iterable<Move> allMoves = standardBoard.getAllLegalMoves();
        assertEquals(Iterables.size(allMoves), 40);
        allMoves.forEach(move ->{
            assertFalse(move.isCapturingMove());
            assertFalse(move.isPawnPromotionMove());
            assertFalse(move.isCastlingMove());
        });

        assertEquals(standardBoard.getTile(0,0), standardBoard.getTile("a8"));
        assertEquals(standardBoard.getTile(7,7), standardBoard.getTile("h1"));

    }

    @Test
    public void initialRandomFisherChessBoard(){
        Board randomBoard = Board.createBoardForChess960();

        final int currentPlayerLegalMovesCount = randomBoard.getCurrentPlayer().getLegalMoves().size();
        assertTrue(currentPlayerLegalMovesCount >= 18 && currentPlayerLegalMovesCount <= 20);
        final int opponentLegalMovesCount = randomBoard.getCurrentPlayer().getOpponentLegalMoves().size();
        assertTrue(opponentLegalMovesCount >= 18 && opponentLegalMovesCount <= 20);

        assertEquals(randomBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(move -> move.isPawnMove()).count(), 16);
        assertEquals(randomBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(move -> move.isPawnMove()).count(), 16);

        final long whiteKnightMoveCount = randomBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(move -> move.getMovedPiece().getPieceType().equals(PieceType.KNIGHT)).count();
        assertTrue(whiteKnightMoveCount >= 2 && whiteKnightMoveCount <= 4);
        final long blackKnightMoveCount = randomBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(move -> move.getMovedPiece().getPieceType().equals(PieceType.KNIGHT)).count();
        assertTrue(blackKnightMoveCount >= 2 && blackKnightMoveCount <= 4);

        assertEquals(randomBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(move -> move.isCapturingMove()).count(), 0);
        assertEquals(randomBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(move -> move.isCapturingMove()).count(), 0);
        assertEquals(randomBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(move -> move.isPawnPromotionMove()).count(), 0);
        assertEquals(randomBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(move -> move.isPawnPromotionMove()).count(), 0);

        assertFalse(randomBoard.getCurrentPlayer().isUnderCheck());
        assertFalse(randomBoard.getCurrentPlayer().isCheckMate());
        assertFalse(randomBoard.getCurrentPlayer().isInStalemate());
        assertFalse(randomBoard.getCurrentPlayer().isCastled());

        assertFalse(randomBoard.getCurrentPlayer().getOpponent().isUnderCheck());
        assertFalse(randomBoard.getCurrentPlayer().getOpponent().isCheckMate());
        assertFalse(randomBoard.getCurrentPlayer().getOpponent().isInStalemate());
        assertFalse(randomBoard.getCurrentPlayer().getOpponent().isCastled());

        assertEquals(randomBoard.getCurrentPlayer(), randomBoard.getWhitePlayer());
        assertEquals(randomBoard.getCurrentPlayer().getOpponent(), randomBoard.getBlackPlayer());

        assertEquals(randomBoard.getCurrentPlayer().toString(), "White");
        assertEquals(randomBoard.getCurrentPlayer().getOpponent().toString(), "Black");

        assertEquals(randomBoard.getWhitePieces().size(), 16);
        assertEquals(randomBoard.getWhitePieces().stream().filter(piece -> piece.getPieceType().isPawn())
                .count(), 8);
        assertEquals(randomBoard.getBlackPieces().size(), 16);
        assertEquals(randomBoard.getBlackPieces().stream().filter(piece -> piece.getPieceType().isPawn())
                .count(), 8);
        assertEquals(randomBoard.getAllActivePieces().size(), 32);

        final Iterable<Move> allMoves = randomBoard.getAllLegalMoves();
        assertTrue(Iterables.size(allMoves) >= 36 && Iterables.size(allMoves) <= 40);
        allMoves.forEach(move ->{
            assertFalse(move.isCapturingMove());
            assertFalse(move.isPawnPromotionMove());
        });

        assertTrue(randomBoard.getQueensRookStartCoordinateX() <
                randomBoard.getCurrentPlayer().getPlayerKing().getLocation().getX());
        assertTrue(randomBoard.getKingsRookStartCoordinateX() >
                randomBoard.getCurrentPlayer().getPlayerKing().getLocation().getX());
        assertTrue(randomBoard.getQueensRookStartCoordinateX() <
                randomBoard.getCurrentPlayer().getOpponent().getPlayerKing().getLocation().getX());
        assertTrue(randomBoard.getKingsRookStartCoordinateX() >
                randomBoard.getCurrentPlayer().getOpponent().getPlayerKing().getLocation().getX());
        assertEquals(randomBoard.getCurrentPlayer().getPlayerKing().getLocation().getX(),
                randomBoard.getCurrentPlayer().getOpponent().getPlayerKing().getLocation().getX());

        for(int x = 0; x < BoardUtils.BOARD_SIZE; ++x){
            final Piece currentWhitePiece =
                    randomBoard.getTile(x, BoardUtils.FIRST_RANK).getPiece();
            final Piece currentBlackPiece =
                    randomBoard.getTile(x, BoardUtils.EIGHTH_RANK).getPiece();
            assertEquals(currentWhitePiece.getPieceType(), currentBlackPiece.getPieceType());
            assertTrue(currentWhitePiece.getAlliance().isWhite());
            assertTrue(currentBlackPiece.getAlliance().isBlack());
        }

        final List<Piece> whiteBishops = new ArrayList<>();
        randomBoard.getCurrentPlayer().getActivePieces().stream()
                .filter(piece -> piece.getPieceType().equals(PieceType.BISHOP)).forEach(bishop ->{
                    whiteBishops.add(bishop);
        });

        assertEquals(whiteBishops.size(), 2);
        assertTrue((randomBoard.getTile(whiteBishops.get(0).getLocation()).isTileLight() &&
                randomBoard.getTile(whiteBishops.get(1).getLocation()).isTileDark()) ||
                (randomBoard.getTile(whiteBishops.get(0).getLocation()).isTileDark() &&
                        randomBoard.getTile(whiteBishops.get(1).getLocation()).isTileLight()));

        final List<Piece> blackBishops = new ArrayList<>();
        randomBoard.getCurrentPlayer().getOpponent().getActivePieces().stream()
                .filter(piece -> piece.getPieceType().equals(PieceType.BISHOP)).forEach(bishop ->{
            blackBishops.add(bishop);
        });

        assertEquals(blackBishops.size(), 2);
        assertTrue((randomBoard.getTile(blackBishops.get(0).getLocation()).isTileLight() &&
                randomBoard.getTile(blackBishops.get(1).getLocation()).isTileDark()) ||
                (randomBoard.getTile(blackBishops.get(0).getLocation()).isTileDark() &&
                        randomBoard.getTile(blackBishops.get(1).getLocation()).isTileLight()));
    }
}