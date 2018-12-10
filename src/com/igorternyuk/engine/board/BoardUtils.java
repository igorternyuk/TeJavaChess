package com.igorternyuk.engine.board;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.igorternyuk.engine.Alliance;
import com.igorternyuk.engine.moves.Move;
import com.igorternyuk.engine.moves.MoveTransition;
import com.igorternyuk.engine.pieces.Piece;
import com.igorternyuk.engine.pieces.PieceType;

import java.util.*;

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
    static final Location[][] LOCATIONS = BoardUtils.createAllPossibleLocations();
    private static final Map<Character, Integer> ALGEBRAIC_NOTATION_FILE_TO_COORDINATE_X =
            BoardUtils.createFileAlgebraicNotationToCoordinateXMap();
    private static final char[] COORDINATE_X_TO_FILE_ALGEBRAIC_NOTATION = BoardUtils.createCoordinateXToAlgebraicNotationFile();
    private static final int[] COORDINATE_Y_TO_RANK_ALGEBRAIC_NOTATION = BoardUtils.createCoordinateYToAlgebraicNotationRank();
    private static final int[] RANK_ALGEBRAIC_NOTATION_TO_COORDINATE_Y = BoardUtils.createAlgebraicNotationRankToCoordinateY();
    private static final Map<String, Location> ALGEBRAIC_NOTATION_TO_LOCATION = BoardUtils.createAlgebraicNotationToLocationMap();
    private static final Map<Location, String> LOCATION_TO_ALGEBRAIC_NOTATION = BoardUtils.createLocationToAlgebraicNotationMap();

    public static boolean kingThreat(final Move move) {
        final Board board = move.getBoard();
        final MoveTransition transition = board.getCurrentPlayer().makeMove(move);
        return transition.getTransitedBoard().getCurrentPlayer().isUnderCheck();
    }

    // MVV-LVA (Most Valuable Victim - Least Valuable Aggressor),
    public static int mvvlva(final Move move) {
        final Piece movedPiece = move.getMovedPiece();
        if (move.isCapturingMove()) {
            final Piece capturedPiece = move.getCapturedPiece();
            return (capturedPiece.getValue() - movedPiece.getValue() + PieceType.KING.getValue()) * 100;
        }
        return PieceType.KING.getValue() - movedPiece.getValue();
    }

    public static Collection<Move> getMoveHistory(final Board board, int numberOfMoves) {
        final List<Move> moveHistory = new ArrayList<>(numberOfMoves);
        Move currentMove = board.getTransitionMove();
        int i = 0;
        while (!currentMove.equals(Move.MoveFactory.NULL_MOVE) && i < numberOfMoves) {
            moveHistory.add(currentMove);
            currentMove = currentMove.getBoard().getTransitionMove();
            ++i;
        }
        return ImmutableList.copyOf(moveHistory);
    }

    public static boolean isThreatenedBoardImmediate(final Board board) {
        return board.getWhitePlayer().isUnderCheck() || board.getBlackPlayer().isUnderCheck();
    }

    public static boolean isValidLocation(final Location location) {
        return location.getX() >= 0 && location.getX() < BOARD_SIZE && location.getY() >= 0 &&
                location.getY() < BOARD_SIZE;
    }

    public static boolean isValidLocation(final int x, final int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
    }

    public static int getAlgebraicNotationForCoordinateY(final int y){
        return COORDINATE_Y_TO_RANK_ALGEBRAIC_NOTATION[y];
    }

    public static int getCoordinateYForAlgebraicNotation(final int rank){
        return RANK_ALGEBRAIC_NOTATION_TO_COORDINATE_Y[rank];
    }

    public static char getAlgebraicNotationForCoordinateX(final int x){
        return COORDINATE_X_TO_FILE_ALGEBRAIC_NOTATION[x];
    }

    public static int getCoordinateXForAlgebraicNotation(final char file){
        return ALGEBRAIC_NOTATION_FILE_TO_COORDINATE_X.get(file);
    }

    public static String getAlgebraicNotationFromPosition(final Location location) {
        return LOCATION_TO_ALGEBRAIC_NOTATION.get(location);
    }

    public static String getAlgebraicNotationFromCoordinates(final int x, final int y){

        return LOCATION_TO_ALGEBRAIC_NOTATION.get(getLocation(x, y));
    }

    public static Location getLocation(final int x, final int y) {
        return LOCATIONS[y][x];
    }

    public static Location getLocation(final String algebraicNotation) {
        return ALGEBRAIC_NOTATION_TO_LOCATION.get(algebraicNotation);
    }

    public static Location getLocation(final char file, final int rank) {
        return getLocation(String.valueOf(file) + String.valueOf(rank));
    }

    public static Location getKingsSideCastlingKingTargetLocation(final Alliance alliance) {
        return alliance.isWhite() ? getLocation("g1") : getLocation("g8");
    }

    public static Location getKingsSideCastlingRookTargetLocation(final Alliance alliance) {
        return alliance.isWhite() ? getLocation("f1") : getLocation("f8");
    }

    public static Location getQueensSideCastlingKingTargetLocation(final Alliance alliance) {
        return alliance.isWhite() ? getLocation("c1") : getLocation("c8");
    }

    public static Location getQueensSideCastlingRookTargetLocation(final Alliance alliance) {
        return alliance.isWhite() ? getLocation("d1") : getLocation("d8");
    }

    private static Location[][] createAllPossibleLocations() {
        Location[][] allPossibleLocations = new Location[BoardUtils.BOARD_SIZE][BoardUtils.BOARD_SIZE];
        for(int y = 0; y < BoardUtils.BOARD_SIZE; ++y){
            for(int x = 0; x < BoardUtils.BOARD_SIZE; ++x){
                allPossibleLocations[y][x] = new Location(x, y);
            }
        }
        return allPossibleLocations;
    }

    private static Map<Character, Integer> createFileAlgebraicNotationToCoordinateXMap() {
        Map<Character, Integer> map = new HashMap<>();
        for(int i = 0; i < BoardUtils.BOARD_SIZE; ++i){
            map.put((char)(i + 97), i);
        }
        return ImmutableMap.copyOf(map);
    }

    private static char[] createCoordinateXToAlgebraicNotationFile() {
        char[] array = new char[BoardUtils.BOARD_SIZE];
        ALGEBRAIC_NOTATION_FILE_TO_COORDINATE_X.forEach((k, v) -> array[v] = k);
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

    private static Map<String, Location> createAlgebraicNotationToLocationMap() {
        Map<String, Location> map = new HashMap<>();
        for(int y = 0; y < BoardUtils.BOARD_SIZE; ++y){
            for(int x = 0; x < BoardUtils.BOARD_SIZE; ++x){
                final Location location = BoardUtils.LOCATIONS[y][x];
                final String algebraicNotation = String.valueOf(COORDINATE_X_TO_FILE_ALGEBRAIC_NOTATION[x]) +
                        String.valueOf(COORDINATE_Y_TO_RANK_ALGEBRAIC_NOTATION[y]);
                map.put(algebraicNotation, location);
            }
        }
        return map;
    }

    private static Map<Location, String> createLocationToAlgebraicNotationMap() {
        Map<Location, String> map = new HashMap<>();
        ALGEBRAIC_NOTATION_TO_LOCATION.forEach((k, v) -> map.put(v, k));
        return map;
    }
}
