package com.igorternyuk.engine.player.ai;

import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.pieces.Piece;
import com.igorternyuk.engine.player.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by igor on 08.12.18.
 */
public class RookPositionAnalyzer {
    private static final int OPEN_COLUMN_ROOK_BONUS = 25;
    private static final int ROOK_AGAINST_ENEMY_QUEEN_BONUS = 20;
    private static final int ROOK_AGAINST_ENEMY_KING_BONUS = 20;
    private static final int ROOKS_CONNECTED_VERTICALLY_BONUS = 40;
    private static final int ROOKS_CONNECTED_HORIZONTALLY_BONUS = 30;

    private Player player;
    private List<Piece> rooks;

    public RookPositionAnalyzer(final Player player) {
        this.player = player;
        this.rooks = player.getActivePieces().stream().filter(piece -> piece.getPieceType().isRook())
                .collect(Collectors.toList());
    }

    public int rookPositionScore() {
        return calculateConnectedRooksBonus() + calculateOpenFileRookBonus();
    }

    private int calculateOpenFileRookBonus() {
        int bonus = 0;
        for (final Piece rook : this.rooks) {
            final int rookFile = rook.getLocation().getX();
            final List<Piece> piecesOnRookFile = this.player.getBoard().getAllActivePieces()
                    .stream().filter(piece -> piece.getLocation().getX() == rookFile)
                    .collect(Collectors.toList());
            if (piecesOnRookFile.size() == 1 && piecesOnRookFile.get(0).equals(rook)) {
                bonus += OPEN_COLUMN_ROOK_BONUS;
            } else {
                boolean enemyQueenOnRookFile = piecesOnRookFile.stream()
                        .anyMatch(piece ->
                                piece.getAlliance().equals(this.player.getOpponentAlliance())
                                        && piece.getPieceType().isQueen()
                        );
                boolean enemyKingOnRookFile = piecesOnRookFile.stream()
                        .anyMatch(piece ->
                                piece.getAlliance().equals(this.player.getOpponentAlliance())
                                        && piece.getPieceType().isKing()
                        );
                if (enemyQueenOnRookFile) {
                    bonus += ROOK_AGAINST_ENEMY_QUEEN_BONUS;
                }

                if (enemyKingOnRookFile) {
                    bonus += ROOK_AGAINST_ENEMY_KING_BONUS;
                }
            }

        }
        return bonus;
    }

    private int calculateConnectedRooksBonus() {
        if (this.rooks.size() == 2) {
            final int x1 = Math.min(this.rooks.get(0).getLocation().getX()
                    , this.rooks.get(1).getLocation().getX());
            final int y1 = Math.min(this.rooks.get(0).getLocation().getY()
                    , this.rooks.get(1).getLocation().getY());
            final int x2 = Math.max(this.rooks.get(0).getLocation().getX()
                    , this.rooks.get(1).getLocation().getX());
            final int y2 = Math.max(this.rooks.get(0).getLocation().getY()
                    , this.rooks.get(1).getLocation().getY());

            //Check vertical connection

            if (x1 == x2) {
                List<Piece> betweenPieces =
                        this.player.getBoard().getAllActivePieces().stream()
                                .filter(piece -> {
                                    int px = piece.getLocation().getX();
                                    int py = piece.getLocation().getY();
                                    return px == x1 && (py > y1 && py < y2);
                                }).collect(Collectors.toList());
                if (betweenPieces.isEmpty()) {
                    return ROOKS_CONNECTED_VERTICALLY_BONUS;
                }
            } else if (y1 == y2) {

                //Check horizontal connection

                boolean isGoodRank = false;
                if (this.player.getAlliance().isWhite()) {
                    isGoodRank = y1 == BoardUtils.FIRST_RANK || y1 == BoardUtils.SEVENTH_RANK
                            || y1 == BoardUtils.EIGHTH_RANK;
                } else if (this.player.getAlliance().isBlack()) {
                    isGoodRank = y1 == BoardUtils.EIGHTH_RANK || y1 == BoardUtils.SECOND_RANK
                            || y1 == BoardUtils.FIRST_RANK;
                }
                if (isGoodRank) {
                    List<Piece> betweenPieces =
                            this.player.getBoard().getAllActivePieces().stream()
                                    .filter(piece -> {
                                        int px = piece.getLocation().getX();
                                        int py = piece.getLocation().getY();
                                        return py == y1 && (px > x1 && px < x2);
                                    }).collect(Collectors.toList());
                    if (betweenPieces.isEmpty()) {
                        return ROOKS_CONNECTED_HORIZONTALLY_BONUS;
                    } else if (betweenPieces.size() == 1) {
                        if (!betweenPieces.get(0).getAlliance().equals(this.player.getAlliance())) {
                            return ROOKS_CONNECTED_HORIZONTALLY_BONUS;
                        }
                    }
                }
            }
        }
        return 0;
    }
}
