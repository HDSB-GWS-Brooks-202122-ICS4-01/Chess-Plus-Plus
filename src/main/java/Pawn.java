import java.util.ArrayList;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;

/**
 * Pawn class which extends the abstract Piece class.
 * 
 * @author Akil Pathiranage
 */
public class Pawn extends Piece {
    int passant;

    public Pawn(Byte id) {
        this.id = id;
        setType();

        // 0 if the color is black, 1 if the color isn't black
        this.color = (byte) (id / Constants.pieceIDs.COLOR_DIVISOR);

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
                byte[] forwardMove = { gridX, (byte) (gridY + 1) };
                if (isNotUnderCheck(boardPositions, forwardMove, false)) {
                    possibleMoves.add(forwardMove);
                }
                // if the pawn is at its starting position on the board.
                if (gridX == Constants.boardData.INITIAL_POSITIONS[id][0]
                        && gridY == Constants.boardData.INITIAL_POSITIONS[this.id][1] && gridY + 2 < 8
                        && boardPositions[gridX][gridY + 2] == Constants.pieceIDs.EMPTY_CELL) {
                    byte[] doubleForwardMove = { gridX, (byte) (gridY + 2) };
                    if (isNotUnderCheck(boardPositions, doubleForwardMove, false)) {
                        possibleMoves.add(doubleForwardMove);
                    }
                }

            }

