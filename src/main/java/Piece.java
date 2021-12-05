import javafx.scene.image.ImageView;

/**
 * Abstract class for pieces. 
 */
public abstract class Piece {
    protected ImageView sprite;
    protected byte id;
    protected byte color;
    protected byte gridX;
    protected byte gridY;


    /**
     * Method for getting the possible moves of this piece based on the positions of the piece on the board. 
     * @param boardPositions
     * @return a 2d array containing all the possible moves based on this board. 
     */
    public abstract byte[][] getPossibleMoves(byte[][] boardPositions);

    /**
     * Method for getting the sprite object to be added into javafx.animation
     * 
     * @return an ImageView object representing the sprite. 
     */
    public ImageView getSprite(){
        return this.sprite;
    }

    /**
     * Method for getting the color of a piece.
     * 
     * @return a byte representing the color of the piece.
     */
    public byte getColor() {
        return color;
    }

    /**
     * Method for getting the id of a piece.
     * 
     * @return a byte representing the id of the piece.
     */
    public byte getId(){
        return this.id;
    }

    /**
     * Method for getting the grid x value.
     * @return
     */
    public byte getGridX(){
        return this.gridX;
    }

    /**
     * Method for getting the grid y value.
     * @return
     */
    public byte getGridY(){
        return this.gridY;
    }

    /**
     * Method for setting both the gridX and gridY attributes.
     * 
     * @param x new value for gridX.
     * @param y new Value for girdY.
     */
    public void setGridPos(byte x, byte y) {
        this.gridX = x;
        this.gridY = y;
    }

    /**
     * Method for setting the gridX attribute.
     * 
     * @param x new value for gridX.
     */
    public void setGridX(byte x){
        this.gridX = x;
    }
    

    /**
     * Method for setting the gridY attribute.
     * 
     * @param y new value for gridY.
     */
    public void setGridY(byte y){
        this.gridY = y;
    }

}
