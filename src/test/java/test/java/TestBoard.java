package test.java;

import com.google.common.collect.Iterables;
import com.igorternyuk.engine.Alliance;
import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.moves.Move;
import com.igorternyuk.engine.moves.MoveTransition;
import com.igorternyuk.engine.pieces.Piece;
import com.igorternyuk.engine.pieces.PieceType;
import com.igorternyuk.engine.player.ai.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by igor on 08.03.18.
 */
public class TestBoard {

    @Test
    public void initialStandardBoard(){
        Board standardBoard = Board.createStandardBoard();

        assertThat(standardBoard.getCurrentPlayer().getLegalMoves().size(), is(20));
        assertEquals(standardBoard.getCurrentPlayer().getOpponentLegalMoves().size(), 20);
        assertEquals(standardBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(Move::isPawnMove).count(), 16);
        assertEquals(standardBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(Move::isPawnMove).count(), 16);
        assertEquals(standardBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(move -> move.getMovedPiece().getPieceType().equals(PieceType.KNIGHT)).count(), 4);
        assertEquals(standardBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(move -> move.getMovedPiece().getPieceType().equals(PieceType.KNIGHT)).count(), 4);
        assertEquals(standardBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(Move::isCastlingMove).count(), 0);
        assertEquals(standardBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(Move::isCastlingMove).count(), 0);
        assertEquals(standardBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(Move::isCapturingMove).count(), 0);
        assertEquals(standardBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(Move::isCapturingMove).count(), 0);
        assertEquals(standardBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(Move::isPawnPromotionMove).count(), 0);
        assertEquals(standardBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(Move::isPawnPromotionMove).count(), 0);

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

        assertEquals(standardBoard.getCurrentPlayer().toString(), "White player");
        assertEquals(standardBoard.getCurrentPlayer().getOpponent().toString(), "Black player");

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
        allMoves.forEach(move -> {
            assertFalse(move.isCapturingMove());
            assertFalse(move.isPawnPromotionMove());
            assertFalse(move.isCastlingMove());
        });

        assertEquals(standardBoard.getTile("a8"), standardBoard.getTile(0, 0));
        assertEquals(standardBoard.getTile("h1"), standardBoard.getTile(7, 7));

    }

    @Test
    public void initialRandomFisherChessBoard(){
        Board randomBoard = Board.createBoardForChess960();

        final int currentPlayerLegalMovesCount = randomBoard.getCurrentPlayer().getLegalMoves().size();
        assertTrue(currentPlayerLegalMovesCount >= 18 && currentPlayerLegalMovesCount <= 20);
        final int opponentLegalMovesCount = randomBoard.getCurrentPlayer().getOpponentLegalMoves().size();
        assertTrue(opponentLegalMovesCount >= 18 && opponentLegalMovesCount <= 20);

        assertEquals(randomBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(Move::isPawnMove).count(), 16);
        assertEquals(randomBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(Move::isPawnMove).count(), 16);

        final long whiteKnightMoveCount = randomBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(move -> move.getMovedPiece().getPieceType().equals(PieceType.KNIGHT)).count();
        assertTrue(whiteKnightMoveCount >= 2 && whiteKnightMoveCount <= 4);
        final long blackKnightMoveCount = randomBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(move -> move.getMovedPiece().getPieceType().equals(PieceType.KNIGHT)).count();
        assertTrue(blackKnightMoveCount >= 2 && blackKnightMoveCount <= 4);

        assertEquals(randomBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(Move::isCapturingMove).count(), 0);
        assertEquals(randomBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(Move::isCapturingMove).count(), 0);
        assertEquals(randomBoard.getCurrentPlayer().getLegalMoves().stream()
                .filter(Move::isPawnPromotionMove).count(), 0);
        assertEquals(randomBoard.getCurrentPlayer().getOpponentLegalMoves().stream()
                .filter(Move::isPawnPromotionMove).count(), 0);

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

        assertEquals(randomBoard.getCurrentPlayer().toString(), "White player");
        assertEquals(randomBoard.getCurrentPlayer().getOpponent().toString(), "Black player");

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
                .filter(piece -> piece.getPieceType().equals(PieceType.BISHOP)).forEach(whiteBishops::add);

        assertEquals(whiteBishops.size(), 2);
        assertTrue((randomBoard.getTile(whiteBishops.get(0).getLocation()).isTileLight() &&
                randomBoard.getTile(whiteBishops.get(1).getLocation()).isTileDark()) ||
                (randomBoard.getTile(whiteBishops.get(0).getLocation()).isTileDark() &&
                        randomBoard.getTile(whiteBishops.get(1).getLocation()).isTileLight()));

        final List<Piece> blackBishops = new ArrayList<>();
        randomBoard.getCurrentPlayer().getOpponent().getActivePieces().stream()
                .filter(piece -> piece.getPieceType().equals(PieceType.BISHOP)).forEach(blackBishops::add);

        assertEquals(2, blackBishops.size());
        assertTrue((randomBoard.getTile(blackBishops.get(0).getLocation()).isTileLight() &&
                randomBoard.getTile(blackBishops.get(1).getLocation()).isTileDark()) ||
                (randomBoard.getTile(blackBishops.get(0).getLocation()).isTileDark() &&
                        randomBoard.getTile(blackBishops.get(1).getLocation()).isTileLight()));
    }

