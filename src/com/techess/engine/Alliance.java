package com.techess.engine;

import com.techess.engine.player.BlackPlayer;
import com.techess.engine.player.Player;
import com.techess.engine.player.WhitePlayer;

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
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return whitePlayer;
        }
    },
    BLACK {
        @Override
        public int getDirectionY() {
            return 1;
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
        public Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer) {
            return blackPlayer;
        }
    };
    public abstract int getDirectionY();
    public abstract boolean isWhite();
    public abstract boolean isBlack();

    public abstract Player choosePlayer(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer);
}
