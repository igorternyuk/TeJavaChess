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

public class King extends Piece {

    private static final Map<Location, King> WHITE_ALREADY_MOVED_KINGS = createAllPossibleWhiteKings(false);
    private static final Map<Location, King> WHITE_NOT_MOVED_KINGS = createAllPossibleWhiteKings(true);
    private static final Map<Location, King> BLACK_ALREADY_MOVED_KINGS = createAllPossibleBlackKings(false);
    private static final Map<Location, King> BLACK_NOT_MOVED_KINGS = createAllPossibleBlackKings(true);

    public static King createKing(final Location location, final Alliance alliance, final boolean isFirstMove) {
        if(isFirstMove) {
            return alliance.equals(Alliance.WHITE) ? WHITE_NOT_MOVED_KINGS.get(location) :
                    BLACK_NOT_MOVED_KINGS.get(location);
        } else {
            return alliance.equals(Alliance.WHITE) ? WHITE_ALREADY_MOVED_KINGS.get(location) :
                    BLACK_ALREADY_MOVED_KINGS.get(location);
        }
    }

    public static King createKing(final int x, final int y, final Alliance alliance, final boolean isFirstMove){
        return createKing(BoardUtils.getPosition(x,y), alliance, isFirstMove);
    }

    public static King createKing(final char file, final int rank, final Alliance alliance,
                                      final boolean isFirstMove){
        return createKing(BoardUtils.getPosition(file,rank), alliance, isFirstMove);
    }

    public static King createKing(final String algebraicNotationForPosition, final Alliance alliance,
                                      final boolean isFirstMove){
        return createKing(BoardUtils.getPosition(algebraicNotationForPosition), alliance, isFirstMove);
    }

    private King(final int x, final int y, final Alliance alliance, final boolean isFirstMove) {
        this(BoardUtils.getPosition(x, y), alliance, isFirstMove);
    }

    private King(final Location pieceLocation, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.KING, pieceLocation, pieceAlliance, isFirstMove);
    }

    @Override
    public void setPossibleOffsets() {
        this.moveOffsets.add(new Point(-1, -1));
        this.moveOffsets.add(new Point(-1, 1));
        this.moveOffsets.add(new Point(1, -1));
        this.moveOffsets.add(new Point(1, 1));
        this.moveOffsets.add(new Point(-1, 0));
        this.moveOffsets.add(new Point(0, 1));
        this.moveOffsets.add(new Point(1, 0));
        this.moveOffsets.add(new Point(0, -1));
    }

    @Override
    public Collection<Move> getLegalMoves(final Board board) {
        return this.getOneStepMovingPieceLegalMoves(board);
    }

    @Override
    public King move(final Move move) {
        if(move.getMovedPiece().getAlliance().equals(Alliance.WHITE)) {
            return WHITE_ALREADY_MOVED_KINGS.get(move.getDestination());
        } else {
            return BLACK_ALREADY_MOVED_KINGS.get(move.getDestination());
        }
    }

    private static final Map<Location, King> createAllPossibleWhiteKings(final boolean isFirstMove) {
        return createAllPossibleKings(Alliance.WHITE, isFirstMove);
    }

    private static final Map<Location, King> createAllPossibleBlackKings(final boolean isFirstMove) {
        return createAllPossibleKings(Alliance.BLACK, isFirstMove);
    }

    private static Map<Location, King> createAllPossibleKings(final Alliance alliance, final boolean isFirstMove) {
        Map<Location, King> kings = new HashMap<>();
        if(isFirstMove){
            final int backRank = alliance.isWhite() ? BoardUtils.FIRST_RANK : BoardUtils.EIGHTH_RANK;
            for(int x = 1; x < BoardUtils.BOARD_SIZE - 1; ++x){
                /*We did not include first and last positions(corners -a1 and h1) because in Random Fisher Chess
                 the king must be located between rooks*/
                final Location currentLocation = BoardUtils.getPosition(x, backRank);
                kings.put(currentLocation, new King(BoardUtils.getPosition(x, backRank), alliance, true));
            }
        } else {
            for (int y = 0; y < BoardUtils.BOARD_SIZE; ++y) {
                for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                    final Location currentLocation = BoardUtils.getPosition(x, y);
                    kings.put(currentLocation, new King(currentLocation, alliance, false));
                }
            }
        }
        return ImmutableMap.copyOf(kings);
    }

    /*@Override
    public String toString() {
        return PieceType.KING.getName().toUpperCase() + Board.getChessNotationTileName(this.getLocation);
    }*/
}
