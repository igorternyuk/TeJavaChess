package com.igorternyuk.engine.player.ai;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.moves.Move;

/**
 * Created by igor on 06.12.18.
 */
public interface MoveStrategy {
    Move execute(Board board);
}
