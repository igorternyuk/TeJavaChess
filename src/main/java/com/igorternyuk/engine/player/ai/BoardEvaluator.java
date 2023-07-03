package com.igorternyuk.engine.player.ai;

import com.igorternyuk.engine.board.Board;

/**
 * Created by igor on 06.12.18.
 */
public interface BoardEvaluator {
    long evaluate(Board board, int depth);
}
