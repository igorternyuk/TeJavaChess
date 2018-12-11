package com.igorternyuk.engine.board;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.igorternyuk.engine.Alliance;
import com.igorternyuk.engine.moves.Move;
import com.igorternyuk.engine.pieces.*;
import com.igorternyuk.engine.player.BlackPlayer;
import com.igorternyuk.engine.player.Player;
import com.igorternyuk.engine.player.WhitePlayer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by igor on 01.12.17.
 */

public class Board {
    private final GameType gameType;
    private final Pawn enPassantPawn;
    private final int kingsRookStartCoordinateX;
    private final int queensRookStartCoordinateX;
    private final Map<Location, Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;
    private final Collection<Piece> allActivePieces;
    private final Collection<Move> legalMovesWhitePieces;
    private final Collection<Move> legalMovesBlackPieces;
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final boolean isInsufficientMaterial;
    private final Move transitionMove;

    private Board(final Builder builder) {
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
        this.isInsufficientMaterial = checkIfInsufficientMaterial();
        this.transitionMove = builder.transitionMove == null
                ? Move.MoveFactory.NULL_MOVE
                : builder.transitionMove;
        if (builder.checkEndGamePhase()) {
            this.setEndGameKings();
        }
    }

    public boolean isEndGamePhase() {
        int numMinorPieces = 0;
        int numMajorPieces = 0;
        boolean noQueens = false;
        for (final Piece piece : this.allActivePieces) {
            if (piece.getPieceType().isMinorPiece()) {
                ++numMinorPieces;
            } else if (piece.getPieceType().isMajorPiece()) {
                ++numMajorPieces;
            }
            if (piece.getPieceType().isQueen()) {
                noQueens = false;
            }
        }
        if (noQueens && (numMinorPieces + numMajorPieces) <= 8) {
            return true;
        }
        if (!noQueens && (numMinorPieces + numMajorPieces) <= 2) {
            return true;
        }
        return false;
    }

    public Move getTransitionMove() {
        return this.transitionMove;
    }

    public Map<Location, Tile> getGameBoard() {
        return this.gameBoard;
    }

    public boolean isInsufficientMaterial() {
        return isInsufficientMaterial;
    }

    public boolean isEndGameScenario() {
        return getCurrentPlayer().isCheckMate()
                || getCurrentPlayer().isInStalemate()
                || isInsufficientMaterial();
    }

    public GameType getGameType() {
        return this.gameType;
    }

    public int getKingsRookStartCoordinateX() {
        return this.kingsRookStartCoordinateX;
    }

