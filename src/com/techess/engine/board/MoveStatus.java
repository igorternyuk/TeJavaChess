package com.techess.engine.board;

/**
 * Created by igor on 25.02.18.
 */

public enum MoveStatus {
    DONE {
        @Override
        public boolean isDone() {
            return true;
        }
    },
    ILLEGAL_MOVE {
        @Override
        public boolean isDone() {
            return false;
        }
    },
    KING_IS_UNDER_CHECK {
        @Override
        public boolean isDone() {
            return false;
        }
    };
    public abstract boolean isDone();
}
