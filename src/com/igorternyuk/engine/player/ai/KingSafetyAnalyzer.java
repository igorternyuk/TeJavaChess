package com.igorternyuk.engine.player.ai;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.board.Location;
import com.igorternyuk.engine.moves.Move;
import com.igorternyuk.engine.pieces.Piece;
import com.igorternyuk.engine.pieces.PieceType;
import com.igorternyuk.engine.player.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by igor on 08.12.18.
 */
public class KingSafetyAnalyzer {
    private static final int[] ATTACK_WEIGHT_TABLE = createAttackWeightTable();
    private static final Map<PieceType, Integer> ATTACK_VALUE_MAP = createAttackValueMap();
    private static final int STORMING_ENEMY_PAWN_PENALTY = -20;
    private static final int OPEN_FILE_AGAINST_KING_PENALTY = -40;
    private static final int SEMIOPEN_FILE_AGAINST_KING_PENALTY = -30;
    /*
    * 3.King's safety analyzer
 3.1 Pawn shield
 3.2 Enemy pawn storm
 3.3 Open or semi-open files against King's position
 3.4 Attack score
    * */
    private Player player;
    final Set<Location> kingZone;

    public KingSafetyAnalyzer(final Player player) {
        this.player = player;
        this.kingZone = getKingsZone();
    }

    public int scoreKingSafety() {
        return scorePawnShield() + scoreOpenFilesThreats()
                + scoreEnemyPawnStorm() - scoreEnemyAttackPosibility();
    }

    public Set<Location> getKingsZone() {
        Set<Location> zone = new HashSet<>(12);
        final Location kingLocation = this.player.getPlayerKing().getLocation();
        zone.add(kingLocation);
        /*List<Location> squaresWhereKingCanGo = this.player.getLegalMoves().stream()
                .filter(move -> move.getMovedPiece().getPieceType().isKing())
                .map(move -> move.getDestination()).collect(Collectors.toList());*/
        zone.addAll(BoardUtils.getNeighbours(kingLocation));
        final int leftFile = kingLocation.getX() - 1;

        if (this.player.getAlliance().isWhite()) {
            final int rankBeforePawnShield = kingLocation.getY() - 1;
            for (int x = leftFile; x <= leftFile + 2; ++x) {
                for (int y = rankBeforePawnShield; y >= rankBeforePawnShield - 1; --y) {
                    if (BoardUtils.isValidLocation(x, y)) {
                        zone.add(BoardUtils.getLocation(x, y));
                    }
                }
            }
        } else if (this.player.getAlliance().isBlack()) {
            final int rankBeforePawnShield = kingLocation.getY() + 1;
            for (int x = leftFile; x <= leftFile + 2; ++x) {
                for (int y = rankBeforePawnShield; y <= rankBeforePawnShield + 1; ++y) {
                    if (BoardUtils.isValidLocation(x, y)) {
                        zone.add(BoardUtils.getLocation(x, y));
                    }
                }
            }
        }
        return zone;
    }

    public int scorePawnShield() {
        final Location kingLocation = this.player.getPlayerKing().getLocation();
        int value = 0;
        for (int x = kingLocation.getX() - 1; x <= kingLocation.getX() + 1; ++x) {
            if (this.player.getAlliance().isWhite()) {
                for (int y = kingLocation.getY() - 1; y >= kingLocation.getY() - 2; --y) {
                    if (BoardUtils.isValidLocation(x, y)) {
                        if (this.player.getBoard().getTile(x, y).isOccupied()) {
                            final Piece piece = this.player.getBoard().getTile(x, y).getPiece();
                            if (piece.getPieceType().isPawn()
                                    && piece.getAlliance().equals(this.player.getAlliance())) {
                                value += y == kingLocation.getY() - 1 ? 20 : 10;
                            }
                        }
                    }
                }
            } else {
                for (int y = kingLocation.getY() + 1; y <= kingLocation.getY() + 2; ++y) {
                    if (BoardUtils.isValidLocation(x, y)) {
                        if (this.player.getBoard().getTile(x, y).isOccupied()) {
                            final Piece piece = this.player.getBoard().getTile(x, y).getPiece();
                            if (piece.getPieceType().isPawn()
                                    && piece.getAlliance().equals(this.player.getAlliance())) {
                                value += y == kingLocation.getY() + 1 ? 20 : 10;
                            }
                        }
                    }
                }
            }
        }
        return value;
    }

