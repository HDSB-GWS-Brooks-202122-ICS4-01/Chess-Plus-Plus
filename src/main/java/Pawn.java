import javafx.scene.image.ImageView;

public class Pawn extends Piece{



    public Pawn(int id){
        this.id = id;
        this.color = (int) (id / 16);
        this.sprite = new ImageView();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ImageView getSprite() {
        return sprite;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[][] getPossibleMoves(int[][] boardPositions) {
        int[][] possibleMoves = new int[8][8];

        return possibleMoves;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGridX() {
        return this.gridX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGridY() {
        return this.gridY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGridX(int x) {
        this.gridX = x;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGridY(int y) {
        this.gridY = y;
    }
    
}
