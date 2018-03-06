package com.techess.engine.board;

/**
 * Created by igor on 06.03.18.
 */
public enum GameType {
    CLASSIC_CHESS {
        @Override
        public boolean isClassicChess() {
            return true;
        }

        @Override
        public boolean isRandomFisherChess() {
            return false;
        }
    },
    RANDOM_FISHER_CHESS {
        @Override
        public boolean isClassicChess() {
            return false;
        }

        @Override
        public boolean isRandomFisherChess() {
            return true;
        }
    };

    private boolean randomFisherChess;

    public abstract boolean isClassicChess();

    public abstract boolean isRandomFisherChess();
}
