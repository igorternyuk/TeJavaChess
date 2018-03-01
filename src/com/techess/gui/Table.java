package com.techess.gui;

import com.techess.engine.Alliance;
import com.techess.engine.board.Board;
import com.techess.engine.board.Move;
import com.techess.engine.board.MoveTransition;
import com.techess.engine.board.Tile;
import com.techess.engine.pieces.Piece;
import static com.techess.engine.pieces.Piece.PieceType;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.techess.engine.board.Board.*;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

/**
 * Created by igor on 28.02.18.
 */

public class Table {
    private static final String TITLE_OF_MAIN_WINDOW = "TeChess";
    private static final int DX = 7;
    private static final int DY = 28;
    private static final int MAIN_WINDOW_WIDTH = 640 + DX;
    private static final int MAIN_WINDOW_HEIGHT = 640 + DY;
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(640, 640);
    private static final int TILE_PANEL_SIZE = 64;
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(80,80);
    private static final String DEFAULT_PATH_TO_SPRITE_SET = "resources/img/chessPiecesSpriteSet.png";
    private Board chessBoard;
    private Tile startTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private final JFrame mainWindow;
    private final BoardPanel boardPanel;

    private BufferedImage piecesSpriteSet = null;
    private BufferedImage[][] pieceImages = null;

