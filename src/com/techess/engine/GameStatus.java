package com.techess.engine;

/**
 * Created by igor on 07.03.18.
 */
public enum GameStatus {
    RUNNING {
        @Override
        public String getMessage() {
            return "Game is running.";
        }

        @Override
        public boolean isGameOver() {
            return false;
        }
    },
    WHITE_WON {
        @Override
        public String getMessage() {
            return "White won by checkmate!!!";
        }

        @Override
        public boolean isGameOver() {
            return true;
        }
    },
    BLACK_WON {
        @Override
        public String getMessage() {
            return "Black won by checkmate!!!";
        }

        @Override
        public boolean isGameOver() {
            return true;
        }
    },
    DRAW_BY_STALEMATE {
        @Override
        public String getMessage() {
            return "Draw by stalemate";
        }

        @Override
        public boolean isGameOver() {
            return true;
        }
    },
    DRAW_BY_INSUFFICIENT_MATERIAL {
        @Override
        public String getMessage() {
            return "Draw by insufficient material!";
        }

        @Override
        public boolean isGameOver() {
            return true;
        }
    },
    DRAW_BY_THRESHOLD_REPETITION {
        @Override
        public String getMessage() {
            return "Draw by threshold repetition!";
        }

        @Override
        public boolean isGameOver() {
            return true;
        }
    },
    DRAW_BY_FIFTY_MOVES_RULE {
        @Override
        public String getMessage() {
            return "Draw by fifty moves rule!";
        }

        @Override
        public boolean isGameOver() {
            return true;
        }
    };

    public abstract String getMessage();
    public abstract boolean isGameOver();
}
