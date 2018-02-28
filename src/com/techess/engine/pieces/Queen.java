package com.techess.engine.pieces;

import com.google.common.collect.ImmutableMap;
import com.techess.engine.Alliance;
import com.techess.engine.board.Board;
import com.techess.engine.board.Move;
import com.techess.engine.board.Position;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by igor on 01.12.17.
 */

public class Queen extends Piece {
    private static final int[] DX = { -1, -1,  0,  1, 1, 1, 0, -1 };
    private static final int[] DY = {  0, -1, -1, -1, 0, 1, 1,  1 };
    private static final Map<Position, Queen> WHITE_ALREADY_MOVED_QUEENS = createAllPossibleWhiteQueens(false);
    private static final Map<Position, Queen> WHITE_NOT_MOVED_QUEENS = createAllPossibleWhiteQueens(true);
    private static final Map<Position, Queen> BLACK_ALREADY_MOVED_QUEENS = createAllPossibleBlackQueens(false);
    private static final Map<Position, Queen> BLACK_NOT_MOVED_QUEENS = createAllPossibleBlackQueens(true);

    public static Queen createQueen(final int x, final int y, final Alliance alliance, final boolean isFirstMove){
        return createQueen(Board.position(x, y), alliance, isFirstMove);
    }

    public static Queen createQueen(final Position position, final Alliance alliance, final boolean isFirstMove){
        if(isFirstMove) {
            return alliance.equals(Alliance.WHITE) ? WHITE_NOT_MOVED_QUEENS.get(position) :
                    BLACK_NOT_MOVED_QUEENS.get(position);
        } else {
            return alliance.equals(Alliance.WHITE) ? WHITE_ALREADY_MOVED_QUEENS.get(position) :
                    BLACK_ALREADY_MOVED_QUEENS.get(position);
        }
    }

    private Queen(final int x, final int y, final Alliance alliance, final boolean isFirstMove)
    {
        this(Board.position(x,y), alliance, isFirstMove);
    }

    private Queen(final Position piecePosition, final Alliance alliance, final boolean isFirstMove) {
        super(PieceType.QUEEN, piecePosition, alliance, isFirstMove);
    }

    @Override
    public Collection<Move> getLegalMoves(final Board board) {
        return getLinearlyMovingPiecesLegalMoves(board, DX, DY);
    }

    @Override
    public Queen move(final Move move) {
        if(move.getMovedPiece().getAlliance().equals(Alliance.WHITE)) {
            return WHITE_ALREADY_MOVED_QUEENS.get(move.getDestination());
        } else {
            return BLACK_ALREADY_MOVED_QUEENS.get(move.getDestination());
        }
    }

    private static final Map<Position,Queen> createAllPossibleWhiteQueens(final boolean isFirstMove) {
        return createAllPossibleQueens(Alliance.WHITE, isFirstMove);
    }

    private static final Map<Position,Queen> createAllPossibleBlackQueens(final boolean isFirstMove) {
        return createAllPossibleQueens(Alliance.BLACK, isFirstMove);
    }

    private static final Map<Position, Queen> createAllPossibleQueens(final Alliance alliance,
                                                                      final boolean isFirstMove){
        Map<Position, Queen> queens = new HashMap<>();
        if(isFirstMove){
            final int backRank = alliance.isWhite() ? Board.FIRST_RANK : Board.EIGHTH_RANK;
            for(int x = 0; x < Board.BOARD_SIZE; ++x){
                final Position currentPosition = Board.position(x,backRank);
                queens.put(currentPosition, new Queen(currentPosition, alliance, true));
            }
        } else {
            for (int y = 0; y < Board.BOARD_SIZE; ++y) {
                for (int x = 0; x < Board.BOARD_SIZE; ++x) {
                    final Position currentPosition = Board.position(x, y);
                    queens.put(currentPosition, new Queen(currentPosition, alliance, false));
                }
            }
        }
        return ImmutableMap.copyOf(queens);
    }

    @Override
    public String toString() {
        return PieceType.QUEEN.getName();
    }
}
