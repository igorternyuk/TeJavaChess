package com.techess.gui;

import com.techess.engine.moves.MoveLog;
import com.techess.engine.pieces.Piece;
import com.techess.engine.pieces.PieceType;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.*;

import static com.techess.engine.board.Board.BOARD_SIZE;

/**
 * Created by igor on 03.03.18.
 */

public class TakenPiecesPanel extends JPanel {

    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Color PANEL_COLOR = Color.decode("0xFDF5E6");
    private static final ResourceManager RESOURCE_MANAGER = ResourceManager.getInstance();
    public static final int PANEL_WIDTH = 64;
    public static final int PANEL_HEIGHT = 512;
    private static final Dimension PANEL_DIMENSION = new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
    private JPanel northPanel, southPanel;
    public TakenPiecesPanel(){
        super(new BorderLayout());
        this.setBackground(PANEL_COLOR);
        this.setBorder(PANEL_BORDER);
        this.setPreferredSize(PANEL_DIMENSION);
        this.northPanel = new JPanel(new GridLayout(BOARD_SIZE, PieceType.values().length));
        this.northPanel.setBackground(PANEL_COLOR);
        this.northPanel.setVisible(true);
        this.southPanel = new JPanel(new GridLayout(BOARD_SIZE, PieceType.values().length));
        this.southPanel.setBackground(PANEL_COLOR);
        this.southPanel.setVisible(true);
        this.add(this.northPanel, BorderLayout.NORTH);
        this.add(this.southPanel, BorderLayout.SOUTH);
        this.setVisible(true);
    }

    public void clear()
    {
        this.southPanel.removeAll();
        this.northPanel.removeAll();
    }

    public void update(final MoveLog moveLog){
        this.southPanel.removeAll();
        this.northPanel.removeAll();
        final java.util.List<Piece> whiteTakenPieces = new ArrayList<>();
        final java.util.List<Piece> blackTakenPieces = new ArrayList<>();
        moveLog.getMoves().stream().filter(move -> move.isCapturingMove()).forEach(move ->{
            final Piece capturedPiece = move.getCapturedPiece();
            if(capturedPiece.getAlliance().isWhite()){
                whiteTakenPieces.add(capturedPiece);
            } else {
                blackTakenPieces.add(capturedPiece);
            }
        });

        Collections.sort(whiteTakenPieces, Comparator.comparing(Piece::getValue));

        Collections.sort(blackTakenPieces, Comparator.comparing(Piece::getValue));

        whiteTakenPieces.forEach(piece -> {
           northPanel.add(new JLabel(RESOURCE_MANAGER.getScaledPieceIcon(piece)));
        });

        blackTakenPieces.forEach(piece -> {
            southPanel.add(new JLabel(RESOURCE_MANAGER.getScaledPieceIcon(piece)));
        });

        validate();
        repaint();
    }
}
