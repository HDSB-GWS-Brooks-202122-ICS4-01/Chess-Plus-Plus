import javafx.scene.image.ImageView;

public class Pawn extends Piece{



    public Pawn(int id){
        this.id = id;
        this.color = (int) (id / 16);


        this.sprite = new ImageView();
    }

    @Override
    public ImageView getSprite() {
        // TODO Auto-generated method stub
        return sprite;
    }

    @Override
    public int[][] getPossibleMoves(int[][] boardPositions) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getGridX() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getGridY() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setGridX(int x) {
        this.gridX = x;
    }

    @Override
    public void setGridY(int y) {
        this.gridY = y;
    }
    
}
