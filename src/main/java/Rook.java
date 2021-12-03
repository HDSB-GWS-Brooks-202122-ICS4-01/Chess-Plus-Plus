import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;


/**
 * Rook class which extends the abstract Piece class. 
 */
public class Rook extends Piece{

    public Rook(Byte id) {
        this.id = id;
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
    public int[][] getPossibleMoves(int[][] boardPositions) {
        int[][] possibleMoves = new int[8][8];

        return possibleMoves;
    }
    
}
