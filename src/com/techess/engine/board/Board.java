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
    public static final Map<Character, Integer> fileMap = initializeFileMap();
    public static final Map<Integer, Integer> rankMap = initializeRankMap();
    public static final Position NULL_POSITION = new Position(-1, -1);
    private static final Position[][] positions = createAllPossiblePositions();
    private final Map<Position, Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;
    private final Collection<Piece> allActivePieces;
    private final Collection<Move> legalMovesWhitePieces;
    private final Collection<Move> legalMovesBlackPieces;
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    private Board(final Builder builder){
        for(int y = 0; y < BOARD_SIZE; ++y){
            for(int x = 0; x < BOARD_SIZE; ++x){
                this.positions[y][x] = new Position(x, y);
            }
        }
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = detectActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPieces = detectActivePieces(this.gameBoard, Alliance.BLACK);
        this.allActivePieces = ImmutableList.copyOf(Iterables.concat(this.whitePieces, this.blackPieces));
        this.legalMovesWhitePieces = calculateLegalMoves(this.whitePieces);
        this.legalMovesBlackPieces = calculateLegalMoves(this.blackPieces);
        whitePlayer = new WhitePlayer(this, this.legalMovesWhitePieces, this.legalMovesBlackPieces);
        blackPlayer = new BlackPlayer(this, this.legalMovesBlackPieces, this.legalMovesWhitePieces);
        this.currentPlayer = builder.nextMoveMaker.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    public static boolean isValidPosition(final Position position) {
        return position.getX() >= 0 && position.getX() < BOARD_SIZE && position.getY() >= 0 &&
                position.getY() < BOARD_SIZE;
    }

    public static boolean isValidPosition(final int x, final int y) {
        return x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE;
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

    public static int getChessNotationForRow(final int y){
        Set<Map.Entry<Integer,Integer>> setOfRows = rankMap.entrySet();
        for(Map.Entry entry: setOfRows){
            if(entry.getValue().equals(y)) return (Integer) entry.getKey();
        }
        return -1;
    }

    public static char getChessNotationForColumn(final int x){
        Set<Map.Entry<Character,Integer>> columnsSet = fileMap.entrySet();
        for(Map.Entry entry: columnsSet){
            if(entry.getValue().equals(x)) return (Character) entry.getKey();
        }
        return '\0';
    }

    public static String getChessNotationTileName(final Position position){

        return String.valueOf(getChessNotationForColumn(position.getX())) + getChessNotationForRow(position.getY());
    }

    public static String getChessNotationTileName(final int x, final int y){

        return String.valueOf(getChessNotationForColumn(x)) + getChessNotationForRow(y);
    }

    public Tile getTile(final int x, final int y){
        return isValidPosition(x,y) ? gameBoard.get(this.positions[y][x]) : null;
    }

    public Tile getTile(final char file, final int rank){
        //System.out.println(file + " -> " + fileMap.get(file));
        //System.out.println("rank = " + rank + " -> " + rankMap.get(rank));
        return gameBoard.get(Board.position(fileMap.get(file), rankMap.get(rank)));
    }

    public static final Position position(final char file, final int rank) {
        return Board.position(fileMap.get(file), rankMap.get(rank));
    };

    public static final Position position(final int x, final int y){
        return Board.positions[y][x];
    }

    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.legalMovesWhitePieces, this.legalMovesBlackPieces));
    }

    public static class Builder {
        Map<Position, Piece> boardPattern;
        Alliance nextMoveMaker;
        private Pawn enPassantPawn;

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

        public Board build(){
            return new Board(this);
        }

        public void setEnPassantPawn(final Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }
    }

    public static Board createStandardBoard(){
        Builder builder = new Builder();

        //White pieces

        builder.setPiece(Rook.createRook('a', 1, Alliance.WHITE, true));
        builder.setPiece(Knight.createKnight('b', 1, Alliance.WHITE, true));
        builder.setPiece(Bishop.createBishop('c', 1, Alliance.WHITE, true));
        builder.setPiece(Queen.createQueen('d', 1, Alliance.WHITE, true));
        builder.setPiece(King.createKing('e', 1, Alliance.WHITE, true));
        builder.setPiece(Bishop.createBishop('f', 1, Alliance.WHITE, true));
        builder.setPiece(Knight.createKnight('g', 1, Alliance.WHITE, true));
        builder.setPiece(Rook.createRook('h', 1, Alliance.WHITE, true));

        //White pawns

        for(int i = 0; i < BOARD_SIZE; ++i) {
            builder.setPiece(Pawn.createPawn(i, SECOND_RANK, Alliance.WHITE, true));
        }

        //Black pieces

        builder.setPiece(Rook.createRook('a', 8, Alliance.BLACK, true));
        builder.setPiece(Knight.createKnight('b', 8, Alliance.BLACK, true));
        builder.setPiece(Bishop.createBishop('c', 8, Alliance.BLACK, true));
        builder.setPiece(Queen.createQueen('d', 8, Alliance.BLACK, true));
        builder.setPiece(King.createKing('e', 8, Alliance.BLACK, true));
        builder.setPiece(Bishop.createBishop('f', 8, Alliance.BLACK, true));
        builder.setPiece(Knight.createKnight('g', 8, Alliance.BLACK, true));
        builder.setPiece(Rook.createRook('h', 8, Alliance.BLACK, true));

        //Black pawns

        for(int i = 0; i < BOARD_SIZE; ++i) {
            builder.setPiece(Pawn.createPawn(i, SEVENTH_RANK, Alliance.BLACK, true));
        }

        builder.setMoveMaker(Alliance.WHITE);

        return builder.build();
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

    private static Position[][] createAllPossiblePositions(){
        Position[][] allPosiiblePositions = new Position[BOARD_SIZE][BOARD_SIZE];
        for(int y = 0; y < BOARD_SIZE; ++y){
            for(int x = 0; x < BOARD_SIZE; ++x){
                allPosiiblePositions[y][x] = new Position(x, y);
            }
        }
        return allPosiiblePositions;
    }

    private static Map<Character,Integer> initializeFileMap() {
        Map<Character, Integer> map = new HashMap<>();
        for(int i = 0; i < BOARD_SIZE; ++i){
            map.put((char)(i + 97), i);
        }
        /*map.put('a', 0);
        map.put('b', 1);
        map.put('c', 2);
        map.put('d', 3);
        map.put('e', 4);
        map.put('f', 5);
        map.put('g', 6);
        map.put('h', 7);*/
        return ImmutableMap.copyOf(map);
    }

    private static Map<Integer,Integer> initializeRankMap() {
        Map<Integer, Integer> map = new HashMap<>();
        for(int i = 1; i <= BOARD_SIZE; ++i){
            map.put(i, BOARD_SIZE - i);
            /*
            * 1 - 7
            * 2 - 6
            * 3 - 5
            * 4 - 4
            * 5 - 3
            * 6 - 2
            * 7 - 1
            * 8 - 0
            * */
        }
        return ImmutableMap.copyOf(map);
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
                final Position position = Board.position(x,y);
                final Piece piece = builder.boardPattern.get(position);
                final Tile tile = Tile.createTile(position, piece);
                tiles.put(position, tile);
            }
        }
        return ImmutableMap.copyOf(tiles);
    }
}
