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

public class Bishop extends Piece {
    private static final Table<Location, Alliance, Bishop> ALL_BISHOPS = createAllPossibleBishops(true);
    private static final Table<Location, Alliance, Bishop> ALL_MOVED_BISHOPS = createAllPossibleBishops(false);
    private int value;

    private static Table<Location, Alliance, Bishop> createAllPossibleBishops(final boolean isFirstMove) {
        final ImmutableTable.Builder<Location, Alliance, Bishop> bishops = ImmutableTable.builder();
        for (final Alliance alliance : Alliance.values()) {
            if (isFirstMove) {
                final int backRank = alliance.isWhite() ? BoardUtils.FIRST_RANK : BoardUtils.EIGHTH_RANK;
                for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                    final Location currentLocation = BoardUtils.getLocation(x, backRank);
                    bishops.put(currentLocation, alliance, new Bishop(currentLocation, alliance, true));
                }

            } else {
                for (int y = 0; y < BoardUtils.BOARD_SIZE; ++y) {
                    for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                        final Location currentLocation = BoardUtils.getLocation(x, y);
                        bishops.put(currentLocation, alliance, new Bishop(currentLocation, alliance, false));
                    }
                }
            }
        }
        return bishops.build();
    }

    public static Bishop createBishop(final Location location, final Alliance alliance, final boolean isFirstMove) {
        if(isFirstMove) {
            return ALL_BISHOPS.get(location, alliance);
        } else {
            return ALL_MOVED_BISHOPS.get(location, alliance);
        }
    }

    public static Bishop createBishop(final int x, final int y, final Alliance alliance, final boolean isFirstMove){
        return createBishop(BoardUtils.getLocation(x, y), alliance, isFirstMove);
    }

    public static Bishop createBishop(final char file, final int rank, final Alliance alliance,
                                      final boolean isFirstMove){
        return createBishop(BoardUtils.getLocation(file, rank), alliance, isFirstMove);
    }

    public static Bishop createBishop(final String algebraicNotationForPosition, final Alliance alliance,
                                      final boolean isFirstMove){
        return createBishop(BoardUtils.getLocation(algebraicNotationForPosition), alliance, isFirstMove);
    }

    private Bishop(final int x, final int y, final Alliance alliance, final boolean isFirstMove) {
        this(BoardUtils.getLocation(x, y), alliance, isFirstMove);

    }

    private Bishop(final Location pieceLocation, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.BISHOP, pieceLocation, pieceAlliance, isFirstMove);
        final int index = pieceLocation.getY() * BoardUtils.BOARD_SIZE + pieceLocation.getX();
        this.value = super.getValue();
        if (pieceAlliance.isWhite()) {
            this.value += PieceSquareTables.WHITE_BISHOP[index];
        } else if (pieceAlliance.isBlack()) {
            this.value += PieceSquareTables.BLACK_BISHOP[index];
        }
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public void setPossibleOffsets() {
        this.moveVectors.add(new Point(-1, -1));
        this.moveVectors.add(new Point(-1, 1));
        this.moveVectors.add(new Point(1, -1));
        this.moveVectors.add(new Point(1, 1));
    }

    @Override
    public Collection<Move> getLegalMoves(final Board board) {
        return getSlidingPieceLegalMoves(board);
    }

    @Override
    public Bishop move(final Move move) {
        return ALL_MOVED_BISHOPS.get(move.getDestination(), move.getMovedPiece().getAlliance());
    }



    /*@Override
    public String toString() {
        return PieceType.BISHOP.getName().toUpperCase() + Board.getChessNotationTileName(this.getLocation);
    }*/
}
