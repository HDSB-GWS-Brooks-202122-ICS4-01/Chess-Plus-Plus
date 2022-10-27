package app.pieces;

import java.util.ArrayList;

import app.App;
import app.util.Constants.PieceIDs;
import app.util.Constants.PieceType;
import javafx.scene.image.ImageView;

/**
 * Abstract class for pieces.
 * 
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
     * @param boardPositions A 2d array containing the current positions on the
     *                       board.
     * @return a 2d array containing all the possible moves based on this board.
     */
    public abstract byte[][] getPossibleMoves(byte[][] boardPositions);

    /**
     * Method for setting the type of a piece when it is instantiated.
     */
    protected void setType() {
        if (id == PieceIDs.BLACK_KING || id == PieceIDs.WHITE_KING) {
            this.pieceType = PieceType.KING;
        } else if (id == PieceIDs.BLACK_QUEEN || id == PieceIDs.WHITE_QUEEN) {
            this.pieceType = PieceType.QUEEN;
        } else if (id == PieceIDs.BLACK_KINGS_BISHOP || id == PieceIDs.BLACK_QUEENS_BISHOP
                || id == PieceIDs.WHITE_KINGS_BISHOP || id == PieceIDs.WHITE_QUEENS_BISHOP) {
            this.pieceType = PieceType.BISHOP;
        } else if (id == PieceIDs.BLACK_KINGS_KNIGHT || id == PieceIDs.BLACK_QUEENS_KNIGHT
                || id == PieceIDs.WHITE_KINGS_KNIGHT || id == PieceIDs.WHITE_QUEENS_KNIGHT) {
            this.pieceType = PieceType.KNIGHT;
        } else if (id == PieceIDs.BLACK_KINGS_ROOK || id == PieceIDs.BLACK_QUEENS_ROOK
                || id == PieceIDs.WHITE_KINGS_ROOK || id == PieceIDs.WHITE_QUEENS_ROOK
                || id == PieceIDs.BLACK_PROMOTED_ROOK || id == PieceIDs.WHITE_PROMOTED_ROOK) {
            this.pieceType = PieceType.ROOK;
        } else {
            this.pieceType = PieceType.PAWN;
        }

    }

    /**
     * Method for getting if the king is not under check. This takes in a possible
     * move,
     * and determines if this new board results in check.
     * 
     * @param boardPositions The positions of pieces on the board.
     * @param possibleMove   The move to check.
     * @param isKing         Whether the piece moving is the king.
     * @return True if the king isn't under check, false if it is.
     */
    public boolean isNotUnderCheck(byte[][] boardPositions, byte[] possibleMove, boolean isKing) {
        // Deep copies postions
        byte[][] newBoardPositions = new byte[8][8];
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                newBoardPositions[x][y] = boardPositions[x][y];
            }
        }

        // System.out.println("Checking this move: " + Arrays.toString(possibleMove));

        // If its the king moving, it needs to change where it gets the king position
        // from.
        byte[] kingPos = new byte[2];
        if (isKing) {
            kingPos[0] = possibleMove[0];
            kingPos[1] = possibleMove[1];
        } else {
            kingPos = getKingPos();
        }
        newBoardPositions[possibleMove[0]][possibleMove[1]] = id;
        newBoardPositions[gridX][gridY] = PieceIDs.EMPTY_CELL;

        if (checkPawnAttack(newBoardPositions, kingPos)) {
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

        if (color == PieceIDs.BLACK) {
            for (byte[] position : positionList) {
                if (!inBoardRange(position)) {
                    continue;
                }
                byte id = newBoardPositions[position[0]][position[1]];
                if (id == PieceIDs.WHITE_KINGS_KNIGHT || id == PieceIDs.WHITE_QUEENS_KNIGHT) {
                    return false;
                }
            }
        } else {
            for (byte[] position : positionList) {
                if (!inBoardRange(position)) {
                    continue;
                }
                byte id = newBoardPositions[position[0]][position[1]];
                if (id == PieceIDs.BLACK_KINGS_KNIGHT || id == PieceIDs.BLACK_QUEENS_KNIGHT) {
                    return false;
                }
            }

        }

        // all the possible positions the enemy king can be relative to the team king if its under check
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

        if (color == PieceIDs.WHITE) {
            for (byte[] kPos : kPosList) {
                if (inBoardRange(kPos) && newBoardPositions[kPos[0]][kPos[1]] == PieceIDs.BLACK_KING) {
                    System.out.println("Check by black king");
                    return false;
                }

            }
        } else {
            for (byte[] kPos : kPosList) {
                if (inBoardRange(kPos) && newBoardPositions[kPos[0]][kPos[1]] == PieceIDs.WHITE_KING) {
                    System.out.println("Check by white king");
                    return false;
                }
            }
        }

        // System.out.println("Done checks");
        return true;

    }

    /**
     * Returns this teams king positions
     * 
     * @return an array of length two that contains the position of the king.
     */
    protected byte[] getKingPos() {
        byte[] pos = new byte[2];
        if (color == PieceIDs.BLACK) {
            pos[0] = App.GAME_PIECES[PieceIDs.BLACK_KING].getGridX();
            pos[1] = App.GAME_PIECES[PieceIDs.BLACK_KING].getGridY();
        } else {
            pos[0] = App.GAME_PIECES[PieceIDs.WHITE_KING].getGridX();
            pos[1] = App.GAME_PIECES[PieceIDs.WHITE_KING].getGridY();
        }
        return pos;
    }

    /**
     * Method for checking if a pawn is attacking the king. 
     * 
     * @param boardPositions The positions of everything on the board.
     * @param pos The position of the king. 
     * @return True if the king is under check from a pawn, false if not.
     */
    private boolean checkPawnAttack(byte[][] boardPositions, byte[] pos) {
        if (color == PieceIDs.BLACK) {
            // for black

            // if the postion is within the range
            byte pieceToTheRight = (inBoardRange((byte) (pos[0] + 1), (byte) (pos[1] + 1)))
                    ? boardPositions[pos[0] + 1][pos[1] + 1]
                    : -1;
            byte pieceToTheLeft = (inBoardRange((byte) (pos[0] - 1), (byte) (pos[1] + 1)))
                    ? boardPositions[pos[0] - 1][pos[1] + 1]
                    : -1;
            if ((pieceToTheRight > PieceIDs.BEGIN_WHITE_PAWNS
                    && pieceToTheRight < PieceIDs.END_WHITE_PAWNS)
                    || (pieceToTheLeft > PieceIDs.BEGIN_WHITE_PAWNS
                            && pieceToTheLeft < PieceIDs.END_WHITE_PAWNS)) {
                // if the piece is an enemy pawn
                return true;
            } else {
                return false;
            }
        } else {
            // for white

            // if the position is within the board range
            byte pieceToTheRight = (inBoardRange((byte) (pos[0] + 1), (byte) (pos[1] - 1)))
                    ? boardPositions[pos[0] + 1][pos[1] - 1]
                    : -1;
            byte pieceToTheLeft = (inBoardRange((byte) (pos[0] - 1), (byte) (pos[1] - 1)))
                    ? boardPositions[pos[0] - 1][pos[1] - 1]
                    : -1;
            if ((pieceToTheRight > PieceIDs.BEGIN_BLACK_PAWNS
                    && pieceToTheRight < PieceIDs.END_BLACK_PAWNS)
                    || (pieceToTheLeft > PieceIDs.BEGIN_BLACK_PAWNS
                            && pieceToTheLeft < PieceIDs.END_BLACK_PAWNS)) {
                // if the piece is an enemy pawn
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
        // iteration for pieces to the left.
        for (int i = pos[0] - 1; i > -1; i--) {
            // the piece to the left
            byte pieceAtSquare = boardPositions[i][pos[1]];

            // if the piece to the left is an enemy piece
            if (pieceAtSquare != PieceIDs.EMPTY_CELL
                    && pieceAtSquare / PieceIDs.COLOR_DIVISOR != color) {

                // if the color is black, else white
                if (color == PieceIDs.BLACK) {

                    // if the piece is a rook or promoted rook.
                    if (pieceAtSquare == PieceIDs.WHITE_KINGS_ROOK
                            || pieceAtSquare == PieceIDs.WHITE_QUEENS_ROOK
                            || pieceAtSquare == PieceIDs.WHITE_QUEEN
                            || pieceAtSquare == PieceIDs.WHITE_PROMOTED_ROOK) {
                        return true;
                    } else {
                        // if its not return
                        return false;
                    }
                } else {
                    if (pieceAtSquare == PieceIDs.BLACK_KINGS_ROOK
                            || pieceAtSquare == PieceIDs.BLACK_QUEENS_ROOK
                            || pieceAtSquare == PieceIDs.BLACK_QUEEN
                            || pieceAtSquare == PieceIDs.BLACK_PROMOTED_ROOK) {

                        return true;
                    } else {
                        return false;
                    }

                }
            } else if (pieceAtSquare != PieceIDs.EMPTY_CELL
                    && pieceAtSquare / PieceIDs.COLOR_DIVISOR == color) {
                // stops checking if the piece that is there is a teammate.
                return false;
            } else {
                // continues to the next iteration if there is nothing there.
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
        // iteration for pieces to the right
        // horizontal checks and vertical checks all use the same logic, just different
        // iteration

        for (int i = pos[0] + 1; i < 8; i++) {
            byte pieceAtSquare = boardPositions[i][pos[1]];
            if (pieceAtSquare != PieceIDs.EMPTY_CELL
                    && pieceAtSquare / PieceIDs.COLOR_DIVISOR != color) {
                if (color == PieceIDs.BLACK) {
                    if (pieceAtSquare == PieceIDs.WHITE_KINGS_ROOK
                            || pieceAtSquare == PieceIDs.WHITE_QUEENS_ROOK
                            || pieceAtSquare == PieceIDs.WHITE_QUEEN
                            || pieceAtSquare == PieceIDs.WHITE_PROMOTED_ROOK) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (pieceAtSquare == PieceIDs.BLACK_KINGS_ROOK
                            || pieceAtSquare == PieceIDs.BLACK_QUEENS_ROOK
                            || pieceAtSquare == PieceIDs.BLACK_QUEEN
                            || pieceAtSquare == PieceIDs.BLACK_PROMOTED_ROOK) {
                        return true;
                    } else {
                        return false;
                    }

                }
            } else if (pieceAtSquare != PieceIDs.EMPTY_CELL
                    && pieceAtSquare / PieceIDs.COLOR_DIVISOR == color) {
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
        // iteration for going up
        // origin of the grid is the top left corner

        for (int i = pos[1] - 1; i > -1; i--) {
            byte pieceAtSquare = boardPositions[pos[0]][i];
            if (pieceAtSquare != PieceIDs.EMPTY_CELL
                    && pieceAtSquare / PieceIDs.COLOR_DIVISOR != color) {
                // if the piece is an enemy piece
                if (color == PieceIDs.BLACK) {
                    if (pieceAtSquare == PieceIDs.WHITE_KINGS_ROOK
                            || pieceAtSquare == PieceIDs.WHITE_QUEENS_ROOK
                            || pieceAtSquare == PieceIDs.WHITE_QUEEN
                            || pieceAtSquare == PieceIDs.WHITE_PROMOTED_ROOK) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (pieceAtSquare == PieceIDs.BLACK_KINGS_ROOK
                            || pieceAtSquare == PieceIDs.BLACK_QUEENS_ROOK
                            || pieceAtSquare == PieceIDs.BLACK_QUEEN
                            || pieceAtSquare == PieceIDs.BLACK_PROMOTED_ROOK) {
                        return true;
                    } else {
                        return false;
                    }

                }
            } else if (pieceAtSquare != PieceIDs.EMPTY_CELL
                    && pieceAtSquare / PieceIDs.COLOR_DIVISOR == color) {
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
        // iteration for going down
        for (int i = pos[1] + 1; i < 8; i++) {
            byte pieceAtSquare = boardPositions[pos[0]][i];

            if (pieceAtSquare != PieceIDs.EMPTY_CELL
                    && pieceAtSquare / PieceIDs.COLOR_DIVISOR != color) {
                // if the piece at the square is an enemy piece
                if (color == PieceIDs.BLACK) {
                    if (pieceAtSquare == PieceIDs.WHITE_KINGS_ROOK
                            || pieceAtSquare == PieceIDs.WHITE_QUEENS_ROOK
                            || pieceAtSquare == PieceIDs.WHITE_QUEEN
                            || pieceAtSquare == PieceIDs.WHITE_PROMOTED_ROOK) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (pieceAtSquare == PieceIDs.BLACK_KINGS_ROOK
                            || pieceAtSquare == PieceIDs.BLACK_QUEENS_ROOK
                            || pieceAtSquare == PieceIDs.BLACK_QUEEN
                            || pieceAtSquare == PieceIDs.BLACK_PROMOTED_ROOK) {
                        return true;
                    } else {
                        return false;
                    }

                }
            } else if (pieceAtSquare != PieceIDs.EMPTY_CELL
                    && pieceAtSquare / PieceIDs.COLOR_DIVISOR == color) {
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

        // iteration for checking the up right diagonal, requires two iterators.
        int j = pos[1] - 1;
        for (int i = pos[0] + 1; i < 8; i++) {
            if (j < 0) {
                // if the y is out of range, break out from loop
                break;
            }
            byte pieceAtSquare = boardPositions[i][j];

            if (pieceAtSquare != PieceIDs.EMPTY_CELL
                    && pieceAtSquare / PieceIDs.COLOR_DIVISOR != color) {
                // if the piece at the square is an enemy piece
                if (color == PieceIDs.BLACK) {
                    if (pieceAtSquare == PieceIDs.WHITE_KINGS_BISHOP
                            || pieceAtSquare == PieceIDs.WHITE_QUEENS_BISHOP
                            || pieceAtSquare == PieceIDs.WHITE_QUEEN) {
                        // if the piece is a bishop or queen.
                        return true;
                    } else {
                        break;
                    }
                } else {
                    if (pieceAtSquare == PieceIDs.BLACK_KINGS_BISHOP
                            || pieceAtSquare == PieceIDs.BLACK_QUEENS_BISHOP
                            || pieceAtSquare == PieceIDs.BLACK_QUEEN) {
                        return true;
                    } else {
                        break;
                    }

                }
            } else if (pieceAtSquare != PieceIDs.EMPTY_CELL
                    && pieceAtSquare / PieceIDs.COLOR_DIVISOR == color) {
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
        // iteration for checking the up left diagonal

        int j = pos[1] - 1;
        for (int i = pos[0] - 1; i > -1; i--) {
            if (j < 0) {
                break;
            }
            byte pieceAtSquare = boardPositions[i][j];
            if (pieceAtSquare != PieceIDs.EMPTY_CELL
                    && pieceAtSquare / PieceIDs.COLOR_DIVISOR != color) {
                if (color == PieceIDs.BLACK) {
                    if (pieceAtSquare == PieceIDs.WHITE_KINGS_BISHOP
                            || pieceAtSquare == PieceIDs.WHITE_QUEENS_BISHOP
                            || pieceAtSquare == PieceIDs.WHITE_QUEEN) {
                        return true;
                    } else {
                        break;
                    }
                } else {
                    if (pieceAtSquare == PieceIDs.BLACK_KINGS_BISHOP
                            || pieceAtSquare == PieceIDs.BLACK_QUEENS_BISHOP
                            || pieceAtSquare == PieceIDs.BLACK_QUEEN) {
                        return true;
                    } else {
                        break;
                    }

                }
            } else if (pieceAtSquare != PieceIDs.EMPTY_CELL
                    && pieceAtSquare / PieceIDs.COLOR_DIVISOR == color) {
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
        // iteration for down left diagonal.

        int j = pos[1] + 1;
        for (int i = pos[0] - 1; i > -1; i--) {
            if (j > 7) {
                break;
            }
            byte pieceAtSquare = boardPositions[i][j];
            if (pieceAtSquare != PieceIDs.EMPTY_CELL
                    && pieceAtSquare / PieceIDs.COLOR_DIVISOR != color) {
                if (color == PieceIDs.BLACK) {
                    if (pieceAtSquare == PieceIDs.WHITE_KINGS_BISHOP
                            || pieceAtSquare == PieceIDs.WHITE_QUEENS_BISHOP
                            || pieceAtSquare == PieceIDs.WHITE_QUEEN) {
                        return true;
                    } else {
                        break;
                    }
                } else {
                    if (pieceAtSquare == PieceIDs.BLACK_KINGS_BISHOP
                            || pieceAtSquare == PieceIDs.BLACK_QUEENS_BISHOP
                            || pieceAtSquare == PieceIDs.BLACK_QUEEN) {
                        return true;
                    } else {
                        break;
                    }

                }
            } else if (pieceAtSquare != PieceIDs.EMPTY_CELL
                    && pieceAtSquare / PieceIDs.COLOR_DIVISOR == color) {
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
        // iteration for the down right diagonal.
        int j = pos[1] + 1;
        for (int i = pos[0] + 1; i < 8; i++) {
            if (j > 7) {
                break;
            }
            byte pieceAtSquare = boardPositions[i][j];

            if (pieceAtSquare != PieceIDs.EMPTY_CELL
                    && pieceAtSquare / PieceIDs.COLOR_DIVISOR != color) {

                if (color == PieceIDs.BLACK) {
                    if (pieceAtSquare == PieceIDs.WHITE_KINGS_BISHOP
                            || pieceAtSquare == PieceIDs.WHITE_QUEENS_BISHOP
                            || pieceAtSquare == PieceIDs.WHITE_QUEEN) {
                        return true;
                    } else {
                        break;
                    }
                } else {

                    if (pieceAtSquare == PieceIDs.BLACK_KINGS_BISHOP
                            || pieceAtSquare == PieceIDs.BLACK_QUEENS_BISHOP
                            || pieceAtSquare == PieceIDs.BLACK_QUEEN) {
                        return true;
                    } else {
                        break;
                    }

                }
            } else if (pieceAtSquare != PieceIDs.EMPTY_CELL
                    && pieceAtSquare / PieceIDs.COLOR_DIVISOR == color) {
                return false;
            } else {
                j++;
                continue;
            }
        }
        return false;
    }

    /**
     * Recursive method for getting the possible moves for a queen diagonally up and
     * to the right. Adds to an arraylist of moves
     * and returns the arraylist when it has added all possible moves in this
     * direction.
     * 
     * @param possibleMoves  Array list of byte arrays to add to.
     * @param boardPositions Current positions of the whole board.
     * @param currentLoc     Positions to check from.
     * @return An array list of byte arrays containing all the added moves.
     */
    protected ArrayList<byte[]> upRightDiagonalMoves(ArrayList<byte[]> possibleMoves, byte[][] boardPositions,
            byte[] currentLoc) {

        if (currentLoc[0] + 1 < 8 && currentLoc[1] - 1 > -1) {
            //if the current location is within the board range. 


            byte upRightSquare = boardPositions[currentLoc[0] + 1][currentLoc[1] - 1];
            byte[] possibleMove = { (byte) (currentLoc[0] + 1), (byte) (currentLoc[1] - 1) };

            if (upRightSquare == PieceIDs.EMPTY_CELL) {
                // if the square one up and to the right is empty, add that square and repeat
                // process for that square
                if (isNotUnderCheck(boardPositions, possibleMove, false)) {
                    possibleMoves.add(possibleMove);
                }
                return upRightDiagonalMoves(possibleMoves, boardPositions, possibleMove);
            } else {
                if (upRightSquare / PieceIDs.COLOR_DIVISOR != color) {
                    if (isNotUnderCheck(boardPositions, possibleMove, false)) {
                        possibleMoves.add(possibleMove);
                    }
                }
                return possibleMoves;
            }

        } else {
            return possibleMoves;
        }

    }

    /**
     * Recursive method for getting the possible moves for a queen diagonally up and
     * to the left. Adds to an arraylist of moves
     * and returns the arraylist when it has added all possible moves in this
     * direction.
     * 
     * @param possibleMoves  Array list of byte arrays to add to.
     * @param boardPositions Current positions of the whole board.
     * @param currentLoc     Positions to check from.
     * @return An array list of byte arrays containing all the added moves.
     */
    protected ArrayList<byte[]> upLeftDiagonalMoves(ArrayList<byte[]> possibleMoves, byte[][] boardPositions,
            byte[] currentLoc) {
        if (currentLoc[0] - 1 > -1 && currentLoc[1] - 1 > -1) {
            byte upRightSquare = boardPositions[currentLoc[0] - 1][currentLoc[1] - 1];
            byte[] possibleMove = { (byte) (currentLoc[0] - 1), (byte) (currentLoc[1] - 1) };

            if (upRightSquare == PieceIDs.EMPTY_CELL) {
                // if the square one up and to the right is empty, add that square and repeat
                // process for that square
                if (isNotUnderCheck(boardPositions, possibleMove, false)) {
                    possibleMoves.add(possibleMove);
                }
                return upLeftDiagonalMoves(possibleMoves, boardPositions, possibleMove);
            } else {
                if (upRightSquare / PieceIDs.COLOR_DIVISOR != color) {
                    if (isNotUnderCheck(boardPositions, possibleMove, false)) {
                        possibleMoves.add(possibleMove);
                    }
                }
                return possibleMoves;
            }

        } else {
            return possibleMoves;
        }
    }

    /**
     * Recursive method for getting the possible moves for a queen diagonally down
     * and to the right. Adds to an arraylist of moves
     * and returns the arraylist when it has added all possible moves in this
     * direction.
     * 
     * @param possibleMoves  Array list of byte arrays to add to.
     * @param boardPositions Current positions of the whole board.
     * @param currentLoc     Positions to check from.
     * @return An array list of byte arrays containing all the added moves.
     */
    protected ArrayList<byte[]> downRightDiagonalMoves(ArrayList<byte[]> possibleMoves, byte[][] boardPositions,
            byte[] currentLoc) {
        if (currentLoc[0] + 1 < 8 && currentLoc[1] + 1 < 8) {
            byte upRightSquare = boardPositions[currentLoc[0] + 1][currentLoc[1] + 1];
            byte[] possibleMove = { (byte) (currentLoc[0] + 1), (byte) (currentLoc[1] + 1) };

            if (upRightSquare == PieceIDs.EMPTY_CELL) {
                // if the square one up and to the right is empty, add that square and repeat
                // process for that square
                if (isNotUnderCheck(boardPositions, possibleMove, false)) {
                    possibleMoves.add(possibleMove);
                }
                return downRightDiagonalMoves(possibleMoves, boardPositions, possibleMove);
            } else {
                if (upRightSquare / PieceIDs.COLOR_DIVISOR != color) {
                    if (isNotUnderCheck(boardPositions, possibleMove, false)) {
                        possibleMoves.add(possibleMove);
                    }
                }
                return possibleMoves;
            }

        } else {
            return possibleMoves;
        }

    }

    /**
     * Recursive method for getting the possible moves for a queen diagonally down
     * and to the left. Adds to an arraylist of moves
     * and returns the arraylist when it has added all possible moves in this
     * direction.
     * 
     * @param possibleMoves  Array list of byte arrays to add to.
     * @param boardPositions Current positions of the whole board.
     * @param currentLoc     Positions to check from.
     * @return An array list of byte arrays containing all the added moves.
     */
    protected ArrayList<byte[]> downLeftDiagonalMoves(ArrayList<byte[]> possibleMoves, byte[][] boardPositions,
            byte[] currentLoc) {
        if (currentLoc[0] - 1 > -1 && currentLoc[1] + 1 < 8) {
            byte upRightSquare = boardPositions[currentLoc[0] - 1][currentLoc[1] + 1];
            byte[] possibleMove = { (byte) (currentLoc[0] - 1), (byte) (currentLoc[1] + 1) };

            if (upRightSquare == PieceIDs.EMPTY_CELL) {
                // if the square one up and to the right is empty, add that square and repeat
                // process for that square
                if (isNotUnderCheck(boardPositions, possibleMove, false)) {
                    possibleMoves.add(possibleMove);
                }
                return downLeftDiagonalMoves(possibleMoves, boardPositions, possibleMove);
            } else {
                if (upRightSquare / PieceIDs.COLOR_DIVISOR != color) {
                    if (isNotUnderCheck(boardPositions, possibleMove, false)) {
                        possibleMoves.add(possibleMove);
                    }
                }
                return possibleMoves;
            }

        } else {
            return possibleMoves;
        }

    }

    /**
     * Recursive method for getting the possible moves for a queen moving up. Adds
     * to an arraylist of moves
     * and returns the arraylist when it has added all possible moves in this
     * direction.
     * 
     * @param possibleMoves  Array list of byte arrays to add to.
     * @param boardPositions Current positions of the whole board.
     * @param currentLoc     Positions to check from.
     * @return An array list of byte arrays containing all the added moves.
     */
    protected ArrayList<byte[]> upMoves(ArrayList<byte[]> possibleMoves, byte[][] boardPositions, byte[] currentLoc) {
        if (currentLoc[1] - 1 > -1) {
            // if the location above this piece is within the board.

            // array containing the location
            byte[] possibleMove = { currentLoc[0], (byte) (currentLoc[1] - 1) };

            // the piece located directly above.
            byte upSquare = boardPositions[possibleMove[0]][possibleMove[1]];

            if (upSquare == PieceIDs.EMPTY_CELL) {
                if (isNotUnderCheck(boardPositions, possibleMove, false)) {
                    possibleMoves.add(possibleMove);
                }
                return upMoves(possibleMoves, boardPositions, possibleMove);
            } else {
                if (upSquare / PieceIDs.COLOR_DIVISOR != color) {
                    if (isNotUnderCheck(boardPositions, possibleMove, false)) {
                        possibleMoves.add(possibleMove);
                    }
                }
                return possibleMoves;
            }
        } else {
            return possibleMoves;
        }
    }

    /**
     * Recursive method for getting the possible moves for a queen moving down. Adds
     * to an arraylist of moves
     * and returns the arraylist when it has added all possible moves in this
     * direction.
     * 
     * @param possibleMoves  Array list of byte arrays to add to.
     * @param boardPositions Current positions of the whole board.
     * @param currentLoc     Positions to check from.
     * @return An array list of byte arrays containing all the added moves.
     */
    protected ArrayList<byte[]> downMoves(ArrayList<byte[]> possibleMoves, byte[][] boardPositions, byte[] currentLoc) {
        if (currentLoc[1] + 1 < 8) {
            byte[] possibleMove = { currentLoc[0], (byte) (currentLoc[1] + 1) };
            byte upSquare = boardPositions[possibleMove[0]][possibleMove[1]];

            if (upSquare == PieceIDs.EMPTY_CELL) {
                if (isNotUnderCheck(boardPositions, possibleMove, false)) {
                    possibleMoves.add(possibleMove);
                }
                return downMoves(possibleMoves, boardPositions, possibleMove);
            } else {
                if (upSquare / PieceIDs.COLOR_DIVISOR != color) {
                    if (isNotUnderCheck(boardPositions, possibleMove, false)) {
                        possibleMoves.add(possibleMove);
                    }
                }
                return possibleMoves;
            }
        } else {
            return possibleMoves;
        }
    }

    /**
     * Recursive method for getting the possible moves for a queen moving left. Adds
     * to an arraylist of moves
     * and returns the arraylist when it has added all possible moves in this
     * direction.
     * 
     * @param possibleMoves  Array list of byte arrays to add to.
     * @param boardPositions Current positions of the whole board.
     * @param currentLoc     Positions to check from.
     * @return An array list of byte arrays containing all the added moves.
     */
    protected ArrayList<byte[]> leftMoves(ArrayList<byte[]> possibleMoves, byte[][] boardPositions, byte[] currentLoc) {
        if (currentLoc[0] - 1 > -1) {
            byte[] possibleMove = { (byte) (currentLoc[0] - 1), currentLoc[1] };
            byte upSquare = boardPositions[possibleMove[0]][possibleMove[1]];

            if (upSquare == PieceIDs.EMPTY_CELL) {
                if (isNotUnderCheck(boardPositions, possibleMove, false)) {
                    possibleMoves.add(possibleMove);
                }
                return leftMoves(possibleMoves, boardPositions, possibleMove);
            } else {
                if (upSquare / PieceIDs.COLOR_DIVISOR != color) {
                    if (isNotUnderCheck(boardPositions, possibleMove, false)) {
                        possibleMoves.add(possibleMove);
                    }
                }
                return possibleMoves;
            }
        } else {
            return possibleMoves;
        }
    }

    /**
     * Recursive method for getting the possible moves for a queen moving right.
     * Adds to an arraylist of moves
     * and returns the arraylist when it has added all possible moves in this
     * direction.
     * 
     * @param possibleMoves  Array list of byte arrays to add to.
     * @param boardPositions Current positions of the whole board.
     * @param currentLoc     Positions to check from.
     * @return An array list of byte arrays containing all the added moves.
     */
    protected ArrayList<byte[]> rightMoves(ArrayList<byte[]> possibleMoves, byte[][] boardPositions,
            byte[] currentLoc) {
        // if the location to the right is in the range.
        if (currentLoc[0] + 1 < 8) {
            byte[] possibleMove = { (byte) (currentLoc[0] + 1), currentLoc[1] };
            byte upSquare = boardPositions[possibleMove[0]][possibleMove[1]];

            if (upSquare == PieceIDs.EMPTY_CELL) {
                if (isNotUnderCheck(boardPositions, possibleMove, false)) {
                    possibleMoves.add(possibleMove);
                }
                return rightMoves(possibleMoves, boardPositions, possibleMove);
            } else {
                if (upSquare / PieceIDs.COLOR_DIVISOR != color) {
                    if (isNotUnderCheck(boardPositions, possibleMove, false)) {
                        possibleMoves.add(possibleMove);
                    }
                }
                return possibleMoves;
            }
        } else {
            return possibleMoves;
        }
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
     * 
     * @param x The x position.
     * @param y The y position.
     * @return true if the position is in the board, false if not.
     */
    protected boolean inBoardRange(byte x, byte y) {
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
     * 
     * @return a byte representing the piece type.
     */
    public byte getType() {
        return this.pieceType;
    }

    /**
     * Method for getting the grid x value.
     * 
     * @return The x position of this piece on the grid.
     */
    public byte getGridX() {
        return this.gridX;
    }

    /**
     * Method for getting the grid y value.
     * 
     * @return The y position of this piece on the grid.
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

    /**
     * Returns whether the piece has moved
     * @return
     */
    public boolean getHasMoved(){
        return hasMoved;
    }

    /**
     * Sets hasMoved attribute
     * @param b 
     */
    public void setHasMoved(boolean b) {
        hasMoved = b;
    }

}
