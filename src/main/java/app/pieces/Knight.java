package app.pieces;
import java.util.ArrayList;

import app.App;
import app.util.Constants.PieceIDs;
import app.util.Constants.SpriteSheetDimensions;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;

/**
 * Knight class which extends the abstract Piece class.
 * @author Akil Pathiranage
 * @version 1.0 
 */
public class Knight extends Piece{

    /**
     * Constructor for Knight class. 
     * @param id value of which knight this knight is. 
     */
    public Knight(Byte id) {
        this.id = id;
        setType();
        this.color = (byte) (id / PieceIDs.COLOR_DIVISOR);
        this.sprite = new ImageView(App.getSpritesheet());
        if (this.color == 0) {
            // if the colour is black put a black pawn
            this.sprite.setViewport(new Rectangle2D(SpriteSheetDimensions.KNIGHT_X,
                    SpriteSheetDimensions.BLACK_PIECE_Y, SpriteSheetDimensions.PIECE_WIDTH,
                    SpriteSheetDimensions.PIECE_HEIGHT));
        } else {
            this.sprite.setViewport(new Rectangle2D(SpriteSheetDimensions.KNIGHT_X,
                    SpriteSheetDimensions.WHITE_PIECE_Y, SpriteSheetDimensions.PIECE_WIDTH,
                    SpriteSheetDimensions.PIECE_HEIGHT));
        }

        this.sprite.setFitWidth(SpriteSheetDimensions.PIECE_FIT_WIDTH);
        this.sprite.setFitHeight(SpriteSheetDimensions.PIECE_FIT_HEIGHT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[][] getPossibleMoves(byte[][] boardPositions) {
        ArrayList<byte[]> possibleMoves = new ArrayList<byte[]>();
        byte[][] moveList = {
            {(byte) (gridX+2), (byte) (gridY-1)},
            {(byte) (gridX+2), (byte) (gridY+1)},
            {(byte) (gridX-2), (byte) (gridY-1)},
            {(byte) (gridX-2), (byte) (gridY+1)},

            {(byte) (gridX+1), (byte) (gridY-2)},
            {(byte) (gridX-1), (byte) (gridY-2)},
            {(byte) (gridX+1), (byte) (gridY+2)},
            {(byte) (gridX-1), (byte) (gridY+2)}
        };

        for(byte[] move : moveList){
            if(inBoardRange(move) && (boardPositions[move[0]][move[1]] == PieceIDs.EMPTY_CELL || boardPositions[move[0]][move[1]]/PieceIDs.COLOR_DIVISOR != color)){
                if(isNotUnderCheck(boardPositions, move,false)){
                    possibleMoves.add(move);
                }
            }
        }

        byte[][] moves = new byte[possibleMoves.size()][];

        for (int i = 0; i < possibleMoves.size(); i++) {
            moves[i] = new byte[2];

            moves[i][0] = possibleMoves.get(i)[0];
            moves[i][1] = possibleMoves.get(i)[1];
        }
        return moves;
    }

}
