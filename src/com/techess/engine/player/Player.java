package com.techess.engine.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.techess.engine.Alliance;
import com.techess.engine.board.*;
import com.techess.engine.pieces.King;
import com.techess.engine.pieces.Piece;
import com.techess.engine.pieces.Rook;
import com.techess.engine.board.Move.KingsSideCastling;
import com.techess.engine.board.Move.QueensSideCastling;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by igor on 03.12.17.
 */

public abstract class Player {
    protected final Board board;
    protected final King king;
    protected final Collection<Move> legalMoves;
    protected final Collection<Move> opponentLegalMoves;
    private final boolean isInCheck;

    public Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {
        this.board = board;
        this.king = establishKing();
        //Don't forget to concatenate castles
        this.opponentLegalMoves = opponentMoves;
        final Collection<Move> castles = this.calculateCastles(legalMoves, opponentMoves);
        final Alliance currentAlliance = this.getAlliance();
        System.out.println((currentAlliance.isWhite() ? "White " : "Black ") + "player has " + castles.size() +
                " castles");
        System.out.println("Size of legal moves collection before adding the castles = " + legalMoves.size());
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, castles));
        System.out.println("Size of legal moves collection after adding the castles = " + this.legalMoves.size());
        this.isInCheck = !Player.calculateAttacksOnTile(this.king.getPosition(), opponentMoves).isEmpty();
    }

    public King getPlayerKing() {
        return this.king;
    }

    public Collection<Move> getLegalMoves(){
        return this.legalMoves;
    }

    public Collection<Move> getOpponentLegalMoves(){
        return this.opponentLegalMoves;
    }

    public boolean isMoveLegal(final Move move){
        return this.legalMoves.contains(move);
    }

    //TODO implement these methods below!!!

    public boolean isUnderCheck(){
        return this.isInCheck;
    }

    public boolean isCheckMate(){
        return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStalemate(){
        return !this.isInCheck && !hasEscapeMoves();
    }

    public boolean isCastled(){
        return false;
    }

    public MoveTransition makeMove(final Move move){
        if(!isMoveLegal(move)){
            System.out.println("Illegal move");
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitedBoard = move.execute(); //This method transfer the turn to the opponent
        final Player playerWhoseMoveIsChecking = transitedBoard.getCurrentPlayer().getOpponent();
        final Position currentPlayerPosition = playerWhoseMoveIsChecking.getPlayerKing().getPosition();
        final Collection<Move> opponentLegalMoves = playerWhoseMoveIsChecking.getOpponentLegalMoves();
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(currentPlayerPosition, opponentLegalMoves);

        if(!kingAttacks.isEmpty()){
            System.out.println("King is under check");
            return new MoveTransition(transitedBoard, move, MoveStatus.KING_IS_UNDER_CHECK);
        }
        return new MoveTransition(transitedBoard, move, MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    public abstract Collection<Piece> getOpponentActivePieces();
    public abstract Alliance getOpponentAlliance();
    protected Collection<Move> calculateCastles(final Collection<Move> playerLegalMoves,
                                                final Collection<Move> opponentLegalMoves) {
        //this.board.getCurrentPlayer().getAlliance().equals(Alliance.WHITE)
        final int lastRank = this.getAlliance().isWhite() ? 1 : 8;
        List<Move> castles = new ArrayList<>();
        System.out.println("this.king.isFirstMove() = " + this.king.isFirstMove());
        System.out.println("!this.isUnderCheck() = " + !this.isUnderCheck());
        if(this.king.isFirstMove() && !this.isUnderCheck()){
            System.out.println("King has no moved yet and is not under check");
            //King's side castling
            final Tile kingsRookDestinationTile = this.board.getTile('f', lastRank);
            final Tile kingsSideKingsDestinationTile = this.board.getTile('g', lastRank);
            if(kingsRookDestinationTile.isEmpty() && kingsSideKingsDestinationTile.isEmpty()){
                System.out.println("There are no pieces between king and rook");
                final Tile kingsRookStartTile = this.board.getTile('h', lastRank);
                if(kingsRookStartTile.isOccupied() && kingsRookStartTile.getPiece().getPieceType().isRook() &&
                        kingsRookStartTile.getPiece().isFirstMove()) {
                    if( Player.calculateAttacksOnTile(kingsRookDestinationTile.getTilePosition(),
                            opponentLegalMoves).isEmpty() &&
                            Player.calculateAttacksOnTile(kingsSideKingsDestinationTile.getTilePosition(),
                                    opponentLegalMoves).isEmpty()) {
                        castles.add(new KingsSideCastling(this.board, this.king,
                                                          kingsSideKingsDestinationTile.getTilePosition(),
                                                          (Rook)kingsRookStartTile.getPiece(),
                                                          kingsRookStartTile.getTilePosition(),
                                                          kingsRookDestinationTile.getTilePosition()));
                    }
                }
            }

            //Queen's side castling
            final Tile queensSideKnightsTile = this.board.getTile('b', lastRank);
            final Tile queensSideKingsDestinationTile = this.board.getTile('c', lastRank);
            final Tile queensRookDestinationTile = this.board.getTile('d', lastRank);
            if(queensSideKnightsTile.isEmpty() && queensSideKingsDestinationTile.isEmpty() &&
               queensRookDestinationTile.isEmpty()){
                final Tile queensRookStartTile = this.board.getTile('a', lastRank);
                if(queensRookStartTile.isOccupied() && queensRookStartTile.getPiece().getPieceType().isRook() &&
                        queensRookStartTile.getPiece().isFirstMove()){
                    if(Player.calculateAttacksOnTile(queensSideKingsDestinationTile.getTilePosition(),
                            this.getOpponentLegalMoves()).isEmpty() &&
                            Player.calculateAttacksOnTile(queensRookDestinationTile.getTilePosition(),
                                    this.getOpponentLegalMoves()).isEmpty()) {
                        castles.add(new QueensSideCastling(this.board, this.king,
                                                           queensSideKingsDestinationTile.getTilePosition(),
                                                           (Rook)queensRookStartTile.getPiece(),
                                                           queensRookStartTile.getTilePosition(),
                                                           queensRookDestinationTile.getTilePosition()));
                    }
                }

            }
        }
        return ImmutableList.copyOf(castles);
    }

    private King establishKing() throws RuntimeException {
        for (Piece piece : this.getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("\nPlayer should have the king!\n");
    }

    protected static Collection<Move> calculateAttacksOnTile(final Position position,
                                                             final Collection<Move> opponentMoves) {
        final List<Move> attackMoves = new ArrayList<>();
        opponentMoves.forEach(move -> {
            if(move.getDestination().equals(position)){
                attackMoves.add(move);
            }
        });
        return attackMoves;
    }

    private boolean hasEscapeMoves(){
        for(final Move move: this.legalMoves) {
            final MoveTransition transition = makeMove(move);
            if(transition.getMoveStatus().isDone()){
                return true;
            }
        }
        return false;
    }
}
