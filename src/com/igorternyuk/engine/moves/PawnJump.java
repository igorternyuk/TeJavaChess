package com.igorternyuk.engine.moves;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.pieces.Pawn;
import com.igorternyuk.engine.pieces.Piece;

/**
 * Created by igor on 08.08.18.
 */
public class PawnJump extends PawnMove {
    public PawnJump(final Board board, final Piece movedPiece, final Location destination) {
        super(board, movedPiece, destination);
    }

    @Override
    public Board execute() {
        final Board.Builder builder = new Board.Builder();
        this.board.getCurrentPlayer().getActivePieces().stream().filter(piece -> !this.movedPiece.equals(piece))
                .forEach(builder::setPiece);
        this.board.getCurrentPlayer().getOpponentActivePieces().forEach(builder::setPiece);
        Pawn jumpedPawn = (Pawn) this.movedPiece.move(this);
        builder.setEnPassantPawn(jumpedPawn);
        builder.setPiece(jumpedPawn);
        builder.setGameType(this.board.getGameType());
        builder.setKingsRookStartCoordinateX(this.board.getKingsRookStartCoordinateX());
        builder.setQueensRookStartCoordinateX(this.board.getQueensRookStartCoordinateX());
        builder.setMoveMaker(this.board.getCurrentPlayer().getOpponentAlliance());
        return builder.build();
    }

    @Override
    public boolean equals(final Object other) {
        return this == other || ((other instanceof PawnJump) && super.equals(other));
    }
}