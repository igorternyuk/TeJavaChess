package com.techess.engine.moves;

import com.techess.engine.board.Board;

/**
 * Created by igor on 25.02.18.
 */

public class MoveTransition {
    private final Board transitedBoard;
    private final Move lastMove;
    private final MoveStatus moveStatus;

    public MoveTransition(Board transitedBoard, Move lastMove, MoveStatus moveStatus) {
        this.transitedBoard = transitedBoard;
        this.lastMove = lastMove;
        this.moveStatus = moveStatus;
    }

    public Board getTransitedBoard(){
        return this.transitedBoard;
    }

    public MoveStatus getMoveStatus(){
        return this.moveStatus;
    }

    public Move getLastMove(){
        return this.lastMove;
    }
}
