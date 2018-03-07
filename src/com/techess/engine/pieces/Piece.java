package com.techess.engine.pieces;

import com.google.common.collect.ImmutableList;
import com.techess.engine.Alliance;
import com.techess.engine.board.Board;
import com.techess.engine.board.BoardUtils;
import com.techess.engine.moves.Move;
import com.techess.engine.board.Position;
import com.techess.engine.board.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by igor on 26.11.17.
 */

public abstract class Piece{
    protected final PieceType pieceType;
    protected final Position position;
    protected final boolean isFirstMove;
    private final int cachedHashCode;

    public PieceType getPieceType() {
        return this.pieceType;
    }

    public int getValue() { return this.pieceType.getValue(); }

    public Position getPosition() {
        return this.position;
    }

    public Alliance getAlliance() {
        return this.alliance;
    }

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    protected Alliance alliance;

    public Piece(final PieceType pieceType, final Position position, final Alliance alliance, boolean isFirstMove){
        this.pieceType = pieceType;
        this.position = position;
        this.alliance = alliance;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
    }

    public Piece(final PieceType pieceType, final Position position, final Alliance alliance) {
        this(pieceType, position, alliance, false);
    }

    public Piece(PieceType pieceType, final int x, final int y, final Alliance alliance){
        this(pieceType, BoardUtils.getPosition(x,y), alliance);
    }

    public abstract Collection<Move> getLegalMoves(final Board board);
    public abstract Piece move(final Move move);

    protected final Collection<Move> getLinearlyMovingPiecesLegalMoves(final Board board, final int[] DX,
                                                                       final int[] DY){
        List<Move> legalMoves = new ArrayList<>();
        for(int i = 0; i < DX.length; ++i){
            //int counter = 0;
            int destX = this.position.getX() + DX[i];
            int destY = this.position.getY() + DY[i];
            while (checkAndAddMoveIfLegal(board, legalMoves, destX, destY)){
                //++counter;
                //System.out.println("Infinite loop -> " + counter);
                destX += DX[i];
                destY += DY[i];
            }
        }

        return ImmutableList.copyOf(legalMoves);
    }

    protected final Collection<Move> getOneStepMovingPieceLegalMoves(final Board board, final int[] DX,
                                                                     final int[] DY) {
        List<Move> legalMoves = new ArrayList<>();
        for(int i = 0; i < DX.length; ++i){
            final int destX = this.position.getX() + DX[i];
            final int destY = this.position.getY() + DY[i];
            checkAndAddMoveIfLegal(board, legalMoves, destX, destY);
        }

        return ImmutableList.copyOf(legalMoves);
    }

    private boolean checkAndAddMoveIfLegal(final Board board, List<Move> legalMoves, final int destX,
                                           final int destY){
        boolean hasMoreMovesInTheCurrentDirection = true;
        if(BoardUtils.isValidPosition(destX, destY)){
            final Position candidateDestination = BoardUtils.getPosition(destX, destY);
            final Tile destinationTile = board.getTile(destX, destY);
            if(destinationTile.isOccupied()){
                final Piece capturedPiece = destinationTile.getPiece();
                if(!this.getAlliance().equals(capturedPiece.getAlliance())){
                    legalMoves.add(new Move.PieceCapturingMove(board, this, candidateDestination,
                            capturedPiece));
                }
                hasMoreMovesInTheCurrentDirection = false;
            }
            else {
                legalMoves.add(new Move.RegularMove(board, this, candidateDestination));
            }
        } else {
            hasMoreMovesInTheCurrentDirection = false;
        }
        return hasMoreMovesInTheCurrentDirection;
    }



    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof Piece)) return false;

        final Piece otherPiece = (Piece) other;
        return Objects.equals(this.isFirstMove, otherPiece.isFirstMove()) &&
                Objects.equals(this.pieceType, otherPiece.getPieceType()) &&
                Objects.equals(this.position, otherPiece.getPosition()) &&
                Objects.equals(this.getAlliance(), otherPiece.getAlliance());
    }

    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    @Override
    public String toString(){
        return this.pieceType.getName().toUpperCase();
    }

    private int computeHashCode(){
        final int hash = 31;
        int result = getPieceType() != null ? getPieceType().hashCode() : 0;
        result = hash * result + (getPosition() != null ? getPosition().hashCode() : 0);
        result = hash * result + (isFirstMove() ? 1 : 0);
        result = hash * result + (getAlliance() != null ? getAlliance().hashCode() : 0);
        return result;
    }

}
