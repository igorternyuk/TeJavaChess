package com.techess;

import com.techess.engine.board.Board;
import com.techess.gui.Table;

/**
 * Created by igor on 03.12.17.
 */

public class App {
    public static void main(String[] args) {
        Board board = Board.createStandardBoard();
        Table table = new Table();
        if(board == null)
        {
            System.out.println("The board was not created");
        }
        else {
            System.out.println(board);
        }
    }
}
