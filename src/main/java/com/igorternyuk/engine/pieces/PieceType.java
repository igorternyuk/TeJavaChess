package com.igorternyuk.engine.pieces;

/**
 * Created by igor on 05.03.18.
 */
public enum PieceType {
    KING("k", 1000) {
        @Override
        public boolean isKing() {
            return true;
        }

        @Override
        public boolean isRook() {
            return false;
        }

        @Override
        public boolean isBishop() {
            return false;
        }

        @Override
        public boolean isKnight() {
            return false;
        }

        @Override
        public boolean isQueen() {
            return false;
        }

        @Override
        public boolean isPawn() {
            return false;
        }

        @Override
        public boolean isMinorPiece() {
            return false;
        }

        @Override
        public boolean isMajorPiece() {
            return false;
        }
    },
    QUEEN("q", 900) {
        @Override
        public boolean isKing() {
            return false;
        }

        @Override
        public boolean isRook() {
            return false;
        }

        @Override
        public boolean isBishop() {
            return false;
        }

        @Override
        public boolean isKnight() {
            return false;
        }

        @Override
        public boolean isQueen() {
            return true;
        }

        @Override
        public boolean isPawn() {
            return false;
        }

        @Override
        public boolean isMinorPiece() {
            return false;
        }

        @Override
        public boolean isMajorPiece() {
            return true;
        }
    },
    BISHOP("b", 350) {
        @Override
        public boolean isKing() {
            return false;
        }

        @Override
        public boolean isRook() {
            return false;
        }

        @Override
        public boolean isBishop() {
            return true;
        }

        @Override
        public boolean isKnight() {
            return false;
        }

        @Override
        public boolean isQueen() {
            return false;
        }

        @Override
        public boolean isPawn() {
            return false;
        }

        @Override
        public boolean isMinorPiece() {
            return true;
        }

        @Override
        public boolean isMajorPiece() {
            return false;
        }
    },
    KNIGHT("n", 300) {
        @Override
        public boolean isKing() {
            return false;
        }

        @Override
        public boolean isRook() {
            return false;
        }

        @Override
        public boolean isBishop() {
            return false;
        }

        @Override
        public boolean isKnight() {
            return true;
        }

        @Override
        public boolean isQueen() {
            return false;
        }

        @Override
        public boolean isPawn() {
            return false;
        }

        @Override
        public boolean isMinorPiece() {
            return true;
        }

        @Override
        public boolean isMajorPiece() {
            return false;
        }
    },
    ROOK("r", 500) {
        @Override
        public boolean isKing() {
            return false;
        }

        @Override
        public boolean isRook() {
            return true;
        }

        @Override
        public boolean isBishop() {
            return false;
        }

        @Override
        public boolean isKnight() {
            return false;
        }

        @Override
        public boolean isQueen() {
            return false;
        }

        @Override
        public boolean isPawn() {
            return false;
        }

        @Override
        public boolean isMinorPiece() {
            return false;
        }

        @Override
        public boolean isMajorPiece() {
            return true;
        }
    },
    PAWN("p", 100) {
        @Override
        public boolean isKing() {
            return false;
        }

        @Override
        public boolean isRook() {
            return false;
        }

        @Override
        public boolean isBishop() {
            return false;
        }

        @Override
        public boolean isKnight() {
            return false;
        }

        @Override
        public boolean isQueen() {
            return false;
        }

        @Override
        public boolean isPawn() {
            return true;
        }

        @Override
        public boolean isMinorPiece() {
            return false;
        }

        @Override
        public boolean isMajorPiece() {
            return false;
        }
    };

    PieceType(final String name, final int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public abstract boolean isKing();

    public abstract boolean isRook();

    public abstract boolean isBishop();

    public abstract boolean isKnight();

    public abstract boolean isQueen();

    public abstract boolean isPawn();

    public abstract boolean isMinorPiece();

    public abstract boolean isMajorPiece();

    private String name;
    private int value;
}
