import java.util.ArrayList;

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

        this.sprite.setFitWidth(Constants.SpriteSheetDimensions.PIECE_FIT_WIDTH);
        this.sprite.setFitHeight(Constants.SpriteSheetDimensions.PIECE_FIT_HEIGHT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[][] getPossibleMoves(byte[][] boardPositions) {
        ArrayList<byte[]> possibleMoves = new ArrayList<byte[]>();
        byte[][] moveList = {
            {(byte) (gridX+1), gridY},
            {(byte) (gridX-1), gridY},
            {gridX, (byte) (gridY-1)},
            {gridX, (byte) (gridY+1)},

            {(byte) (gridX+1), (byte) (gridY+1)},
            {(byte) (gridX+1), (byte) (gridY-1)},
            {(byte) (gridX-1), (byte) (gridY+1)},
            {(byte) (gridX-1), (byte) (gridY-1)}
        };

        for(byte[] move : moveList){
            if(inBoardRange(move) && (boardPositions[move[0]][move[1]] == Constants.pieceIDs.EMPTY_CELL || boardPositions[move[0]][move[1]]/16 != color)){
                if(isNotUnderCheck(boardPositions, move, true)){
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

    /**
     * Checks to see if this position is in the board.
     * @param pos
     * @return true if the position is in the board, false if not.
     */
    public boolean inBoardRange(byte[] pos){
        return (pos[0] > -1 && pos[0] < 8) && (pos[1] > -1 && pos[1] < 8);
    }
    
}
