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

public class King extends Piece {

    private static final Table<Location, Alliance, King> ALL_KINGS = createAllPossibleKings(true);
    private static final Table<Location, Alliance, King> ALL_MOVED_KINGS = createAllPossibleKings(false);
    private int value;

    private static Table<Location, Alliance, King> createAllPossibleKings(final boolean isFirstMove) {
        final ImmutableTable.Builder<Location, Alliance, King> kings = ImmutableTable.builder();
        for (final Alliance alliance : Alliance.values()) {
            if (isFirstMove) {
                final int backRank = alliance.isWhite() ? BoardUtils.FIRST_RANK : BoardUtils.EIGHTH_RANK;
                for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                    final Location currentLocation = BoardUtils.getLocation(x, backRank);
                    kings.put(currentLocation, alliance, new King(currentLocation, alliance, true));
                }

            } else {
                for (int y = 0; y < BoardUtils.BOARD_SIZE; ++y) {
                    for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                        final Location currentLocation = BoardUtils.getLocation(x, y);
                        kings.put(currentLocation, alliance, new King(currentLocation, alliance, false));
                    }
                }
            }
        }
        return kings.build();
    }

    public static King createKing(final Location location, final Alliance alliance, final boolean isFirstMove) {
        if (isFirstMove) {
            return ALL_KINGS.get(location, alliance);
        } else {
            return ALL_MOVED_KINGS.get(location, alliance);
        }
    }

    public static King createKing(final int x, final int y, final Alliance alliance, final boolean isFirstMove) {
        return createKing(BoardUtils.getLocation(x, y), alliance, isFirstMove);
    }

    public static King createKing(final char file, final int rank, final Alliance alliance,
                                  final boolean isFirstMove) {
        return createKing(BoardUtils.getLocation(file, rank), alliance, isFirstMove);
    }

    public static King createKing(final String algebraicNotationForPosition, final Alliance alliance,
                                  final boolean isFirstMove) {
        return createKing(BoardUtils.getLocation(algebraicNotationForPosition), alliance, isFirstMove);
    }

    private King(final int x, final int y, final Alliance alliance, final boolean isFirstMove) {
        this(BoardUtils.getLocation(x, y), alliance, isFirstMove);
    }

    private King(final Location pieceLocation, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.KING, pieceLocation, pieceAlliance, isFirstMove);
        final int index = pieceLocation.getY() * BoardUtils.BOARD_SIZE + pieceLocation.getX();
        this.value = super.getValue();
        if (pieceAlliance.isWhite()) {
            this.value += PieceSquareTables.WHITE_KING_MIDDLE_GAME[index];
        } else if (pieceAlliance.isBlack()) {
            this.value += PieceSquareTables.WHITE_KING_MIDDLE_GAME[index];
        }
    }

    public void setEndGameValue() {
        final int index = getLocation().getY() * BoardUtils.BOARD_SIZE + getLocation().getX();
        if (getAlliance().isWhite()) {
            this.value -= PieceSquareTables.WHITE_KING_MIDDLE_GAME[index];
            this.value += PieceSquareTables.WHITE_KING_ENDGAME[index];
        } else if (getAlliance().isBlack()) {
            this.value -= PieceSquareTables.WHITE_KING_MIDDLE_GAME[index];
            this.value += PieceSquareTables.WHITE_KING_ENDGAME[index];
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
        this.moveVectors.add(new Point(-1, 0));
        this.moveVectors.add(new Point(0, 1));
        this.moveVectors.add(new Point(1, 0));
        this.moveVectors.add(new Point(0, -1));
    }

    @Override
    public Collection<Move> getLegalMoves(final Board board) {
        return this.getJumpingPieceLegalMoves(board);
    }

    @Override
    public King move(final Move move) {
        return ALL_MOVED_KINGS.get(move.getDestination(), move.getMovedPiece().getAlliance());
    }

    /*@Override
    public String toString() {
        return PieceType.KING.getName();
    }*/
}
