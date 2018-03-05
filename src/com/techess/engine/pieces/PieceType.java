package com.techess.engine.pieces;

/**
 * Created by igor on 05.03.18.
 */
public enum PieceType {
    KING("k", 10000, true, false, false),
    QUEEN("q", 900, false, false, false),
    BISHOP("b", 300, false, false, false),
    KNIGHT("n", 300, false, false, false),
    ROOK("r", 500, false, true, false),
    PAWN("p", 100, false, false, true);

    private PieceType(final String name, final int value, final boolean isKing, final boolean isRook,
                      final boolean isPawn) {
        this.name = name;
        this.value = value;
        this.isKing = isKing;
        this.isRook = isRook;
        this.isPawn = isPawn;
    }

    public String getName() {
        return this.name;
    }

    public int getValue() {
        return this.value;
    }

    public boolean isKing() {
        return this.isKing;
    }

    public boolean isRook() {
        return this.isRook;
    }

    public boolean isPawn() {
        return this.isPawn;
    }

    private String name;
    private int value;
    private boolean isKing;
    private boolean isRook;
    private boolean isPawn;
}
