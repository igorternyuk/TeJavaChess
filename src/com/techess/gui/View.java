package com.techess.gui;

import com.techess.engine.Alliance;
import com.techess.engine.board.*;
import com.techess.engine.moves.Move;
import com.techess.engine.moves.MoveLog;
import com.techess.engine.moves.MoveTransition;
import com.techess.engine.pieces.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import static com.techess.engine.board.Board.*;
import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

/**
 * Created by igor on 28.02.18.
 */

public class View {
    private static final String TITLE_OF_MAIN_WINDOW = "TeChess";
    private static final int DX = 3;
    private static final int DY = 48;
    private static final int TILE_SIZE = 64;
    private static final int MAIN_WINDOW_WIDTH = TILE_SIZE * BOARD_SIZE + TakenPiecesPanel.PANEL_WIDTH +
            GameHistoryPanel.PANEL_WIDTH + DX;
    private static final int MAIN_WINDOW_HEIGHT = TILE_SIZE * BOARD_SIZE + DY;
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(TILE_SIZE * BOARD_SIZE,
            TILE_SIZE * BOARD_SIZE);
    private static final int LEGAL_MOVE_HIGHLIGHT_CIRCLE_RADIUS = 5;
    private static final int LAST_MOVE_HIGHLIGHT_ARROW_HEIGHT = 30;
    private static final int LAST_MOVE_HIGHLIGHT_ARROW_ANGLE = 30;
    private static final int LAST_MOVE_HIGHLIGHT_ARROW_LINE_WIDTH = 5;
    final String[] PROMOTED_PIECE_OPTIONS = {"Rook", "Bishop", "Knight", "Queen"};
    private static final ResourceManager RESOURCE_MANAGER = ResourceManager.getInstance();
    private Board chessBoard;
    private MoveLog moveLog;
    private Tile startTile;
    private Tile destinationTile;
    private Move lastMove;
    private Piece humanMovedPiece;
    private int humanMovedPieceX, humanMovedPieceY;
    private final JFrame mainWindow;
    private final BoardPanel boardPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final GameHistoryPanel gameHistoryPanel;
    private BoardOrientation boardOrientation;
    private boolean highlightLegalMoves = false;
    private boolean highlightLastMove = false;
    private boolean isAutoQueenEnabled = false;

    public View(){
        this.chessBoard = Board.createStandardBoard();
        this.moveLog = new MoveLog();
        this.mainWindow = new JFrame(TITLE_OF_MAIN_WINDOW);
        this.mainWindow.setLayout(new BorderLayout());
        this.mainWindow.setSize(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
        this.mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainWindow.setLocationRelativeTo(null);
        this.mainWindow.setResizable(false);
        final JMenuBar tableMenuBar = createMenuBar();
        this.mainWindow.setJMenuBar(tableMenuBar);
        this.boardPanel = new BoardPanel();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.gameHistoryPanel = new GameHistoryPanel();
        this.boardOrientation = BoardOrientation.REGULAR;
        this.mainWindow.add(this.boardPanel, BorderLayout.CENTER);
        this.mainWindow.add(this.takenPiecesPanel, BorderLayout.WEST);
        this.mainWindow.add(this.gameHistoryPanel, BorderLayout.EAST);
        this.mainWindow.setVisible(true);
    }

    private JMenuBar createMenuBar(){
        final JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createPreferencesMenu());
        return menuBar;
    }