    public int scoreEnemyPawnStorm() {
        final long numberOfEnemyPawnsInKingZone =
                this.player.getOpponentActivePieces().stream()
                        .filter(piece -> piece.getPieceType().isPawn()
                                && calcChebyshevDistance(this.player.getPlayerKing(), piece) < 2).count();
        return (int) (STORMING_ENEMY_PAWN_PENALTY * numberOfEnemyPawnsInKingZone);
    }

    public int scoreOpenFilesThreats() {
        final long numberOfOpponentMajorPieces = this.player.getOpponentActivePieces()
                .stream().filter(piece -> piece.getPieceType().isMajorPiece()).count();
        final int kingFile = this.player.getPlayerKing().getLocation().getX();
        if (numberOfOpponentMajorPieces > 0) {
            int value = 0;
            List<Piece> allPawns = this.player.getBoard().getAllActivePieces().stream()
                    .filter(piece -> piece.getPieceType().isPawn()).collect(Collectors.toList());

            final int leftFile = kingFile - 1;
            final int rightFile = kingFile - 1;
            Set<Integer> files = new HashSet<>(3);
            files.add(kingFile);
            if (BoardUtils.isValidFile(leftFile)) {
                files.add(leftFile);
            }
            if (BoardUtils.isValidFile(rightFile)) {
                files.add(rightFile);
            }
            for (final Integer file : files) {
                final List<Piece> pawnsOnFile = allPawns.stream().filter(piece ->
                        piece.getLocation().getX() == file
                ).collect(Collectors.toList());

                if (pawnsOnFile.isEmpty()) {
                    value += OPEN_FILE_AGAINST_KING_PENALTY;
                } else if (pawnsOnFile.size() == 1) {
                    value += SEMIOPEN_FILE_AGAINST_KING_PENALTY;
                }
            }
            return value;
        } else {
            return 0;
        }
    }

    public int scoreEnemyAttackPosibility() {
        final Set<Location> kingZone = getKingsZone();
        final Set<Piece> attackingPieces = new HashSet<>(8);
        final List<Move> attackingMoves = this.player.getOpponentLegalMoves().stream()
                .filter(move -> kingZone.contains(move.getDestination()))
                .collect(Collectors.toList());
        attackingMoves.forEach(move -> attackingPieces.add(move.getMovedPiece()));
        final int numberOfAttackers = attackingPieces.size();
        final int attackWeight = getAttackWeight(numberOfAttackers);
        final ListMultimap<Location, Piece> attackTable = ArrayListMultimap.create();
        attackingMoves.forEach(move -> {
            attackTable.put(move.getDestination(), move.getMovedPiece());
        });

        int valueOfAttack = 0;
        for (final Location location : attackTable.asMap().keySet()) {
            List<Piece> currentLocationAttackers = attackTable.get(location);
            for (final Piece piece : currentLocationAttackers) {
                valueOfAttack += ATTACK_VALUE_MAP.get(piece.getPieceType());
            }
        }
        return (int) (valueOfAttack * attackWeight / 100.f);
    }

    private int getAttackWeight(int numberOfAttackers) {
        if (numberOfAttackers <= 7) {
            return ATTACK_WEIGHT_TABLE[numberOfAttackers];
        } else {
            return 100;
        }
    }

    private int calcChebyshevDistance(final Piece first, final Piece second) {
        final int dx = first.getLocation().getX() - second.getLocation().getX();
        final int dy = first.getLocation().getY() - second.getLocation().getY();
        return Math.max(Math.abs(dx), Math.abs(dy));
    }

    private static int[] createAttackWeightTable() {
        int[] table = new int[8];
        table[0] = table[1] = 0;
        table[2] = 50;
        table[3] = 75;
        table[4] = 88;
        table[5] = 94;
        table[6] = 97;
        table[7] = 99;
        return table;
    }

    private static Map<PieceType, Integer> createAttackValueMap() {
        Map<PieceType, Integer> table = new HashMap<>();
        table.put(PieceType.KNIGHT, 20);
        table.put(PieceType.BISHOP, 20);
        table.put(PieceType.ROOK, 40);
        table.put(PieceType.QUEEN, 80);
        table.put(PieceType.KING, 0);
        table.put(PieceType.PAWN, 5);
        return table;
    }
}
