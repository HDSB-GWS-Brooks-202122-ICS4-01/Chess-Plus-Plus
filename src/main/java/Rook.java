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
        if(this.id == Constants.pieceIDs.BLACK_PROMOTED_ROOK){
            this.color = 0;
        }else if(this.id == Constants.pieceIDs.WHITE_PROMOTED_ROOK){
            this.color = 1;
        }else {
            this.color = (byte) (id / Constants.pieceIDs.COLOR_DIVISOR);
        }
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
        this.teamPromotedRook = (this.color == Constants.pieceIDs.BLACK) ? Constants.pieceIDs.BLACK_PROMOTED_ROOK : Constants.pieceIDs.WHITE_PROMOTED_ROOK;
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

}
