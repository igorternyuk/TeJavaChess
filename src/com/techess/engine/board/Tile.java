package com.techess.engine.board;

import com.google.common.collect.ImmutableMap;
import com.techess.engine.pieces.Piece;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by igor on 26.11.17.
 */

public abstract class Tile {

    protected final Position tilePosition;

    private static final Map<Position, EmptyTile> EMPTY_TILES = createAllPossibleEmptyTiles();

    private Tile(final Position tilePosition) {
        this.tilePosition = tilePosition;
    }

    private static Map<Position,EmptyTile> createAllPossibleEmptyTiles() {
        final Map<Position,EmptyTile> emptyTilesMap = new HashMap<>();
        for(int y = 0; y < Board.BOARD_SIZE; ++y){
            for (int x = 0; x < Board.BOARD_SIZE; ++x){
                emptyTilesMap.put(Board.getPosition(x,y), new EmptyTile(Board.getPosition(x,y)));
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

    public abstract boolean isOccupied();
    public abstract boolean isEmpty();
    public abstract Piece getPiece();


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
        public String toString() {
            return pieceOnTile.getAlliance().isWhite() ? pieceOnTile.toString().toUpperCase() :
                    pieceOnTile.toString().toLowerCase();
        }
    }
}
