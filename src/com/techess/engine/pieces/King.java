package com.techess.engine.pieces;

import com.google.common.collect.ImmutableMap;
import com.techess.engine.Alliance;
import com.techess.engine.board.Board;
import com.techess.engine.moves.Move;
import com.techess.engine.board.Position;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by igor on 01.12.17.
 */

public class King extends Piece {
    private static final int[] DX = { -1, -1,  0,  1, 1, 1, 0, -1 };
    private static final int[] DY = {  0, -1, -1, -1, 0, 1, 1,  1 };
    private static final Map<Position, King> WHITE_ALREADY_MOVED_KINGS = createAllPossibleWhiteKings(false);
    private static final Map<Position, King> WHITE_NOT_MOVED_KINGS = createAllPossibleWhiteKings(true);
    private static final Map<Position, King> BLACK_ALREADY_MOVED_KINGS = createAllPossibleBlackKings(false);
    private static final Map<Position, King> BLACK_NOT_MOVED_KINGS = createAllPossibleBlackKings(true);

    public static King createKing(final Position position, final Alliance alliance, final boolean isFirstMove){
        if(isFirstMove) {
            return alliance.equals(Alliance.WHITE) ? WHITE_NOT_MOVED_KINGS.get(position) :
                    BLACK_NOT_MOVED_KINGS.get(position);
        } else {
            return alliance.equals(Alliance.WHITE) ? WHITE_ALREADY_MOVED_KINGS.get(position) :
                    BLACK_ALREADY_MOVED_KINGS.get(position);
        }
    }

    public static King createKing(final int x, final int y, final Alliance alliance, final boolean isFirstMove){
        return createKing(Board.getPosition(x,y), alliance, isFirstMove);
    }

    public static King createKing(final char file, final int rank, final Alliance alliance,
                                      final boolean isFirstMove){
        return createKing(Board.getPosition(file,rank), alliance, isFirstMove);
    }

    public static King createKing(final String algebraicNotationForPosition, final Alliance alliance,
                                      final boolean isFirstMove){
        return createKing(Board.getPosition(algebraicNotationForPosition), alliance, isFirstMove);
    }

    private King(final int x, final int y, final Alliance alliance, final boolean isFirstMove) {
        this(Board.getPosition(x, y), alliance, isFirstMove);
    }

    private King(final Position piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.KING, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> getLegalMoves(final Board board) {
        return this.getOneStepMovingPieceLegalMoves(board, DX, DY);
    }

    @Override
    public King move(final Move move) {
        if(move.getMovedPiece().getAlliance().equals(Alliance.WHITE)) {
            return WHITE_ALREADY_MOVED_KINGS.get(move.getDestination());
        } else {
            return BLACK_ALREADY_MOVED_KINGS.get(move.getDestination());
        }
    }

    private static final Map<Position, King> createAllPossibleWhiteKings(final boolean isFirstMove) {
        return createAllPossibleKings(Alliance.WHITE, isFirstMove);
    }

    private static final Map<Position, King> createAllPossibleBlackKings(final boolean isFirstMove) {
        return createAllPossibleKings(Alliance.BLACK, isFirstMove);
    }

    private static Map<Position, King> createAllPossibleKings(final Alliance alliance, final boolean isFirstMove) {
        Map<Position, King> kings = new HashMap<>();
        if(isFirstMove){
            final int backRank = alliance.isWhite() ? Board.FIRST_RANK : Board.EIGHTH_RANK;
            for(int x = 1; x < Board.BOARD_SIZE - 1; ++x){
                /*We did not include first and last positions(corners -a1 and h1) because in Random Fisher Chess
                 the king must be located between rooks*/
                final Position currentPosition = Board.getPosition(x, backRank);
                kings.put(currentPosition, new King(Board.getPosition(x, backRank), alliance, true));
            }
        } else {
            for (int y = 0; y < Board.BOARD_SIZE; ++y) {
                for (int x = 0; x < Board.BOARD_SIZE; ++x) {
                    final Position currentPosition = Board.getPosition(x, y);
                    kings.put(currentPosition, new King(currentPosition, alliance, false));
                }
            }
        }
        return ImmutableMap.copyOf(kings);
    }

    /*@Override
    public String toString() {
        return PieceType.KING.getName().toUpperCase() + Board.getChessNotationTileName(this.getPosition);
    }*/
}
