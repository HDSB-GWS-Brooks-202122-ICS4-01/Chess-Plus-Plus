
import javafx.scene.image.ImageView;

/**
 * Abstract class for pieces.
 * @author Akil Pathiranage
 * @version 1.0 
 */
public abstract class Piece {
    protected ImageView sprite;
    protected byte id;
    protected byte pieceType;
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
     * Method for setting the type of a piece when it is instantiated.
     */
    protected void setType(){
        if (id == Constants.pieceIDs.BLACK_KING || id == Constants.pieceIDs.WHITE_KING) {
            this.pieceType = Constants.pieceType.KING;
         } else if (id == Constants.pieceIDs.BLACK_QUEEN || id == Constants.pieceIDs.WHITE_QUEEN) {
            this.pieceType = Constants.pieceType.QUEEN;
         } else if (id == Constants.pieceIDs.BLACK_KINGS_BISHOP || id == Constants.pieceIDs.BLACK_QUEENS_BISHOP
               || id == Constants.pieceIDs.WHITE_KINGS_BISHOP || id == Constants.pieceIDs.WHITE_QUEENS_BISHOP) {
            this.pieceType = Constants.pieceType.BISHOP;
         } else if (id == Constants.pieceIDs.BLACK_KINGS_KNIGHT || id == Constants.pieceIDs.BLACK_QUEENS_KNIGHT
               || id == Constants.pieceIDs.WHITE_KINGS_KNIGHT || id == Constants.pieceIDs.WHITE_QUEENS_KNIGHT) {
            this.pieceType = Constants.pieceType.KNIGHT;
         } else if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK
               || id == Constants.pieceIDs.WHITE_KINGS_ROOK || id == Constants.pieceIDs.WHITE_QUEENS_ROOK
               || id == Constants.pieceIDs.BLACK_PROMOTED_ROOK || id == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
            this.pieceType = Constants.pieceType.ROOK;
         } else {
            this.pieceType = Constants.pieceType.PAWN;
         }

    }

    /**
     * Method for getting if the king is not under check.
     * 
     * @param boardPositions The positions of pieces on the board.
     * @param possibleMove   The move to check.
     * @param isKing         if the move it is trying involves moving the king.
     * @return
     */
    public boolean isNotUnderCheck(byte[][] boardPositions, byte[] possibleMove, boolean isKing) {
        byte[][] newBoardPositions = new byte[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                newBoardPositions[x][y] = boardPositions[x][y];
            }
        }

        //System.out.println("Checking this move: " + Arrays.toString(possibleMove));

        byte[] kingPos = new byte[2];
        if (isKing) {
            kingPos[0] = possibleMove[0];
            kingPos[1] = possibleMove[1];
        } else {
            kingPos = getKingPos();
        }
        newBoardPositions[possibleMove[0]][possibleMove[1]] = id;
        newBoardPositions[gridX][gridY] = Constants.pieceIDs.EMPTY_CELL;

        
        if(checkPawnAttack(newBoardPositions, kingPos)){
          System.out.println("Check from a pawn");
          return false;
        }
         
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

        // checks against KNIGHTs
        byte[][] positionList = {
                { (byte) (kingPos[0] + 2), (byte) (kingPos[1] - 1) },
                { (byte) (kingPos[0] + 2), (byte) (kingPos[1] + 1) },
                { (byte) (kingPos[0] - 2), (byte) (kingPos[1] - 1) },
                { (byte) (kingPos[0] - 2), (byte) (kingPos[1] + 1) },

                { (byte) (kingPos[0] + 1), (byte) (kingPos[1] - 2) },
                { (byte) (kingPos[0] - 1), (byte) (kingPos[1] - 2) },
                { (byte) (kingPos[0] + 1), (byte) (kingPos[1] + 2) },
                { (byte) (kingPos[0] - 1), (byte) (kingPos[1] + 2) }
        };

        if (color == Constants.pieceIDs.BLACK) {
            for (byte[] position : positionList) {
                if (!inBoardRange(position)) {
                    continue;
                }
                byte id =newBoardPositions[position[0]][position[1]];
                if(id == Constants.pieceIDs.WHITE_KINGS_KNIGHT || id == Constants.pieceIDs.WHITE_QUEENS_KNIGHT){
                    return false;
                }
            }
        } else {
            for (byte[] position : positionList) {
                if (!inBoardRange(position)) {
                    continue;
                }
                byte id =newBoardPositions[position[0]][position[1]];
                if(id == Constants.pieceIDs.BLACK_KINGS_KNIGHT || id == Constants.pieceIDs.BLACK_QUEENS_KNIGHT){
                    return false;
                }
            }

        }

        // position list for checking checks by other king
        byte[][] kPosList = {
                { (byte) (kingPos[0] + 1), kingPos[1] },
                { (byte) (kingPos[0] - 1), kingPos[1] },
                { (byte) (kingPos[0] + 1), (byte) (kingPos[1] + 1) },
                { (byte) (kingPos[0] + 1), (byte) (kingPos[1] - 1) },
                { (byte) (kingPos[0] - 1), (byte) (kingPos[1] + 1) },
                { (byte) (kingPos[0] - 1), (byte) (kingPos[1] - 1) },
                { kingPos[0], (byte) (kingPos[1] - 1) },
                { kingPos[0], (byte) (kingPos[1] + 1) }

        };

        if (color == Constants.pieceIDs.WHITE) {
            for (byte[] kPos : kPosList) {
                if (inBoardRange(kPos) && newBoardPositions[kPos[0]][kPos[1]] == Constants.pieceIDs.BLACK_KING) {
                    System.out.println("Check by black king");
                    return false;
                }

            }
        } else {
            for (byte[] kPos : kPosList) {
                if (inBoardRange(kPos) && newBoardPositions[kPos[0]][kPos[1]] == Constants.pieceIDs.WHITE_KING) {
                    System.out.println("Check by white king");
                    return false;
                }
            }
        }

        //System.out.println("Done checks");
        return true;

    }

    /**
     * Returns this teams king positions
     * 
     * @return an array of length two that contains the position of the king.
     */
    protected byte[] getKingPos() {
        byte[] pos = new byte[2];
        if (color == Constants.pieceIDs.BLACK) {
            pos[0] = App.GAME_PIECES[Constants.pieceIDs.BLACK_KING].getGridX();
            pos[1] = App.GAME_PIECES[Constants.pieceIDs.BLACK_KING].getGridY();
        } else {
            pos[0] = App.GAME_PIECES[Constants.pieceIDs.WHITE_KING].getGridX();
            pos[1] = App.GAME_PIECES[Constants.pieceIDs.WHITE_KING].getGridY();
        }
        return pos;
    }

    /**
     * Method for checking if a pawn is attacking the king. NOTE: This method
     * shouldn't exist as it can and should be implemented into the
     * diagonal check methods. The only reason I am doing this is because there is a
     * bug so this is a temporary fix.
     * 
     * @param boardPositions
     * @param pos
     * @return
     */
    private boolean checkPawnAttack(byte[][] boardPositions, byte[] pos) {
        if (color == Constants.pieceIDs.BLACK) {
            byte pieceToTheRight = (inBoardRange((byte) (pos[0]+1), (byte) (pos[1]+1))) ? boardPositions[pos[0] + 1][pos[1] + 1] : -1;
            byte pieceToTheLeft = (inBoardRange((byte) (pos[0]-1), (byte) (pos[1]+1))) ? boardPositions[pos[0] - 1][pos[1] + 1] : -1;
            if ((pieceToTheRight > 23 && pieceToTheRight < 32) || (pieceToTheLeft > 23 && pieceToTheLeft < 32)) {
                return true;
            } else {
                return false;
            }
        } else {
            byte pieceToTheRight = (inBoardRange((byte) (pos[0]+1), (byte) (pos[1]-1))) ? boardPositions[pos[0] + 1][pos[1] - 1] : -1;
            byte pieceToTheLeft = (inBoardRange((byte) (pos[0]-1), (byte) (pos[1]-1))) ? boardPositions[pos[0] - 1][pos[1] - 1] : -1;
            if ((pieceToTheRight > 7 && pieceToTheRight < 16) || (pieceToTheLeft > 7 && pieceToTheLeft < 16)) {
                return true;
            } else {
                return false;
            }
        }

    }

    /**
     * Method for seeing if the king is under check, specifically checks if it is
     * under check from the left.
     * 
     * @param boardPositions positions of all pieces on the board
     * @param pos            position of the king
     * @return true if king is under check from this direction, false if not.
     */
    private boolean checkLeft(byte[][] boardPositions, byte[] pos) {
        for (int i = pos[0] - 1; i > -1; i--) {
            byte pieceAtSquare = boardPositions[i][pos[1]];
            //
            if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 != color) {
                if (color == Constants.pieceIDs.BLACK) {
                    if (pieceAtSquare == Constants.pieceIDs.WHITE_KINGS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEENS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEEN
                            || pieceAtSquare == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (pieceAtSquare == Constants.pieceIDs.BLACK_KINGS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEENS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEEN
                            || pieceAtSquare == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                        return true;
                    } else {
                        return false;
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
     * Method for seeing if the king is under check, specifically checks if it is
     * under check from the right.
     * 
     * @param boardPositions positions of all pieces on the board
     * @param pos            position of the king
     * @return true if king is under check from this direction, false if not.
     */
    private boolean checkRight(byte[][] boardPositions, byte[] pos) {
        for (int i = pos[0] + 1; i < 8; i++) {
            byte pieceAtSquare = boardPositions[i][pos[1]];
            //
            if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 != color) {
                if (color == Constants.pieceIDs.BLACK) {
                    if (pieceAtSquare == Constants.pieceIDs.WHITE_KINGS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEENS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEEN
                            || pieceAtSquare == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (pieceAtSquare == Constants.pieceIDs.BLACK_KINGS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEENS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEEN
                            || pieceAtSquare == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                        return true;
                    } else {
                        return false;
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
     * Method for seeing if the king is under check, specifically checks if it is
     * under check from the top.
     * 
     * @param boardPositions positions of all pieces on the board
     * @param pos            position of the king
     * @return true if king is under check from this direction, false if not.
     */
    private boolean checkUp(byte[][] boardPositions, byte[] pos) {
        for (int i = pos[1] - 1; i > -1; i--) {
            byte pieceAtSquare = boardPositions[pos[0]][i];
            if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 != color) {
                if (color == Constants.pieceIDs.BLACK) {
                    if (pieceAtSquare == Constants.pieceIDs.WHITE_KINGS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEENS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEEN
                            || pieceAtSquare == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (pieceAtSquare == Constants.pieceIDs.BLACK_KINGS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEENS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEEN
                            || pieceAtSquare == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                        return true;
                    } else {
                        return false;
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
     * Method for seeing if the king is under check, specifically checks if it is
     * under check from the bottom.
     * 
     * @param boardPositions positions of all pieces on the board
     * @param pos            position of the king
     * @return true if king is under check from this direction, false if not.
     */
    private boolean checkDown(byte[][] boardPositions, byte[] pos) {
        for (int i = pos[1] + 1; i < 8; i++) {
            byte pieceAtSquare = boardPositions[pos[0]][i];
            //
            if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 != color) {
                if (color == Constants.pieceIDs.BLACK) {
                    if (pieceAtSquare == Constants.pieceIDs.WHITE_KINGS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEENS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEEN
                            || pieceAtSquare == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (pieceAtSquare == Constants.pieceIDs.BLACK_KINGS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEENS_ROOK
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEEN
                            || pieceAtSquare == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                        return true;
                    } else {
                        return false;
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
     * Method for seeing if the king is under check, specifically checks if it is
     * under check from the top right diagonal.
     * 
     * @param boardPositions positions of all pieces on the board
     * @param pos            position of the king
     * @return true if king is under check from this direction, false if not.
     */
    private boolean checkUpRightDiagonal(byte[][] boardPositions, byte[] pos) {

        int j = pos[1] - 1;
        for (int i = pos[0] + 1; i < 8; i++) {
            if (j < 0) {
                break;
            }
            byte pieceAtSquare = boardPositions[i][j];
            //
            if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 != color) {
                if (color == Constants.pieceIDs.BLACK) {
                    if (pieceAtSquare == Constants.pieceIDs.WHITE_KINGS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEENS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEEN) {
                        return true;
                    } else {
                        break;
                    }
                } else {
                    if (pieceAtSquare == Constants.pieceIDs.BLACK_KINGS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEENS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEEN) {
                        return true;
                    } else {
                        break;
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
     * Method for seeing if the king is under check, specifically checks if it is
     * under check from the top left diagonal.
     * 
     * @param boardPositions positions of all pieces on the board
     * @param pos            position of the king
     * @return true if king is under check from this direction, false if not.
     */
    private boolean checkUpLeftDiagonal(byte[][] boardPositions, byte[] pos) {
        int j = pos[1] - 1;
        for (int i = pos[0] - 1; i > -1; i--) {
            if (j < 0) {
                break;
            }
            byte pieceAtSquare = boardPositions[i][j];
            if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 != color) {
                if (color == Constants.pieceIDs.BLACK) {
                    if (pieceAtSquare == Constants.pieceIDs.WHITE_KINGS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEENS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEEN) {
                        return true;
                    } else {
                        break;
                    }
                } else {
                    if (pieceAtSquare == Constants.pieceIDs.BLACK_KINGS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEENS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEEN) {
                        return true;
                    } else {
                        break;
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
     * Method for seeing if the king is under check, specifically checks if it is
     * under check from the bottom left diagonal.
     * 
     * @param boardPositions positions of all pieces on the board
     * @param pos            position of the king
     * @return true if king is under check from this direction, false if not.
     */
    private boolean checkDownLeftDiagonal(byte[][] boardPositions, byte[] pos) {

        int j = pos[1] + 1;
        for (int i = pos[0] - 1; i > -1; i--) {
            if (j > 7) {
                break;
            }
            byte pieceAtSquare = boardPositions[i][j];
            if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 != color) {
                if (color == Constants.pieceIDs.BLACK) {
                    if (pieceAtSquare == Constants.pieceIDs.WHITE_KINGS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEENS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEEN) {
                        return true;
                    } else {
                        break;
                    }
                } else {
                    if (pieceAtSquare == Constants.pieceIDs.BLACK_KINGS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEENS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEEN) {
                        return true;
                    } else {
                        break;
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
     * Method for seeing if the king is under check, specifically checks if it is
     * under check from the bottom right diagonal.
     * 
     * @param boardPositions positions of all pieces on the board
     * @param pos            position of the king
     * @return true if king is under check from this direction, false if not.
     */
    private boolean checkDownRightDiagonal(byte[][] boardPositions, byte[] pos) {
        int j = pos[1] + 1;
        for (int i = pos[0] + 1; i < 8; i++) {
            if (j > 7) {
                break;
            }
            byte pieceAtSquare = boardPositions[i][j];
            if (pieceAtSquare != Constants.pieceIDs.EMPTY_CELL && pieceAtSquare / 16 != color) {
                if (color == Constants.pieceIDs.BLACK) {
                    if (pieceAtSquare == Constants.pieceIDs.WHITE_KINGS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEENS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.WHITE_QUEEN) {
                        return true;
                    } else {
                        break;
                    }
                } else {
                    if (pieceAtSquare == Constants.pieceIDs.BLACK_KINGS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEENS_BISHOP
                            || pieceAtSquare == Constants.pieceIDs.BLACK_QUEEN) {
                        return true;
                    } else {
                        break;
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
     * 
     * @param pos
     * @return true if the position is in the board, false if not.
     */
    protected boolean inBoardRange(byte[] pos) {
        return (pos[0] > -1 && pos[0] < 8) && (pos[1] > -1 && pos[1] < 8);
    }

    /**
     * Checks to see if this position is in the board.
     * @param x The x position.
     * @param y The y position.
     * @return true if the position is in the board, false if not. 
     */
    protected boolean inBoardRange(byte x, byte y){
        return (x > -1 && x < 8) && (y > -1 && y < 8);
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
     * Method for getting the type of a piece.
     * @return a byte representing the piece type.
     */
    public byte getType(){
        return this.pieceType;
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
