import java.util.ArrayList;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;

/**
 * Rook class which extends the abstract Piece class.
 * @author Akil Pathiranage
 * @version 1.0 
 */
public class Rook extends Piece {

    /**
     * Cosntructor for rook class. 
     * 
     * @param id value of which rook this rook is. 
     */
    public Rook(Byte id) {
        this.id = id;
        setType();
        this.color = (byte) (id / 16);
        this.sprite = new ImageView(App.getSpritesheet());
        if (this.color == 0) {
            // if the colour is black put a black pawn
            this.sprite.setViewport(new Rectangle2D(Constants.SpriteSheetDimensions.ROOK_X,
                    Constants.SpriteSheetDimensions.BLACK_PIECE_Y, Constants.SpriteSheetDimensions.PIECE_WIDTH,
                    Constants.SpriteSheetDimensions.PIECE_HEIGHT));
        } else {
            this.sprite.setViewport(new Rectangle2D(Constants.SpriteSheetDimensions.ROOK_X,
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
        byte[] currentLoc = {gridX, gridY};
        possibleMoves = upMoves(possibleMoves, boardPositions, currentLoc);
        possibleMoves = downMoves(possibleMoves, boardPositions, currentLoc);
        possibleMoves = leftMoves(possibleMoves, boardPositions, currentLoc);
        possibleMoves = rightMoves(possibleMoves, boardPositions, currentLoc);

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
     * Recursive method for getting the possible moves for a rook moving up. Adds to an arraylist of moves
     * and returns the arraylist when it has added all possible moves in this direction. 
     * 
     * @param possibleMoves Array list of byte arrays to add to.
     * @param boardPositions Current positions of the whole board.
     * @param currentLoc Positions to check from.
     * @return An array list of byte arrays containing all the added moves. 
     */
    public ArrayList<byte[]> upMoves(ArrayList<byte[]> possibleMoves, byte[][] boardPositions, byte[] currentLoc) {
        if (currentLoc[1] - 1 > -1) {
            byte[] possibleMove = { currentLoc[0], (byte) (currentLoc[1] - 1) };
            byte upSquare = boardPositions[possibleMove[0]][possibleMove[1]];

            if (upSquare == Constants.pieceIDs.EMPTY_CELL) {
                if(isNotUnderCheck(boardPositions, possibleMove, false)){
                    possibleMoves.add(possibleMove);
                }
                return upMoves(possibleMoves, boardPositions, possibleMove);
            } else {
                if (upSquare / 16 != color) {
                    if(isNotUnderCheck(boardPositions, possibleMove, false)){
                        possibleMoves.add(possibleMove);
                    }
                }
                return possibleMoves;
            }
        } else {
            return possibleMoves;
        }
    }


    /**
     * Recursive method for getting the possible moves for a rook moving down. Adds to an arraylist of moves
     * and returns the arraylist when it has added all possible moves in this direction. 
     * 
     * @param possibleMoves Array list of byte arrays to add to.
     * @param boardPositions Current positions of the whole board.
     * @param currentLoc Positions to check from.
     * @return An array list of byte arrays containing all the added moves. 
     */
    public ArrayList<byte[]> downMoves(ArrayList<byte[]> possibleMoves, byte[][] boardPositions, byte[] currentLoc) {
        if (currentLoc[1] + 1 < 8) {
            byte[] possibleMove = { currentLoc[0], (byte) (currentLoc[1] + 1) };
            byte upSquare = boardPositions[possibleMove[0]][possibleMove[1]];

            if (upSquare == Constants.pieceIDs.EMPTY_CELL) {
                if(isNotUnderCheck(boardPositions, possibleMove, false)){
                    possibleMoves.add(possibleMove);
                }
                return downMoves(possibleMoves, boardPositions, possibleMove);
            } else {
                if (upSquare / 16 != color) {
                    if(isNotUnderCheck(boardPositions, possibleMove, false)){
                        possibleMoves.add(possibleMove);
                    }
                }
                return possibleMoves;
            }
        } else {
            return possibleMoves;
        }
    }

    /**
     * Recursive method for getting the possible moves for a rook moving left. Adds to an arraylist of moves
     * and returns the arraylist when it has added all possible moves in this direction. 
     * 
     * @param possibleMoves Array list of byte arrays to add to.
     * @param boardPositions Current positions of the whole board.
     * @param currentLoc Positions to check from.
     * @return An array list of byte arrays containing all the added moves. 
     */
    public ArrayList<byte[]> leftMoves(ArrayList<byte[]> possibleMoves, byte[][] boardPositions, byte[] currentLoc) {
        if (currentLoc[0] - 1 > -1) {
            byte[] possibleMove = { (byte) (currentLoc[0] - 1), currentLoc[1]};
            byte upSquare = boardPositions[possibleMove[0]][possibleMove[1]];

            if (upSquare == Constants.pieceIDs.EMPTY_CELL) {
                if(isNotUnderCheck(boardPositions, possibleMove, false)){
                    possibleMoves.add(possibleMove);
                }
                return leftMoves(possibleMoves, boardPositions, possibleMove);
            } else {
                if (upSquare / 16 != color) {
                    if(isNotUnderCheck(boardPositions, possibleMove, false)){
                        possibleMoves.add(possibleMove);
                    }
                }
                return possibleMoves;
            }
        } else {
            return possibleMoves;
        }
    }

    /**
     * Recursive method for getting the possible moves for a rook moving right. Adds to an arraylist of moves
     * and returns the arraylist when it has added all possible moves in this direction. 
     * 
     * @param possibleMoves Array list of byte arrays to add to.
     * @param boardPositions Current positions of the whole board.
     * @param currentLoc Positions to check from.
     * @return An array list of byte arrays containing all the added moves. 
     */
    public ArrayList<byte[]> rightMoves(ArrayList<byte[]> possibleMoves, byte[][] boardPositions, byte[] currentLoc) {
        if (currentLoc[0] + 1 < 8) {
            byte[] possibleMove = { (byte) (currentLoc[0] + 1), currentLoc[1]};
            byte upSquare = boardPositions[possibleMove[0]][possibleMove[1]];

            if (upSquare == Constants.pieceIDs.EMPTY_CELL) {
                if(isNotUnderCheck(boardPositions, possibleMove, false)){
                    possibleMoves.add(possibleMove);
                }
                return rightMoves(possibleMoves, boardPositions, possibleMove);
            } else {
                if (upSquare / 16 != color) {
                    if(isNotUnderCheck(boardPositions, possibleMove, false)){
                        possibleMoves.add(possibleMove);
                    }
                }
                return possibleMoves;
            }
        } else {
            return possibleMoves;
        }
    }

}
