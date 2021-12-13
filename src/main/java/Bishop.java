import java.util.ArrayList;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;

/**
 * Bishop class which extends the abstract Piece class. 
 */
public class Bishop extends Piece{

    public Bishop(Byte id) {
        this.id = id;
        setType();
        this.color = (byte) (id / 16);
        this.sprite = new ImageView(App.getSpritesheet());
        if (this.color == 0) {
            // if the colour is black put a black pawn
            this.sprite.setViewport(new Rectangle2D(Constants.SpriteSheetDimensions.BISHOP_X,
                    Constants.SpriteSheetDimensions.BLACK_PIECE_Y, Constants.SpriteSheetDimensions.PIECE_WIDTH,
                    Constants.SpriteSheetDimensions.PIECE_HEIGHT));
        } else {
            this.sprite.setViewport(new Rectangle2D(Constants.SpriteSheetDimensions.BISHOP_X,
                    Constants.SpriteSheetDimensions.WHITE_PIECE_Y, Constants.SpriteSheetDimensions.PIECE_WIDTH,
                    Constants.SpriteSheetDimensions.PIECE_HEIGHT));
        }

        this.sprite.setFitWidth(Constants.SpriteSheetDimensions.PIECE_FIT_WIDTH);
        this.sprite.setFitHeight(Constants.SpriteSheetDimensions.PIECE_FIT_HEIGHT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[][] getPossibleMoves(byte[][] boardPositions) {
        ArrayList<byte[]> possibleMoves = new ArrayList<byte[]>();
        byte[] piecePos = {gridX, gridY};


        possibleMoves = upRightDiagonalMoves(possibleMoves, boardPositions, piecePos);
        possibleMoves = upLeftDiagonalMoves(possibleMoves, boardPositions, piecePos);
        possibleMoves = downRightDiagonalMoves(possibleMoves, boardPositions, piecePos);
        possibleMoves  = downLeftDiagonalMoves(possibleMoves, boardPositions, piecePos);


        byte[][] moves = new byte[possibleMoves.size()][];

        for (int i = 0; i < possibleMoves.size(); i++) {
            moves[i] = new byte[2];

            moves[i][0] = possibleMoves.get(i)[0];
            moves[i][1] = possibleMoves.get(i)[1];
        }

        // Object[] moves = ;
        return moves;
    }


    /**
     * Recursive method for getting the possible moves for a bishop diagonally up and to the right. Adds to an arraylist of moves
     * and returns the arraylist when it has added all possible moves in this direction. 
     * 
     * @param possibleMoves Array list of byte arrays to add to.
     * @param boardPositions Current positions of the whole board.
     * @param currentLoc Positions to check from.
     * @return An array list of byte arrays containing all the added moves. 
     */
    private ArrayList<byte[]> upRightDiagonalMoves(ArrayList<byte[]> possibleMoves, byte[][] boardPositions, byte[] currentLoc){
        if(currentLoc[0]+1 < 8 && currentLoc[1] -1 > -1){
            byte upRightSquare = boardPositions[currentLoc[0]+1][currentLoc[1]-1];
            byte[] possibleMove = {(byte) (currentLoc[0]+1), (byte) (currentLoc[1] -1)};

            if(upRightSquare == Constants.pieceIDs.EMPTY_CELL){
                //if the square one up and to the right is empty, add that square and repeat process for  that square
                if(isNotUnderCheck(boardPositions, possibleMove, false)){
                    possibleMoves.add(possibleMove);
                }
                return upRightDiagonalMoves(possibleMoves, boardPositions, possibleMove);
            } else {
                if(upRightSquare/16 != color){
                    if(isNotUnderCheck(boardPositions, possibleMove, false)){
                        possibleMoves.add(possibleMove);
                    }
                }
                return possibleMoves;
            }

        } else{
            return possibleMoves;
        }

    }

    /**
     * Recursive method for getting the possible moves for a bishop diagonally up and to the left. Adds to an arraylist of moves
     * and returns the arraylist when it has added all possible moves in this direction. 
     * 
     * @param possibleMoves Array list of byte arrays to add to.
     * @param boardPositions Current positions of the whole board.
     * @param currentLoc Positions to check from.
     * @return An array list of byte arrays containing all the added moves. 
     */
    private ArrayList<byte[]> upLeftDiagonalMoves(ArrayList<byte[]> possibleMoves,  byte[][] boardPositions, byte[] currentLoc){
        if(currentLoc[0]-1 > -1 && currentLoc[1] -1 > -1){
            byte upRightSquare = boardPositions[currentLoc[0]-1][currentLoc[1]-1];
            byte[] possibleMove = {(byte) (currentLoc[0]-1), (byte) (currentLoc[1] -1)};

            if(upRightSquare == Constants.pieceIDs.EMPTY_CELL){
                //if the square one up and to the right is empty, add that square and repeat process for  that square
                if(isNotUnderCheck(boardPositions, possibleMove, false)){
                    possibleMoves.add(possibleMove);
                }
                return upLeftDiagonalMoves(possibleMoves, boardPositions, possibleMove);
            } else {
                if(upRightSquare/16 != color){
                    if(isNotUnderCheck(boardPositions, possibleMove, false)){
                        possibleMoves.add(possibleMove);
                    }
                }
                return possibleMoves;
            }

        } else{
            return possibleMoves;
        }
    }

    /**
     * Recursive method for getting the possible moves for a bishop diagonally down and to the right. Adds to an arraylist of moves
     * and returns the arraylist when it has added all possible moves in this direction. 
     * 
     * @param possibleMoves Array list of byte arrays to add to.
     * @param boardPositions Current positions of the whole board.
     * @param currentLoc Positions to check from.
     * @return An array list of byte arrays containing all the added moves. 
     */
    private ArrayList<byte[]> downRightDiagonalMoves(ArrayList<byte[]> possibleMoves,  byte[][] boardPositions, byte[] currentLoc){
        if(currentLoc[0]+1 < 8 && currentLoc[1] +1 < 8){
            byte upRightSquare = boardPositions[currentLoc[0]+1][currentLoc[1]+1];
            byte[] possibleMove = {(byte) (currentLoc[0]+1), (byte) (currentLoc[1] +1)};

            if(upRightSquare == Constants.pieceIDs.EMPTY_CELL){
                //if the square one up and to the right is empty, add that square and repeat process for  that square
                if(isNotUnderCheck(boardPositions, possibleMove, false)){
                    possibleMoves.add(possibleMove);
                }
                return downRightDiagonalMoves(possibleMoves, boardPositions, possibleMove);
            } else {
                if(upRightSquare/16 != color){
                    if(isNotUnderCheck(boardPositions, possibleMove, false)){
                        possibleMoves.add(possibleMove);
                    }
                }
                return possibleMoves;
            }

        } else{
            return possibleMoves;
        }
    }

    /**
     * Recursive method for getting the possible moves for a bishop diagonally down and to the left. Adds to an arraylist of moves
     * and returns the arraylist when it has added all possible moves in this direction. 
     * 
     * @param possibleMoves Array list of byte arrays to add to.
     * @param boardPositions Current positions of the whole board.
     * @param currentLoc Positions to check from.
     * @return An array list of byte arrays containing all the added moves. 
     */
    private ArrayList<byte[]> downLeftDiagonalMoves(ArrayList<byte[]> possibleMoves,  byte[][] boardPositions, byte[] currentLoc){
        if(currentLoc[0]-1 > -1 && currentLoc[1] +1 < 8){
            byte upRightSquare = boardPositions[currentLoc[0]-1][currentLoc[1]+1];
            byte[] possibleMove = {(byte) (currentLoc[0]-1), (byte) (currentLoc[1] +1)};

            if(upRightSquare == Constants.pieceIDs.EMPTY_CELL){
                //if the square one up and to the right is empty, add that square and repeat process for  that square
                if(isNotUnderCheck(boardPositions, possibleMove, false)){
                    possibleMoves.add(possibleMove);
                }
                return downLeftDiagonalMoves(possibleMoves, boardPositions, possibleMove);
            } else {
                if(upRightSquare/16 != color){
                    if(isNotUnderCheck(boardPositions, possibleMove, false)){
                        possibleMoves.add(possibleMove);
                    }
                }
                return possibleMoves;
            }

        } else{
            return possibleMoves;
        }

    }
    
}