    public int getQueensRookStartCoordinateX() {
        return this.queensRookStartCoordinateX;
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    public Collection<Piece> getWhitePieces() {
        return this.whitePieces;
    }

    public Collection<Piece> getBlackPieces() {
        return this.blackPieces;
    }

    public Collection<Piece> getAllActivePieces() {
        return this.allActivePieces;
    }

    public Player getWhitePlayer() {
        return this.whitePlayer;
    }

    public Player getBlackPlayer() {
        return this.blackPlayer;
    }

    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public Tile getTile(final Location candidateDestination) {
        return BoardUtils.isValidLocation(candidateDestination) ? gameBoard.get(candidateDestination) : null;
    }

    public Tile getTile(final int x, final int y) {
        return BoardUtils.isValidLocation(x, y) ? gameBoard.get(BoardUtils.LOCATIONS[y][x]) : null;
    }

    public Tile getTile(final char file, final int rank) {
        return gameBoard.get(BoardUtils.getLocation(file, rank));
    }

    public Tile getTile(final String algebraicNotation) {
        return gameBoard.get(BoardUtils.getLocation(algebraicNotation));
    }

    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.legalMovesWhitePieces, this.legalMovesBlackPieces));
    }

    public void setEndGameKings() {
        if (this.isEndGamePhase()) {
            this.getWhitePlayer().getPlayerKing().setEndGameValue();
            this.getBlackPlayer().getPlayerKing().setEndGameValue();
        }
    }

    private boolean checkIfInsufficientMaterial() {
        List<Piece> whitePieces = (List<Piece>) this.whitePieces;
        List<Piece> blackPieces = (List<Piece>) this.blackPieces;
        if (whitePieces.size() > 3 && blackPieces.size() > 3) return false;
        if (whitePieces.size() == 1) {
            if (blackPieces.size() == 1) return true;
            if (blackPieces.size() == 2) {
                final Piece secondBlackPiece = blackPieces.get(0).getPieceType().isKing() ?
                        blackPieces.get(1) : blackPieces.get(0);
                if (secondBlackPiece.getPieceType().isMinorPiece())
                    return true;
            } else if (blackPieces.size() == 3) {
                return blackPieces.stream().filter(piece -> !piece.getPieceType().isKing())
                        .allMatch(piece -> piece.getPieceType().equals(PieceType.KNIGHT));
            }
        } else if (whitePieces.size() == 2) {
            if (blackPieces.size() == 1) {
                final Piece secondWhitePiece = whitePieces.get(0).getPieceType().isKing() ?
                        whitePieces.get(1) : whitePieces.get(0);
                if (secondWhitePiece.getPieceType().isMinorPiece())
                    return true;
            } else if (blackPieces.size() == 2) {
                final Piece secondWhitePiece = whitePieces.get(0).getPieceType().isKing() ?
                        whitePieces.get(1) : whitePieces.get(0);
                final Piece secondBlackPiece = blackPieces.get(0).getPieceType().isKing() ?
                        blackPieces.get(1) : blackPieces.get(0);
                if ((secondWhitePiece.getPieceType().isMinorPiece()) &&
                        (secondBlackPiece.getPieceType().isMinorPiece())) {
                    return true;
                }
            }
        } else if (whitePieces.size() == 3) {
            if (blackPieces.size() == 1) {
                return whitePieces.stream().filter(piece -> !piece.getPieceType().isKing())
                        .allMatch(piece -> piece.getPieceType().equals(PieceType.KNIGHT));
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Board board = (Board) o;

        if (kingsRookStartCoordinateX != board.kingsRookStartCoordinateX) return false;
        if (queensRookStartCoordinateX != board.queensRookStartCoordinateX) return false;
        if (gameType != board.gameType) return false;
        if (enPassantPawn != null ? !enPassantPawn.equals(board.enPassantPawn) : board.enPassantPawn != null)
            return false;
        if (gameBoard != null ? !gameBoard.equals(board.gameBoard) : board.gameBoard != null) return false;
        if (whitePieces != null ? !whitePieces.equals(board.whitePieces) : board.whitePieces != null) return false;
        if (blackPieces != null ? !blackPieces.equals(board.blackPieces) : board.blackPieces != null) return false;
        if (allActivePieces != null ? !allActivePieces.equals(board.allActivePieces) : board.allActivePieces != null)
            return false;
        if (legalMovesWhitePieces != null ? !legalMovesWhitePieces.equals(board.legalMovesWhitePieces) : board.legalMovesWhitePieces != null)
            return false;
        return legalMovesBlackPieces != null ? legalMovesBlackPieces.equals(board.legalMovesBlackPieces) : board.legalMovesBlackPieces == null;
    }

    @Override
    public int hashCode() {
        int result = gameType != null ? gameType.hashCode() : 0;
        result = 31 * result + (enPassantPawn != null ? enPassantPawn.hashCode() : 0);
        result = 31 * result + kingsRookStartCoordinateX;
        result = 31 * result + queensRookStartCoordinateX;
        result = 31 * result + (gameBoard != null ? gameBoard.hashCode() : 0);
        result = 31 * result + (whitePieces != null ? whitePieces.hashCode() : 0);
        result = 31 * result + (blackPieces != null ? blackPieces.hashCode() : 0);
        result = 31 * result + (allActivePieces != null ? allActivePieces.hashCode() : 0);
        result = 31 * result + (legalMovesWhitePieces != null ? legalMovesWhitePieces.hashCode() : 0);
        result = 31 * result + (legalMovesBlackPieces != null ? legalMovesBlackPieces.hashCode() : 0);
        return result;
    }

    public static class Builder {
        private Map<Location, Piece> boardPattern;
        private Alliance nextMoveMaker;
        private Pawn enPassantPawn;
        private GameType gameType;
        private int kingsRookStartCoordinateX;
        private int queensRookStartCoordinateX;
        private Move transitionMove;
        private boolean checkEndGamePhase = false;

        public Builder() {
            this.boardPattern = new HashMap<>();
            this.nextMoveMaker = Alliance.WHITE;
        }

        public Builder setPiece(final Piece piece) {
            this.boardPattern.put(piece.getLocation(), piece);
            return this;
        }

        public Builder setMoveMaker(final Alliance alliance) {
            this.nextMoveMaker = alliance;
            return this;
        }

        public void setCheckEndGamePhase(boolean checkEndGamePhase) {
            this.checkEndGamePhase = checkEndGamePhase;
        }

        public boolean checkEndGamePhase() {
            return this.checkEndGamePhase;
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

        public void setTransitionMove(final Move transitionMove) {
            this.transitionMove = transitionMove;
        }

        public Board build() {
            return new Board(this);
        }

        public void setEnPassantPawn(final Pawn enPassantPawn) {
            this.enPassantPawn = enPassantPawn;
        }
    }

    public static Board createStandardBoard() {
        Builder builder = new Builder();
        builder.setGameType(GameType.CLASSIC_CHESS);

        //White pieces
        builder.setPiece(Rook.createRook("a1", Alliance.WHITE, true));
        builder.setKingsRookStartCoordinateX(BoardUtils.getCoordinateXForAlgebraicNotation('a'));
        builder.setPiece(Knight.createKnight("b1", Alliance.WHITE, true));
        builder.setPiece(Bishop.createBishop("c1", Alliance.WHITE, true));
        builder.setPiece(Queen.createQueen("d1", Alliance.WHITE, true));
        builder.setPiece(King.createKing("e1", Alliance.WHITE, true));
        builder.setPiece(Bishop.createBishop("f1", Alliance.WHITE, true));
        builder.setPiece(Knight.createKnight("g1", Alliance.WHITE, true));
        builder.setPiece(Rook.createRook("h1", Alliance.WHITE, true));
        builder.setQueensRookStartCoordinateX(BoardUtils.getCoordinateXForAlgebraicNotation('h'));

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
        for (int i = 0; i < BoardUtils.BOARD_SIZE; ++i) {
            builder.setPiece(Pawn.createPawn(i, BoardUtils.SECOND_RANK, Alliance.WHITE, true));
            builder.setPiece(Pawn.createPawn(i, BoardUtils.SEVENTH_RANK, Alliance.BLACK, true));
        }

        builder.setMoveMaker(Alliance.WHITE);

        return builder.build();
    }

    public static Board createBoardForChess960() {
        final Builder builder = new Builder();
        builder.setGameType(GameType.RANDOM_FISHER_CHESS);
        final Random random = new Random();
        final boolean[] rowOccupation = new boolean[BoardUtils.BOARD_SIZE];
        Arrays.fill(rowOccupation, false);
        //Bishops
        final int[] lightSquareBishopPossibleCoordinateX = {1, 3, 5, 7};
        final int[] darkSquareBishopPossibleCoordinateX = {0, 2, 4, 6};

        final int randomLightSquareBishopCoordinateX =
                lightSquareBishopPossibleCoordinateX[random.nextInt(lightSquareBishopPossibleCoordinateX.length)];
        builder.setPiece(Bishop.createBishop(randomLightSquareBishopCoordinateX, BoardUtils.FIRST_RANK, Alliance.WHITE, true));
        builder.setPiece(Bishop.createBishop(randomLightSquareBishopCoordinateX, BoardUtils.EIGHTH_RANK, Alliance.BLACK, true));
        rowOccupation[randomLightSquareBishopCoordinateX] = true;
        final int randomDarkSquareBishopCoordinateX =
                darkSquareBishopPossibleCoordinateX[random.nextInt(darkSquareBishopPossibleCoordinateX.length)];
        builder.setPiece(Bishop.createBishop(randomDarkSquareBishopCoordinateX, BoardUtils.FIRST_RANK, Alliance.WHITE, true));
        builder.setPiece(Bishop.createBishop(randomDarkSquareBishopCoordinateX, BoardUtils.EIGHTH_RANK, Alliance.BLACK, true));
        rowOccupation[randomDarkSquareBishopCoordinateX] = true;

        //Knights
        for (int i = 0; i < 2; ++i) {
            int knightRandomCoordinateX = random.nextInt(BoardUtils.BOARD_SIZE);
            while (rowOccupation[knightRandomCoordinateX]) {
                knightRandomCoordinateX = random.nextInt(BoardUtils.BOARD_SIZE);
            }
            builder.setPiece(Knight.createKnight(knightRandomCoordinateX, BoardUtils.FIRST_RANK, Alliance.WHITE, true));
            builder.setPiece(Knight.createKnight(knightRandomCoordinateX, BoardUtils.EIGHTH_RANK, Alliance.BLACK, true));
            rowOccupation[knightRandomCoordinateX] = true;
        }

        //Queens
        int queenRandomCoordinateX = random.nextInt(BoardUtils.BOARD_SIZE);
        while (rowOccupation[queenRandomCoordinateX]) {
            queenRandomCoordinateX = random.nextInt(BoardUtils.BOARD_SIZE);
        }
        builder.setPiece(Queen.createQueen(queenRandomCoordinateX, BoardUtils.FIRST_RANK, Alliance.WHITE, true));
        builder.setPiece(Queen.createQueen(queenRandomCoordinateX, BoardUtils.EIGHTH_RANK, Alliance.BLACK, true));
        rowOccupation[queenRandomCoordinateX] = true;

        final int[] emptyPositions = new int[3];
        int counter = 0;
        for (int x = 0; x < rowOccupation.length; ++x) {
            if (!rowOccupation[x]) {
                emptyPositions[counter++] = x;
            }
        }

        //Kings and rooks
        builder.setPiece(Rook.createRook(emptyPositions[0], BoardUtils.FIRST_RANK, Alliance.WHITE, true));
        builder.setPiece(Rook.createRook(emptyPositions[0], BoardUtils.EIGHTH_RANK, Alliance.BLACK, true));
        builder.setQueensRookStartCoordinateX(emptyPositions[0]);
        builder.setPiece(King.createKing(emptyPositions[1], BoardUtils.FIRST_RANK, Alliance.WHITE, true));
        builder.setPiece(King.createKing(emptyPositions[1], BoardUtils.EIGHTH_RANK, Alliance.BLACK, true));
        builder.setPiece(Rook.createRook(emptyPositions[2], BoardUtils.FIRST_RANK, Alliance.WHITE, true));
        builder.setPiece(Rook.createRook(emptyPositions[2], BoardUtils.EIGHTH_RANK, Alliance.BLACK, true));
        builder.setKingsRookStartCoordinateX(emptyPositions[2]);


        //Pawns
        for (int i = 0; i < BoardUtils.BOARD_SIZE; ++i) {
            builder.setPiece(Pawn.createPawn(i, BoardUtils.SECOND_RANK, Alliance.WHITE, true));
            builder.setPiece(Pawn.createPawn(i, BoardUtils.SEVENTH_RANK, Alliance.BLACK, true));
        }

        builder.setMoveMaker(Alliance.WHITE);

        return builder.build();
    }

    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList<>();
        pieces.forEach(piece -> legalMoves.addAll(piece.getLegalMoves(this)));
        return ImmutableList.copyOf(legalMoves);
    }

    private static Collection<Piece> detectActivePieces(final Map<Location, Tile> gameBoard,
                                                        final Alliance alliance) {
        final List<Piece> pieces = gameBoard.values().stream()
                .filter(tile -> tile.isOccupied() && tile.getPiece().getAlliance().equals(alliance))
                .map(Tile::getPiece).collect(Collectors.toList());
        return ImmutableList.copyOf(pieces);
    }

    private static Map<Location, Tile> createGameBoard(final Builder builder) {
        final Map<Location, Tile> tiles = new HashMap<>();
        for (int y = 0; y < BoardUtils.BOARD_SIZE; ++y) {
            for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                final Location location = BoardUtils.getLocation(x, y);
                final Piece piece = builder.boardPattern.get(location);
                final Tile tile = Tile.createTile(location, piece);
                tiles.put(location, tile);
            }
        }
        return ImmutableMap.copyOf(tiles);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("\n-----------------------\n");
        for (int y = 0; y < BoardUtils.BOARD_SIZE; ++y) {
            for (int x = 0; x < BoardUtils.BOARD_SIZE; ++x) {
                final String tileText = this.getTile(x, y).toString();
                stringBuilder.append(String.format("%3s", tileText));
            }
            stringBuilder.append("\n");
        }
        stringBuilder.append("\n-----------------------\n");
        return stringBuilder.toString();
    }
}
