package com.techess.engine.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.techess.engine.Alliance;
import com.techess.engine.board.Board;
import com.techess.engine.board.Position;
import com.techess.engine.board.Tile;
import com.techess.engine.moves.Move;
import com.techess.engine.moves.Move.KingsSideCastling;
import com.techess.engine.moves.Move.QueensSideCastling;
import com.techess.engine.moves.MoveStatus;
import com.techess.engine.moves.MoveTransition;
import com.techess.engine.pieces.King;
import com.techess.engine.pieces.Piece;
import com.techess.engine.pieces.Rook;
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
        this.isInCheck = !Player.calculateAttacksOnTile(this.king.getPosition(), opponentMoves).isEmpty();
        final Collection<Move> castles = this.calculateCastles(legalMoves, opponentMoves);
        System.out.println((this.getAlliance().isWhite() ? "White king" : "Black king") +
                " has castles = " + castles.size());
        final Alliance currentAlliance = this.getAlliance();
        System.out.println("Size of legal moves collection before adding the castles = " + legalMoves.size());
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, castles));
        System.out.println("Size of legal moves collection after adding the castles = " + this.legalMoves.size());
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
        System.out.println("is castling = " + move.isCastlingMove());
        if(!isMoveLegal(move)){
            System.out.println("Illegal move");
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitedBoard = move.execute(); //This method transfer the turn to the opponent
        final Player playerWhoseMoveIsGoingToBeChecked = transitedBoard.getCurrentPlayer().getOpponent();
        final Position currentPlayerPosition = playerWhoseMoveIsGoingToBeChecked.getPlayerKing().getPosition();
        final Collection<Move> opponentLegalMoves = playerWhoseMoveIsGoingToBeChecked.getOpponentLegalMoves();
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(currentPlayerPosition, opponentLegalMoves);

        if(!kingAttacks.isEmpty()){
            System.out.println("Trying to execute " + move);
            System.out.println("Moving piece - " + move.getMovedPiece().getPieceType().getName());
            System.out.println("Start position " + move.getMovedPiece().getPosition());
            System.out.println("Target position " + move.getDestination());
            System.out.println("Is pawn promotion - " + move.isPawnPromotionMove());
            System.out.println("King is under check");
            kingAttacks.forEach(attack -> {
                System.out.println("attack = " + attack.toString());
            });
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
        //this.board.getCurrentPlayer().getAlliance().equals(Alliance.WHITE)
        if(this.board.getGameType().isRandomFisherChess() && this.getAlliance().isWhite()){
            System.out.println("");
        }
        final int lastRank = this.getAlliance().isWhite() ? Board.getAlgebraicNotationForCoordinateY(Board.FIRST_RANK) :
                Board.getAlgebraicNotationForCoordinateY(Board.EIGHTH_RANK);
        List<Move> castles = new ArrayList<>();
        if(this.king.getAlliance().isWhite()){
            //System.out.println("CASTLING CALCULATION FOR WHITE KING!");
        } else {
            //System.out.println("CASTLING CALCULATION FOR BLACK KING!");
        }
        if(this.king.isFirstMove() && !this.isUnderCheck()){
            //King's side castling
            //System.out.println("King is not under check and not moved yet");
            final Tile kingsRookDestinationTile = this.board.getTile('f', lastRank);
            final Tile kingsSideKingsDestinationTile = this.board.getTile('g', lastRank);
            //System.out.println("lastRank = " + lastRank);
            boolean isKingsRookDestinationTileEmpty = kingsRookDestinationTile.isEmpty();
            boolean isKingsSideKingsDestinationTileEmpty = kingsSideKingsDestinationTile.isEmpty();
            boolean isGameTypeIsChess960 = this.board.getGameType().isRandomFisherChess();
            //System.out.println("isKingsRookDestinationTileEmpty = " + isKingsRookDestinationTileEmpty);
            //System.out.println("isKingsSideKingsDestinationTileEmpty = " + isKingsSideKingsDestinationTileEmpty);
            //System.out.println("isGameTypeIsChess960 = " + isGameTypeIsChess960);

            if(this.board.getGameType().isClassicChess()) {
                if(kingsRookDestinationTile.isEmpty() && kingsSideKingsDestinationTile.isEmpty()) {
                    final Tile kingsRookStartTile = this.board.getTile('h', lastRank);
                    if (kingsRookStartTile.isOccupied() && kingsRookStartTile.getPiece().getPieceType().isRook() &&
                            kingsRookStartTile.getPiece().isFirstMove()) {
                        if (Player.calculateAttacksOnTile(kingsRookDestinationTile.getTilePosition(),
                                opponentLegalMoves).isEmpty() &&
                                Player.calculateAttacksOnTile(kingsSideKingsDestinationTile.getTilePosition(),
                                        opponentLegalMoves).isEmpty()) {
                            castles.add(new KingsSideCastling(this.board, this.king,
                                    kingsSideKingsDestinationTile.getTilePosition(),
                                    (Rook) kingsRookStartTile.getPiece(),
                                    kingsRookStartTile.getTilePosition(),
                                    kingsRookDestinationTile.getTilePosition()));
                        }
                    }
                }
            } else if(this.board.getGameType().isRandomFisherChess()){
                //System.out.println("King's side castling");
                final int backRankCoordinateY = this.getAlliance().isWhite()? Board.FIRST_RANK : Board.EIGHTH_RANK;
                final int kingX = this.king.getPosition().getX();
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
                    if(isKingDestinationOK && isRookStartTileOK ){
                        final boolean isCastlingRookDestinationOK = kingsRookDestinationTile.isEmpty() ||
                                (kingsRookDestinationTile.isOccupied() &&
                                        (kingsRookDestinationTile.getPiece().equals(castlingRook) ||
                                                kingsRookDestinationTile.getPiece().equals(this.king)));
                        //System.out.println("isCastlingRookDestinationOK = " + isCastlingRookDestinationOK);
                        if(isCastlingRookDestinationOK){
                            boolean isAllBetweenTilesOK = true;
                            final int kingDestinationX =  kingsSideKingsDestinationTile.getTilePosition().getX();
                            for(int x = kingX + 1; x <= kingDestinationX; ++x){
                                final Tile currentTile = this.board.getTile(x, backRankCoordinateY);
                                //System.out.println("Checking between tile x = " + x);
                                final boolean isCurrentTileOccupiedNotByCastlingRook = currentTile.isOccupied() &&
                                        !currentTile.getPiece().equals(castlingRook);
                                final boolean isCurrentTileUnderCheck = !Player.calculateAttacksOnTile(currentTile.getTilePosition(),
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
                                        kingsSideKingsDestinationTile.getTilePosition(),
                                        castlingRook,
                                        kingsRookStartTile.getTilePosition(),
                                        kingsRookDestinationTile.getTilePosition()));
                            }
                        }
                    }
                }
            }

            //Queen's side castling

            final Tile queensSideKingsDestinationTile = this.board.getTile('c', lastRank);
            final Tile queensRookDestinationTile = this.board.getTile('d', lastRank);
            boolean isQueensSideKingsDestinationTileEmpty = queensSideKingsDestinationTile.isEmpty();
            boolean isQueensRookDestinationTileEmpty = queensRookDestinationTile.isEmpty();

            if(this.board.getGameType().isClassicChess()) {
                if(queensSideKingsDestinationTile.isEmpty() && queensRookDestinationTile.isEmpty()){
                    final Tile queensSideKnightsTile = this.board.getTile('b', lastRank);
                    final Tile queensRookStartTile = this.board.getTile('a', lastRank);
                    if(queensSideKnightsTile.isEmpty() && queensRookStartTile.isOccupied() &&
                            queensRookStartTile.getPiece().getPieceType().isRook() &&
                            queensRookStartTile.getPiece().isFirstMove()) {
                        if (Player.calculateAttacksOnTile(queensSideKingsDestinationTile.getTilePosition(),
                                this.getOpponentLegalMoves()).isEmpty() &&
                                Player.calculateAttacksOnTile(queensRookDestinationTile.getTilePosition(),
                                        this.getOpponentLegalMoves()).isEmpty()) {
                            castles.add(new QueensSideCastling(this.board, this.king,
                                    queensSideKingsDestinationTile.getTilePosition(),
                                    (Rook) queensRookStartTile.getPiece(),
                                    queensRookStartTile.getTilePosition(),
                                    queensRookDestinationTile.getTilePosition()));
                        }
                    }
                }
            } else {
                //System.out.println("Queen's side castling");
                final int backRankCoordinateY = this.getAlliance().isWhite()? Board.FIRST_RANK : Board.EIGHTH_RANK;
                final int kingX = this.king.getPosition().getX();
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
                    if(isKingDestinationOK && isRookStartTileOK){

                        final boolean isCastlingRookDestinationOK = queensRookDestinationTile.isEmpty() ||
                                (queensRookDestinationTile.isOccupied() &&
                                        (queensRookDestinationTile.getPiece().equals(castlingRook) ||
                                         queensRookDestinationTile.getPiece().equals(this.king)));
                        //System.out.println("isCastlingRookDestinationOK = " + isCastlingRookDestinationOK);
                        if(isCastlingRookDestinationOK){
                            boolean isAllBetweenTilesOK = true;
                            final int kingDestinationX =  queensSideKingsDestinationTile.getTilePosition().getX();
                            for(int x = kingX - 1; x >= kingDestinationX; --x){
                                //System.out.println("Checking between tile x = " + x);
                                final Tile currentTile = this.board.getTile(x, backRankCoordinateY);
                                final boolean isCurrentTileOccupiedNotByCastlingRook = currentTile.isOccupied() &&
                                        !currentTile.getPiece().equals(castlingRook);
                                //System.out.println("isCurrentTileOccupiedByCastlingRook = " + isCurrentTileOccupiedNotByCastlingRook);
                                final boolean isCurrentTileUnderCheck = !Player.calculateAttacksOnTile(currentTile.getTilePosition(),
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
                                        queensSideKingsDestinationTile.getTilePosition(),
                                        castlingRook,
                                        queensRookStartTile.getTilePosition(),
                                        queensRookDestinationTile.getTilePosition()));
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
