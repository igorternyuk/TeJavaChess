package com.igorternyuk.gui;

import com.igorternyuk.engine.board.Board;
import com.igorternyuk.engine.moves.Move;
import com.igorternyuk.engine.moves.MoveLog;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by igor on 03.03.18.
 */
public class GameHistoryPanel extends JPanel{
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Color PANEL_COLOR = Color.decode("0xFDF5E6");
    public static final int PANEL_WIDTH = 140;
    public static final int PANEL_HEIGHT = 512;
    private static final Dimension PANEL_DIMENSION = new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
    private static final int TABLE_ROW_HEIGHT = 15;
    private static final int MOVE_NUMBER_COLUMN_WIDTH = 50;
    private final DataModel dataModel;
    private final JTable table;
    private final JScrollPane scrollPane;

    public GameHistoryPanel(){
        super(new BorderLayout());
        this.setBackground(PANEL_COLOR);
        this.setBorder(PANEL_BORDER);
        this.setPreferredSize(PANEL_DIMENSION);
        this.dataModel = new DataModel();
        this.table = new JTable(this.dataModel);
        this.table.setRowHeight(TABLE_ROW_HEIGHT);
        this.table.getColumnModel().getColumn(DataModel.MOVE_NUMBER_COLUMN).setPreferredWidth(MOVE_NUMBER_COLUMN_WIDTH);
        this.scrollPane = new JScrollPane(this.table);
        this.scrollPane.setColumnHeaderView(this.table.getTableHeader());
        this.add(this.scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    public void clear(){
        this.dataModel.clear();
        this.table.setVisible(false);
        this.table.setVisible(true);
        this.validate();
        this.repaint();
    }

    public void update(final Board chessBoard, final MoveLog moveLog){
        this.dataModel.clear();
        final List<Move> moves = moveLog.getMoves();
        int currentRow = 0;
        for(final Move currentMove: moves){
            final String moveText = currentMove.toString();
            if(currentMove.getMovedPiece().getAlliance().isWhite()){
                int number = currentRow + 1;
                this.dataModel.setValueAt(number, currentRow, DataModel.MOVE_NUMBER_COLUMN);
                this.dataModel.setValueAt(moveText, currentRow, DataModel.WHITE_MOVE_COLUMN);
            } else {
                this.dataModel.setValueAt(moveText, currentRow, DataModel.BLACK_MOVE_COLUMN);
                ++currentRow;
            }
        }

       // System.out.println("Current row = " + currentRow);

        if(!moves.isEmpty()){
            final Move lastMove = moves.get(moves.size() - 1);
            if(lastMove.getMovedPiece().getAlliance().isWhite()){
                this.dataModel.setValueAt(lastMove.toString() + calculateCheckAndMateHash(chessBoard),
                        currentRow, DataModel.WHITE_MOVE_COLUMN);
            } else {
                this.dataModel.setValueAt(lastMove.toString() + calculateCheckAndMateHash(chessBoard),
                        currentRow - 1, DataModel.BLACK_MOVE_COLUMN);
            }
        }

        final JScrollBar verticalScrollBar = this.scrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        this.table.setVisible(false);
        this.table.setVisible(true);
        this.validate();
        this.repaint();
    }

    private String calculateCheckAndMateHash(final Board board){
        if(board.getCurrentPlayer().isCheckMate()){
            return "#";
        } else if(board.getCurrentPlayer().isUnderCheck()){
            return "+";
        }
        return "";
    }

    private class MoveRecord{
        private int moveNumber;
        private String whiteMove, blackMove;

        public MoveRecord() {
            this.moveNumber = 0;
            this.whiteMove = "";
            this.blackMove = "";
        }

        public MoveRecord(final int moveNumber, final String whiteMove, final String blackMove) {
            this.moveNumber = moveNumber;
            this.whiteMove = whiteMove;
            this.blackMove = blackMove;
        }

        public int getMoveNumber() {
            return moveNumber;
        }

        public void setMoveNumber(int moveNumber) {
            this.moveNumber = moveNumber;
        }

        public String getWhiteMove() {
            return whiteMove;
        }

        public void setWhiteMove(String whiteMove) {
            this.whiteMove = whiteMove;
        }

        public String getBlackMove() {
            return blackMove;
        }

        public void setBlackMove(String blackMove) {
            this.blackMove = blackMove;
        }
    }

    private class DataModel /*extends DefaultTableModel */implements TableModel{
        private static final int MOVE_NUMBER_COLUMN = 0;
        private static final int WHITE_MOVE_COLUMN = 1;
        private static final int BLACK_MOVE_COLUMN = 2;
        private List<MoveRecord> records;
        private String[] HEADERS = {"№", "White", "Black"};

        public DataModel(){
            this.records = new ArrayList<>();
        }

        public void clear(){
            this.records.clear();
            //setRowCount(0);
        }
        @Override
        public int getRowCount() {
            return this.records == null ? 0 : this.records.size();
        }

        @Override
        public int getColumnCount() {
            return HEADERS.length;
        }

        @Override
        public String getColumnName(final int columnIndex) {
            if(columnIndex >= 0 && columnIndex < HEADERS.length)
                return HEADERS[columnIndex];
            else
                return "";
        }

        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            if(records.isEmpty()){
                return Object.class;
            } else {
                return getValueAt(0, columnIndex).getClass();
            }
        }

        @Override
        public boolean isCellEditable(final int rowIndex, final int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            validateIndexes(rowIndex, columnIndex);
            final MoveRecord record = this.records.get(rowIndex);
            String result = null;

            switch (columnIndex){
                case MOVE_NUMBER_COLUMN:
                    result = String.valueOf(record.getMoveNumber());
                    break;
                case WHITE_MOVE_COLUMN:
                    result = record.getWhiteMove();
                    break;
                case BLACK_MOVE_COLUMN:
                    result = record.getBlackMove();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid column index");

            }
            return result;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            final MoveRecord currentRecord;
           // System.out.println("Setting new value at row = " + rowIndex);
           // System.out.println("this.getRowCount() = " + this.getRowCount());
            if(rowIndex > this.getRowCount() - 1){
           //     System.out.println("Adding new row");
                currentRecord = new MoveRecord();
                this.records.add(currentRecord);
            }
            if(columnIndex < 0 || columnIndex > HEADERS.length - 1){
                throw new IllegalArgumentException("Invalid column index");
            }
            switch (columnIndex){
                case MOVE_NUMBER_COLUMN:
                    this.records.get(rowIndex).setMoveNumber((int)aValue);
                    break;
                case WHITE_MOVE_COLUMN:
                    this.records.get(rowIndex).setWhiteMove((String)aValue);
                    break;
                case BLACK_MOVE_COLUMN:
                    this.records.get(rowIndex).setBlackMove((String)aValue);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid column index");

            }
        }

        @Override
        public void addTableModelListener(TableModelListener l) {

        }

        @Override
        public void removeTableModelListener(TableModelListener l) {

        }

        private void validateIndexes(final int rowIndex, final int columnIndex){
            if(rowIndex < 0 || rowIndex > this.records.size() - 1){
                throw new IllegalArgumentException("Invalid row index");
            }
            if(columnIndex < 0 || columnIndex > HEADERS.length - 1){
                throw new IllegalArgumentException("Invalid column index");
            }
        }
    }
}


