package com.techess.gui;

import com.techess.engine.board.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import static com.techess.engine.board.Board.*;

/**
 * Created by igor on 28.02.18.
 */

public class Table {
    private static final String TITLE_OF_MAIN_WINDOW = "TeChess";
    private static final int DX = 7;
    private static final int DY = 28;
    private static final int MAIN_WINDOW_WIDTH = 600 + DX;
    private static final int MAIN_WINDOW_HEIGHT = 600 + DY;
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(600, 600);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(75,75);
    private static final int TILE_PANEL_SIZE = 64;
    private final JFrame mainWindow;
    private final BoardPanel boardPanel;
    public Table(){
        this.mainWindow = new JFrame(TITLE_OF_MAIN_WINDOW);
        this.mainWindow.setLayout(new BorderLayout());
        this.mainWindow.setSize(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
        this.mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainWindow.setLocationRelativeTo(null);
        this.mainWindow.setResizable(false);
        final JMenuBar tableMenuBar = createMenuBar();
        this.mainWindow.setJMenuBar(tableMenuBar);
        this.boardPanel = new BoardPanel();
        this.mainWindow.add(this.boardPanel, BorderLayout.CENTER);
        this.mainWindow.setVisible(true);
    }

    private JMenuBar createMenuBar(){
        final JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        return menuBar;
    }

    private JMenu createFileMenu(){
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem openPGN = new JMenuItem("Load pgn file");
        openPGN.addActionListener(event -> {
            System.out.println("Open up that pgn file!!!");
        });
        fileMenu.add(openPGN);
        return fileMenu;
    }

    private class BoardPanel extends JPanel {
        final List<TilePanel> boardTiles;
        public BoardPanel(){
            super(new GridLayout(BOARD_SIZE, BOARD_SIZE));
            this.setPreferredSize(BOARD_PANEL_DIMENSION);
            this.boardTiles = new ArrayList<>();
            for(int y = 0; y < BOARD_SIZE; ++y){
                for(int x = 0; x < BOARD_SIZE; ++x){
                    final TilePanel tilePanel = new TilePanel(x,y);
                    boardTiles.add(tilePanel);
                    this.add(tilePanel);
                    this.validate();
                }
            }
        }
    }

    private class TilePanel extends JPanel{
        private final int id;
        private final int x, y;

        public TilePanel(final int x, final int y){
            this.id = y * BOARD_SIZE + x;
            this.x = x;
            this.y = y;
            this.setPreferredSize(TILE_PANEL_DIMENSION);
            this.setBackground((x + y) % 2 == 0 ? Color.WHITE.darker() : Color.BLACK.brighter());
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    System.out.println("id = " + id + " x = " + x + " y = " + y);
                    super.mouseReleased(e);
                }
            });
        }
    }
}
