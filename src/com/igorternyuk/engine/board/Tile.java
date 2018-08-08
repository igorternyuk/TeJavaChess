package com.igorternyuk.engine.board;

import com.google.common.collect.ImmutableMap;
import com.igorternyuk.engine.pieces.Piece;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by igor on 26.11.17.
 */

public abstract class Tile {

    protected final Location tileLocation;
    protected final boolean isLight;

    private static final Map<Location, EmptyTile> EMPTY_TILES = createAllPossibleEmptyTiles();

    private Tile(final Location tileLocation) {

        this.tileLocation = tileLocation;
        this.isLight = (this.tileLocation.getX() + this.tileLocation.getY()) % 2 == 0;
    }

    private static Map<Location, EmptyTile> createAllPossibleEmptyTiles() {
        final Map<Location, EmptyTile> emptyTilesMap = new HashMap<>();
        for(int y = 0; y < BoardUtils.BOARD_SIZE; ++y){
            for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x){
                emptyTilesMap.put(BoardUtils.getLocation(x, y), new EmptyTile(BoardUtils.getLocation(x, y)));
            }
        }
        return ImmutableMap.copyOf(emptyTilesMap);
    }

    public static Tile createTile(Location tileLocation, Piece piece) {
        return piece != null ? new OccupiedTile(tileLocation, piece) : EMPTY_TILES.get(tileLocation);
    }

    public Location getTileLocation() {
        return this.tileLocation;
    }

    public boolean isTileLight(){
        return this.isLight;
    }

    public boolean isTileDark(){
        return !this.isLight;
    }

    public abstract boolean isOccupied();
    public abstract boolean isEmpty();
    public abstract Piece getPiece();

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || !(other instanceof Tile)) return false;
        Tile otherTile = (Tile) other;
        return Objects.equals(this.tileLocation, otherTile.getTileLocation());
    }

    @Override
    public int hashCode() {
        return this.tileLocation.hashCode();
    }

    public static final class EmptyTile extends Tile{

        private EmptyTile(final Location tileLocation) {
            super(tileLocation);
        }

        @Override
        public boolean isOccupied() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public String toString() {
            return "[]";
        }
    }

    public static final class OccupiedTile extends Tile{
        private final Piece pieceOnTile;

        private OccupiedTile(final Location tileLocation, Piece piece) {
            super(tileLocation);
            pieceOnTile = piece;
        }

        @Override
        public boolean isOccupied() {
            return true;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return pieceOnTile;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || !(other instanceof OccupiedTile)) return false;
            Tile otherOccupiedTile = (Tile) other;
            return Objects.equals(this.pieceOnTile, otherOccupiedTile.getPiece()) &&
                   super.equals(otherOccupiedTile);
        }

        @Override
        public int hashCode() {
            final int prime = 73;
            return prime * this.tileLocation.hashCode() + this.pieceOnTile.hashCode();
        }

        @Override
        public String toString() {
            return pieceOnTile.getAlliance().isWhite() ? pieceOnTile.toString().toUpperCase() :
                    pieceOnTile.toString().toLowerCase();
        }
    }
}
