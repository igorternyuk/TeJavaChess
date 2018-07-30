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

public class Rook extends Piece {
    private static final int[] DX = { -1, 1, 0, 0 };
    private static final int[] DY = { 0, 0, -1, 1 };
    private static final Map<Position, Rook> WHITE_ALREADY_MOVED_ROOKS = createAllPossibleWhiteRooks(false);
    private static final Map<Position, Rook> WHITE_NOT_MOVED_ROOKS = createAllPossibleWhiteRooks(true);
    private static final Map<Position, Rook> BLACK_ALREADY_MOVED_ROOKS = createAllPossibleBlackRooks(false);
    private static final Map<Position, Rook> BLACK_NOT_MOVED_ROOKS = createAllPossibleBlackRooks(true);

    public static Rook createRook(final Position position, final Alliance alliance, final boolean isFirstMove){
        if(isFirstMove) {
            return alliance.equals(Alliance.WHITE) ? WHITE_NOT_MOVED_ROOKS.get(position) :
                    BLACK_NOT_MOVED_ROOKS.get(position);
        } else {
            return alliance.equals(Alliance.WHITE) ? WHITE_ALREADY_MOVED_ROOKS.get(position) :
                    BLACK_ALREADY_MOVED_ROOKS.get(position);
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

    private Rook(final Position piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.ROOK, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> getLegalMoves(final Board board) {
        return getLinearlyMovingPiecesLegalMoves(board, DX, DY);
    }

    @Override
    public Rook move(final Move move) {
        if(move.getMovedPiece().getAlliance().equals(Alliance.WHITE)) {
            return WHITE_ALREADY_MOVED_ROOKS.get(move.getDestination());
        } else {
            return BLACK_ALREADY_MOVED_ROOKS.get(move.getDestination());
        }
    }

    private static final Map<Position,Rook> createAllPossibleWhiteRooks(final boolean isFirstMove) {
        return createAllPossibleRooks(Alliance.WHITE, isFirstMove);
    }

    private static final Map<Position,Rook> createAllPossibleBlackRooks(final boolean isFirstMove) {
        return createAllPossibleRooks(Alliance.BLACK, isFirstMove);
    }

    private static final Map<Position, Rook> createAllPossibleRooks(final Alliance alliance, final boolean isFirstMove){
        Map<Position, Rook> rooks = new HashMap<>();
        if(isFirstMove){
            final int backRank = alliance.isWhite() ? BoardUtils.FIRST_RANK : BoardUtils.EIGHTH_RANK;
            for(int x = 0; x < BoardUtils.BOARD_SIZE; ++x){
                final Position currentPosition = BoardUtils.getPosition(x,backRank);
                rooks.put(currentPosition, new Rook(currentPosition, alliance, true));
            }
        } else {
            for (int y = 0; y < BoardUtils.BOARD_SIZE; ++y) {
                for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                    final Position currentPosition = BoardUtils.getPosition(x, y);
                    rooks.put(currentPosition, new Rook(currentPosition, alliance, false));
                }
            }
        }
        return ImmutableMap.copyOf(rooks);
    }

    /*@Override
    public String toString() {
        return PieceType.ROOK.getName().toUpperCase() + Board.getChessNotationTileName(this.getPosition);
    }*/
}
