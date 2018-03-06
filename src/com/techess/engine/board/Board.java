package com.techess.engine.board;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.techess.engine.Alliance;
import com.techess.engine.moves.Move;
import com.techess.engine.pieces.*;
import com.techess.engine.player.BlackPlayer;
import com.techess.engine.player.Player;
import com.techess.engine.player.WhitePlayer;
import javafx.geometry.Pos;

import java.util.*;

/**
 * Created by igor on 01.12.17.
 */

public class Board {
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
    public static final Position NULL_POSITION = new Position(-1, -1);
    private static final Position[][] positions = createAllPossiblePositions();
    private static final Map<Character, Integer> ALGEBRAIC_NOTATION_FILE_TO_COORDINATE_X =
            createAlgebraicNotationFileToCoordinateX();
    private static final char[] COORDINATE_X_TO_ALGEBRAIC_NOTATION_FILE = createCoordinateXToAlgebraicNotationFile();
    private static final int[] COORDINATE_Y_TO_ALGEBRAIC_NOTATION_RANK = createCoordinateYToAlgebraicNotationRank();
    private static final int[] ALGEBRAIC_NOTATION_RANK_TO_COORDINATE_Y = createAlgebraicNotationRankToCoordinateY();
    private static final Map<String, Position> ALGEBRAIC_NOTATION_TO_POSITION = createAlgebraicNotationToPositionMap();
    private static final Map<Position, String> POSITION_TO_ALGEBRAIC_NOTATION = createPositionToAlgebraicNotationMap();
    private final GameType gameType;
    private final Map<Position, Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;
    private final Collection<Piece> allActivePieces;
    private final Collection<Move> legalMovesWhitePieces;
    private final Collection<Move> legalMovesBlackPieces;
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;
    private final int kingsRookStartCoordinateX;
    private final int queensRookStartCoordinateX;