    @Test
    public void testFoolsMate() {
        final Board board = Board.createStandardBoard();
        MoveTransition mt1 = board.getCurrentPlayer().makeMove(Move.MoveFactory.createMove(board, "f2", "f4"));
        assertThat(mt1.getMoveStatus().isDone(), is(true));
        final MoveTransition mt2 = mt1.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt1.getTransitedBoard(), "e7", "e6"));
        assertThat(mt2.getMoveStatus().isDone(), is(true));
        final MoveTransition mt3 = mt2.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt2.getTransitedBoard(), "g2", "g4"));
        assertThat(mt3.getMoveStatus().isDone(), is(true));
        /*final MoveTransition mt4 = mt3.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt3.getTransitedBoard(), "d8", "h4"));
        assertThat(mt4.getMoveStatus().isDone(), is(true));
        assertThat(mt4.getTransitedBoard().getWhitePlayer().isCheckMate(), is(true));*/

        final MoveStrategy moveStrategy = new MiniMax(3);
        final Move aiMove = moveStrategy.execute(mt3.getTransitedBoard());
        System.out.println("Moved piece = " + aiMove.getMovedPiece() + " dest = " + aiMove.getDestination());
        final Move bestMove = Move.MoveFactory.createMove(mt3.getTransitedBoard(), "d8", "h4");
        assertEquals(bestMove, aiMove);
    }

    @Test
    public void testCheckMate2() {
        String[][] pattern = {
                {"[ ]", "[R]", "[ ]", "[.]", "[ ]", "[.]", "[K]", "[.]"},
                {"[r]", "[B]", "[B]", "[ ]", "[.]", "[ ]", "[P]", "[ ]"},
                {"[ ]", "[P]", "[ ]", "[.]", "[P]", "[.]", "[ ]", "[.]"},
                {"[.]", "[ ]", "[.]", "[Q]", "[n]", "[ ]", "[.]", "[P]"},
                {"[ ]", "[.]", "[n]", "[p]", "[ ]", "[.]", "[ ]", "[.]"},
                {"[.]", "[ ]", "[.]", "[ ]", "[q]", "[ ]", "[p]", "[p]"},
                {"[ ]", "[.]", "[ ]", "[.]", "[ ]", "[p]", "[ ]", "[k]"},
                {"[.]", "[ ]", "[.]", "[ ]", "[.]", "[ ]", "[.]", "[ ]"}
        };

        Board board = Board.createBoardByPattern(pattern, Alliance.BLACK);
        System.out.println(board.toDecoratedString());

        final MoveStrategy moveStrategy = new AlphaBeta(4);
        final Move aiMove = moveStrategy.execute(board);
        System.out.println("Moved piece = " + aiMove.getMovedPiece() + " dest = " + aiMove.getDestination());
        final Move bestMove = Move.MoveFactory.createMove(board, "d5", "h1");
        assertEquals(bestMove, aiMove);
    }

    @Test
    public void testEval1() {
        final Board board = Board.createStandardBoard();
        MoveTransition mt1 = board.getCurrentPlayer().makeMove(Move.MoveFactory.createMove(board, "f2", "f4"));
        assertThat(mt1.getMoveStatus().isDone(), is(true));
        final MoveTransition mt2 = mt1.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt1.getTransitedBoard(), "d7", "d5"));
        assertThat(mt2.getMoveStatus().isDone(), is(true));
        final MoveTransition mt3 = mt2.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt2.getTransitedBoard(), "e2", "e3"));
        assertThat(mt3.getMoveStatus().isDone(), is(true));

        final MoveTransition mt4 = mt3.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt3.getTransitedBoard(), "c7", "c5"));
        assertThat(mt4.getMoveStatus().isDone(), is(true));

        final MoveTransition mt5 = mt4.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt4.getTransitedBoard(), "f1", "b5"));
        assertThat(mt5.getMoveStatus().isDone(), is(true));

        final MoveTransition mt6 = mt5.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt5.getTransitedBoard(), "b8", "c6"));
        assertThat(mt6.getMoveStatus().isDone(), is(true));

        final MoveTransition mt7 = mt6.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt6.getTransitedBoard(), "b5", "c6"));
        assertThat(mt7.getMoveStatus().isDone(), is(true));

        final MoveTransition mt8 = mt7.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt7.getTransitedBoard(), "b7", "c6"));
        assertThat(mt8.getMoveStatus().isDone(), is(true));

        final MoveStrategy moveStrategy = new MiniMax(4);
        final Move aiMove = moveStrategy.execute(mt8.getTransitedBoard());
        System.out.println("Move " + aiMove.toString() + " Moved piece = " + aiMove.getMovedPiece() + " dest = " + aiMove.getDestination());
        System.out.println(mt8.getTransitedBoard().toDecoratedString());
        //final Move bestMove = Move.MoveFactory.createMove(mt3.getTransitedBoard(), "d8", "h4");
        //assertEquals(aiMove, bestMove);
    }

    @Test
    public void testAI() {
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
                .makeMove(Move.MoveFactory.createMove(mt3.getTransitedBoard(), "d8", "g5"));
        assertThat(mt4.getMoveStatus().isDone(), is(true));
        final MoveTransition mt5 = mt4.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt4.getTransitedBoard(), "f1", "c4"));
        assertThat(mt5.getMoveStatus().isDone(), is(true));
        final MoveTransition mt6 = mt5.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt5.getTransitedBoard(), "g8", "f6"));
        assertThat(mt6.getMoveStatus().isDone(), is(true));
        //final MoveStrategy moveStrategy = new AlphaBeta(3);
        //final Move aiMove = moveStrategy.execute(mt3.getTransitedBoard());
        final Collection<Move> history = BoardUtils.getMoveHistory(mt6.getTransitedBoard(), 4);
        System.out.println("History:");
        history.forEach(System.out::println);
        System.out.println("Sorted moves:");
        Collection<Move> sortedMoves = MoveSorter.SMART.sort(mt6.getTransitedBoard().getCurrentPlayer().getLegalMoves());
        sortedMoves.forEach(System.out::println);

        KingSafetyAnalyzer ksa = new KingSafetyAnalyzer(mt6.getTransitedBoard().getCurrentPlayer());
        Set<Location> zoneW = ksa.getKingsZone();
        System.out.println("White King's zone:");
        zoneW.forEach(l -> {
            System.out.println(BoardUtils.getAlgebraicNotationFromLocation(l));
        });

        KingSafetyAnalyzer ksa2 = new KingSafetyAnalyzer(mt5.getTransitedBoard().getCurrentPlayer());
        Set<Location> zoneB = ksa2.getKingsZone();
        System.out.println("Black King's zone:");

        zoneB.forEach(l -> {
            System.out.println(BoardUtils.getAlgebraicNotationFromLocation(l));
        });

        final MoveTransition mt7 = mt6.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt6.getTransitedBoard(), "e1", "g1"));
        assertThat(mt7.getMoveStatus().isDone(), is(true));

        final MoveTransition mt8 = mt7.getTransitedBoard()
                .getCurrentPlayer()
                .makeMove(Move.MoveFactory.createMove(mt7.getTransitedBoard(), "f8", "e7"));
        assertThat(mt8.getMoveStatus().isDone(), is(true));

        KingSafetyAnalyzer kingSafetyAnalyzer = new KingSafetyAnalyzer(mt8.getTransitedBoard().getCurrentPlayer());
        System.out.println("kingSafetyAnalyzer.scoreEnemyAttackPosibility() = " + kingSafetyAnalyzer.scoreEnemyAttackPosibility());
        System.out.println("kingSafetyAnalyzer.scoreEnemyPawnStorm() = " + kingSafetyAnalyzer.scoreEnemyPawnStorm());
        System.out.println("kingSafetyAnalyzer.scorePawnShield() = " + kingSafetyAnalyzer.scorePawnShield());
        System.out.println("kingSafetyAnalyzer.scoreOpenFilesThreats() = " + kingSafetyAnalyzer.scoreOpenFilesThreats());
        //System.out.println("aiMove = " + aiMove);
        //final Move bestMove = Move.MoveFactory.createMove(mt3.getTransitedBoard(), "d8", "h4");
        //assertEquals(aiMove, bestMove);
    }
}