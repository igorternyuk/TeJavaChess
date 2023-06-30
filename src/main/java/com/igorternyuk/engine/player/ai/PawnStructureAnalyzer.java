package com.igorternyuk.engine.player.ai;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.pieces.Piece;
import com.igorternyuk.engine.player.Player;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by igor on 08.12.18.
 */
public class PawnStructureAnalyzer {

    public static final int ISOLATED_PAWN_PENALTY = -15;
    public static final int DOUBLED_PAWN_PENALTY = -35;
    private Player player;
    private Collection<Piece> playerPawns;
    private ListMultimap<Integer, Piece> pawnColumnTable;

    public PawnStructureAnalyzer(final Player player) {
        this.player = player;
        this.playerPawns = getPlayerPawns(player);
        this.pawnColumnTable = createPawnColumnTable(this.playerPawns);
    }

    public static Collection<Piece> getPlayerPawns(final Player player) {
        Collection<Piece> pawns = player.getActivePieces().stream()
                .filter(piece -> piece.getPieceType().isPawn()).collect(Collectors.toList());
        return ImmutableList.copyOf(pawns);
    }

    private static ListMultimap<Integer, Piece> createPawnColumnTable(final Collection<Piece> pawns) {
        final ListMultimap<Integer, Piece> pawnTable = ArrayListMultimap.create(
                BoardUtils.BOARD_SIZE, BoardUtils.BOARD_SIZE - 2);
        pawns.forEach(pawn -> pawnTable.put(pawn.getLocation().getX(), pawn));
        return pawnTable;
    }

    public int pawnStructureScore() {
        return calculateTotalDoubledPawnsPenalty() + calculateTotalIsolatedPawnsPenalty();
    }


    public int calculateTotalDoubledPawnsPenalty() {
        return calculatePawnStackPenalty();
    }

    public int calculateTotalIsolatedPawnsPenalty() {
        return calculateIsolatedPawnPenalty();
    }

    private int calculatePawnStackPenalty() {
        int pawnStackPenalty = 0;
        for (final Integer i : this.pawnColumnTable.keySet()) {
            int pawnStackSize = this.pawnColumnTable.get(i).size();
            if (pawnStackSize > 1) {
                pawnStackPenalty += pawnStackSize;
            }
        }
        return pawnStackPenalty * DOUBLED_PAWN_PENALTY;
    }

    private int calculateIsolatedPawnPenalty() {
        int numIsolatedPawns = 0;
        for (final Integer file : this.pawnColumnTable.keys()) {
            int leftFile = file - 1;
            int rightFile = file + 1;
            boolean leftFileFree = true;
            boolean rightFileFree = true;
            if (BoardUtils.isValidFile(leftFile)) {
                leftFileFree = this.pawnColumnTable.get(leftFile).isEmpty();
            }
            if (BoardUtils.isValidFile(rightFile)) {
                rightFileFree = this.pawnColumnTable.get(leftFile).isEmpty();
            }

            if (leftFileFree && this.pawnColumnTable.get(file).isEmpty() && rightFileFree) {
                numIsolatedPawns += this.pawnColumnTable.get(file).size();
            }
        }
        return numIsolatedPawns * ISOLATED_PAWN_PENALTY;
    }
}
