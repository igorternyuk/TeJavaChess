package com.techess.engine.board;

/**
 * Created by igor on 04.03.18.
 */

public enum BoardOrientation {
    REGULAR {
        @Override
        public boolean isOpposite() {
            return false;
        }

        @Override
        public BoardOrientation getOpposite() {
            return BoardOrientation.OPPOSITE;
        }
    },
    OPPOSITE {
        @Override
        public boolean isOpposite() {
            return true;
        }

        @Override
        public BoardOrientation getOpposite() {
            return BoardOrientation.REGULAR;
        }
    };

    public abstract boolean isOpposite();

    public abstract BoardOrientation getOpposite();
}
