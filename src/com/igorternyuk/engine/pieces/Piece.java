package com.igorternyuk.engine.pieces;

import com.google.common.collect.ImmutableList;
import com.igorternyuk.engine.Alliance;
import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.board.Tile;
import com.igorternyuk.engine.moves.Move;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by igor on 26.11.17.
 */

public abstract class Piece {
    protected final PieceType pieceType;
    protected final Location location;
    protected final boolean isFirstMove;
    protected Alliance alliance;
    protected final List<Point> moveVectors = new ArrayList<>();
    private final int cachedHashCode;


    protected Piece(final PieceType pieceType, final Location location, final Alliance alliance,
                    final boolean isFirstMove) {
        this.pieceType = pieceType;
        this.location = location;
        this.alliance = alliance;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = computeHashCode();
        setPossibleOffsets();
    }

    public Piece(final PieceType pieceType, final Location location, final Alliance alliance) {
        this(pieceType, location, alliance, false);
    }

    public Piece(PieceType pieceType, final int x, final int y, final Alliance alliance) {
        this(pieceType, BoardUtils.getLocation(x, y), alliance);
    }

    public abstract void setPossibleOffsets();

    public abstract Collection<Move> getLegalMoves(final Board board);

    public abstract Piece move(final Move move);

    public PieceType getPieceType() {
        return this.pieceType;
    }

    public int getValue() {
        return this.pieceType.getValue();
    }

    public Location getLocation() {
        return this.location;
    }

    public Alliance getAlliance() {
        return this.alliance;
    }

    public boolean isFirstMove() {
        return this.isFirstMove;
    }

    protected final Collection<Move> getSlidingPieceLegalMoves(final Board board) {
        List<Move> legalMoves = new ArrayList<>();
        this.moveVectors.forEach(offset -> {
            int destX = this.location.getX() + offset.x;
            int destY = this.location.getY() + offset.y;
            if (BoardUtils.isValidLocation(destX, destY)) {
                Location candidateDestination = BoardUtils.getLocation(destX, destY);
                while (true) {
                    Move move = getNextMove(board, candidateDestination);
                    if (move.equals(Move.NULL_MOVE)) {
                        break;
                    } else {
                        legalMoves.add(move);
                        if (move.isCapturingMove()) {
                            break;
                        } else {
                            destX += offset.x;
                            destY += offset.y;
                            if (BoardUtils.isValidLocation(destX, destY)) {
                                candidateDestination = BoardUtils.getLocation(destX, destY);
                            } else {
                                break;
                            }
                        }

                    }
                }
            }
        });
        return ImmutableList.copyOf(legalMoves);
    }

    protected final Collection<Move> getJumpingPieceLegalMoves(final Board board) {
        List<Move> legalMoves = new ArrayList<>();
        this.moveVectors.forEach(offset -> {
            int destX = this.location.getX() + offset.x;
            int destY = this.location.getY() + offset.y;
            if (BoardUtils.isValidLocation(destX, destY)) {
                final Location candidateDestination = BoardUtils.getLocation(destX, destY);
                Move move = getNextMove(board, candidateDestination);
                if (!move.equals(Move.NULL_MOVE)) {
                    legalMoves.add(move);
                }
            }
        });
        return ImmutableList.copyOf(legalMoves);
    }

    private Move getNextMove(final Board board, final Location candidateDestination) {
        if (BoardUtils.isValidLocation(candidateDestination)) {
            final Tile destinationTile = board.getTile(candidateDestination);
            if (destinationTile.isOccupied()) {
                final Piece capturedPiece = destinationTile.getPiece();
                if (!this.getAlliance().equals(capturedPiece.getAlliance())) {
                    return new Move.PieceCapturingMove(board, this, candidateDestination,
                            capturedPiece);
                }
            } else {
                return new Move.RegularMove(board, this, candidateDestination);
            }
        }
        return Move.NULL_MOVE;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof Piece)) return false;

        final Piece otherPiece = (Piece) other;
        return Objects.equals(this.isFirstMove, otherPiece.isFirstMove()) &&
                Objects.equals(this.pieceType, otherPiece.getPieceType()) &&
                Objects.equals(this.location, otherPiece.getLocation()) &&
                Objects.equals(this.getAlliance(), otherPiece.getAlliance());
    }

    @Override
    public int hashCode() {
        return this.cachedHashCode;
    }

    @Override
    public String toString() {
        return this.pieceType.getName().toUpperCase();
    }

    private int computeHashCode() {
        final int hash = 31;
        int result = getPieceType() != null ? getPieceType().hashCode() : 0;
        result = hash * result + (getLocation() != null ? getLocation().hashCode() : 0);
        result = hash * result + (isFirstMove() ? 1 : 0);
        result = hash * result + (getAlliance() != null ? getAlliance().hashCode() : 0);
        return result;
    }

}
