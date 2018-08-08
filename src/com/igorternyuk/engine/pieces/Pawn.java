package com.igorternyuk.engine.pieces;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import com.igorternyuk.engine.Alliance;
import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.board.Tile;
import com.igorternyuk.engine.moves.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by igor on 01.12.17.
 */

public class Pawn extends Piece {
    private static final Table<Location, Alliance, Pawn> ALL_PAWNS = createAllPossiblePawns(true);
    private static final Table<Location, Alliance, Pawn> ALL_MOVED_PAWNS = createAllPossiblePawns(false);

    private static final Table<Location, Alliance, Pawn> createAllPossiblePawns(final boolean isFirstMove) {
        ImmutableTable.Builder<Location, Alliance, Pawn> pawns = ImmutableTable.builder();
        for (Alliance alliance : Alliance.values()) {
            if (isFirstMove) {
                final int startRank = alliance.equals(Alliance.WHITE) ? BoardUtils.SECOND_RANK : BoardUtils.SEVENTH_RANK;
                for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                    final Location currentLocation = BoardUtils.getLocation(x, startRank);
                    pawns.put(currentLocation, alliance, new Pawn(currentLocation, alliance, true));
                }
            } else {
                final int from = alliance.equals(Alliance.WHITE) ? BoardUtils.EIGHTH_RANK : BoardUtils.SIXTH_RANK;
                final int to = alliance.equals(Alliance.WHITE) ? BoardUtils.THIRD_RANK : BoardUtils.FIRST_RANK;
                for (int y = from; y <= to; ++y) {
                    for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                        final Location currentLocation = BoardUtils.getLocation(x, y);
                        pawns.put(currentLocation, alliance, new Pawn(currentLocation, alliance, false));
                    }
                }
            }
        }

        return pawns.build();
    }

    public static Pawn createPawn(final Location location, final Alliance alliance, final boolean isFirstMove) {
        if (isFirstMove) {
            return ALL_PAWNS.get(location, alliance);
        } else {
            return ALL_MOVED_PAWNS.get(location, alliance);
        }
    }

    public static Pawn createPawn(final int x, final int y, final Alliance alliance, final boolean isFirstMove) {
        return createPawn(BoardUtils.getLocation(x, y), alliance, isFirstMove);
    }

    public static Pawn createPawn(final char file, final int rank, final Alliance alliance,
                                  final boolean isFirstMove) {
        return createPawn(BoardUtils.getLocation(file, rank), alliance, isFirstMove);
    }

    public static Pawn createPawn(final String algebraicNotationForPosition, final Alliance alliance,
                                  final boolean isFirstMove) {
        return createPawn(BoardUtils.getLocation(algebraicNotationForPosition), alliance, isFirstMove);
    }

    private Pawn(final int x, final int y, final Alliance alliance, final boolean isFirstMove) {
        this(BoardUtils.getLocation(x, y), alliance, isFirstMove);
    }

    private Pawn(final Location pieceLocation, final Alliance pieceAlliance, final boolean isFirstMove) {
        super(PieceType.PAWN, pieceLocation, pieceAlliance, isFirstMove);
    }

    @Override
    public void setPossibleOffsets() {
        this.moveVectors.add(new Point(-1, this.getAlliance().getDirectionY()));
        this.moveVectors.add(new Point(0, this.getAlliance().getDirectionY()));
        this.moveVectors.add(new Point(1, this.getAlliance().getDirectionY()));
    }

    @Override
    public Collection<Move> getLegalMoves(final Board board) {
        List<Move> legalMoves = new ArrayList<>();
        this.moveVectors.forEach(offset -> {

            final int destX = this.location.getX() + offset.x;
            final int destY = this.location.getY() + offset.y;

            if (!BoardUtils.isValidLocation(destX, destY)) {
                return;
            }

            final Location candidateDestination = BoardUtils.getLocation(destX, destY);
            if (offset.x == 0) {
                if (!board.getTile(candidateDestination).isOccupied()) {
                    //Regular move
                    if (this.getAlliance().isPawnPromotionSquare(candidateDestination)) {
                        addAllPossiblePawnPromotions(board, candidateDestination, legalMoves);
                    } else {
                        legalMoves.add(new PawnMove(board, this, candidateDestination));
                    }

                    if (this.isFirstMove) {
                        //Pawn jump
                        final int jumpDestY = candidateDestination.getY() + this.getAlliance().getDirectionY();
                        if (!board.getTile(destX, jumpDestY).isOccupied()) {
                            legalMoves.add(new PawnJump(board, this, BoardUtils.getLocation(destX, jumpDestY)));
                        }
                    }
                }
            } else {
                // Diagonal capture
                final Tile destinationTile = board.getTile(candidateDestination);
                if (destinationTile.isOccupied()) {
                    final Piece capturedPiece = destinationTile.getPiece();
                    if (capturedPiece.getAlliance() != this.alliance) {
                        // Pawn promotion by capturing
                        if (this.getAlliance().isPawnPromotionSquare(candidateDestination)) {
                            addAllPossiblePawnPromotions(board, candidateDestination, legalMoves);
                        } else {
                            legalMoves.add(new PawnCapturingMove(board, this, candidateDestination,
                                    capturedPiece));
                        }
                    }
                } else {
                    //En passant capture
                    final Pawn enPassantPawn = board.getEnPassantPawn();
                    if (enPassantPawn != null && !enPassantPawn.getAlliance().equals(this.alliance) &&
                            enPassantPawn.getAlliance().getDirectionY() == this.alliance.getOppositeDirectionY() &&
                            enPassantPawn.getLocation().getY() == this.location.getY()) {
                        if (enPassantPawn.getLocation().getX() - this.location.getX() == offset.x) {
                            legalMoves.add(new PawnEnPassantCapture(board, this, candidateDestination,
                                    enPassantPawn));
                        }
                    }
                }
            }
        });

        return ImmutableList.copyOf(legalMoves);
    }

    private void addAllPossiblePawnPromotions(final Board board, final Location candidateDestination,
                                              final List<Move> legalMoves) {
        legalMoves.add(new PawnPromotion(new PawnMove(board,
                this, candidateDestination), Queen.createQueen(candidateDestination,
                this.alliance, false)));
        legalMoves.add(new PawnPromotion(new PawnMove(board,
                this, candidateDestination), Rook.createRook(candidateDestination,
                this.alliance, false)));
        legalMoves.add(new PawnPromotion(new PawnMove(board,
                this, candidateDestination), Knight.createKnight(candidateDestination,
                this.alliance, false)));
        legalMoves.add(new PawnPromotion(new PawnMove(board,
                this, candidateDestination), Bishop.createBishop(candidateDestination,
                this.alliance, false)));
    }

    @Override
    public Pawn move(final Move move) {
        return ALL_MOVED_PAWNS.get(move.getDestination(), move.getMovedPiece().getAlliance());
    }

    @Override
    public String toString() {
        return String.valueOf(BoardUtils.getAlgebraicNotationForCoordinateX(this.location.getX()));
    }
}
