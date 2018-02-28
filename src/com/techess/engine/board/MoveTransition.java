package com.techess.engine.board;

import com.techess.engine.board.Board;
import com.techess.engine.board.Move;
import com.techess.engine.board.MoveStatus;

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

    public MoveStatus getMoveStatus(){
        return this.moveStatus;
    }

    public Move getLastMove(){
        return this.lastMove;
    }
}
