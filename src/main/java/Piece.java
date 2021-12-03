import javafx.scene.image.ImageView;

public abstract class Piece {
    protected ImageView sprite;
    protected int id;
    protected int color;
    protected int gridX;
    protected int gridY;

    public abstract ImageView getSprite();

    /**
     * Method for getting the possible moves of this piece based on the positions of the piece on the board. 
     * @param boardPositions
     * @return a 2d array containing all the possible moves based on this board. 
     */
    public abstract int[][] getPossibleMoves(int[][] boardPositions);

    /**
     * Method for getting the grid x value.
     * @return
     */
    public abstract int getGridX();

    /**
     * Method for getting the grid y value.
     * @return
     */
    public abstract int getGridY();

    /**
     * Method for setting the gridX attribute.
     * 
     * @param x new value for gridX
     */
    public abstract void setGridX(int x);
    

    /**
     * Method for setting the gridY attribute.
     * 
     * @param y new value for gridY
     */
    public abstract void setGridY(int y);

}
