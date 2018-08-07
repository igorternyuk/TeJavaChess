package com.igorternyuk.engine.board;

import com.google.common.collect.ImmutableMap;
import com.igorternyuk.engine.Alliance;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by igor on 07.03.18.
 */
public class BoardUtils {
    public static final int TOTAL_NUMBER_OF_TILES = 64;
    public static final int BOARD_SIZE = 8;
    public static final int FIRST_RANK = 7;
    public static final int SECOND_RANK = 6;
    public static final int THIRD_RANK = 5;
    public static final int FOURTH_RANK = 4;
    public static final int FIFTH_RANK = 3;
    public static final int SIXTH_RANK = 2;
    public static final int SEVENTH_RANK = 1;
    public static final int EIGHTH_RANK = 0;
    public static final Location NULL_LOCATION = new Location(-1, -1);
    static final Location[][] LOCATIONS = BoardUtils.createAllPossiblePositions();
    static final Map<Character, Integer> ALGEBRAIC_NOTATION_FILE_TO_COORDINATE_X =
            BoardUtils.createAlgebraicNotationFileToCoordinateX();
    static final char[] COORDINATE_X_TO_ALGEBRAIC_NOTATION_FILE = BoardUtils.createCoordinateXToAlgebraicNotationFile();
    static final int[] COORDINATE_Y_TO_ALGEBRAIC_NOTATION_RANK = BoardUtils.createCoordinateYToAlgebraicNotationRank();
    static final int[] ALGEBRAIC_NOTATION_RANK_TO_COORDINATE_Y = BoardUtils.createAlgebraicNotationRankToCoordinateY();
    static final Map<String, Location> ALGEBRAIC_NOTATION_TO_POSITION = BoardUtils.createAlgebraicNotationToPositionMap();
    static final Map<Location, String> POSITION_TO_ALGEBRAIC_NOTATION = BoardUtils.createPositionToAlgebraicNotationMap();

    public static boolean isValidPosition(final Location location) {
        return location.getX() >= 0 && location.getX() < BOARD_SIZE && location.getY() >= 0 &&
                location.getY() < BOARD_SIZE;
    }

    public static boolean isValidPosition(final int x, final int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }

    public static int getAlgebraicNotationForCoordinateY(final int y){
        return COORDINATE_Y_TO_ALGEBRAIC_NOTATION_RANK[y];
    }

    public static int getCoordinateYForAlgebraicNotation(final int rank){
        return ALGEBRAIC_NOTATION_RANK_TO_COORDINATE_Y[rank];
    }

    public static char getAlgebraicNotationForCoordinateX(final int x){
        return COORDINATE_X_TO_ALGEBRAIC_NOTATION_FILE[x];
    }

    public static int getCoordinateXForAlgebraicNotation(final char file){
        return ALGEBRAIC_NOTATION_FILE_TO_COORDINATE_X.get(file);
    }

    public static String getAlgebraicNotationFromPosition(final Location location) {
        return POSITION_TO_ALGEBRAIC_NOTATION.get(location);
    }

    public static String getAlgebraicNotationFromCoordinates(final int x, final int y){

        return POSITION_TO_ALGEBRAIC_NOTATION.get(getPosition(x,y));
    }

    public static final Location getPosition(final int x, final int y) {
        return LOCATIONS[y][x];
    }

    public static Location getPosition(final String algebraicNotation) {
        return ALGEBRAIC_NOTATION_TO_POSITION.get(algebraicNotation);
    }

    public static Location getPosition(final char file, final int rank) {
        return getPosition(String.valueOf(file) + String.valueOf(rank));
    }

    public static Location getKingsSideCastlingKingTargetPosition(final Alliance alliance) {
        return alliance.isWhite() ? getPosition("g1") : getPosition("g8");
    }

    public static Location getKingsSideCastlingRookTargetPosition(final Alliance alliance) {
        return alliance.isWhite() ? getPosition("f1") : getPosition("f8");
    }

    public static Location getQueensSideCastlingKingTargetPosition(final Alliance alliance) {
        return alliance.isWhite() ? getPosition("c1") : getPosition("c8");
    }

    public static Location getQueensSideCastlingRookTargetPosition(final Alliance alliance) {
        return alliance.isWhite() ? getPosition("d1") : getPosition("d8");
    }

    private static Location[][] createAllPossiblePositions() {
        Location[][] allPossibleLocations = new Location[BoardUtils.BOARD_SIZE][BoardUtils.BOARD_SIZE];
        for(int y = 0; y < BoardUtils.BOARD_SIZE; ++y){
            for(int x = 0; x < BoardUtils.BOARD_SIZE; ++x){
                allPossibleLocations[y][x] = new Location(x, y);
            }
        }
        return allPossibleLocations;
    }

    private static Map<Character,Integer> createAlgebraicNotationFileToCoordinateX() {
        Map<Character, Integer> map = new HashMap<>();
        for(int i = 0; i < BoardUtils.BOARD_SIZE; ++i){
            map.put((char)(i + 97), i);
        }
        return ImmutableMap.copyOf(map);
    }

    private static char[] createCoordinateXToAlgebraicNotationFile() {
        char[] array = new char[BoardUtils.BOARD_SIZE];
        ALGEBRAIC_NOTATION_FILE_TO_COORDINATE_X.forEach((k,v) -> {
            array[v] = k;
        });
        return array;
    }

    private static int[] createCoordinateYToAlgebraicNotationRank() {
        int[] array = new int[BoardUtils.BOARD_SIZE];
        for(int i = 1; i <= BoardUtils.BOARD_SIZE; ++i){
            array[BoardUtils.BOARD_SIZE - i] = i;
        }
        return array;
    }

    private static int[] createAlgebraicNotationRankToCoordinateY() {
        int[] array = new int[BoardUtils.BOARD_SIZE + 1];
        for(int i = 1; i <= BoardUtils.BOARD_SIZE; ++i){
            array[i] = BoardUtils.BOARD_SIZE - i;
        }
        return array;
    }

    private static Map<String, Location> createAlgebraicNotationToPositionMap() {
        Map<String, Location> map = new HashMap<>();
        for(int y = 0; y < BoardUtils.BOARD_SIZE; ++y){
            for(int x = 0; x < BoardUtils.BOARD_SIZE; ++x){
                final Location location = BoardUtils.LOCATIONS[y][x];
                final String algebraicNotation = String.valueOf(COORDINATE_X_TO_ALGEBRAIC_NOTATION_FILE[x]) +
                        String.valueOf(COORDINATE_Y_TO_ALGEBRAIC_NOTATION_RANK[y]);
                map.put(algebraicNotation, location);
            }
        }
        return map;
    }

    private static Map<Location, String> createPositionToAlgebraicNotationMap() {
        Map<Location, String> map = new HashMap<>();
        ALGEBRAIC_NOTATION_TO_POSITION.forEach((k,v) -> {
            map.put(v,k);
        });
        return map;
    }
}
