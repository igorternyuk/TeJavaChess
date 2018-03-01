package com.techess.engine.pieces;

import com.google.common.collect.ImmutableMap;
import com.techess.engine.Alliance;
import com.techess.engine.board.Board;
import com.techess.engine.board.Move;
import com.techess.engine.board.Position;
import javafx.geometry.Pos;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by igor on 01.12.17.
 */

public class Bishop extends Piece {
    private static final int[] DX = { -1, -1, 1, 1 };
    private static final int[] DY = { -1, 1, -1, 1 };
    private static final Map<Position, Bishop> WHITE_ALREADY_MOVED_BISHOPS = createAllPossibleWhiteBishops(false);
    private static final Map<Position, Bishop> WHITE_NOT_MOVED_BISHOPS = createAllPossibleWhiteBishops(true);
    private static final Map<Position, Bishop> BLACK_ALREADY_MOVED_BISHOPS = createAllPossibleBlackBishops(false);
    private static final Map<Position, Bishop> BLACK_NOT_MOVED_BISHOPS = createAllPossibleBlackBishops(true);

    public static Bishop createBishop(final int x, final int y, final Alliance alliance, final boolean isFirstMove){
        return createBishop(Board.position(x,y), alliance, isFirstMove);
    }

    public static Bishop createBishop(final char fileOnChessBoard, final int rank, final Alliance alliance,
                                    final boolean isFirstMove){
        return createBishop(Board.position(fileOnChessBoard, rank), alliance, isFirstMove);
    }

    public static Bishop createBishop(final Position position, final Alliance alliance, final boolean isFirstMove){
        if(isFirstMove) {
            return alliance.equals(Alliance.WHITE) ? WHITE_NOT_MOVED_BISHOPS.get(position) :
                    BLACK_NOT_MOVED_BISHOPS.get(position);
        } else {
            return alliance.equals(Alliance.WHITE) ? WHITE_ALREADY_MOVED_BISHOPS.get(position) :
                    BLACK_ALREADY_MOVED_BISHOPS.get(position);
        }
    }

    private Bishop(final int x, final int y, final Alliance alliance, final boolean isFirstMove) {
        this(Board.position(x,y), alliance, isFirstMove);
    }

    private Bishop(final Position piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.BISHOP, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> getLegalMoves(final Board board) {
        return getLinearlyMovingPiecesLegalMoves(board, DX, DY);
    }

    @Override
    public Bishop move(final Move move) {
        if(move.getMovedPiece().getAlliance().equals(Alliance.WHITE)) {
            return WHITE_ALREADY_MOVED_BISHOPS.get(move.getDestination());
        } else {
            return BLACK_ALREADY_MOVED_BISHOPS.get(move.getDestination());
        }
    }

    private static final Map<Position,Bishop> createAllPossibleWhiteBishops(final boolean isFirstMove) {
        return createAllPossibleBishops(Alliance.WHITE, isFirstMove);
    }

    private static final Map<Position,Bishop> createAllPossibleBlackBishops(final boolean isFirstMove) {
        return createAllPossibleBishops(Alliance.BLACK, isFirstMove);
    }

    private static final Map<Position,Bishop> createAllPossibleBishops(final Alliance alliance,
                                                                       final boolean isFirstMove){
        Map<Position, Bishop> bishops = new HashMap<>();
        if(isFirstMove){
            final int backRank = alliance.isWhite() ? Board.FIRST_RANK : Board.EIGHTH_RANK;
            for(int x = 0; x < Board.BOARD_SIZE; ++x){
                final Position currentPosition = Board.position(x,backRank);
                bishops.put(currentPosition, new Bishop(currentPosition, alliance, true));
            }

        } else {
            for(int y = 0; y < Board.BOARD_SIZE; ++y){
                for(int x = 0; x < Board.BOARD_SIZE; ++x){
                    final Position currentPosition = Board.position(x,y);
                    bishops.put(currentPosition, new Bishop(currentPosition, alliance, false));
                }
            }
        }
        return ImmutableMap.copyOf(bishops);
    }

    @Override
    public String toString() {
        return PieceType.BISHOP.getName();
    }
}
