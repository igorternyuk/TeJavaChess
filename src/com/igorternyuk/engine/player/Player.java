package com.igorternyuk.engine.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.igorternyuk.engine.Alliance;
import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.board.Tile;
import com.igorternyuk.engine.moves.*;
import com.igorternyuk.engine.pieces.King;
import com.igorternyuk.engine.pieces.Piece;
import com.igorternyuk.engine.pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by igor on 03.12.17.
 */

public abstract class Player {
    protected final Board board;
    private final King king;
    private final Collection<Move> legalMoves;
    private final Collection<Move> opponentLegalMoves;
    private final boolean isInCheck;
    private final boolean kingSideCastlingCapable;
    private final boolean queenSideCastlingCapable;

    protected Player(final Board board, final Collection<Move> legalMoves, final Collection<Move> opponentMoves) {
        this.board = board;
        this.king = establishKing();
        this.opponentLegalMoves = opponentMoves;
        this.isInCheck = !Player.calculateAttacksOnTile(this.king.getLocation(), opponentMoves).isEmpty();
        if (!this.isCastled()) {
            final Collection<Move> castles = this.calculateCastles(legalMoves, opponentMoves);
            this.kingSideCastlingCapable = castles.stream().anyMatch(move -> move.isKingSideCastling());
            this.queenSideCastlingCapable = castles.stream().anyMatch(move -> move.isQueenSideCastling());
            /*System.out.println((this.getAlliance().isWhite() ? "White king" : "Black king") +
                    " has castles = " + castles.size());*/
            //System.out.println("Size of legal moves collection before adding the castles = " + legalMoves.size());
            this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, castles));
        } else {
            this.legalMoves = ImmutableList.copyOf(legalMoves);
            this.kingSideCastlingCapable = false;
            this.queenSideCastlingCapable = false;
        }
        //System.out.println("Size of legal moves collection after adding the castles = " + this.legalMoves.size());
    }

    public Board getBoard() {
        return this.board;
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

    public boolean canCastleKingSide() {
        return this.legalMoves.stream().anyMatch(Move::isKingSideCastling);
    }

    public boolean canCastleQueenSide() {
        return this.legalMoves.stream().anyMatch(Move::isQueenSideCastling);
    }

    public boolean canCastle() {
        return this.kingSideCastlingCapable || this.queenSideCastlingCapable;
    }

    public boolean isKingSideCastlingCapable() {
        return this.kingSideCastlingCapable;
    }

    public boolean isQueenSideCastlingCapable() {
        return this.queenSideCastlingCapable;
    }

    private boolean isMoveLegal(final Move move) {
        return this.legalMoves.contains(move);
    }

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
        return !this.king.isFirstMove();
    }

    public MoveTransition makeMove(final Move move){
        if(!isMoveLegal(move)){
            System.out.println("Illegal move");
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitedBoard = move.execute(); //This method transfers the turn to the opponent
        final Player playerWhoseMoveIsGoingToBeChecked = transitedBoard.getCurrentPlayer().getOpponent();
        final Location currentPlayerLocation = playerWhoseMoveIsGoingToBeChecked.getPlayerKing().getLocation();
        final Collection<Move> opponentLegalMoves = playerWhoseMoveIsGoingToBeChecked.getOpponentLegalMoves();
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(currentPlayerLocation, opponentLegalMoves);

        if(!kingAttacks.isEmpty()){
            return new MoveTransition(transitedBoard, move, MoveStatus.KING_IS_UNDER_CHECK);
        }
        return new MoveTransition(transitedBoard, move, MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    public abstract Collection<Piece> getOpponentActivePieces();
    public abstract Alliance getOpponentAlliance();

    private Collection<Move> calculateCastles(final Collection<Move> playerLegalMoves,
                                              final Collection<Move> opponentLegalMoves) {
        List<Move> castles = new ArrayList<>();
        if(this.king.isFirstMove() && !this.isUnderCheck()){
            final int lastRank = this.getAlliance().isWhite() ?
                    BoardUtils.getAlgebraicNotationForCoordinateY(BoardUtils.FIRST_RANK) :
                    BoardUtils.getAlgebraicNotationForCoordinateY(BoardUtils.EIGHTH_RANK);
            //King's side castling
            //System.out.println("King is not under check and not moved yet");
            final Tile kingsRookDestinationTile = this.board.getTile('f', lastRank);
            final Tile kingsSideKingsDestinationTile = this.board.getTile('g', lastRank);
            //System.out.println("lastRank = " + lastRank);

            if(this.board.getGameType().isClassicChess()) {
                if(kingsRookDestinationTile.isEmpty() && kingsSideKingsDestinationTile.isEmpty()) {
                    final Tile kingsRookStartTile = this.board.getTile('h', lastRank);
                    if (kingsRookStartTile.isOccupied() && kingsRookStartTile.getPiece().getPieceType().isRook() &&
                            kingsRookStartTile.getPiece().isFirstMove()) {
                        if (Player.calculateAttacksOnTile(kingsRookDestinationTile.getTileLocation(),
                                opponentLegalMoves).isEmpty() &&
                                Player.calculateAttacksOnTile(kingsSideKingsDestinationTile.getTileLocation(),
                                        opponentLegalMoves).isEmpty()) {
                            castles.add(new KingsSideCastling(this.board, this.king,
                                    kingsSideKingsDestinationTile.getTileLocation(),
                                    (Rook) kingsRookStartTile.getPiece(),
                                    kingsRookStartTile.getTileLocation(),
                                    kingsRookDestinationTile.getTileLocation()));
                        }
                    }
                }
            } else if(this.board.getGameType().isRandomFisherChess()){
                //System.out.println("King's side castling");
                final int backRankCoordinateY = this.getAlliance().isWhite()? BoardUtils.FIRST_RANK : BoardUtils.EIGHTH_RANK;
                final int kingX = this.king.getLocation().getX();
                final int rookX = this.board.getKingsRookStartCoordinateX();
                final Tile kingsRookStartTile = this.board.getTile(rookX, backRankCoordinateY);
                final boolean isRookStartTileOK = kingsRookStartTile.isOccupied() &&
                                                  kingsRookStartTile.getPiece().getPieceType().isRook() &&
                                                  kingsRookStartTile.getPiece().isFirstMove();
                if(isRookStartTileOK){
                    final Rook castlingRook = (Rook)kingsRookStartTile.getPiece();
                    final boolean isKingDestinationOK = kingsSideKingsDestinationTile.isEmpty() ||
                            (kingsSideKingsDestinationTile.isOccupied() &&
                                    (kingsSideKingsDestinationTile.getPiece().equals(this.king) ||
                                            kingsSideKingsDestinationTile.getPiece().equals(castlingRook)));

                    //System.out.println("isKingDestinationOK = " + isKingDestinationOK);
                    //System.out.println("isRookStartTileOK = " + isRookStartTileOK);
                    if (isKingDestinationOK) {
                        final boolean isCastlingRookDestinationOK = kingsRookDestinationTile.isEmpty() ||
                                (kingsRookDestinationTile.isOccupied() &&
                                        (kingsRookDestinationTile.getPiece().equals(castlingRook) ||
                                                kingsRookDestinationTile.getPiece().equals(this.king)));
                        //System.out.println("isCastlingRookDestinationOK = " + isCastlingRookDestinationOK);
                        if(isCastlingRookDestinationOK){
                            boolean isAllBetweenTilesOK = true;
                            final int kingDestinationX = kingsSideKingsDestinationTile.getTileLocation().getX();
                            for(int x = kingX + 1; x <= kingDestinationX; ++x){
                                final Tile currentTile = this.board.getTile(x, backRankCoordinateY);
                                //System.out.println("Checking between tile x = " + x);
                                final boolean isCurrentTileOccupiedNotByCastlingRook = currentTile.isOccupied() &&
                                        !currentTile.getPiece().equals(castlingRook);
                                final boolean isCurrentTileUnderCheck =
                                        !Player.calculateAttacksOnTile(currentTile.getTileLocation(),
                                        opponentLegalMoves).isEmpty();
                                //System.out.println("isCurrentTileOccupiedNotByCastlingRook = " +
                                        //isCurrentTileOccupiedNotByCastlingRook);
                                //System.out.println("isCurrentTileUnderCheck = " + isCurrentTileUnderCheck);
                                if(isCurrentTileOccupiedNotByCastlingRook || isCurrentTileUnderCheck){
                                    isAllBetweenTilesOK = false;
                                    break;
                                }
                            }
                            //System.out.println("isAllBetweenTilesOK = " + isAllBetweenTilesOK);
                            if(isAllBetweenTilesOK){
                                castles.add(new KingsSideCastling(this.board, this.king,
                                        kingsSideKingsDestinationTile.getTileLocation(),
                                        castlingRook,
                                        kingsRookStartTile.getTileLocation(),
                                        kingsRookDestinationTile.getTileLocation()));
                            }
                        }
                    }
                }
            }

            //Queen's side castling

            final Tile queensSideKingsDestinationTile = this.board.getTile('c', lastRank);
            final Tile queensRookDestinationTile = this.board.getTile('d', lastRank);

            if(this.board.getGameType().isClassicChess()) {
                if(queensSideKingsDestinationTile.isEmpty() && queensRookDestinationTile.isEmpty()){
                    final Tile queensSideKnightsTile = this.board.getTile('b', lastRank);
                    final Tile queensRookStartTile = this.board.getTile('a', lastRank);
                    if(queensSideKnightsTile.isEmpty() && queensRookStartTile.isOccupied() &&
                            queensRookStartTile.getPiece().getPieceType().isRook() &&
                            queensRookStartTile.getPiece().isFirstMove()) {
                        if (Player.calculateAttacksOnTile(queensSideKingsDestinationTile.getTileLocation(),
                                this.getOpponentLegalMoves()).isEmpty() &&
                                Player.calculateAttacksOnTile(queensRookDestinationTile.getTileLocation(),
                                        this.getOpponentLegalMoves()).isEmpty()) {
                            castles.add(new QueensSideCastling(this.board, this.king,
                                    queensSideKingsDestinationTile.getTileLocation(),
                                    (Rook) queensRookStartTile.getPiece(),
                                    queensRookStartTile.getTileLocation(),
                                    queensRookDestinationTile.getTileLocation()));
                        }
                    }
                }
            } else {
                //System.out.println("Queen's side castling");
                final int backRankCoordinateY = this.getAlliance().isWhite()? BoardUtils.FIRST_RANK :
                        BoardUtils.EIGHTH_RANK;
                final int kingX = this.king.getLocation().getX();
                final int rookX = this.board.getQueensRookStartCoordinateX();
                final Tile queensRookStartTile = this.board.getTile(rookX, backRankCoordinateY);
                final boolean isRookStartTileOK = queensRookStartTile.isOccupied() &&
                              queensRookStartTile.getPiece().getPieceType().isRook() &&
                              queensRookStartTile.getPiece().isFirstMove();
                if(isRookStartTileOK){
                    final Rook castlingRook = (Rook)queensRookStartTile.getPiece();
                    final boolean isKingDestinationOK = queensSideKingsDestinationTile.isEmpty() ||
                            (queensSideKingsDestinationTile.isOccupied() &&
                                    (queensSideKingsDestinationTile.getPiece().equals(this.king) ||
                                            queensSideKingsDestinationTile.getPiece().equals(castlingRook)));

                    //System.out.println("isKingDestinationOK = " + isKingDestinationOK);
                    //System.out.println("isRookStartTileOK = " + isRookStartTileOK);
                    if (isKingDestinationOK) {

                        final boolean isCastlingRookDestinationOK = queensRookDestinationTile.isEmpty() ||
                                (queensRookDestinationTile.isOccupied() &&
                                        (queensRookDestinationTile.getPiece().equals(castlingRook) ||
                                         queensRookDestinationTile.getPiece().equals(this.king)));
                        //System.out.println("isCastlingRookDestinationOK = " + isCastlingRookDestinationOK);
                        if(isCastlingRookDestinationOK){
                            boolean isAllBetweenTilesOK = true;
                            final int kingDestinationX = queensSideKingsDestinationTile.getTileLocation().getX();
                            for(int x = kingX - 1; x >= kingDestinationX; --x){
                                //System.out.println("Checking between tile x = " + x);
                                final Tile currentTile = this.board.getTile(x, backRankCoordinateY);
                                final boolean isCurrentTileOccupiedNotByCastlingRook = currentTile.isOccupied() &&
                                        !currentTile.getPiece().equals(castlingRook);
                                //System.out.println("isCurrentTileOccupiedByCastlingRook = " + isCurrentTileOccupiedNotByCastlingRook);
                                final boolean isCurrentTileUnderCheck =
                                        !Player.calculateAttacksOnTile(currentTile.getTileLocation(),
                                        opponentLegalMoves).isEmpty();
                                //System.out.println("isCurrentTileUnderCheck = " + isCurrentTileUnderCheck);
                                if(isCurrentTileOccupiedNotByCastlingRook || isCurrentTileUnderCheck){
                                    isAllBetweenTilesOK = false;
                                    break;
                                }
                            }
                            //System.out.println("isAllBetweenTilesOK = " + isAllBetweenTilesOK);
                            if(isAllBetweenTilesOK){
                                castles.add(new QueensSideCastling(this.board, this.king,
                                        queensSideKingsDestinationTile.getTileLocation(),
                                        castlingRook,
                                        queensRookStartTile.getTileLocation(),
                                        queensRookDestinationTile.getTileLocation()));
                            }
                        }
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

    private static Collection<Move> calculateAttacksOnTile(final Location location,
                                                           final Collection<Move> opponentMoves) {
        final List<Move> attackMoves = new ArrayList<>();
        opponentMoves.stream().filter(move -> move.getDestination().equals(location)).forEach(attackMoves::add);
        return attackMoves;
    }

    public List<Move> calcEscapeMoves() {
        return this.legalMoves.stream().filter(move -> {
            final MoveTransition transition = makeMove(move);
            return transition.getMoveStatus().isDone();
        }).collect(Collectors.toList());
    }

    private boolean hasEscapeMoves(){
        return !calcEscapeMoves().isEmpty();
    }
}