    public Table(){
        this.chessBoard = Board.createStandardBoard();;
        this.mainWindow = new JFrame(TITLE_OF_MAIN_WINDOW);
        this.mainWindow.setLayout(new BorderLayout());
        this.mainWindow.setSize(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
        this.mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainWindow.setLocationRelativeTo(null);
        this.mainWindow.setResizable(false);
        final JMenuBar tableMenuBar = createMenuBar();
        this.mainWindow.setJMenuBar(tableMenuBar);

        try {
            this.piecesSpriteSet = ImageIO.read(new File(DEFAULT_PATH_TO_SPRITE_SET));
            this.pieceImages = createPieceImages();
            System.out.println("The sprite set of chess pieces has been successfully loaded");
            //ImageIcon icon = new ImageIcon(Table.class.getResource("spriteSet/chessPiecesSpriteSet.png"));

            //ImageIcon icoFill = new ImageIcon(View.class.getResource("img/btnFill.png"));
        } catch (IOException e) {
            System.out.println("Could not load image");
            e.printStackTrace();
        }

        this.boardPanel = new BoardPanel();
        this.mainWindow.add(this.boardPanel, BorderLayout.CENTER);
        this.mainWindow.setVisible(true);
    }

    private BufferedImage[][] createPieceImages(){
        final int numOfAlliances = Alliance.values().length;
        final int numOfPieceTypes = PieceType.values().length;
        BufferedImage[][] images = new BufferedImage[numOfAlliances][numOfPieceTypes];
        for(int y = 0; y < numOfAlliances; ++y){
            for(int x = 0; x < numOfPieceTypes; ++x){
                images[y][x] = this.piecesSpriteSet.getSubimage(x * TILE_PANEL_SIZE, y * TILE_PANEL_SIZE,
                                                                TILE_PANEL_SIZE, TILE_PANEL_SIZE);
            }
        }
        return images;
    }

    private final BufferedImage getPieceImage(final Piece piece){
        int x = piece.getPieceType().ordinal();
        int y = piece.getAlliance().ordinal();
        return this.pieceImages[y][x];
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

        final JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(event -> {
            int userAnswer = JOptionPane.showConfirmDialog(
                    null,
                    "Do you really want to exit?",
                    "Confirm exit, please",
                    JOptionPane.YES_NO_OPTION
            );
            if(userAnswer == JOptionPane.YES_OPTION){
                System.exit(0);
            }
        });

        fileMenu.add(exitMenuItem);
        return fileMenu;
    }

    private void cleanMoveTilesUp(){
        startTile = null;
        destinationTile = null;
        humanMovedPiece = null;
        boardPanel.boardTiles.forEach(boardTile -> {
            boardTile.setBorder(BorderFactory.createEmptyBorder());
            boardTile.assignDefaultTileColor();
        });
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

        private void drawBoard(){
            this.removeAll();
            boardTiles.forEach(tile -> {
                add(tile);
                tile.drawTile(chessBoard);
            });
            validate();
            repaint();
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
            this.setBackground((x + y) % 2 == 0 ? Color.WHITE.brighter().brighter() :
                    Color.GREEN.darker().darker());
            this.assignTileImage(chessBoard);
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    System.out.println("id = " + id + " x = " + x + " y = " + y);
                    if(isRightMouseButton(e)){
                        System.out.println("Right mouse button was clicked");
                        cleanMoveTilesUp();
                    } else if(isLeftMouseButton(e)){
                        System.out.println("Left mouse button was clicked");
                        //setBorder(BorderFactory.createEmptyBorder());
                        /*
                        * jPanel.setBorder(BorderFactory.createLineBorder(Color.black));
                        * */
                        if(startTile == null){
                            startTile = chessBoard.getTile(x,y);
                            if(startTile.isOccupied()) {
                                highlightTile();
                                humanMovedPiece = startTile.getPiece();
                                System.out.println("Selected piece = " + startTile.getPiece().getPieceType().getName());
                            } else {
                                startTile = null;
                            }
                        } else {
                            destinationTile = chessBoard.getTile(x,y);
                            if(destinationTile != null){
                                System.out.println("Destination tile = " + destinationTile.getTilePosition());
                                final Move move = Move.MoveFactory.createMove(chessBoard, startTile.getTilePosition(),
                                                                        destinationTile.getTilePosition());
                                final MoveTransition moveTransition = chessBoard.getCurrentPlayer().makeMove(move);
                                if(moveTransition.getMoveStatus().isDone()){
                                    System.out.println("Legal move from " + startTile.getTilePosition() + " to " +
                                            destinationTile.getTilePosition());
                                    chessBoard = moveTransition.getTransitedBoard();
                                }
                                if(chessBoard.getCurrentPlayer().isCheckMate()){
                                    SwingUtilities.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            boardPanel.drawBoard();
                                        }
                                    });
                                    final Alliance alliance = chessBoard.getCurrentPlayer().getOpponentAlliance();
                                    final String message = alliance + " wins by checkmate!";
                                    System.out.println(message);
                                    int userAnswer = JOptionPane.showConfirmDialog(
                                            null,
                                            message + "\nDo you want play again?",
                                            message,
                                            JOptionPane.YES_NO_OPTION
                                    );
                                    if(userAnswer == JOptionPane.YES_OPTION){
                                        chessBoard  = Board.createStandardBoard();
                                    } else {
                                        System.exit(0);
                                    }
                                    //chessBoard = Board.createStandardBoard();
                                }
                                cleanMoveTilesUp();
                                SwingUtilities.invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        boardPanel.drawBoard();
                                    }
                                });
                            } else {
                                cleanMoveTilesUp();
                            }
                        }

                    }
                    super.mouseReleased(e);
                }
            });
        }

        private void highlightTile(){
            setBorder(BorderFactory.createLineBorder(Color.RED.brighter()));
            assignTileColor(Color.RED.brighter().brighter());
        }

        private void drawTile(final Board board){
            assignDefaultTileColor();
            assignTileImage(board);
            validate();
            repaint();
        }

        private void assignDefaultTileColor(){
            this.setBackground((x + y) % 2 == 0 ? Color.WHITE.brighter().brighter() : Color.GREEN.darker().darker());
        }

        private void assignTileColor(final Color color){
            this.setBackground(color);
        }

        private void assignTileImage(final Board board){
            this.removeAll();
            final Tile tile = board.getTile(x, y);
            if(tile.isOccupied()){
                this.add(new JLabel(new ImageIcon(getPieceImage(tile.getPiece()))), BorderLayout.CENTER);
            }
        }
    }
}
