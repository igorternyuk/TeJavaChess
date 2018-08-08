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

public class Rook extends Piece {

    private static final Table<Location, Alliance, Rook> ALL_ROOKS = createAllPossibleRooks(true);
    private static final Table<Location, Alliance, Rook> ALL_MOVED_ROOKS = createAllPossibleRooks(false);

    private static Table<Location, Alliance, Rook> createAllPossibleRooks(final boolean isFirstMove) {
        final ImmutableTable.Builder<Location, Alliance, Rook> rooks = ImmutableTable.builder();
        for (final Alliance alliance : Alliance.values()) {
            if (isFirstMove) {
                final int backRank = alliance.isWhite() ? BoardUtils.FIRST_RANK : BoardUtils.EIGHTH_RANK;
                for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                    final Location currentLocation = BoardUtils.getPosition(x, backRank);
                    rooks.put(currentLocation, alliance, new Rook(currentLocation, alliance, true));
                }

            } else {
                for (int y = 0; y < BoardUtils.BOARD_SIZE; ++y) {
                    for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                        final Location currentLocation = BoardUtils.getPosition(x, y);
                        rooks.put(currentLocation, alliance, new Rook(currentLocation, alliance, false));
                    }
                }
            }
        }
        return rooks.build();
    }

    public static Rook createRook(final Location location, final Alliance alliance, final boolean isFirstMove) {
        if(isFirstMove) {
            return ALL_ROOKS.get(location, alliance);
        } else {
            return ALL_MOVED_ROOKS.get(location, alliance);
        }
    }

    public static Rook createRook(final int x, final int y, final Alliance alliance, final boolean isFirstMove){
        return createRook(BoardUtils.getPosition(x,y), alliance, isFirstMove);
    }

    public static Rook createRook(final char file, final int rank, final Alliance alliance,
                                    final boolean isFirstMove){
        return createRook(BoardUtils.getPosition(file,rank), alliance, isFirstMove);
    }

    public static Rook createRook(final String algebraicNotationForPosition, final Alliance alliance,
                                  final boolean isFirstMove){
        return createRook(BoardUtils.getPosition(algebraicNotationForPosition), alliance, isFirstMove);
    }

    private Rook(final int x, final int y, final Alliance alliance, final boolean isFirstMove) {
        this(BoardUtils.getPosition(x,y),alliance, isFirstMove);
    }

    private Rook(final Location pieceLocation, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.ROOK, pieceLocation, pieceAlliance, isFirstMove);
    }

    @Override
    public void setPossibleOffsets() {
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
    public Rook move(final Move move) {
        return ALL_MOVED_ROOKS.get(move.getDestination(), move.getMovedPiece().getAlliance());
    }

    /*@Override
    public String toString() {
        return PieceType.ROOK.getName().toUpperCase() + Board.getChessNotationTileName(this.getLocation);
    }*/
}
