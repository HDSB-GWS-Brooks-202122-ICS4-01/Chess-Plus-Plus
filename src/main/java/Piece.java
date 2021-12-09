import java.util.Arrays;

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
    protected boolean hasMoved;

    /**
     * Method for getting the possible moves of this piece based on the positions of
     * the piece on the board.
     * 
     * @param boardPositions
     * @return a 2d array containing all the possible moves based on this board.
     */
    public abstract byte[][] getPossibleMoves(byte[][] boardPositions);

    /**
     * Method for getting if the king is not under check. 
     * @param boardPositions The positions of pieces on the board.
     * @param possibleMove The move to check.
     * @param isKing if the move it is trying involves moving the king.
     * @return
     */
    public boolean isNotUnderCheck(byte[][] boardPositions, byte[] possibleMove, boolean isKing) {
        byte[][] newBoardPositions = new byte[8][8];
        for (int x = 0; x < 8; x++){
            for (int y = 0; y < 8; y++){
                newBoardPositions[x][y] = boardPositions[x][y];
            }
        }

        byte[] kingPos = new byte[2];
        if(isKing){
            kingPos[0] = possibleMove[0];
            kingPos[1] = possibleMove[1];
        } else {
            kingPos = getKingPos();
        }
        newBoardPositions[possibleMove[0]][possibleMove[1]] = id;
        newBoardPositions[gridX][gridY] = Constants.pieceIDs.EMPTY_CELL;

        if (checkLeft(newBoardPositions, kingPos)) {
            System.out.println("Check from left side");
            return false;
        }
        if (checkDown(newBoardPositions, kingPos)) {
            System.out.println("Check from down side");
            return false;
        }
        if (checkUp(newBoardPositions, kingPos)) {
            System.out.println("Check from up side");
            return false;
        }
        if (checkRight(newBoardPositions, kingPos)) {
            System.out.println("Check from right side");
            return false;
        }
        if (checkUpRightDiagonal(newBoardPositions, kingPos)) {
            System.out.println("Check from right up diagonal");
            return false;
        }
        if (checkUpLeftDiagonal(newBoardPositions, kingPos)) {
            System.out.println("Check from up left diagonal");
            return false;
        }
        if (checkDownLeftDiagonal(newBoardPositions, kingPos)) {
            System.out.println("Check from down left diagonal");
            return false;
        }
        if (checkDownRightDiagonal(newBoardPositions, kingPos)) {
            System.out.println("Check from down right diagonal");
            return false;
        }

        byte[][] moveList = {
            {(byte) (kingPos[0]+2), (byte) (kingPos[1]-1)},
            {(byte) (kingPos[0]+2), (byte) (kingPos[1]+1)},
            {(byte) (kingPos[0]-2), (byte) (kingPos[1]-1)},
            {(byte) (kingPos[0]-2), (byte) (kingPos[1]+1)},

            {(byte) (kingPos[0]+1), (byte) (kingPos[1]-2)},
            {(byte) (kingPos[0]-1), (byte) (kingPos[1]-2)},
            {(byte) (kingPos[0]+1), (byte) (kingPos[1]+2)},
            {(byte) (kingPos[0]-1), (byte) (kingPos[1]+2)}
        };

        if(color == Constants.pieceIDs.BLACK){
            for (byte[] move : moveList){
                if(!inBoardRange(move)){
                    continue;
                }
                byte id =newBoardPositions[move[0]][move[1]];
                if(id == Constants.pieceIDs.WHITE_KINGS_HORSE || id == Constants.pieceIDs.WHITE_QUEENS_HORSE){
                    return false;
                }
            }
        } else {
            for (byte[] move : moveList){
                if(!inBoardRange(move)){
                    continue;
                }
                byte id =newBoardPositions[move[0]][move[1]];
                if(id == Constants.pieceIDs.BLACK_KINGS_HORSE || id == Constants.pieceIDs.BLACK_QUEENS_HORSE){
                    return false;
                }
            }
        

        
        }
        return true;

    }

    /**
     * Returns this teams king positions
     * 
     * @return an array of length two that contains the position of the king.
     */
    protected byte[] getKingPos() {
        byte[] pos = new byte[2];
        if(color == Constants.pieceIDs.BLACK){
            pos[0] = App.GAME_PIECES[Constants.pieceIDs.BLACK_KING].getGridX();
            pos[1] = App.GAME_PIECES[Constants.pieceIDs.BLACK_KING].getGridY();
        } else {
            pos[0] = App.GAME_PIECES[Constants.pieceIDs.WHITE_KING].getGridX();
            pos[1] = App.GAME_PIECES[Constants.pieceIDs.WHITE_KING].getGridY();
        }
        System.out.println(Arrays.toString(pos));
        return pos;
    }

    /**
     * Method for seeing if the king is under check, specifically checks if it is under check from the left.
     * @param boardPositions positions of all pieces on the board
     * @param pos position of the king
     * @return true if king is under check from this direction, false if not. 
     */
    private boolean checkLeft(byte[][] boardPositions, byte[] pos) {
        for (int i = pos[0]-1; i > -1; i--) {
            byte pieceAtSquare = boardPositions[i][pos[1]];
            //
            if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 != color) {
                if (color == Constants.pieceIDs.BLACK) {
                    if (pieceAtSquare == Constants.pieceIDs.WHITE_KINGS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEENS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEEN) {
                        return true;
                    } else {
                        continue;
                    }
                } else {
                    if (pieceAtSquare == Constants.pieceIDs.BLACK_KINGS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEENS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEEN) {
                        return true;
                    } else {
                        continue;
                    }

                }
            } else if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 == color) {
                return false;
            } else {
                continue;
            }
        }
        return false;
    }

    /**
     * Method for seeing if the king is under check, specifically checks if it is under check from the right.
     * @param boardPositions positions of all pieces on the board
     * @param pos position of the king
     * @return true if king is under check from this direction, false if not. 
     */
    private boolean checkRight(byte[][] boardPositions, byte[] pos) {
        for (int i = pos[0]+1; i < 8; i++) {
            byte pieceAtSquare = boardPositions[i][pos[1]];
            //
            if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 != color) {
                if (color == Constants.pieceIDs.BLACK) {
                    if (pieceAtSquare == Constants.pieceIDs.WHITE_KINGS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEENS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEEN) {
                        return true;
                    } else {
                        continue;
                    }
                } else {
                    if (pieceAtSquare == Constants.pieceIDs.BLACK_KINGS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEENS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEEN) {
                        return true;
                    } else {
                        continue;
                    }

                }
            } else if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 == color) {
                return false;
            } else {
                continue;
            }
        }
        return false;
    }

    /**
     * Method for seeing if the king is under check, specifically checks if it is under check from the top.
     * @param boardPositions positions of all pieces on the board
     * @param pos position of the king
     * @return true if king is under check from this direction, false if not. 
     */
    private boolean checkUp(byte[][] boardPositions, byte[] pos) {
        for (int i = pos[1]-1; i > -1; i--) {
            byte pieceAtSquare = boardPositions[pos[0]][i];
            if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 != color) {
                if (color == Constants.pieceIDs.BLACK) {
                    System.out.println(pieceAtSquare);
                    if (pieceAtSquare == Constants.pieceIDs.WHITE_KINGS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEENS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEEN) {
                        return true;
                    } else {
                        continue;
                    }
                } else {
                    if (pieceAtSquare == Constants.pieceIDs.BLACK_KINGS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEENS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEEN) {
                        return true;
                    } else {
                        continue;
                    }

                }
            } else if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 == color) {
                return false;
            } else {
                continue;
            }
        }
        return false;
    }

    /**
     * Method for seeing if the king is under check, specifically checks if it is under check from the bottom.
     * @param boardPositions positions of all pieces on the board
     * @param pos position of the king
     * @return true if king is under check from this direction, false if not. 
     */
    private boolean checkDown(byte[][] boardPositions, byte[] pos) {
        for (int i = pos[1]+1; i < 8; i++) {
            byte pieceAtSquare = boardPositions[pos[0]][i];
            //
            if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 != color) {
                if (color == Constants.pieceIDs.BLACK) {
                    if (pieceAtSquare == Constants.pieceIDs.WHITE_KINGS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEENS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEEN) {
                        return true;
                    } else {
                        continue;
                    }
                } else {
                    if (pieceAtSquare == Constants.pieceIDs.BLACK_KINGS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEENS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEEN) {
                        return true;
                    } else {
                        continue;
                    }

                }
            } else if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 == color) {
                return false;
            } else {
                continue;
            }
        }
        return false;
    }



    /**
     * Method for seeing if the king is under check, specifically checks if it is under check from the top right diagonal.
     * @param boardPositions positions of all pieces on the board
     * @param pos position of the king
     * @return true if king is under check from this direction, false if not. 
     */
    private boolean checkUpRightDiagonal(byte[][] boardPositions, byte[] pos) {
        int j = pos[1]-1;
        for (int i = pos[0]+1; i < 8; i++) {
            if(j < 0){
                break;
            }
            byte pieceAtSquare = boardPositions[i][j];
            //
            if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 != color) {
                if (color== Constants.pieceIDs.BLACK) {
                    if (pieceAtSquare == Constants.pieceIDs.WHITE_KINGS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEENS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEEN) {
                        return true;
                    } else {
                        j--;
                        continue;
                    }
                } else {
                    if (pieceAtSquare == Constants.pieceIDs.BLACK_KINGS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEENS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEEN) {
                        return true;
                    } else {
                        j--;
                        continue;
                    }

                }
            } else if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 == color) {
                return false;
            } else {
                j--;
                continue;
            }
        }
        return false;
    }

    /**
     * Method for seeing if the king is under check, specifically checks if it is under check from the top left diagonal.
     * @param boardPositions positions of all pieces on the board
     * @param pos position of the king
     * @return true if king is under check from this direction, false if not. 
     */
    private boolean checkUpLeftDiagonal(byte[][] boardPositions, byte[] pos) {
        int j = pos[1]-1;
        for (int i = pos[0]-1; i > -1; i--) {
            if(j < 0){
                break;
            }
            byte pieceAtSquare = boardPositions[i][j];
            System.out.println(pieceAtSquare);
            //
            if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 != color) {
                if (color == Constants.pieceIDs.BLACK) {
                    if (pieceAtSquare == Constants.pieceIDs.WHITE_KINGS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEENS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEEN) {
                        return true;
                    } else {
                        j--;
                        continue;
                    }
                } else {
                    if (pieceAtSquare == Constants.pieceIDs.BLACK_KINGS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEENS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEEN) {
                        return true;
                    } else {
                        j--;
                        continue;
                    }

                }
            } else if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 == color) {
                return false;
            } else {
                j--;
                continue;
            }
        }
        return false;
    }

    /**
     * Method for seeing if the king is under check, specifically checks if it is under check from the bottom left diagonal.
     * @param boardPositions positions of all pieces on the board
     * @param pos position of the king
     * @return true if king is under check from this direction, false if not. 
     */
    private boolean checkDownLeftDiagonal(byte[][] boardPositions, byte[] pos) {
        int j = pos[1]+1;
        for (int i = pos[0]-1; i > -1; i--) {
            if(j > 7){
                break;
            }
            byte pieceAtSquare = boardPositions[i][j];
            System.out.println(pieceAtSquare);
            //
            if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 != color) {
                if (color == Constants.pieceIDs.BLACK) {
                    if (pieceAtSquare == Constants.pieceIDs.WHITE_KINGS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEENS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEEN) {
                        return true;
                    } else {
                        j++;
                        continue;
                    }
                } else {
                    if (pieceAtSquare == Constants.pieceIDs.BLACK_KINGS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEENS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEEN) {
                        return true;
                    } else {
                        j++;
                        continue;
                    }

                }
            } else if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 == color) {
                return false;
            } else {
                j++;
                continue;
            }
        }
        return false;
    }

    /**
     * Method for seeing if the king is under check, specifically checks if it is under check from the bottom right diagonal.
     * @param boardPositions positions of all pieces on the board
     * @param pos position of the king
     * @return true if king is under check from this direction, false if not. 
     */
    private boolean checkDownRightDiagonal(byte[][] boardPositions, byte[] pos) {
        int j = pos[1]+1;
        for (int i = pos[0]+1; i < 8; i++) {
            if(j > 7){
                break;
            }
            byte pieceAtSquare = boardPositions[i][j];
            System.out.println(pieceAtSquare);
            //
            if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 != color) {
                if (color == Constants.pieceIDs.BLACK) {
                    if (pieceAtSquare == Constants.pieceIDs.WHITE_KINGS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEENS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEEN) {
                        return true;
                    } else {
                        j++;
                        continue;
                    }
                } else {
                    if (pieceAtSquare == Constants.pieceIDs.BLACK_KINGS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEENS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEEN) {
                        return true;
                    } else {
                        j++;
                        continue;
                    }

                }
            } else if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 == color) {
                return false;
            } else {
                j++;
                continue;
            }
        }
        return false;
    }


    /**
     * Checks to see if this position is in the board.
     * @param pos
     * @return true if the position is in the board, false if not.
     */
    protected boolean inBoardRange(byte[] pos){
        return (pos[0] > -1 && pos[0] < 8) && (pos[1] > -1 && pos[1] < 8);
    }

    /**
     * Method for getting the sprite object to be added into javafx.animation
     * 
     * @return an ImageView object representing the sprite.
     */
    public ImageView getSprite() {
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
    public byte getId() {
        return this.id;
    }

    /**
     * Method for getting the grid x value.
     * 
     * @return
     */
    public byte getGridX() {
        return this.gridX;
    }

    /**
     * Method for getting the grid y value.
     * 
     * @return
     */
    public byte getGridY() {
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

}
