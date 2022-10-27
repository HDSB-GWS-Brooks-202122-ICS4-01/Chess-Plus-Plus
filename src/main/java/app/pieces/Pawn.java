package app.pieces;
import java.util.ArrayList;

import app.App;
import app.util.Constants.BoardData;
import app.util.Constants.PieceIDs;
import app.util.Constants.SpriteSheetDimensions;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;

/**
 * Pawn class which extends the abstract Piece class.
 * 
 * @author Akil Pathiranage
 */
public class Pawn extends Piece {
    int passant;

    /**
     * Constructor for pawns.
     * @param id The id to create the pawn with.
     */
    public Pawn(Byte id) {
        this.id = id;
        setType();

        // 0 if the color is black, 1 if the color isn't black
        this.color = (byte) (id / PieceIDs.COLOR_DIVISOR);

        this.sprite = new ImageView(App.getSpritesheet());
        if (this.color == 0) {
            // if the colour is black put a black pawn
            this.sprite.setViewport(new Rectangle2D(SpriteSheetDimensions.PAWN_X,
                    SpriteSheetDimensions.BLACK_PIECE_Y, SpriteSheetDimensions.PIECE_WIDTH,
                    SpriteSheetDimensions.PIECE_HEIGHT));
        } else {
            this.sprite.setViewport(new Rectangle2D(SpriteSheetDimensions.PAWN_X,
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

        if (color == PieceIDs.BLACK) {
            // if the pawn can move forward
            if (boardPositions[gridX][gridY + 1] == PieceIDs.EMPTY_CELL && gridY + 1 < 8) {
                byte[] forwardMove = { gridX, (byte) (gridY + 1) };
                if (isNotUnderCheck(boardPositions, forwardMove, false)) {
                    possibleMoves.add(forwardMove);
                }
                // if the pawn is at its starting position on the board.
                if (gridX == BoardData.INITIAL_POSITIONS[id][0]
                        && gridY == BoardData.INITIAL_POSITIONS[this.id][1] && gridY + 2 < 8
                        && boardPositions[gridX][gridY + 2] == PieceIDs.EMPTY_CELL) {
                    byte[] doubleForwardMove = { gridX, (byte) (gridY + 2) };
                    if (isNotUnderCheck(boardPositions, doubleForwardMove, false)) {
                        possibleMoves.add(doubleForwardMove);
                    }
                }

            }

            if (gridX - 1 > -1 && gridY + 1 < 8
                    && (byte) boardPositions[gridX - 1][gridY + 1] / PieceIDs.COLOR_DIVISOR == PieceIDs.WHITE) {
                byte[] attackLeft = { (byte) (gridX - 1), (byte) (gridY + 1) };
                if (isNotUnderCheck(boardPositions, attackLeft, false)) {
                    possibleMoves.add(attackLeft);
                }
            }
            if (gridX + 1 < 8 && gridY + 1 < 8
                    && (byte) boardPositions[gridX + 1][gridY + 1] / PieceIDs.COLOR_DIVISOR == PieceIDs.WHITE) {
                byte[] attackRight = { (byte) (gridX + 1), (byte) (gridY + 1) };
                if (isNotUnderCheck(boardPositions, attackRight, false)) {
                    possibleMoves.add(attackRight);
                }
            }

        } else {
            if (boardPositions[gridX][gridY - 1] == PieceIDs.EMPTY_CELL && gridY - 1 > -1) {
                byte[] forwardMove = { gridX, (byte) (gridY - 1) };
                if (isNotUnderCheck(boardPositions, forwardMove, false)) {
                    possibleMoves.add(forwardMove);
                }
                // if the pawn is at its starting position on the board.
                if (gridX == BoardData.INITIAL_POSITIONS[id][0]
                        && gridY == BoardData.INITIAL_POSITIONS[this.id][1] && gridY - 2 > -1
                        && boardPositions[gridX][gridY - 2] == PieceIDs.EMPTY_CELL) {
                    byte[] doubleForwardMove = { gridX, (byte) (gridY - 2) };
                    if (isNotUnderCheck(boardPositions, doubleForwardMove, false)) {
                        possibleMoves.add(doubleForwardMove);
                    }
                }
            }

            if (gridX - 1 > -1 && gridY - 1 > -1
                    && (byte) boardPositions[gridX - 1][gridY - 1] != PieceIDs.EMPTY_CELL
                    && boardPositions[gridX - 1][gridY - 1] / PieceIDs.COLOR_DIVISOR == PieceIDs.BLACK) {
                byte[] attackLeft = { (byte) (gridX - 1), (byte) (gridY - 1) };
                if (isNotUnderCheck(boardPositions, attackLeft, false)) {
                    possibleMoves.add(attackLeft);
                }
            }
            if (gridX + 1 < 8 && gridY - 1 > -1
                    && (byte) boardPositions[gridX + 1][gridY - 1] != PieceIDs.EMPTY_CELL
                    && boardPositions[gridX + 1][gridY - 1] / PieceIDs.COLOR_DIVISOR == PieceIDs.BLACK) {
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

    /**
     * Method for determining if a pawn can make an en passant move to the left.
     * 
     * 
     * @param pawnLeft The piece to the left of the pawn.
     * @param boardPositions The current positions on the board. 
     * @return True if the pawn can do the move, false if not.
     */
    public boolean canPassantLeft(byte pawnLeft, byte[][] boardPositions) {
        if (color == PieceIDs.BLACK) {
            if (pawnLeft > PieceIDs.BEGIN_WHITE_PAWNS && pawnLeft < PieceIDs.END_WHITE_PAWNS
                    && ((Pawn) App.GAME_PIECES[pawnLeft]).getPassant() == App.MOVE_COUNT - 1 && gridY + 1 < 8) {
                        byte[][] newBoardPositions = App.deepCopy(boardPositions);
                        newBoardPositions[gridX-1][gridY] = PieceIDs.EMPTY_CELL;
                        byte[] attackSquare = {(byte) (gridX-1), (byte) (gridY+1)};
                        if(isNotUnderCheck(newBoardPositions, attackSquare, false)){
                            return true;
                        }
                }
        } else {
            if (pawnLeft > PieceIDs.BEGIN_BLACK_PAWNS && pawnLeft < PieceIDs.END_BLACK_PAWNS
                    && ((Pawn) App.GAME_PIECES[pawnLeft]).getPassant() == App.MOVE_COUNT - 1 && gridY - 1 > -1) {
                        byte[][] newBoardPositions = App.deepCopy(boardPositions);
                        newBoardPositions[gridX-1][gridY] = PieceIDs.EMPTY_CELL;
                        byte[] attackSquare = {(byte) (gridX-1), (byte) (gridY-1)};
                        if(isNotUnderCheck(newBoardPositions, attackSquare, false)){
                            return true;
                        }
            }
        }

        return false;
    }

    /**
     * Method for determining if a pawn can make an en passant move to the right.
     * 
     * 
     * @param pawnLeft The piece to the right of the pawn.
     * @param boardPositions The current positions on the board. 
     * @return True if the pawn can do the move, false if not.
     */
    public boolean canPassantRight(byte pawnRight, byte[][] boardPositions) {
        if (color == PieceIDs.BLACK) {
            if (pawnRight > PieceIDs.BEGIN_WHITE_PAWNS && pawnRight < PieceIDs.END_WHITE_PAWNS
                    && ((Pawn) App.GAME_PIECES[pawnRight]).getPassant() == App.MOVE_COUNT - 1 && gridY + 1 < 8) {
                        byte[][] newBoardPositions = App.deepCopy(boardPositions);
                        newBoardPositions[gridX+1][gridY] = PieceIDs.EMPTY_CELL;
                        byte[] attackSquare = {(byte) (gridX+1), (byte) (gridY+1)};
                        if(isNotUnderCheck(newBoardPositions, attackSquare, false)){
                            return true;
                        }
                }
        } else {
            if (pawnRight > PieceIDs.BEGIN_BLACK_PAWNS && pawnRight < PieceIDs.END_BLACK_PAWNS
                    && ((Pawn) App.GAME_PIECES[pawnRight]).getPassant() == App.MOVE_COUNT - 1 && gridY - 1 > -1) {
                        byte[][] newBoardPositions = App.deepCopy(boardPositions);
                        newBoardPositions[gridX+1][gridY] = PieceIDs.EMPTY_CELL;
                        byte[] attackSquare = {(byte) (gridX+1), (byte) (gridY-1)};
                        if(isNotUnderCheck(newBoardPositions, attackSquare, false)){
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
