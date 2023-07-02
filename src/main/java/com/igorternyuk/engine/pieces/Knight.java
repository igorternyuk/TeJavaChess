package com.igorternyuk.engine.pieces;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.igorternyuk.engine.Alliance;
import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.moves.Move;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by igor on 01.12.17.
 */

public class Knight extends Piece {

    private static final Table<Location, Alliance, Knight> ALL_KNIGHTS = createAllPossibleKnights(true);
    private static final Table<Location, Alliance, Knight> ALL_MOVED_KNIGHTS = createAllPossibleKnights(false);
    private int value;

    private static Table<Location, Alliance, Knight> createAllPossibleKnights(boolean isFirstMove) {
        final ImmutableTable.Builder<Location, Alliance, Knight> knights = ImmutableTable.builder();
        for (final Alliance alliance : Alliance.values()) {
            if (isFirstMove) {
                final int backRank = alliance.isWhite() ? BoardUtils.FIRST_RANK : BoardUtils.EIGHTH_RANK;
                for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                    final Location currentLocation = BoardUtils.getLocation(x, backRank);
                    knights.put(currentLocation, alliance, new Knight(currentLocation, alliance, true));
                }

            } else {
                for (int y = 0; y < BoardUtils.BOARD_SIZE; ++y) {
                    for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                        final Location currentLocation = BoardUtils.getLocation(x, y);
                        knights.put(currentLocation, alliance, new Knight(currentLocation, alliance, false));
                    }
                }
            }
        }
        return knights.build();
    }

    public static Knight createKnight(final Location location, final Alliance alliance, final boolean isFirstMove) {
        if (isFirstMove) {
            return ALL_KNIGHTS.get(location, alliance);
        } else {
            return ALL_MOVED_KNIGHTS.get(location, alliance);
        }
    }

    public static Knight createKnight(final int x, final int y, final Alliance alliance, final boolean isFirstMove) {
        return createKnight(BoardUtils.getLocation(x, y), alliance, isFirstMove);
    }

    public static Knight createKnight(final char file, final int rank, final Alliance alliance,
                                      final boolean isFirstMove) {
        return createKnight(BoardUtils.getLocation(file, rank), alliance, isFirstMove);
    }

    public static Knight createKnight(final String algebraicNotationForPosition, final Alliance alliance,
                                      final boolean isFirstMove) {
        return createKnight(BoardUtils.getLocation(algebraicNotationForPosition), alliance, isFirstMove);
    }

    private Knight(final int x, final int y, final Alliance alliance, final boolean isFirstMove) {
        this(BoardUtils.getLocation(x, y), alliance, isFirstMove);
    }

    private Knight(final Location pieceLocation, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.KNIGHT, pieceLocation, pieceAlliance, isFirstMove);
        final int index = pieceLocation.getY() * BoardUtils.BOARD_SIZE + pieceLocation.getX();
        this.value = super.getValue();
        if (pieceAlliance.isWhite()) {
            this.value += PieceSquareTables.WHITE_KNIGHT[index];
        } else if (pieceAlliance.isBlack()) {
            this.value += PieceSquareTables.BLACK_KNIGHT[index];
        }
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public void setPossibleOffsets() {

        this.moveVectors.add(new Point(-2, -1));
        this.moveVectors.add(new Point(-2, 1));
        this.moveVectors.add(new Point(-1, -2));
        this.moveVectors.add(new Point(-1, 2));
        this.moveVectors.add(new Point(1, -2));
        this.moveVectors.add(new Point(1, 2));
        this.moveVectors.add(new Point(2, -1));
        this.moveVectors.add(new Point(2, 1));
    }

    @Override
    public Collection<Move> getLegalMoves(final Board board) {
        return this.getJumpingPieceLegalMoves(board);
    }

    @Override
    public Knight move(final Move move) {
        return ALL_MOVED_KNIGHTS.get(move.getDestination(), move.getMovedPiece().getAlliance());
    }

    private static Map<Location, Knight> createAllPossibleWhiteKnights(final boolean isFirstMove) {
        return createAllPossibleKnights(Alliance.WHITE, isFirstMove);
    }

    private static Map<Location, Knight> createAllPossibleBlackKnights(final boolean isFirstMove) {
        return createAllPossibleKnights(Alliance.BLACK, isFirstMove);
    }

    private static Map<Location, Knight> createAllPossibleKnights(final Alliance alliance, final boolean isFirstMove) {
        Map<Location, Knight> knights = new HashMap<>();
        if (isFirstMove) {
            final int backRank = alliance.isWhite() ? BoardUtils.FIRST_RANK : BoardUtils.EIGHTH_RANK;
            for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                final Location currentLocation = BoardUtils.getLocation(x, backRank);
                knights.put(currentLocation, new Knight(currentLocation, alliance, true));
            }

        } else {
            for (int y = 0; y < BoardUtils.BOARD_SIZE; ++y) {
                for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                    final Location currentLocation = BoardUtils.getLocation(x, y);
                    knights.put(currentLocation, new Knight(currentLocation, alliance, false));
                }
            }
        }
        return ImmutableMap.copyOf(knights);
    }

    /*@Override
    public String toString() {
        return PieceType.KNIGHT.getName();
    }*/
}
