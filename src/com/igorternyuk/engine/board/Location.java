package com.igorternyuk.engine.board;

import java.util.Objects;

/**
 * Created by igor on 01.12.17.
 */

public class Location {
    private int x,y;

    public Location() {
        this(0,0);
    }

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object that) {
        if (that == null || !(that instanceof Location)) return false;
        if (this == that) return true;
        Location other = (Location) that;
        return Objects.equals(this.x, other.getX()) && Objects.equals(this.y, other.getY());
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString(){
        return "(" + x + "," + y + ")";
    }
}
