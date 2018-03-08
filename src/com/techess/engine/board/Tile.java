package com.techess.engine.board;

import com.google.common.collect.ImmutableMap;
import com.techess.engine.pieces.Piece;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by igor on 26.11.17.
 */

public abstract class Tile {

    protected final Position tilePosition;
    protected final boolean isLight;

    private static final Map<Position, EmptyTile> EMPTY_TILES = createAllPossibleEmptyTiles();

    private Tile(final Position tilePosition) {

        this.tilePosition = tilePosition;
        this.isLight = (this.tilePosition.getX() + this.tilePosition.getY()) % 2 == 0;
    }

    private static Map<Position,EmptyTile> createAllPossibleEmptyTiles() {
        final Map<Position,EmptyTile> emptyTilesMap = new HashMap<>();
        for(int y = 0; y < BoardUtils.BOARD_SIZE; ++y){
            for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x){
                emptyTilesMap.put(BoardUtils.getPosition(x,y), new EmptyTile(BoardUtils.getPosition(x,y)));
            }
        }
        return ImmutableMap.copyOf(emptyTilesMap);
    }

    public static Tile createTile(Position tilePosition, Piece piece){
        return piece != null ? new OccupiedTile(tilePosition, piece) : EMPTY_TILES.get(tilePosition);
    }

    public Position getTilePosition(){
        return this.tilePosition;
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
        return Objects.equals(this.tilePosition, otherTile.getTilePosition());
    }

    @Override
    public int hashCode() {
        return this.tilePosition.hashCode();
    }

    public static final class EmptyTile extends Tile{

        private EmptyTile(final Position tilePosition) {
            super(tilePosition);
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

        private OccupiedTile(final Position tilePosition, Piece piece) {
            super(tilePosition);
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
            return prime * this.tilePosition.hashCode() + this.pieceOnTile.hashCode();
        }

        @Override
        public String toString() {
            return pieceOnTile.getAlliance().isWhite() ? pieceOnTile.toString().toUpperCase() :
                    pieceOnTile.toString().toLowerCase();
        }
    }
}
