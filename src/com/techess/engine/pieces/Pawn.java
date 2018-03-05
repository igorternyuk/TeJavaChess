package com.techess.engine.pieces;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.techess.engine.Alliance;
import com.techess.engine.board.Board;
import com.techess.engine.moves.Move;
import com.techess.engine.board.Position;
import com.techess.engine.board.Tile;

import java.util.*;

/**
 * Created by igor on 01.12.17.
 */

public class Pawn extends Piece {
    public static final int[] DX = { -1, 0, 1};
    private static final Map<Position, Pawn> WHITE_ALREADY_MOVED_PAWNS = createAllPossibleWhitePawns(false);
    private static final Map<Position, Pawn> WHITE_NOT_MOVED_PAWNS = createAllPossibleWhitePawns(true);
    private static final Map<Position, Pawn> BLACK_ALREADY_MOVED_PAWNS = createAllPossibleBlackPawns(false);
    private static final Map<Position, Pawn> BLACK_NOT_MOVED_PAWNS = createAllPossibleBlackPawns(true);

    public static Pawn createPawn(final Position position, final Alliance alliance, final boolean isFirstMove){
        if(isFirstMove) {
            return alliance.equals(Alliance.WHITE) ? WHITE_NOT_MOVED_PAWNS.get(position) :
                    BLACK_NOT_MOVED_PAWNS.get(position);
        } else {
            return alliance.equals(Alliance.WHITE) ? WHITE_ALREADY_MOVED_PAWNS.get(position) :
                    BLACK_ALREADY_MOVED_PAWNS.get(position);
        }
    }

    public static Pawn createPawn(final int x, final int y, final Alliance alliance, final boolean isFirstMove){
        return createPawn(Board.getPosition(x,y), alliance, isFirstMove);
    }

    public static Pawn createPawn(final char file, final int rank, final Alliance alliance,
                                      final boolean isFirstMove){
        return createPawn(Board.getPosition(file,rank), alliance, isFirstMove);
    }

    public static Pawn createPawn(final String algebraicNotationForPosition, final Alliance alliance,
                                      final boolean isFirstMove){
        return createPawn(Board.getPosition(algebraicNotationForPosition), alliance, isFirstMove);
    }

    private Pawn(final int x, final int y, final Alliance alliance, final boolean isFirstMove) {
        this(Board.getPosition(x,y), alliance, isFirstMove);
    }

    private Pawn(final Position piecePosition, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> getLegalMoves(final Board board) {
        List<Move> legalMoves = new ArrayList<>();
        for (int i = 0; i < DX.length; ++i){
            final int destX = this.position.getX() + DX[i];
            final int destY = this.position.getY() + this.getAlliance().getDirectionY();

            if(!Board.isValidPosition(destX, destY)){
                continue;
            }

            final Position candidateDestination = Board.getPosition(destX, destY);
            if(DX[i] == 0){
                if(!board.getTile(candidateDestination).isOccupied()) {
                    //Regular move
                    legalMoves.add(new Move.PawnMove(board, this, candidateDestination));
                    // TODO regular pawn promotion
                    if(this.isFirstMove){
                        //Pawn jump
                        final int jumpDestY = candidateDestination.getY() + this.getAlliance().getDirectionY();
                        if(!board.getTile(destX, jumpDestY).isOccupied()){
                            legalMoves.add(new Move.PawnJump(board, this, Board.getPosition(destX, jumpDestY)));
                        }
                    }
                }
            } else {
                // Diagonal capture
                final Tile destinationTile = board.getTile(candidateDestination);
                if(destinationTile.isOccupied()){
                    final Piece capturedPiece = destinationTile.getPiece();
                    if(capturedPiece.getAlliance() != this.alliance){
                        legalMoves.add(new Move.PawnCapturingMove(board, this, candidateDestination,
                                capturedPiece));
                        // TODO pawn promotion by capturing
                    }
                } else {
                    //En passant capture
                    final Pawn enPassantPawn = board.getEnPassantPawn();
                    if(enPassantPawn != null){
                        if(enPassantPawn.getPosition().getX() - this.position.getX() == DX[i]){
                            legalMoves.add(new Move.PawnEnPassantCapture(board, this, candidateDestination,
                                    enPassantPawn));
                        }
                    }
                }
            }

        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Pawn move(final Move move) {
        if(move.getMovedPiece().getAlliance().equals(Alliance.WHITE)) {
            return WHITE_ALREADY_MOVED_PAWNS.get(move.getDestination());
        } else {
            return BLACK_ALREADY_MOVED_PAWNS.get(move.getDestination());
        }
    }

    private static final Map<Position, Pawn> createAllPossibleWhitePawns(final boolean isFirstMove) {
        return createAllPossiblePawns(Alliance.WHITE, isFirstMove);
    }

    private static final Map<Position, Pawn> createAllPossibleBlackPawns(final boolean isFirstMove) {
        return createAllPossiblePawns(Alliance.BLACK, isFirstMove);
    }

    private static final Map<Position, Pawn> createAllPossiblePawns(final Alliance alliance,
                                                                    final boolean isFirstMove){
        Map<Position, Pawn> pawns = new HashMap<>();
        if(isFirstMove){
            final int startRank = alliance.equals(Alliance.WHITE) ? Board.SECOND_RANK : Board.SEVENTH_RANK;
            for(int x = 0; x < Board.BOARD_SIZE; ++x){
                final Position currentPosition = Board.getPosition(x, startRank);
                pawns.put(currentPosition, new Pawn(currentPosition, alliance, true));
            }
        } else {
            final int from = alliance.equals(Alliance.WHITE) ? Board.EIGHTH_RANK : Board.SIXTH_RANK;
            final int to = alliance.equals(Alliance.WHITE) ? Board.THIRD_RANK : Board.FIRST_RANK;
            for (int y = from; y <= to; ++y) {
                for (int x = 0; x < Board.BOARD_SIZE; ++x) {
                    final Position currentPosition = Board.getPosition(x, y);
                    pawns.put(currentPosition, new Pawn(currentPosition, alliance, false));
                }
            }
        }
        return ImmutableMap.copyOf(pawns);
    }

    @Override
    public String toString() {
        return String.valueOf(Board.getAlgebraicNotationForCoordinateX(this.position.getX()));
        //return Board.getChessNotationTileName(this.getPosition);
    }
}
