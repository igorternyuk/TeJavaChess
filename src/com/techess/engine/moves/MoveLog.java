package com.techess.engine.moves;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igor on 04.03.18.
 */

public class MoveLog {
    private List<Move> moves;

    public MoveLog() {
        this.moves = new ArrayList<>();
    }

    public List<Move> getMoves() {
        return ImmutableList.copyOf(this.moves);
    }

    public void addMove(final Move move) {
        this.moves.add(move);
    }

    public boolean removeMove(final Move move) {
        return this.moves.remove(move);
    }

    public void removeMove(final int index) {
        this.moves.remove(index);
    }

    public void clear() {
        this.moves.clear();
    }

    public int size() {
        return this.moves.size();
    }
}
