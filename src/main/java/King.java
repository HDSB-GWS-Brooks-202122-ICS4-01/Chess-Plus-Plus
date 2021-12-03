import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;

/**
 * King class which extends the abstract Piece class. 
 */
public class King extends Piece{

    public King(Byte id) {
        this.id = id;
        this.color = (byte) (id / 16);
        this.sprite = new ImageView(App.getSpritesheet());
        if (this.color == 0) {
            // if the colour is black put a black pawn
            this.sprite.setViewport(new Rectangle2D(Constants.SpriteSheetDimensions.KING_X,
                    Constants.SpriteSheetDimensions.BLACK_PIECE_Y, Constants.SpriteSheetDimensions.PIECE_WIDTH,
                    Constants.SpriteSheetDimensions.PIECE_HEIGHT));
        } else {
            this.sprite.setViewport(new Rectangle2D(Constants.SpriteSheetDimensions.KING_X,
                    Constants.SpriteSheetDimensions.WHITE_PIECE_Y, Constants.SpriteSheetDimensions.PIECE_WIDTH,
                    Constants.SpriteSheetDimensions.PIECE_HEIGHT));
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public int[][] getPossibleMoves(int[][] boardPositions) {
        int[][] possibleMoves = new int[8][8];

        return possibleMoves;
    }
    
}
