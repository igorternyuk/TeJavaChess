package com.igorternyuk.engine.pieces;

import com.google.common.collect.ImmutableMap;
import com.igorternyuk.engine.Alliance;
import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.moves.Move;
import com.igorternyuk.engine.board.Position;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by igor on 01.12.17.
 */

public class Knight extends Piece {

    private static final int[] DX = { -2, -2, -1, -1, 1, 1, 2, 2 };
    private static final int[] DY = { -1, 1, -2, 2, -2, 2, -1, 1 };
    private static final Map<Position, Knight> WHITE_ALREADY_MOVED_KNIGHTS = createAllPossibleWhiteKnights(false);
    private static final Map<Position, Knight> WHITE_NOT_MOVED_KNIGHTS = createAllPossibleWhiteKnights(true);
    private static final Map<Position, Knight> BLACK_ALREADY_MOVED_KNIGHTS = createAllPossibleBlackKnights(false);
    private static final Map<Position, Knight> BLACK_NOT_MOVED_KNIGHTS = createAllPossibleBlackKnights(true);

    public static Knight createKnight(final Position position, final Alliance alliance, final boolean isFirstMove){
        if(isFirstMove) {
            return alliance.equals(Alliance.WHITE) ? WHITE_NOT_MOVED_KNIGHTS.get(position) :
                    BLACK_NOT_MOVED_KNIGHTS.get(position);
        } else {
            return alliance.equals(Alliance.WHITE) ? WHITE_ALREADY_MOVED_KNIGHTS.get(position) :
                    BLACK_ALREADY_MOVED_KNIGHTS.get(position);
        }
    }

    public static Knight createKnight(final int x, final int y, final Alliance alliance, final boolean isFirstMove){
        return createKnight(BoardUtils.getPosition(x,y), alliance, isFirstMove);
    }

    public static Knight createKnight(final char file, final int rank, final Alliance alliance,
                                  final boolean isFirstMove){
        return createKnight(BoardUtils.getPosition(file,rank), alliance, isFirstMove);
    }

    public static Knight createKnight(final String algebraicNotationForPosition, final Alliance alliance,
                                      final boolean isFirstMove){
        return createKnight(BoardUtils.getPosition(algebraicNotationForPosition), alliance, isFirstMove);
    }

    private Knight(final int x, final int y, final Alliance alliance, final boolean isFirstMove) {
        this(BoardUtils.getPosition(x,y), alliance, isFirstMove);
    }

    private Knight(final Position piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.KNIGHT, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> getLegalMoves(final Board board) {
        return this.getOneStepMovingPieceLegalMoves(board, DX, DY);
    }

    @Override
    public Knight move(final Move move) {
        if(move.getMovedPiece().getAlliance().equals(Alliance.WHITE)){
            return WHITE_ALREADY_MOVED_KNIGHTS.get(move.getDestination());
        } else {
            return BLACK_ALREADY_MOVED_KNIGHTS.get(move.getDestination());
        }
    }

    private static final Map<Position, Knight> createAllPossibleWhiteKnights(final boolean isFirstMove) {
        return createAllPossibleKnights(Alliance.WHITE, isFirstMove);
    }

    private static final Map<Position, Knight> createAllPossibleBlackKnights(final boolean isFirstMove) {
        return createAllPossibleKnights(Alliance.BLACK, isFirstMove);
    }

    private static Map<Position, Knight> createAllPossibleKnights(final Alliance alliance, final boolean isFirstMove) {
        Map<Position, Knight> knights = new HashMap<>();
        if(isFirstMove){
            final int backRank = alliance.isWhite() ? BoardUtils.FIRST_RANK : BoardUtils.EIGHTH_RANK;
            for(int x = 0; x < BoardUtils.BOARD_SIZE; ++x){
                final Position currentPosition = BoardUtils.getPosition(x,backRank);
                knights.put(currentPosition, new Knight(currentPosition, alliance, true));
            }

        } else {
            for(int y = 0; y < BoardUtils.BOARD_SIZE; ++y){
                for(int x = 0; x < BoardUtils.BOARD_SIZE; ++x){
                    final Position currentPosition = BoardUtils.getPosition(x,y);
                    knights.put(currentPosition, new Knight(currentPosition, alliance, false));
                }
            }
        }
        return ImmutableMap.copyOf(knights);
    }

    /*@Override
    public String toString() {
        return PieceType.KNIGHT.getName().toUpperCase() + Board.getChessNotationTileName(this.getPosition);
    }*/
}
