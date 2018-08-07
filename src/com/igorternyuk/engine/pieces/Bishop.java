package com.igorternyuk.engine.pieces;

import com.google.common.collect.ImmutableMap;
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

public class Bishop extends Piece {
    private static final int[] DX = { -1, -1, 1, 1 };
    private static final int[] DY = { -1, 1, -1, 1 };
    private static final Map<Location, Bishop> WHITE_ALREADY_MOVED_BISHOPS = createAllPossibleWhiteBishops(false);
    private static final Map<Location, Bishop> WHITE_NOT_MOVED_BISHOPS = createAllPossibleWhiteBishops(true);
    private static final Map<Location, Bishop> BLACK_ALREADY_MOVED_BISHOPS = createAllPossibleBlackBishops(false);
    private static final Map<Location, Bishop> BLACK_NOT_MOVED_BISHOPS = createAllPossibleBlackBishops(true);


    public static Bishop createBishop(final Location location, final Alliance alliance, final boolean isFirstMove) {
        if(isFirstMove) {
            return alliance.equals(Alliance.WHITE) ? WHITE_NOT_MOVED_BISHOPS.get(location) :
                    BLACK_NOT_MOVED_BISHOPS.get(location);
        } else {
            return alliance.equals(Alliance.WHITE) ? WHITE_ALREADY_MOVED_BISHOPS.get(location) :
                    BLACK_ALREADY_MOVED_BISHOPS.get(location);
        }
    }

    public static Bishop createBishop(final int x, final int y, final Alliance alliance, final boolean isFirstMove){
        return createBishop(BoardUtils.getPosition(x,y), alliance, isFirstMove);
    }

    public static Bishop createBishop(final char file, final int rank, final Alliance alliance,
                                      final boolean isFirstMove){
        return createBishop(BoardUtils.getPosition(file,rank), alliance, isFirstMove);
    }

    public static Bishop createBishop(final String algebraicNotationForPosition, final Alliance alliance,
                                      final boolean isFirstMove){
        return createBishop(BoardUtils.getPosition(algebraicNotationForPosition), alliance, isFirstMove);
    }

    private Bishop(final int x, final int y, final Alliance alliance, final boolean isFirstMove) {
        this(BoardUtils.getPosition(x,y), alliance, isFirstMove);
    }

    private Bishop(final Location pieceLocation, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.BISHOP, pieceLocation, pieceAlliance, isFirstMove);
    }

    @Override
    public void setPossibleOffsets() {
        this.moveOffsets.add(new Point(-1, -1));
        this.moveOffsets.add(new Point(-1, 1));
        this.moveOffsets.add(new Point(1, -1));
        this.moveOffsets.add(new Point(1, 1));
    }

    @Override
    public Collection<Move> getLegalMoves(final Board board) {
        return getLinearlyMovingPiecesLegalMoves(board);
    }

    @Override
    public Bishop move(final Move move) {
        if(move.getMovedPiece().getAlliance().equals(Alliance.WHITE)) {
            return WHITE_ALREADY_MOVED_BISHOPS.get(move.getDestination());
        } else {
            return BLACK_ALREADY_MOVED_BISHOPS.get(move.getDestination());
        }
    }

    private static final Map<Location, Bishop> createAllPossibleWhiteBishops(final boolean isFirstMove) {
        return createAllPossibleBishops(Alliance.WHITE, isFirstMove);
    }

    private static final Map<Location, Bishop> createAllPossibleBlackBishops(final boolean isFirstMove) {
        return createAllPossibleBishops(Alliance.BLACK, isFirstMove);
    }

    private static final Map<Location, Bishop> createAllPossibleBishops(final Alliance alliance,
                                                                        final boolean isFirstMove){
        Map<Location, Bishop> bishops = new HashMap<>();
        if(isFirstMove){
            final int backRank = alliance.isWhite() ? BoardUtils.FIRST_RANK : BoardUtils.EIGHTH_RANK;
            for(int x = 0; x < BoardUtils.BOARD_SIZE; ++x){
                final Location currentLocation = BoardUtils.getPosition(x, backRank);
                bishops.put(currentLocation, new Bishop(currentLocation, alliance, true));
            }

        } else {
            for(int y = 0; y < BoardUtils.BOARD_SIZE; ++y){
                for(int x = 0; x < BoardUtils.BOARD_SIZE; ++x){
                    final Location currentLocation = BoardUtils.getPosition(x, y);
                    bishops.put(currentLocation, new Bishop(currentLocation, alliance, false));
                }
            }
        }
        return ImmutableMap.copyOf(bishops);
    }

    /*@Override
    public String toString() {
        return PieceType.BISHOP.getName().toUpperCase() + Board.getChessNotationTileName(this.getLocation);
    }*/
}