            if (gridX - 1 > -1 && gridY + 1 < 8
                    && (byte) boardPositions[gridX - 1][gridY + 1] / Constants.pieceIDs.COLOR_DIVISOR == Constants.pieceIDs.WHITE) {
                byte[] attackLeft = { (byte) (gridX - 1), (byte) (gridY + 1) };
                if (isNotUnderCheck(boardPositions, attackLeft, false)) {
                    possibleMoves.add(attackLeft);
                }
            }
            if (gridX + 1 < 8 && gridY + 1 < 8
                    && (byte) boardPositions[gridX + 1][gridY + 1] / Constants.pieceIDs.COLOR_DIVISOR == Constants.pieceIDs.WHITE) {
                byte[] attackRight = { (byte) (gridX + 1), (byte) (gridY + 1) };
                if (isNotUnderCheck(boardPositions, attackRight, false)) {
                    possibleMoves.add(attackRight);
                }
            }

        } else {
            if (boardPositions[gridX][gridY - 1] == Constants.pieceIDs.EMPTY_CELL && gridY - 1 > -1) {
                byte[] forwardMove = { gridX, (byte) (gridY - 1) };
                if (isNotUnderCheck(boardPositions, forwardMove, false)) {
                    possibleMoves.add(forwardMove);
                }
                // if the pawn is at its starting position on the board.
                if (gridX == Constants.boardData.INITIAL_POSITIONS[id][0]
                        && gridY == Constants.boardData.INITIAL_POSITIONS[this.id][1] && gridY - 2 > -1
                        && boardPositions[gridX][gridY - 2] == Constants.pieceIDs.EMPTY_CELL) {
                    byte[] doubleForwardMove = { gridX, (byte) (gridY - 2) };
                    if (isNotUnderCheck(boardPositions, doubleForwardMove, false)) {
                        possibleMoves.add(doubleForwardMove);
                    }
                }
            }

            if (gridX - 1 > -1 && gridY - 1 > -1
                    && (byte) boardPositions[gridX - 1][gridY - 1] != Constants.pieceIDs.EMPTY_CELL
                    && boardPositions[gridX - 1][gridY - 1] / Constants.pieceIDs.COLOR_DIVISOR == Constants.pieceIDs.BLACK) {
                byte[] attackLeft = { (byte) (gridX - 1), (byte) (gridY - 1) };
                if (isNotUnderCheck(boardPositions, attackLeft, false)) {
                    possibleMoves.add(attackLeft);
                }
            }
            if (gridX + 1 < 8 && gridY - 1 > -1
                    && (byte) boardPositions[gridX + 1][gridY - 1] != Constants.pieceIDs.EMPTY_CELL
                    && boardPositions[gridX + 1][gridY - 1] / Constants.pieceIDs.COLOR_DIVISOR == Constants.pieceIDs.BLACK) {
                byte[] attackRight = { (byte) (gridX + 1), (byte) (gridY - 1) };
                if (isNotUnderCheck(boardPositions, attackRight, false)) {
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

    public boolean canPassantLeft(byte pawnLeft, byte[][] boardPositions) {
        if (color == Constants.pieceIDs.BLACK) {
            if (pawnLeft > Constants.pieceIDs.BEGIN_WHITE_PAWNS && pawnLeft < Constants.pieceIDs.END_WHITE_PAWNS
                    && ((Pawn) App.GAME_PIECES[pawnLeft]).getPassant() == App.MOVE_COUNT - 1 && gridY + 1 < 8) {
                        byte[][] newBoardPositions = App.deepCopy(boardPositions);
                        newBoardPositions[gridX-1][gridY] = Constants.pieceIDs.EMPTY_CELL;
                        byte[] attackSquare = {(byte) (gridX-1), (byte) (gridY+1)};
                        if(isNotUnderCheck(newBoardPositions, attackSquare, false)){
                            return true;
                        }
                }
        } else {
            if (pawnLeft > Constants.pieceIDs.BEGIN_BLACK_PAWNS && pawnLeft < Constants.pieceIDs.END_BLACK_PAWNS
                    && ((Pawn) App.GAME_PIECES[pawnLeft]).getPassant() == App.MOVE_COUNT - 1 && gridY - 1 > -1) {
                        byte[][] newBoardPositions = App.deepCopy(boardPositions);
                        newBoardPositions[gridX-1][gridY] = Constants.pieceIDs.EMPTY_CELL;
                        byte[] attackSquare = {(byte) (gridX-1), (byte) (gridY-1)};
                        if(isNotUnderCheck(newBoardPositions, attackSquare, false)){
                            return true;
                        }
            }
        }

        return false;
    }

    public boolean canPassantRight(byte pawnRight, byte[][] boardPositions) {
        if (color == Constants.pieceIDs.BLACK) {
            if (pawnRight > Constants.pieceIDs.BEGIN_WHITE_PAWNS && pawnRight < Constants.pieceIDs.END_WHITE_PAWNS
                    && ((Pawn) App.GAME_PIECES[pawnRight]).getPassant() == App.MOVE_COUNT - 1 && gridY + 1 < 8) {
                        byte[][] newBoardPositions = App.deepCopy(boardPositions);
                        newBoardPositions[gridX+1][gridY] = Constants.pieceIDs.EMPTY_CELL;
                        byte[] attackSquare = {(byte) (gridX+1), (byte) (gridY+1)};
                        if(isNotUnderCheck(newBoardPositions, attackSquare, false)){
                            return true;
                        }
                }
        } else {
            if (pawnRight > Constants.pieceIDs.BEGIN_BLACK_PAWNS && pawnRight < Constants.pieceIDs.END_BLACK_PAWNS
                    && ((Pawn) App.GAME_PIECES[pawnRight]).getPassant() == App.MOVE_COUNT - 1 && gridY - 1 > -1) {
                        byte[][] newBoardPositions = App.deepCopy(boardPositions);
                        newBoardPositions[gridX+1][gridY] = Constants.pieceIDs.EMPTY_CELL;
                        byte[] attackSquare = {(byte) (gridX+1), (byte) (gridY-1)};
                        if(isNotUnderCheck(boardPositions, attackSquare, false)){
                            return true;
                        }
            }
        }
        return false;

    }

    /**
     * Method for setting the passant value of a pawn.
     * 
     * @param num New number to set the passant value to.
     */
    public void setPassant(int num) {
        this.passant = num;
    }

    /**
     * Method for getting the passant value of a pawn.
     * 
     * @return an integer, representing the passant value.
     */
    public int getPassant() {
        return this.passant;
    }

}
