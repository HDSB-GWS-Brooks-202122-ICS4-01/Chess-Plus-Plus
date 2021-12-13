import java.util.ArrayList;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;

/**
 * Pawn class which extends the abstract Piece class.
 */
public class Pawn extends Piece {
    int passant;

    public Pawn(Byte id) {
        this.id = id;
        setType();

        // 0 if the color is black, 1 if the color isn't black
        this.color = (byte) (id / 16);
        
        this.sprite = new ImageView(App.getSpritesheet());
        if (this.color == 0) {
            // if the colour is black put a black pawn
            this.sprite.setViewport(new Rectangle2D(Constants.SpriteSheetDimensions.PAWN_X,
                    Constants.SpriteSheetDimensions.BLACK_PIECE_Y, Constants.SpriteSheetDimensions.PIECE_WIDTH,
                    Constants.SpriteSheetDimensions.PIECE_HEIGHT));
        } else {
            this.sprite.setViewport(new Rectangle2D(Constants.SpriteSheetDimensions.PAWN_X,
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

        if (color == Constants.pieceIDs.BLACK) {
            // if the pawn can move forward
            if (boardPositions[gridX][gridY + 1] == Constants.pieceIDs.EMPTY_CELL && gridY + 1 < 8) {
                byte[] forwardMove = {gridX, (byte) (gridY+1)};
                if(isNotUnderCheck(boardPositions, forwardMove, false)){
                    possibleMoves.add(forwardMove);
                }
                // if the pawn is at its starting position on the board.
                if (gridX == Constants.boardData.INITIAL_POSITIONS[id][0] && gridY == Constants.boardData.INITIAL_POSITIONS[this.id][1] && gridY + 2 < 8 && boardPositions[gridX][gridY + 2] == Constants.pieceIDs.EMPTY_CELL) {
                    byte[] doubleForwardMove = { gridX, (byte) (gridY + 2) };
                    if(isNotUnderCheck(boardPositions, doubleForwardMove, false)){
                        possibleMoves.add(doubleForwardMove);
                        passant = App.MOVE_COUNT;
                    }      
                }

            }


            if(gridX-1 > -1 && gridY + 1 < 8 && (byte) boardPositions[gridX-1][gridY+1]/16 == Constants.pieceIDs.WHITE){
                byte[] attackLeft = {(byte) (gridX-1), (byte) (gridY+1)};
                if(isNotUnderCheck(boardPositions, attackLeft, false)){
                    possibleMoves.add(attackLeft);
                }
            }
            if(gridX+1 <8 && gridY + 1 < 8 && (byte) boardPositions[gridX+1][gridY+1]/16 == Constants.pieceIDs.WHITE){
                byte[] attackRight = {(byte) (gridX+1), (byte) (gridY+1)};
                if(isNotUnderCheck(boardPositions, attackRight, false)){
                    possibleMoves.add(attackRight);
                }
            }

        } else {
            if (boardPositions[gridX][gridY - 1] == Constants.pieceIDs.EMPTY_CELL && gridY - 1 > -1) {
                byte[] forwardMove = { gridX, (byte) (gridY - 1) };
                if(isNotUnderCheck(boardPositions, forwardMove, false)){
                    possibleMoves.add(forwardMove);
                }
                // if the pawn is at its starting position on the board.
                if (gridX == Constants.boardData.INITIAL_POSITIONS[id][0] && gridY == Constants.boardData.INITIAL_POSITIONS[this.id][1] && gridY - 2 > -1 && boardPositions[gridX][gridY - 2] == Constants.pieceIDs.EMPTY_CELL) {
                    byte[] doubleForwardMove = { gridX, (byte) (gridY - 2) };
                    if(isNotUnderCheck(boardPositions, doubleForwardMove, false)){
                        possibleMoves.add(doubleForwardMove);
                        passant = App.MOVE_COUNT;
                    }      
                }
            }

   
            if(gridX-1 > -1 && gridY - 1 > -1 && (byte) boardPositions[gridX-1][gridY-1] != Constants.pieceIDs.EMPTY_CELL && boardPositions[gridX-1][gridY-1]/16 == Constants.pieceIDs.BLACK){
                byte[] attackLeft = {(byte) (gridX-1), (byte) (gridY-1)};
                if(isNotUnderCheck(boardPositions, attackLeft, false)){
                    possibleMoves.add(attackLeft);
                }
            }
            if(gridX+1 <8 && gridY - 1 > -1 && (byte) boardPositions[gridX+1][gridY-1] != Constants.pieceIDs.EMPTY_CELL && boardPositions[gridX+1][gridY-1]/16 == Constants.pieceIDs.BLACK){
                byte[] attackRight = {(byte) (gridX+1), (byte) (gridY-1)};
                if(isNotUnderCheck(boardPositions, attackRight, false)){
                    possibleMoves.add(attackRight);
                }
            }

        }





        byte[][] moves = new byte[possibleMoves.size()][];

        for (int i = 0; i < possibleMoves.size(); i++) {
            moves[i] = new byte[2];

            moves[i][0] = possibleMoves.get(i)[0];
            moves[i][1] = possibleMoves.get(i)[1];
        }

        // Object[] moves = ;
        return moves;
    }

}
