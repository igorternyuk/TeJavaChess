package com.techess.engine;

import com.techess.engine.board.Board;
import com.techess.engine.board.GameType;
import com.techess.engine.board.Position;
import com.techess.engine.board.Tile;
import com.techess.engine.moves.Move;
import com.techess.engine.moves.MoveLog;
import com.techess.engine.moves.MoveTransition;

import java.util.Map;

/**
 * Created by igor on 07.03.18.
 */
public class Game {
    private static final int FIFTY_MOVES_RULE_LIMIT = 50;
    private static final int REPETITION_THRESHOLD = 3;
    private Board chessBoard;
    private MoveLog moveLog = new MoveLog();
    int numberOfWhiteMoves = 0;
    int numberOfBlackMoves = 0;
    private int lastCapturingMoveNumber = 0;
    private int lastPawnMoveNumber = 0;
    private GameStatus gameStatus = GameStatus.RUNNING;
    private boolean wereThereAnyCaptures = false;

    public Game(final GameType gameType){
        if(gameType.isClassicChess()){
            this.chessBoard = Board.createStandardBoard();
        } else {
            this.chessBoard = Board.createBoardForChess960();
        }
        this.moveLog.clear();
    }

    public Board getChessBoard() {
        return this.chessBoard;
    }

    public MoveLog getMoveLog() {
        return this.moveLog;
    }

    public GameStatus getGameStatus() {
        return this.gameStatus;
    }

    public boolean isGameOver(){
        return this.gameStatus.isGameOver();
    }

    public void prepareNewGame(final GameType gameType){
        if(gameType.isClassicChess()){
            this.chessBoard = Board.createStandardBoard();
        } else {
            this.chessBoard = Board.createBoardForChess960();
        }
        this.moveLog.clear();
        lastCapturingMoveNumber = 0;
        lastPawnMoveNumber = 0;
        wereThereAnyCaptures = false;
        gameStatus = GameStatus.RUNNING;
    }

    public boolean tryToMakeTheMove(final Move move){
        final MoveTransition moveTransition = this.chessBoard.getCurrentPlayer().makeMove(move);
        final Alliance currentPlayerAlliance = this.chessBoard.getCurrentPlayer().getAlliance();
        if(moveTransition.getMoveStatus().isDone()){
            if(currentPlayerAlliance.isWhite()){
                ++numberOfWhiteMoves;
                //System.out.println("numberOfWhiteMoves = " + numberOfWhiteMoves);
            } else {
                ++numberOfBlackMoves;
                //System.out.println("numberOfBlackMoves = " + numberOfBlackMoves);
            }
            if(move.isPawnMove()){
                if(currentPlayerAlliance.isWhite()){
                    lastPawnMoveNumber = numberOfWhiteMoves;
                } else {
                    lastPawnMoveNumber = numberOfBlackMoves;
                }
                //System.out.println("lastPawnMoveNumber = " + lastPawnMoveNumber);
            }
            if(move.isCapturingMove()){
                wereThereAnyCaptures = true;
                if(currentPlayerAlliance.isWhite()){
                    lastCapturingMoveNumber = numberOfWhiteMoves;
                } else {
                    lastCapturingMoveNumber = numberOfBlackMoves;
                }
                //System.out.println("lastCapturingMoveNumber = " + lastCapturingMoveNumber);
            }
            this.chessBoard = moveTransition.getTransitedBoard();
            moveLog.addMove(move);
            updateGameStatus();
            return true;
        }
        return false;
    }

    private void updateGameStatus(){
        final Alliance currentPlayerAlliance = this.chessBoard.getCurrentPlayer().getAlliance();
        if(this.chessBoard.getCurrentPlayer().isCheckMate()) {
            if(currentPlayerAlliance.isWhite()){
                this.gameStatus = GameStatus.BLACK_WON;
            } else {
                this.gameStatus = GameStatus.WHITE_WON;
            }
        } else if(this.chessBoard.getCurrentPlayer().isInStalemate()){
            this.gameStatus = GameStatus.DRAW_BY_STALEMATE;
        } else if(this.chessBoard.isInsufficientMaterial()){
            this.gameStatus = GameStatus.DRAW_BY_INSUFFICIENT_MATERIAL;
        } else if(checkThresholdRepetition()){
            this.gameStatus = GameStatus.DRAW_BY_THRESHOLD_REPETITION;
        } else if(checkFiftyMovesRule()){
            this.gameStatus = GameStatus.DRAW_BY_FIFTY_MOVES_RULE;
        }
    }

    private boolean checkFiftyMovesRule(){
        int numberOfMovesSinceLastPawnMove = 0, numberOfMovesSinceLastCapture = 0;
        if(chessBoard.getCurrentPlayer().getAlliance().isWhite()){
            numberOfMovesSinceLastPawnMove = numberOfBlackMoves - lastPawnMoveNumber;
            if(wereThereAnyCaptures) {
                numberOfMovesSinceLastCapture = numberOfBlackMoves - lastCapturingMoveNumber;
            }
        } else {
            numberOfMovesSinceLastPawnMove = numberOfWhiteMoves - lastPawnMoveNumber;
            if(wereThereAnyCaptures) {
                numberOfMovesSinceLastCapture = numberOfWhiteMoves - lastCapturingMoveNumber;
            }
        }
        boolean numberOfMovesSinceLastPawnMoveGreaterThen50 = numberOfMovesSinceLastPawnMove >= FIFTY_MOVES_RULE_LIMIT;
        boolean numberOfMovesSinceLastCaptureGreaterThern50 = wereThereAnyCaptures &&
                numberOfMovesSinceLastCapture >= FIFTY_MOVES_RULE_LIMIT;
        boolean result = numberOfMovesSinceLastPawnMoveGreaterThen50 ||
                numberOfMovesSinceLastCaptureGreaterThern50;
        return result;
    }

    private boolean checkThresholdRepetition(){
        Map<Position,Tile> currentGameBoard = this.chessBoard.getGameBoard();
        long occurenceCount = this.moveLog.getMoves().stream().filter( move ->
                move.getBoard().getGameBoard().equals(currentGameBoard)).count();
        System.out.println("Current position occurence count = " + occurenceCount);
        return (occurenceCount + 1) >= REPETITION_THRESHOLD;
    }

    public int getTotalMovesNumber(){
        return  Math.max(numberOfWhiteMoves, numberOfBlackMoves);
    }
}
