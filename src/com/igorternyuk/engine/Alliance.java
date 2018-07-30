package com.igorternyuk.engine;

import com.igorternyuk.engine.board.BoardUtils;
import com.igorternyuk.engine.board.Position;
import com.igorternyuk.engine.player.BlackPlayer;
import com.igorternyuk.engine.player.Player;
import com.igorternyuk.engine.player.WhitePlayer;

/**
 * Created by igor on 01.12.17.
 */

public enum Alliance {
    WHITE {
        @Override
        public int getDirectionY() {
            return -1;
        }

        @Override
        public int getOppositeDirectionY() {
            return 1;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public boolean isPawnPromotionSquare(final Position position) {
            return position.getY() == BoardUtils.EIGHTH_RANK;
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return whitePlayer;
        }

        @Override
        public String toString(){
            return "White";
        }
    },
    BLACK {
        @Override
        public int getDirectionY() {
            return 1;
        }

        @Override
        public int getOppositeDirectionY() {
            return -1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public boolean isPawnPromotionSquare(final Position position) {
            return position.getY() == BoardUtils.FIRST_RANK;
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return blackPlayer;
        }

        @Override
        public String toString(){
            return "Black";
        }
    };
    public abstract int getDirectionY();
    public abstract int getOppositeDirectionY();
    public abstract boolean isWhite();
    public abstract boolean isBlack();
    public abstract boolean isPawnPromotionSquare(final Position position);

    public abstract Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer);
}
