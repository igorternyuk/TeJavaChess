package com.igorternyuk.engine.pieces;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.igorternyuk.engine.Alliance;
import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.moves.Move;

import java.awt.*;
import java.util.Collection;

/**
 * Created by igor on 01.12.17.
 */

public class Queen extends Piece {

    private static final Table<Location, Alliance, Queen> ALL_QUEENS = createAllPossibleQueens(true);
    private static final Table<Location, Alliance, Queen> ALL_MOVED_QUEENS = createAllPossibleQueens(false);

    private static Table<Location, Alliance, Queen> createAllPossibleQueens(final boolean isFirstMove) {
        final ImmutableTable.Builder<Location, Alliance, Queen> queens = ImmutableTable.builder();
        for (final Alliance alliance : Alliance.values()) {
            if (isFirstMove) {
                final int backRank = alliance.isWhite() ? BoardUtils.FIRST_RANK : BoardUtils.EIGHTH_RANK;
                for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                    final Location currentLocation = BoardUtils.getPosition(x, backRank);
                    queens.put(currentLocation, alliance, new Queen(currentLocation, alliance, true));
                }

            } else {
                for (int y = 0; y < BoardUtils.BOARD_SIZE; ++y) {
                    for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                        final Location currentLocation = BoardUtils.getPosition(x, y);
                        queens.put(currentLocation, alliance, new Queen(currentLocation, alliance, false));
                    }
                }
            }
        }
        return queens.build();
    }

    public static Queen createQueen(final Location location, final Alliance alliance, final boolean isFirstMove) {
        if(isFirstMove) {
            return ALL_QUEENS.get(location, alliance);
        } else {
            return ALL_MOVED_QUEENS.get(location, alliance);
        }
    }

    public static Queen createQueen(final int x, final int y, final Alliance alliance, final boolean isFirstMove){
        return createQueen(BoardUtils.getPosition(x, y), alliance, isFirstMove);
    }

    public static Queen createQueen(final char file, final int rank, final Alliance alliance,
                                  final boolean isFirstMove){
        return createQueen(BoardUtils.getPosition(file,rank), alliance, isFirstMove);
    }

    public static Queen createQueen(final String algebraicNotationForPosition, final Alliance alliance,
                                  final boolean isFirstMove){
        return createQueen(BoardUtils.getPosition(algebraicNotationForPosition), alliance, isFirstMove);
    }

    private Queen(final int x, final int y, final Alliance alliance, final boolean isFirstMove)
    {
        this(BoardUtils.getPosition(x,y), alliance, isFirstMove);
    }

    private Queen(final Location pieceLocation, final Alliance alliance, final boolean isFirstMove) {
        super(PieceType.QUEEN, pieceLocation, alliance, isFirstMove);
    }

    @Override
    public void setPossibleOffsets() {
        this.moveOffsets.add(new Point(-1, -1));
        this.moveOffsets.add(new Point(-1, 1));
        this.moveOffsets.add(new Point(1, -1));
        this.moveOffsets.add(new Point(1, 1));
        this.moveOffsets.add(new Point(-1, 0));
        this.moveOffsets.add(new Point(1, 0));
        this.moveOffsets.add(new Point(0, -1));
        this.moveOffsets.add(new Point(0, 1));
    }

    @Override
    public Collection<Move> getLegalMoves(final Board board) {
        return getSlidingPieceLegalMoves(board);
    }

    @Override
    public Queen move(final Move move) {
        return ALL_MOVED_QUEENS.get(move.getDestination(), move.getMovedPiece().getAlliance());
    }

   /* @Override
    public String toString() {
        return PieceType.QUEEN.getName().toUpperCase() + Board.getChessNotationTileName(this.getLocation);
    }*/
}
