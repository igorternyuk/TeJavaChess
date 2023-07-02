package com.igorternyuk.engine.player.ai;

import com.igorternyuk.engine.board.Board;

/**
 * Created by igor on 06.12.18.
 */
public interface BoardEvaluator {
    double evaluate(Board board, int depth);
}
