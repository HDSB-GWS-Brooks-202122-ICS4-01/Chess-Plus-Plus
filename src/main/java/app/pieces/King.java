package app.pieces;

import java.util.ArrayList;

import app.App;
import app.util.Constants.PieceIDs;
import app.util.Constants.SpriteSheetDimensions;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;

/**
 * King class which extends the abstract Piece class.
 * 
 * @author Akil Pathiranage
 * @version 1.0 
 */
public class King extends Piece{


    /**
     * Constructor for king pieces.
     * @param id The id to set to create the king with. 
     */
    public King(Byte id) {
        this.id = id;
        setType();
        this.color = (byte) (id / PieceIDs.COLOR_DIVISOR);
        this.sprite = new ImageView(App.getSpritesheet());
        if (this.color == 0) {
            // if the colour is black put a black pawn
            this.sprite.setViewport(new Rectangle2D(SpriteSheetDimensions.KING_X,
                    SpriteSheetDimensions.BLACK_PIECE_Y, SpriteSheetDimensions.PIECE_WIDTH,
                    SpriteSheetDimensions.PIECE_HEIGHT));
        } else {
            this.sprite.setViewport(new Rectangle2D(SpriteSheetDimensions.KING_X,
                    SpriteSheetDimensions.WHITE_PIECE_Y, SpriteSheetDimensions.PIECE_WIDTH,
                    SpriteSheetDimensions.PIECE_HEIGHT));
        }

        this.sprite.setFitWidth(SpriteSheetDimensions.PIECE_FIT_WIDTH);
        this.sprite.setFitHeight(SpriteSheetDimensions.PIECE_FIT_HEIGHT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[][] getPossibleMoves(byte[][] boardPositions) {
        ArrayList<byte[]> possibleMoves = new ArrayList<byte[]>();
        byte[][] moveList = {
            {(byte) (gridX+1), gridY},
            {(byte) (gridX-1), gridY},
            {gridX, (byte) (gridY-1)},
            {gridX, (byte) (gridY+1)},

            {(byte) (gridX+1), (byte) (gridY+1)},
            {(byte) (gridX+1), (byte) (gridY-1)},
            {(byte) (gridX-1), (byte) (gridY+1)},
            {(byte) (gridX-1), (byte) (gridY-1)}
        };

        for(byte[] move : moveList){
            if(inBoardRange(move) && (boardPositions[move[0]][move[1]] == PieceIDs.EMPTY_CELL || boardPositions[move[0]][move[1]]/PieceIDs.COLOR_DIVISOR != color)){
                if(isNotUnderCheck(boardPositions, move, true)){
                    possibleMoves.add(move);
                }
            }
        }

        byte[][] moves = new byte[possibleMoves.size()][];

        for (int i = 0; i < possibleMoves.size(); i++) {
            moves[i] = new byte[2];

            moves[i][0] = possibleMoves.get(i)[0];
            moves[i][1] = possibleMoves.get(i)[1];
        }
        return moves;
    }


    /**
     * Method for determining if the king can castle to the left.
     * 
     * @param boardPositions The current positions of pieces on the board.
     * @return True if the king can castle to the left, false if not.
     */
    public boolean canCastleLeft(byte[][] boardPositions){
        if(color == PieceIDs.WHITE){
            if(!hasMoved && !App.GAME_PIECES[PieceIDs.WHITE_QUEENS_ROOK].hasMoved && boardPositions[gridX-4][gridY] == PieceIDs.WHITE_QUEENS_ROOK){
                if(boardPositions[gridX-1][gridY] == PieceIDs.EMPTY_CELL && boardPositions[gridX-2][gridY] == PieceIDs.EMPTY_CELL && boardPositions[gridX-3][gridY] == PieceIDs.EMPTY_CELL){
                    byte[] posRightNow = {gridX, gridY};
                    byte[] squareOne = {(byte) (gridX-1), gridY};
                    byte[] squareTwo = {(byte) (gridX-2), gridY};
                    if(isNotUnderCheck(boardPositions, squareOne, true) && isNotUnderCheck(boardPositions, squareTwo, true) && isNotUnderCheck(boardPositions, posRightNow, true)){
                        return true;
                    }
                }
            }
        } else {
            if(!hasMoved && !App.GAME_PIECES[PieceIDs.BLACK_QUEENS_ROOK].hasMoved && boardPositions[gridX-4][gridY] == PieceIDs.BLACK_QUEENS_ROOK){
                if(boardPositions[gridX-1][gridY] == PieceIDs.EMPTY_CELL && boardPositions[gridX-2][gridY] == PieceIDs.EMPTY_CELL && boardPositions[gridX-3][gridY] == PieceIDs.EMPTY_CELL){
                    byte[] posRightNow = {gridX, gridY};
                    byte[] squareOne = {(byte) (gridX-1), gridY};
                    byte[] squareTwo = {(byte) (gridX-2), gridY};
                    if(isNotUnderCheck(boardPositions, squareOne, true) && isNotUnderCheck(boardPositions, squareTwo, true) && isNotUnderCheck(boardPositions, posRightNow, true)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Method for determining if the king can castle to the right.
     * 
     * @param boardPositions The current positions of pieces on the board.
     * @return True if the king can castle to the right, false if not.
     */
    public boolean canCastleRight(byte[][] boardPositions){
        if(color == PieceIDs.WHITE){
            if(!hasMoved && !App.GAME_PIECES[PieceIDs.WHITE_KINGS_ROOK].hasMoved && boardPositions[gridX+3][gridY] == PieceIDs.WHITE_KINGS_ROOK){
                if(boardPositions[gridX+1][gridY] == PieceIDs.EMPTY_CELL && boardPositions[gridX+2][gridY] == PieceIDs.EMPTY_CELL){
                    byte[] posRightNow = {gridX, gridY};
                    byte[] squareOne = {(byte) (gridX+1), gridY};
                    byte[] squareTwo = {(byte) (gridX+2), gridY};
                    if(isNotUnderCheck(boardPositions, squareOne, true) && isNotUnderCheck(boardPositions, squareTwo, true) && isNotUnderCheck(boardPositions, posRightNow, true)){
                        return true;
                    }
                }
            }
        } else {
            if(!hasMoved && !App.GAME_PIECES[PieceIDs.BLACK_KINGS_ROOK].hasMoved  && boardPositions[gridX+3][gridY] == PieceIDs.BLACK_KINGS_ROOK){
                if(boardPositions[gridX+1][gridY] == PieceIDs.EMPTY_CELL && boardPositions[gridX+2][gridY] == PieceIDs.EMPTY_CELL){
                    byte[] posRightNow = {gridX, gridY};
                    byte[] squareOne = {(byte) (gridX+1), gridY};
                    byte[] squareTwo = {(byte) (gridX+2), gridY};
                    if(isNotUnderCheck(boardPositions, squareOne, true) && isNotUnderCheck(boardPositions, squareTwo, true) && isNotUnderCheck(boardPositions, posRightNow, true)){
                        return true;
                    }
                }
            }
        }
        return false;
    } 

    /**
     * Checks to see if this position is in the board.
     * @param pos
     * @return true if the position is in the board, false if not.
     */
    public boolean inBoardRange(byte[] pos){
        return (pos[0] > -1 && pos[0] < 8) && (pos[1] > -1 && pos[1] < 8);
    }
    
}