    private JMenu createFileMenu(){
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem newClassicChessGameMenuItem = new JMenuItem("New classic chess game");
        newClassicChessGameMenuItem.addActionListener(event->{
            this.prepareNewClassicChessGame();
        });

        fileMenu.add(newClassicChessGameMenuItem);

        final JMenuItem newRandomFisherChessGameMenuItem = new JMenuItem("New random Fisher chess game");
        newRandomFisherChessGameMenuItem.addActionListener(event->{
            this.prepareNewRandomFisherChessGame();
        });

        fileMenu.add(newRandomFisherChessGameMenuItem);

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

    private JMenu createPreferencesMenu(){
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip board");
        flipBoardMenuItem.addActionListener(event -> {
            this.boardOrientation = this.boardOrientation.getOpposite();
            this.boardPanel.redraw();
        });
        preferencesMenu.add(flipBoardMenuItem);

        final JCheckBoxMenuItem highlightLegalMovesMenuItem = new JCheckBoxMenuItem("Highlight legal moves",
                                                                                 false);
        highlightLegalMovesMenuItem.addActionListener(event -> {
            highlightLegalMoves = highlightLegalMovesMenuItem.isSelected();
            System.out.println("highlightLegalMoves has been set to " + highlightLegalMoves + " now.");
        });
        preferencesMenu.addSeparator();
        preferencesMenu.add(highlightLegalMovesMenuItem);

        final JCheckBoxMenuItem highlightLastMoveMenuItem = new JCheckBoxMenuItem("Highlight last move",
                false);
        highlightLastMoveMenuItem.addActionListener(event ->{
            highlightLastMove = highlightLastMoveMenuItem.isSelected();
        });
        preferencesMenu.add(highlightLastMoveMenuItem);

        final JCheckBoxMenuItem setAutoQueenMenuItem = new JCheckBoxMenuItem("Set autoqueen");
        setAutoQueenMenuItem.addActionListener(event->{
            isAutoQueenEnabled = setAutoQueenMenuItem.isSelected();
        });

        preferencesMenu.add(setAutoQueenMenuItem);
        return preferencesMenu;
    }

    private void cleanMoveTilesUp(){
        startTile = null;
        destinationTile = null;
        humanMovedPiece = null;
        humanMovedPieceX = -TILE_SIZE;
        humanMovedPieceY = -TILE_SIZE;
    }

    private void cleanAllUpForNewGame(){
        this.takenPiecesPanel.clear();
        this.gameHistoryPanel.clear();
        cleanMoveTilesUp();
        moveLog.clear();
        lastMove = null;
    }

    private void prepareNewClassicChessGame(){
        cleanAllUpForNewGame();
        chessBoard = Board.createStandardBoard();
    }

    private void prepareNewRandomFisherChessGame(){
        cleanAllUpForNewGame();
        chessBoard = Board.createBoardForChess960();
    }

    private Piece choosePromotedPiece() {
        final Piece newPiece;
        int userAnswer = JOptionPane.showOptionDialog(null, "Choose a piece for promotion",
                "Pawn promotion", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                PROMOTED_PIECE_OPTIONS, PROMOTED_PIECE_OPTIONS[0]);
        switch (userAnswer){
            case 0:
                newPiece = Rook.createRook(destinationTile.getTilePosition(),
                        humanMovedPiece.getAlliance(),false);
                break;
            case 1:
                newPiece = Bishop.createBishop(destinationTile.getTilePosition(),
                        humanMovedPiece.getAlliance(),false);
                break;
            case 2:
                newPiece = Knight.createKnight(destinationTile.getTilePosition(),
                        humanMovedPiece.getAlliance(),false);
                break;
            default:
                newPiece = Queen.createQueen(destinationTile.getTilePosition(),
                        humanMovedPiece.getAlliance(),false);
                break;
        }
        return newPiece;
    }


    private class BoardPanel extends JPanel implements MouseMotionListener{
        public BoardPanel(){
            this.setPreferredSize(BOARD_PANEL_DIMENSION);
            this.addMouseMotionListener(this);
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if(isRightMouseButton(e)){
                        cleanMoveTilesUp();
                    } else if(isLeftMouseButton(e)) {
                        int x = e.getX() / TILE_SIZE;
                        int y = e.getY() / TILE_SIZE;
                        if(boardOrientation.isOpposite()){
                            x = calculateFlippedCoordinate(x);
                            y = calculateFlippedCoordinate(y);
                        }
                        if(startTile == null){
                            startTile = chessBoard.getTile(x,y);
                            if(startTile.isOccupied()) {
                                humanMovedPiece = startTile.getPiece();
                                //System.out.println("Selected piece = " + startTile.getPiece().getPieceType().getName());
                            } else {
                                startTile = null;
                            }
                        } else {
                            destinationTile = chessBoard.getTile(x,y);
                            if(destinationTile != null){
                                //System.out.println("Destination tile = " + destinationTile.getTilePosition());
                                final Move move = detectMove();
                                System.out.println("From View move.getMovedPiece() = " + (move.getMovedPiece() == null));
                                //System.out.println("From view move is - " + move);
                                System.out.println("Moving piece - " + move.getMovedPiece().getPieceType().getName());
                                final MoveTransition moveTransition = chessBoard.getCurrentPlayer().makeMove(move);
                                if(moveTransition.getMoveStatus().isDone()){
                                    //System.out.println("Legal move from " + startTile.getTilePosition() + " to " +
                                    //        destinationTile.getTilePosition());
                                    chessBoard = moveTransition.getTransitedBoard();
                                    lastMove = move;
                                    moveLog.addMove(move);
                                    gameHistoryPanel.update(chessBoard, moveLog);
                                    if(move.isCapturingMove()) {
                                        takenPiecesPanel.update(moveLog);
                                    }
                                }
                                cleanMoveTilesUp();
                                checkForMate();
                                redraw();
                            } else {
                                cleanMoveTilesUp();
                            }
                        }
                    }

                    super.mouseReleased(e);
                }
            });
        }

        private Move detectMove(){
            if (chessBoard.getGameType().isRandomFisherChess() &&
                    humanMovedPiece.getPieceType().isKing()){
                final int kingsRookStartCoordinateX = chessBoard.getKingsRookStartCoordinateX();
                final int queensRookStartCoordinateX = chessBoard.getQueensRookStartCoordinateX();
                System.out.println("kingsRookStartPosition = " + kingsRookStartCoordinateX);
                System.out.println("queensRookStartPosition = " + queensRookStartCoordinateX);
                final Alliance currentPlayerAlliance = chessBoard.getCurrentPlayer().getAlliance();
                final int backRankCoordinateY =
                        chessBoard.getCurrentPlayer().getAlliance().isWhite() ?
                                Board.FIRST_RANK : Board.EIGHTH_RANK;
                final Position kingSideRookStartPosition =
                        Board.getPosition(kingsRookStartCoordinateX, backRankCoordinateY);
                final Position queenSideRookStartPosition =
                        Board.getPosition(queensRookStartCoordinateX, backRankCoordinateY);
                final Position kingsSideKingPosition = currentPlayerAlliance.isWhite() ?
                                chessBoard.getTile("g1").getTilePosition() :
                                chessBoard.getTile("g8").getTilePosition();

                final Position queensSideKingPosition = currentPlayerAlliance.isWhite() ?
                                chessBoard.getTile("c1").getTilePosition() :
                                chessBoard.getTile("c8").getTilePosition();

                final Position kingsSideRookTargetPosition = currentPlayerAlliance.isWhite() ?
                        chessBoard.getTile("f1").getTilePosition() :
                        chessBoard.getTile("f8").getTilePosition();

                final Position queensSideRookTargetPosition = currentPlayerAlliance.isWhite() ?
                        chessBoard.getTile("d1").getTilePosition() :
                        chessBoard.getTile("d8").getTilePosition();

                if(destinationTile.getTilePosition().equals(kingSideRookStartPosition)) {
                    Tile kingsRookStartTile = chessBoard.getTile(kingSideRookStartPosition);
                    if(kingsRookStartTile.isOccupied() && kingsRookStartTile.getPiece().getPieceType().isRook()){
                        Rook castlingRook = (Rook)kingsRookStartTile.getPiece();
                        if(castlingRook.getAlliance().equals(humanMovedPiece.getAlliance())) {
                            return Move.MoveFactory.createRandomFisherChessCastling(chessBoard, startTile.getTilePosition(),
                                    kingsSideKingPosition, castlingRook, kingsSideRookTargetPosition);
                        }
                    }

                } else if(destinationTile.getTilePosition().equals(queenSideRookStartPosition)){
                    Tile queensRookStartTile = chessBoard.getTile(queenSideRookStartPosition);
                    if(queensRookStartTile.isOccupied() && queensRookStartTile.getPiece().getPieceType().isRook()){
                        Rook castlingRook = (Rook)queensRookStartTile.getPiece();
                        if(castlingRook.getAlliance().equals(humanMovedPiece.getAlliance())) {
                            return Move.MoveFactory.createRandomFisherChessCastling(chessBoard, startTile.getTilePosition(),
                                    queensSideKingPosition, castlingRook, queensSideRookTargetPosition);
                        }
                    }
                }
            }

            if(humanMovedPiece.getPieceType().isPawn() &&
                    chessBoard.getCurrentPlayer().getAlliance().isPawnPromotionSquare(destinationTile
                            .getTilePosition())){
                final Piece promotedPiece = isAutoQueenEnabled ?
                        Queen.createQueen(destinationTile.getTilePosition(),
                                humanMovedPiece.getAlliance(),false) :
                        choosePromotedPiece();
                return Move.MoveFactory.createPawnPromotionMove(chessBoard,
                       startTile.getTilePosition(),
                       destinationTile.getTilePosition(),
                       promotedPiece);

            }
            System.out.println("from move factory isStartTileEmpty = " + startTile.isEmpty());
            return Move.MoveFactory.createMove(chessBoard, startTile.getTilePosition(),
                    destinationTile.getTilePosition());
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            e.consume();
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            if(humanMovedPiece != null){
                humanMovedPieceX = e.getX() - TILE_SIZE / 2;
                humanMovedPieceY = e.getY() - TILE_SIZE / 2;
                redraw();
            }
            e.consume();
        }

        int calculateFlippedCoordinate(final int coordinate){
            return BOARD_SIZE - 1 - coordinate;
        }

        int toBoardCoordinate(final int coordinate){
            return coordinate * TILE_SIZE;
        }

        private void redraw(){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    boardPanel.repaint();
                }
            });
        }

        private void drawChessField(Graphics2D g2D){
            for(int y = 0; y < BOARD_SIZE; ++y){
                for(int x = 0; x < BOARD_SIZE; ++x){
                    g2D.setColor((x + y) % 2 == 0 ? Color.WHITE : new Color(0, 202, 255));
                    g2D.fillRect(x * TILE_SIZE,y * TILE_SIZE,TILE_SIZE, TILE_SIZE);
                    final Tile currentTile = chessBoard.getTile(x, y);
                    //Highlights king which is under check
                    if((chessBoard.getCurrentPlayer().isUnderCheck() && currentTile.isOccupied() &&
                            currentTile.getPiece().getPieceType().isKing() &&
                            currentTile.getPiece().getAlliance().equals(chessBoard.getCurrentPlayer().getAlliance()))) {
                        g2D.setColor(Color.RED);
                        int checkedKingX = x;
                        int checkedKingY = y;
                        if(boardOrientation.isOpposite()){
                            checkedKingX = calculateFlippedCoordinate(checkedKingX);
                            checkedKingY = calculateFlippedCoordinate(checkedKingY);
                        }
                        g2D.fillRect(checkedKingX * TILE_SIZE, checkedKingY * TILE_SIZE,TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        private void drawAllActivePieces(Graphics2D g2D){
            chessBoard.getAllActivePieces().stream().filter(piece -> !piece.equals(humanMovedPiece))
                    .forEach((Piece piece) -> {
                        final BufferedImage bufferedImage = RESOURCE_MANAGER.getPieceImage(piece);
                        final int pieceX = piece.getPosition().getX();
                        final int pieceY = piece.getPosition().getY();
                        if(boardOrientation.isOpposite()){
                            final int flippedX = calculateFlippedCoordinate(pieceX);
                            final int flippedY = calculateFlippedCoordinate(pieceY);
                            g2D.drawImage(bufferedImage, flippedX * TILE_SIZE, flippedY * TILE_SIZE,
                                    TILE_SIZE, TILE_SIZE, this);
                        } else {
                            g2D.drawImage(bufferedImage, pieceX * TILE_SIZE, pieceY * TILE_SIZE, TILE_SIZE,
                                    TILE_SIZE, this);
                        }
                    });
        }

        private void drawLegalMovesHints(Graphics2D g2D){
            //Draw current player's legal moves
            if(highlightLegalMoves){
                chessBoard.getCurrentPlayer().getLegalMoves().stream()
                        .filter(move -> move.getMovedPiece().equals(humanMovedPiece)).forEach(move -> {
                    g2D.setColor(Color.GREEN.brighter());
                    int x = move.getDestination().getX();
                    int y = move.getDestination().getY();
                    if(boardOrientation.isOpposite()) {
                        x = calculateFlippedCoordinate(x);
                        y = calculateFlippedCoordinate(y);
                    }
                    x = x * TILE_SIZE + TILE_SIZE / 2;
                    y = y * TILE_SIZE + TILE_SIZE / 2;
                    g2D.fillOval(x - LEGAL_MOVE_HIGHLIGHT_CIRCLE_RADIUS,
                            y - LEGAL_MOVE_HIGHLIGHT_CIRCLE_RADIUS,
                            2 * LEGAL_MOVE_HIGHLIGHT_CIRCLE_RADIUS,
                            2 * LEGAL_MOVE_HIGHLIGHT_CIRCLE_RADIUS);

                });
            }
            //Draws selected piece which is following mouse cursor
            g2D.drawImage(RESOURCE_MANAGER.getPieceImage(humanMovedPiece), humanMovedPieceX, humanMovedPieceY,
                    TILE_SIZE, TILE_SIZE, this);
        }

        private void drawLastMoveHighlight(Graphics2D g2D){
            //This part of code draw arrow which highlights last move
            g2D.setColor(Color.RED);
            g2D.setStroke(new BasicStroke(LAST_MOVE_HIGHLIGHT_ARROW_LINE_WIDTH));
            int arrowStartPointX = lastMove.getMovedPiece().getPosition().getX();
            int arrowStartPointY = lastMove.getMovedPiece().getPosition().getY();
            int arrowEndPointX = lastMove.getDestination().getX();
            int arrowEndPointY = lastMove.getDestination().getY();

            if(boardOrientation.isOpposite()) {
                arrowStartPointX = calculateFlippedCoordinate(arrowStartPointX);
                arrowStartPointY = calculateFlippedCoordinate(arrowStartPointY);
                arrowEndPointX = calculateFlippedCoordinate(arrowEndPointX);
                arrowEndPointY = calculateFlippedCoordinate(arrowEndPointY);
            }

            arrowStartPointX = arrowStartPointX * TILE_SIZE + TILE_SIZE / 2;
            arrowStartPointY = arrowStartPointY * TILE_SIZE + TILE_SIZE / 2;
            arrowEndPointX = arrowEndPointX * TILE_SIZE + TILE_SIZE / 2;
            arrowEndPointY = arrowEndPointY * TILE_SIZE + TILE_SIZE / 2;

            final int dx = arrowEndPointX - arrowStartPointX;
            final int dy = arrowEndPointY - arrowStartPointY;
            int arrowHeadLeftPointX = 0,arrowHeadLeftPointY = 0,arrowHeadRightPointX = 0,arrowHeadRightPointY = 0;

            final double arrowHalfAngle = Math.toRadians(LAST_MOVE_HIGHLIGHT_ARROW_ANGLE / 2);
            //Horizontal move
            if(dy == 0){
                arrowHeadLeftPointX = arrowHeadRightPointX = arrowEndPointX +
                        ((dx > 0) ? -LAST_MOVE_HIGHLIGHT_ARROW_HEIGHT : LAST_MOVE_HIGHLIGHT_ARROW_HEIGHT);
                arrowHeadLeftPointY = (int)(arrowEndPointY - LAST_MOVE_HIGHLIGHT_ARROW_HEIGHT *
                        Math.tan(arrowHalfAngle));
                arrowHeadRightPointY = (int)(arrowEndPointY + LAST_MOVE_HIGHLIGHT_ARROW_HEIGHT *
                        Math.tan(arrowHalfAngle));
            } else if(dx == 0){
                //Vertical move
                arrowHeadLeftPointY = arrowHeadRightPointY = arrowEndPointY +
                        ((dy > 0) ? -LAST_MOVE_HIGHLIGHT_ARROW_HEIGHT : LAST_MOVE_HIGHLIGHT_ARROW_HEIGHT);
                arrowHeadLeftPointX = (int)(arrowEndPointX - LAST_MOVE_HIGHLIGHT_ARROW_HEIGHT *
                        Math.tan(arrowHalfAngle));
                arrowHeadRightPointX = (int)(arrowEndPointX + LAST_MOVE_HIGHLIGHT_ARROW_HEIGHT *
                        Math.tan(arrowHalfAngle));
            } else {
                final double arrowTilt = Math.atan2(dy, dx);

                arrowHeadLeftPointX = (int)(arrowEndPointX - LAST_MOVE_HIGHLIGHT_ARROW_HEIGHT *
                        Math.cos(arrowTilt + arrowHalfAngle));
                arrowHeadLeftPointY = (int)(arrowEndPointY - LAST_MOVE_HIGHLIGHT_ARROW_HEIGHT *
                        Math.sin(arrowTilt + arrowHalfAngle));
                arrowHeadRightPointX = (int)(arrowEndPointX - LAST_MOVE_HIGHLIGHT_ARROW_HEIGHT *
                        Math.cos(arrowTilt - arrowHalfAngle));
                arrowHeadRightPointY = (int)(arrowEndPointY - LAST_MOVE_HIGHLIGHT_ARROW_HEIGHT *
                        Math.sin(arrowTilt - arrowHalfAngle));
            }

            g2D.drawLine(arrowStartPointX, arrowStartPointY, arrowEndPointX, arrowEndPointY);
            g2D.drawLine(arrowHeadLeftPointX, arrowHeadLeftPointY, arrowEndPointX, arrowEndPointY);
            g2D.drawLine(arrowHeadRightPointX, arrowHeadRightPointY, arrowEndPointX, arrowEndPointY);
        }


        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2D = (Graphics2D)g;
            g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //Draws chess field
            drawChessField(g2D);
            //Draws all active pieces
            drawAllActivePieces(g2D);
            //Highlights
            if(humanMovedPiece != null) {
                drawLegalMovesHints(g2D);
            } else if(highlightLastMove && lastMove != null){
                drawLastMoveHighlight(g2D);
            }
        }
    }

    private void checkForMate() {
        if(chessBoard.getCurrentPlayer().isCheckMate()){
            boardPanel.redraw();
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
                lastMove = null;
                moveLog.clear();
            } else {
                System.exit(0);
            }
        }
    }

}
