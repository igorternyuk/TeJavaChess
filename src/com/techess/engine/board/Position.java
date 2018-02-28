package com.techess.engine.board;

import java.util.Objects;

/**
 * Created by igor on 01.12.17.
 */

public class Position {
    private int x,y;

    public Position(){
        this(0,0);
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

   /* public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }*/

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object that) {
        if (that == null || !(that instanceof Position)) return false;
        if (this == that) return true;
        Position other = (Position) that;
        return Objects.equals(this.x, other.getX()) && Objects.equals(this.y, other.getY());
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