    private Board(final Builder builder){
        this.gameType = builder.gameType;
        this.kingsRookStartCoordinateX = builder.kingsRookStartCoordinateX;
        this.queensRookStartCoordinateX = builder.queensRookStartCoordinateX;
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = detectActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPieces = detectActivePieces(this.gameBoard, Alliance.BLACK);
        this.allActivePieces = ImmutableList.copyOf(Iterables.concat(this.whitePieces, this.blackPieces));
        this.enPassantPawn = builder.enPassantPawn;
        this.legalMovesWhitePieces = calculateLegalMoves(this.whitePieces);
        this.legalMovesBlackPieces = calculateLegalMoves(this.blackPieces);
        this.whitePlayer = new WhitePlayer(this, this.legalMovesWhitePieces, this.legalMovesBlackPieces);
        this.blackPlayer = new BlackPlayer(this, this.legalMovesBlackPieces, this.legalMovesWhitePieces);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);

    }

    public GameType getGameType(){
        return this.gameType;
    }

    public int getKingsRookStartCoordinateX() {
        return this.kingsRookStartCoordinateX;
    }

    public int getQueensRookStartCoordinateX() {
        return this.queensRookStartCoordinateX;
    }

    public Pawn getEnPassantPawn(){
        return this.enPassantPawn;
    }

    public Collection<Piece> getWhitePieces() {
        return this.whitePieces;
    }

    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    public Collection<Piece> getAllActivePieces() { return this.allActivePieces; }

    public Player getWhitePlayer(){
        return this.whitePlayer;
    }

    public Player getBlackPlayer(){
        return this.blackPlayer;
    }

    public Player getCurrentPlayer(){
        return this.currentPlayer;
    }

    public Tile getTile(final Position candidateDestination) {
        return isValidPosition(candidateDestination) ? gameBoard.get(candidateDestination) : null;
    }

    public Tile getTile(final int x, final int y){
        return isValidPosition(x,y) ? gameBoard.get(this.positions[y][x]) : null;
    }

    public Tile getTile(final char file, final int rank){
        return gameBoard.get(getPosition(file, rank));
    }

    public Tile getTile(final String algebraicNotation){
        return gameBoard.get(getPosition(algebraicNotation));
    }

    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.legalMovesWhitePieces, this.legalMovesBlackPieces));
    }

    public static boolean isValidPosition(final Position position) {
        return position.getX() >= 0 && position.getX() < BOARD_SIZE && position.getY() >= 0 &&
                position.getY() < BOARD_SIZE;
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

    public static String getAlgebraicNotationFromPosition(final Position position){
        return POSITION_TO_ALGEBRAIC_NOTATION.get(position);
    }

    public static String getAlgebraicNotationFromCoordinates(final int x, final int y){

        return POSITION_TO_ALGEBRAIC_NOTATION.get(Board.getPosition(x,y));
    }

    public static final Position getPosition(final int x, final int y){
        return Board.positions[y][x];
    }

    public static Position getPosition(final String algebraicNotation){
        return ALGEBRAIC_NOTATION_TO_POSITION.get(algebraicNotation);
    }

    public static Position getPosition(final char file, final int rank){
        return getPosition(String.valueOf(file) + String.valueOf(rank));
    }

    public static class Builder {
        private Map<Position, Piece> boardPattern;
        private Alliance nextMoveMaker;
        private Pawn enPassantPawn;
        private GameType gameType;
        public int kingsRookStartCoordinateX;
        public int queensRookStartCoordinateX;

        public Builder(){
            this.boardPattern = new HashMap<>();
            this.nextMoveMaker = Alliance.WHITE;
        }

        public Builder setPiece(final Piece piece){
            this.boardPattern.put(piece.getPosition(), piece);
            return this;
        }

        public Builder setMoveMaker(final Alliance alliance){
            this.nextMoveMaker = alliance;
            return this;
        }

        public void setGameType(GameType gameType) {
            this.gameType = gameType;
        }

        public void setKingsRookStartCoordinateX(int kingsRookStartCoordinateX) {
            this.kingsRookStartCoordinateX = kingsRookStartCoordinateX;
        }

        public void setQueensRookStartCoordinateX(int queensRookStartCoordinateX) {
            this.queensRookStartCoordinateX = queensRookStartCoordinateX;
        }

        public Board build(){
            return new Board(this);
        }

        public void setEnPassantPawn(final Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }
    }

    public static Board createStandardBoard(){
        Builder builder = new Builder();
        builder.setGameType(GameType.CLASSIC_CHESS);

        //White pieces
        builder.setPiece(Rook.createRook("a1", Alliance.WHITE, true));
        builder.setKingsRookStartCoordinateX(Board.getCoordinateXForAlgebraicNotation('a'));
        builder.setPiece(Knight.createKnight("b1",  Alliance.WHITE, true));
        builder.setPiece(Bishop.createBishop("c1",Alliance.WHITE, true));
        builder.setPiece(Queen.createQueen("d1", Alliance.WHITE, true));
        builder.setPiece(King.createKing("e1", Alliance.WHITE, true));
        builder.setPiece(Bishop.createBishop("f1", Alliance.WHITE, true));
        builder.setPiece(Knight.createKnight("g1", Alliance.WHITE, true));
        builder.setPiece(Rook.createRook("h1",  Alliance.WHITE, true));
        builder.setQueensRookStartCoordinateX(Board.getCoordinateXForAlgebraicNotation('h'));

        //Black pieces
        builder.setPiece(Rook.createRook("a8", Alliance.BLACK, true));
        builder.setPiece(Knight.createKnight("b8", Alliance.BLACK, true));
        builder.setPiece(Bishop.createBishop("c8", Alliance.BLACK, true));
        builder.setPiece(Queen.createQueen("d8", Alliance.BLACK, true));
        builder.setPiece(King.createKing("e8", Alliance.BLACK, true));
        builder.setPiece(Bishop.createBishop("f8", Alliance.BLACK, true));
        builder.setPiece(Knight.createKnight("g8", Alliance.BLACK, true));
        builder.setPiece(Rook.createRook("h8", Alliance.BLACK, true));

        //Pawns
        for(int i = 0; i < BOARD_SIZE; ++i) {
            builder.setPiece(Pawn.createPawn(i, SECOND_RANK, Alliance.WHITE, true));
            builder.setPiece(Pawn.createPawn(i, SEVENTH_RANK, Alliance.BLACK, true));
        }

        builder.setMoveMaker(Alliance.WHITE);

        return builder.build();
    }

    public static Board createBoardForChess960(){
        Builder builder = new Builder();
        builder.setGameType(GameType.RANDOM_FISHER_CHESS);
        Random random = new Random();
        final boolean[] rowOccupation = new boolean[BOARD_SIZE];
        for (boolean positionX : rowOccupation) {
            positionX = false;
        }
        //Bishops
        final int[] lightSquareBishopPossibleCoordinateX = {1,3,5,7};
        final int[] darkSquareBishopPossibleCoordinateX = {0,2,4,6};

        final int randomLightSquareBishopCoordinateX =
                lightSquareBishopPossibleCoordinateX[random.nextInt( lightSquareBishopPossibleCoordinateX.length)];
        builder.setPiece(Bishop.createBishop(randomLightSquareBishopCoordinateX, Board.FIRST_RANK,Alliance.WHITE,true));
        builder.setPiece(Bishop.createBishop(randomLightSquareBishopCoordinateX, Board.EIGHTH_RANK,Alliance.BLACK,true));
        rowOccupation[randomLightSquareBishopCoordinateX] = true;
        final int randomDarkSquareBishopCoordinateX =
                darkSquareBishopPossibleCoordinateX[random.nextInt(darkSquareBishopPossibleCoordinateX.length)];
        builder.setPiece(Bishop.createBishop(randomDarkSquareBishopCoordinateX, Board.FIRST_RANK, Alliance.WHITE, true));
        builder.setPiece(Bishop.createBishop(randomDarkSquareBishopCoordinateX, Board.EIGHTH_RANK, Alliance.BLACK, true));
        rowOccupation[randomDarkSquareBishopCoordinateX] = true;

        //Knights
        for(int i = 0; i < 2; ++i){
            int knightRandomCoordinateX = random.nextInt(BOARD_SIZE);
            while (rowOccupation[knightRandomCoordinateX]){
                knightRandomCoordinateX = random.nextInt(BOARD_SIZE);
            }
            builder.setPiece(Knight.createKnight(knightRandomCoordinateX, Board.FIRST_RANK,Alliance.WHITE,true));
            builder.setPiece(Knight.createKnight(knightRandomCoordinateX, Board.EIGHTH_RANK,Alliance.BLACK,true));
            rowOccupation[knightRandomCoordinateX] = true;
        }

        //Queens
        int queenRandomCoordinateX = random.nextInt(BOARD_SIZE);
        while (rowOccupation[queenRandomCoordinateX]){
            queenRandomCoordinateX = random.nextInt(BOARD_SIZE);
        }
        builder.setPiece(Queen.createQueen(queenRandomCoordinateX, Board.FIRST_RANK,Alliance.WHITE,true));
        builder.setPiece(Queen.createQueen(queenRandomCoordinateX, Board.EIGHTH_RANK,Alliance.BLACK,true));
        rowOccupation[queenRandomCoordinateX] = true;

        final int[] emptyPositions = new int[3];
        int counter = 0;
        for (int x = 0; x < rowOccupation.length; ++x) {
            if(!rowOccupation[x]){
                emptyPositions[counter++] = x;
            }
        }

        //Kings and rooks
        builder.setPiece(Rook.createRook(emptyPositions[0], Board.FIRST_RANK, Alliance.WHITE, true));
        builder.setPiece(Rook.createRook(emptyPositions[0], Board.EIGHTH_RANK, Alliance.BLACK, true));
        builder.setQueensRookStartCoordinateX(emptyPositions[0]);
        builder.setPiece(King.createKing(emptyPositions[1], Board.FIRST_RANK, Alliance.WHITE, true));
        builder.setPiece(King.createKing(emptyPositions[1], Board.EIGHTH_RANK, Alliance.BLACK, true));
        builder.setPiece(Rook.createRook(emptyPositions[2], Board.FIRST_RANK, Alliance.WHITE, true));
        builder.setPiece(Rook.createRook(emptyPositions[2], Board.EIGHTH_RANK, Alliance.BLACK, true));
        builder.setKingsRookStartCoordinateX(emptyPositions[2]);


        //Pawns
        for(int i = 0; i < BOARD_SIZE; ++i) {
            builder.setPiece(Pawn.createPawn(i, SECOND_RANK, Alliance.WHITE, true));
            builder.setPiece(Pawn.createPawn(i, SEVENTH_RANK, Alliance.BLACK, true));
        }

        builder.setMoveMaker(Alliance.WHITE);

        return builder.build();
    }



    private static Position[][] createAllPossiblePositions(){
        Position[][] allPossiblePositions = new Position[BOARD_SIZE][BOARD_SIZE];
        for(int y = 0; y < BOARD_SIZE; ++y){
            for(int x = 0; x < BOARD_SIZE; ++x){
                allPossiblePositions[y][x] = new Position(x, y);
            }
        }
        return allPossiblePositions;
    }

    private static Map<Character,Integer> createAlgebraicNotationFileToCoordinateX() {
        Map<Character, Integer> map = new HashMap<>();
        for(int i = 0; i < BOARD_SIZE; ++i){
            map.put((char)(i + 97), i);
        }
        return ImmutableMap.copyOf(map);
    }

    private static char[] createCoordinateXToAlgebraicNotationFile() {
        char[] array = new char[BOARD_SIZE];
        ALGEBRAIC_NOTATION_FILE_TO_COORDINATE_X.forEach((k,v) -> {
            array[v] = k;
        });
        return array;
    }

    private static int[] createCoordinateYToAlgebraicNotationRank() {
        int[] array = new int[BOARD_SIZE];
        for(int i = 1; i <= BOARD_SIZE; ++i){
            array[BOARD_SIZE - i] = i;
        }
        return array;
    }

    private static int[] createAlgebraicNotationRankToCoordinateY() {
        int[] array = new int[BOARD_SIZE + 1];
        for(int i = 1; i <= BOARD_SIZE; ++i){
            array[i] = BOARD_SIZE - i;
        }
        return array;
    }

    private static Map<String, Position> createAlgebraicNotationToPositionMap(){
        Map<String, Position> map = new HashMap<>();
        for(int y = 0; y < BOARD_SIZE; ++y){
            for(int x = 0; x < BOARD_SIZE; ++x){
                final Position position = Board.positions[y][x];
                final String algebraicNotation = String.valueOf(COORDINATE_X_TO_ALGEBRAIC_NOTATION_FILE[x]) +
                        String.valueOf(COORDINATE_Y_TO_ALGEBRAIC_NOTATION_RANK[y]);
                map.put(algebraicNotation, position);
            }
        }
        return map;
    }

    private static Map<Position, String> createPositionToAlgebraicNotationMap(){
        Map<Position, String> map = new HashMap<>();
        ALGEBRAIC_NOTATION_TO_POSITION.forEach((k,v) -> {
            map.put(v,k);
        });
        return map;
    }

    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {
        List<Move> legalMoves = new ArrayList<>();
        for(final Piece piece: pieces){
            legalMoves.addAll(piece.getLegalMoves(this));
        }
        return ImmutableList.copyOf(legalMoves);
    }

    private static Collection<Piece> detectActivePieces(final Map<Position, Tile> gameBoard,
                                                        final Alliance alliance) {
        List<Piece> pieces = new ArrayList<>();
        gameBoard.forEach((position, tile) -> {
            if(tile.isOccupied()){
                final Piece piece = tile.getPiece();
                if(piece.getAlliance().equals(alliance)){
                    pieces.add(piece);
                }
            }
        });
        return ImmutableList.copyOf(pieces);
    }

    private static Map<Position, Tile> createGameBoard(final Builder builder){
        final Map<Position, Tile> tiles = new HashMap<>();
        for(int y = 0; y < BOARD_SIZE; ++y){
            for(int x = 0; x < BOARD_SIZE; ++x){
                final Position position = Board.getPosition(x,y);
                final Piece piece = builder.boardPattern.get(position);
                final Tile tile = Tile.createTile(position, piece);
                tiles.put(position, tile);
            }
        }
        return ImmutableMap.copyOf(tiles);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("\n-----------------------\n");
        for(int y = 0; y < BOARD_SIZE; ++y){
            for(int x = 0; x < BOARD_SIZE; ++x){
                final String tileText = this.getTile(x,y).toString();
                stringBuilder.append(String.format("%3s", tileText));
            }
            stringBuilder.append("\n");
        }
        stringBuilder.append("\n-----------------------\n");
        return stringBuilder.toString();
    }
}
