import java.util.ArrayList;

public class Bot {
    public final BoardInteractions BI = new BoardInteractions();
    int depth;
    final boolean black;

    /**
     * Constructor for the bot.
     * 
     * @param diff  The difficulty the bot is set to.
     * @param black The color the bot is playing as.
     */
    public Bot(String diff, boolean black) {
        switch (diff) {
            case "easy":
                depth = 4;
                break;
            case "medium":
                depth = 6;
                break;
            case "hard":
                depth = 8;
                break;
            default:
                depth = 4;
                break;
        }
        this.depth = 4;
        this.black = black;
    }

    /**
     * Method for getting the move the bot wants to perform.
     * 
     * @param boardPositions
     * @param deadPiecesObjects
     * @return
     */
    public String getMove(byte[][] boardPositions, ArrayList<Piece> deadPiecesObjects) {

        // creating array containing hasmoved data
        boolean[] hasMoved = new boolean[6];
        hasMoved[0] = App.GAME_PIECES[Constants.pieceIDs.BLACK_KINGS_ROOK].hasMoved;
        hasMoved[1] = App.GAME_PIECES[Constants.pieceIDs.BLACK_QUEENS_ROOK].hasMoved;
        hasMoved[2] = App.GAME_PIECES[Constants.pieceIDs.WHITE_KINGS_ROOK].hasMoved;
        hasMoved[3] = App.GAME_PIECES[Constants.pieceIDs.WHITE_QUEENS_ROOK].hasMoved;
        hasMoved[4] = App.GAME_PIECES[Constants.pieceIDs.BLACK_KING].hasMoved;
        hasMoved[5] = App.GAME_PIECES[Constants.pieceIDs.WHITE_KING].hasMoved;

        // creating array containing passant data
        int[] passant = new int[16];
        for (int i = 0; i < 8; i++) {
            // data for black pawns
            passant[i] = ((Pawn) App.GAME_PIECES[i + 8]).passant;
        }
        for (int i = 8; i < 16; i++) {
            // data for white pawns
            passant[i] = ((Pawn) App.GAME_PIECES[i + 16]).passant;
        }

        BoardInfo boardInfo = new BoardInfo(boardPositions, passant, hasMoved, App.MOVE_COUNT);
        boardInfo.setKingPos(true, App.GAME_PIECES[Constants.pieceIDs.BLACK_KING].gridX,
                App.GAME_PIECES[Constants.pieceIDs.BLACK_KING].gridY);
        boardInfo.setKingPos(false, App.GAME_PIECES[Constants.pieceIDs.WHITE_KING].gridX,
                App.GAME_PIECES[Constants.pieceIDs.WHITE_KING].gridY);


        BoardInfo calculated = minimax(depth, black, boardInfo);
        System.out.println("Move: " + calculated.previousMove);
        return calculated.previousMove;
    }

    /**
     * Minimax algorithm used for the A.I.
     * 
     * @param depth     The depth to continue the recursion to.
     * @param max       Variable for determining whether it is maximizing, or
     *                  minimizing, true if maximizing, false if minimizing,
     * @param boardInfo The board to generate from.
     * @return The best board.
     */
    private BoardInfo minimax(int depth, boolean max, BoardInfo boardInfo) {
        BoardInfo lastChild = null;
        BoardInfo boardToEvaluate;
        BoardInfo[] children = generateBoards(boardInfo, max);
        if (depth == 0 || children.length == 0 || children == null) {
            // end case for recursions, at this point the board has generated the
            return boardInfo;
        }

        // If it is trying to maximize, the initial eval is set to the lowest so that
        // any eval that comes after is higher
        // If it is trying to minimize, the intial eval is set to the highest so that
        // any eval that comes after is lower

        int lastEval;
        int thisEval;

        if (max) {
            lastEval = -1000;
            // Generates boards that come after this board.
            // System.out.println("Children" + Arrays.toString(children));

            for (BoardInfo child : children) {
                // Sorts through the children to find the highest or lowest one, returns the
                // highest/lowest.
                boardToEvaluate = minimax(depth - 1, !max, child);
                thisEval = evaluate(boardToEvaluate);
                if (thisEval > lastEval) {
                    lastEval = thisEval;
                    lastChild = boardToEvaluate;
                }
            }
        } else {
            lastEval = +1000;
            // Generates boards that come after this board.

            for (BoardInfo child : children) {
                // Sorts through the children to find the highest or lowest one, returns the
                // highest/lowest.

                boardToEvaluate = minimax(depth - 1, !max, child);
                thisEval = evaluate(boardToEvaluate);
                if (thisEval < lastEval) {
                    lastEval = thisEval;
                    lastChild = boardToEvaluate;
                }
            }
        }

        return lastChild;

    }

    /**
     * Method for evaluating the value of the board. Evaluating a board means to
     * apply a value for it,
     * this is used in the minimax algorithm.
     * 
     * @return an Integer represnting the score for this board.
     */
    public int evaluate(BoardInfo boardInfo) {
        // black is maximizing colors
        int score = 0;

        if (!isNotUnderCheck(boardInfo, true)) {
            // System.out.println("Check for black Previous move leading to this one: " +
            // boardInfo.previousMove);
            score += 100;
        } else if (!isNotUnderCheck(boardInfo, false)) {
            // System.out.println("Check for white Previous move leading to this one: " +
            // boardInfo.previousMove);
            score -= 100;
        }

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                byte id = boardInfo.board[x][y];
                boolean color = ((id / Constants.pieceIDs.COLOR_DIVISOR == Constants.pieceIDs.BLACK) || id == Constants.pieceIDs.BLACK_PROMOTED_ROOK);
                if (id == Constants.pieceIDs.EMPTY_CELL) {
                    continue;
                }
                if (color) {
                    score++;
                } else {
                    score--;
                }
                score += Constants.ScoringIDs.scoringMap[id];
            }
        }

        return score;
    }

    /**
     * Method for generating all the possible boards/outcomes from this board.
     * 
     * @param boardInfo The original board to generate from.
     * @return An array of BoardInfo objects, representing all the possible moves
     *         generated from the original board.
     */
    private BoardInfo[] generateBoards(BoardInfo boardInfo, boolean color) {
        // TODO Akil finish this
        ArrayList<BoardInfo> generatedBoards = new ArrayList<BoardInfo>();
        // System.out.println("Color to generate for: " + ((color) ? "black" :
        // "white"));

        byte colorId = (color) ? Constants.pieceIDs.BLACK : Constants.pieceIDs.WHITE;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                byte id = boardInfo.board[x][y];
                if (id == Constants.pieceIDs.EMPTY_CELL || id / Constants.pieceIDs.COLOR_DIVISOR != colorId
                        || (color && id == Constants.pieceIDs.WHITE_PROMOTED_ROOK)
                        || (!color && id == Constants.pieceIDs.BLACK_PROMOTED_ROOK)) {
                    //move onto next iteration if the id is an empty cell oh a piece that is not on this team.
                    continue;
                }
                // if the piece is the color it is generating for.

                if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.WHITE_KINGS_ROOK
                        || id == Constants.pieceIDs.BLACK_QUEENS_ROOK
                        || id == Constants.pieceIDs.WHITE_QUEENS_ROOK
                        || id == Constants.pieceIDs.BLACK_PROMOTED_ROOK
                        || id == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {

                    // if the piece it is generating for is a rook

                    generatedBoards = movesRook(generatedBoards, boardInfo, x, y, color, id);
                    // System.out.println("Boards generated for rook " + id + ": " +
                    // generatedBoards.size());
                } else if (id == Constants.pieceIDs.BLACK_KINGS_BISHOP
                        || id == Constants.pieceIDs.BLACK_QUEENS_BISHOP
                        || id == Constants.pieceIDs.WHITE_QUEENS_BISHOP
                        || id == Constants.pieceIDs.WHITE_KINGS_BISHOP) {

                    // if the piece it is generating for is a bishop

                    generatedBoards = movesBishop(generatedBoards, boardInfo, x, y, color, id);
                    // System.out.println("Boards generated for bishop " + id + ": " +
                    // generatedBoards.size());

                } else if (id == Constants.pieceIDs.BLACK_QUEEN || id == Constants.pieceIDs.WHITE_QUEEN) {
                    // if the piece it is generating for is a queen
                    generatedBoards = movesRook(generatedBoards, boardInfo, x, y, color, id);
                    generatedBoards = movesBishop(generatedBoards, boardInfo, x, y, color, id);
                    // System.out.println("Boards generated for queen " + id + ": " +
                    // generatedBoards.size());
                } else if (id == Constants.pieceIDs.BLACK_KINGS_KNIGHT
                        || id == Constants.pieceIDs.BLACK_QUEENS_KNIGHT
                        || id == Constants.pieceIDs.WHITE_QUEENS_KNIGHT
                        || id == Constants.pieceIDs.WHITE_KINGS_KNIGHT) {
                    // if the piece it is generating for is a knight
                    generatedBoards = movesKnight(generatedBoards, boardInfo, x, y, color, id);
                    // System.out.println("Boards generated for kngiht " + id + ": " +
                    // generatedBoards.size());
                } else if (id == Constants.pieceIDs.BLACK_KING || id == Constants.pieceIDs.WHITE_KING) {
                    // if the piece it is generating for is a king
                    generatedBoards = movesKing(generatedBoards, boardInfo, x, y, color, id);
                    // System.out.println("Boards generated for king " + id + ": " +
                    // generatedBoards.size());
                } else {
                    // if the piece it is generating for is a pawn
                    generatedBoards = movesPawn(generatedBoards, boardInfo, x, y, color, id);
                    // System.out.println("Boards generated for pawn " + id + ": " +
                    // generatedBoards.size());
                }

            }

        }
        BoardInfo[] generatedBoardsArray = generatedBoards.toArray(new BoardInfo[generatedBoards.size()]);

        return generatedBoardsArray;
    }

    /**
     * Method for determining if the king is under check.
     * 
     * @param boardInfo BoardInfo object containing the board data.
     * @param color     Color of the piece, true means black, false means white.
     * 
     * @return true if the king isn't under check, false if it is.
     */
    boolean isNotUnderCheck(BoardInfo boardInfo, boolean color) {
        int x;
        int y;
        byte queen;
        byte queensRook;
        byte kingsRook;
        byte promotedRook;
        byte queensBishop;
        byte kingsBishop;
        byte kingsKnight;
        byte queensKnight;
        byte beginPawnRange;
        byte endPawnRange;
        byte pawnDirection;
        if (color) {
            x = boardInfo.blackKingX;
            y = boardInfo.blackKingY;
            queen = Constants.pieceIDs.WHITE_QUEEN;
            queensRook = Constants.pieceIDs.WHITE_QUEENS_ROOK;
            kingsRook = Constants.pieceIDs.WHITE_KINGS_ROOK;
            promotedRook = Constants.pieceIDs.WHITE_PROMOTED_ROOK;
            queensBishop = Constants.pieceIDs.WHITE_QUEENS_BISHOP;
            kingsBishop = Constants.pieceIDs.WHITE_KINGS_BISHOP;
            kingsKnight = Constants.pieceIDs.WHITE_KINGS_KNIGHT;
            queensKnight = Constants.pieceIDs.WHITE_QUEENS_KNIGHT;

            // range for other teams pawns
            beginPawnRange = Constants.pieceIDs.BEGIN_WHITE_PAWNS;
            endPawnRange = Constants.pieceIDs.END_WHITE_PAWNS;
            pawnDirection = 1;

        } else {
            x = boardInfo.whiteKingX;
            y = boardInfo.whiteKingY;
            queen = Constants.pieceIDs.BLACK_QUEEN;
            queensRook = Constants.pieceIDs.BLACK_QUEENS_ROOK;
            kingsRook = Constants.pieceIDs.BLACK_KINGS_ROOK;
            promotedRook = Constants.pieceIDs.BLACK_PROMOTED_ROOK;
            queensBishop = Constants.pieceIDs.BLACK_QUEENS_BISHOP;
            kingsBishop = Constants.pieceIDs.BLACK_KINGS_BISHOP;
            kingsKnight = Constants.pieceIDs.BLACK_KINGS_KNIGHT;
            queensKnight = Constants.pieceIDs.BLACK_QUEENS_KNIGHT;

            // range for other teams pawns
            beginPawnRange = Constants.pieceIDs.BEGIN_BLACK_PAWNS;
            endPawnRange = Constants.pieceIDs.END_BLACK_PAWNS;
            pawnDirection = -1;

        }

        byte[][] boardPositions = boardInfo.board;
        boolean up = true, down = true, left = true, right = true, upRight = true, downRight = true, upLeft = true,
                downLeft = true;

        // iterator for going up
        int u = y - 1;

        // iterator for going down
        int d = y + 1;

        // iterator for going to the right
        int r = x + 1;

        // iterator for going to the left
        int l = x - 1;

        while (left || right || up || down || upRight || upLeft || downRight || downLeft) {
            // left check
            if (l > -1 && left) {
                if (boardPositions[l][y] == queen
                        || boardPositions[l][y] == kingsRook
                        || boardPositions[l][y] == queensRook
                        || boardPositions[l][y] == promotedRook) {
                    return false;
                } else if (boardPositions[l][y] != Constants.pieceIDs.EMPTY_CELL) {
                    left = false;
                }
            }

            // right check
            if (r < 8 && right) {
                if (boardPositions[r][y] == queen
                        || boardPositions[r][y] == kingsRook
                        || boardPositions[r][y] == queensRook
                        || boardPositions[r][y] == promotedRook) {
                    return false;
                } else if (boardPositions[r][y] != Constants.pieceIDs.EMPTY_CELL) {
                    right = false;
                }
            }

            // down check
            if (d < 8 && down) {
                if (boardPositions[x][d] == queen
                        || boardPositions[x][d] == kingsRook
                        || boardPositions[x][d] == queensRook
                        || boardPositions[x][d] == promotedRook) {
                    return false;
                } else if (boardPositions[x][d] != Constants.pieceIDs.EMPTY_CELL) {
                    down = false;
                }
            }

            // up check
            if (u > -1 && up) {
                if (boardPositions[x][u] == queen
                        || boardPositions[x][u] == kingsRook
                        || boardPositions[x][u] == queensRook
                        || boardPositions[x][u] == promotedRook) {
                    return false;
                } else if (boardPositions[x][u] != Constants.pieceIDs.EMPTY_CELL) {
                    up = false;
                }
            }

            // upright check
            if (r < 8 && u > -1 && upRight) {
                if (boardPositions[r][u] == queen
                        || boardPositions[r][u] == queensBishop
                        || boardPositions[r][u] == kingsBishop) {
                    return false;
                } else if (boardPositions[r][u] != Constants.pieceIDs.EMPTY_CELL) {
                    upRight = false;
                }
            }

            // upLeft
            if (l > -1 && u > -1 && upLeft) {
                if (boardPositions[l][u] == queen
                        || boardPositions[l][u] == queensBishop
                        || boardPositions[l][u] == kingsBishop) {
                    return false;
                } else if (boardPositions[l][u] != Constants.pieceIDs.EMPTY_CELL) {
                    upLeft = false;
                }
            }

            // downRight
            if (r < 8 && d < 8 && downRight) {
                if (boardPositions[r][d] == queen
                        || boardPositions[r][d] == queensBishop
                        || boardPositions[r][d] == kingsBishop) {
                    return false;
                } else if (boardPositions[r][d] != Constants.pieceIDs.EMPTY_CELL) {
                    downRight = false;
                }
            }

            // downLeft
            if (l > -1 && d < 8 && downLeft) {
                if (boardPositions[l][d] == queen
                        || boardPositions[l][d] == queensBishop
                        || boardPositions[l][d] == kingsBishop) {
                    return false;
                } else if (boardPositions[l][d] != Constants.pieceIDs.EMPTY_CELL) {
                    downLeft = false;
                }
            }
            if (d > 7) {
                down = false;
                downRight = false;
                downLeft = false;
            }
            if (r > 7) {
                right = false;
                downRight = false;
                upRight = false;
            }
            if (l < 0) {
                downLeft = false;
                upLeft = false;
                left = false;
            }
            if (u < 0) {
                upRight = false;
                up = false;
                upLeft = false;
            }
            d++;
            l--;
            r++;
            u--;
        }

        // knight check
        byte[][] knightPositions = {
                { (byte) (x + 1), (byte) (y + 2) },
                { (byte) (x + 1), (byte) (y - 2) },
                { (byte) (x - 1), (byte) (y + 2) },
                { (byte) (x - 1), (byte) (y - 2) },
                { (byte) (x - 2), (byte) (y + 1) },
                { (byte) (x + 2), (byte) (y + 1) },
                { (byte) (x - 2), (byte) (y - 1) },
                { (byte) (x + 2), (byte) (y - 1) },
        };

        for (byte[] position : knightPositions) {
            if (position[0] > -1 && position[0] < 8 && position[1] > -1 && position[1] < 8) {
                if (boardPositions[position[0]][position[1]] == kingsKnight
                        || boardPositions[position[0]][position[1]] == queensKnight) {
                    return false;
                }
            }
        }

        // pawnCheck
        // enemy pawn to the right
        if (x + 1 < 8 && (y + pawnDirection > -1 && y + pawnDirection < 8)) {
            if (boardPositions[x + 1][y + pawnDirection] > beginPawnRange
                    && boardPositions[x + 1][y + pawnDirection] < endPawnRange) {
                return false;
            }
        }
        // enemy pawn to the left
        if (x - 1 > -1 && (y + pawnDirection > -1 && y + pawnDirection < 8)) {
            if (boardPositions[x - 1][y + pawnDirection] > beginPawnRange
                    && boardPositions[x - 1][y + pawnDirection] < endPawnRange) {
                return false;
            }
        }

        // king check
        if (Math.abs(boardInfo.blackKingX - boardInfo.whiteKingX) < 2
                && Math.abs(boardInfo.blackKingY - boardInfo.whiteKingY) < 2) {
            return false;
        }

        return true;
    }

    /**
     * Method responsible for generating moves for rooks and queens, focuses on
     * horizonotal and vertical moves.
     * 
     * @param boards       An ArrayList of BoardInfo objects to add the generated
     *                     boards to.
     * @param initialBoard The original board to generate from.
     * @param x            The x location of the piece.
     * @param y            The y location of the piece.
     * @param color        The color of the piece, true if the piece is black, false
     *                     if not.
     * @param id           The id of the piece.
     * @return An ArrayList of BoardInfo objects that contain the newly generated
     *         boards.
     */
    private ArrayList<BoardInfo> movesRook(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        byte teamRook;
        byte beginTeamRange;
        byte endTeamRange;
        BoardInfo newBoard;

        if (color) {
            teamRook = Constants.pieceIDs.BLACK_PROMOTED_ROOK;
            beginTeamRange = -1;
            endTeamRange = 16;
        } else {
            teamRook = Constants.pieceIDs.WHITE_PROMOTED_ROOK;
            beginTeamRange = 15;
            endTeamRange = 32;
        }

        boolean up = true, down = true, left = true, right = true;
        int u = y - 1;
        int d = y + 1;
        int l = x - 1;
        int r = x + 1;

        while (up || down || left || right) {

            // up check
            if (u > -1 && up) {
                // if there is a teammate piece at this location.
                if ((initialBoard.board[x][u] > beginTeamRange && initialBoard.board[x][u] < endTeamRange)
                        || initialBoard.board[x][u] == teamRook) {
                    up = false;
                } else if (initialBoard.board[x][u] != Constants.pieceIDs.EMPTY_CELL) {
                    // if there is an enemy piece at this location.
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;
                    // moves this piece to there.
                    newBoard.board[x][u] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;

                    // sets the string for the previous move
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                            + Integer.toString(y) + "." + Integer.toString(x) + Integer.toString(u));
                    // if the move doesn't place the king in check, it adds it to the list.
                    if (isNotUnderCheck(newBoard, color)) {
                        if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK
                                || id == Constants.pieceIDs.WHITE_KINGS_ROOK
                                || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                        up = false;
                    }
                } else {
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;

                    // moves piece there
                    newBoard.board[x][u] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                            + Integer.toString(y) + "." + Integer.toString(x) + Integer.toString(u));
                    if (isNotUnderCheck(newBoard, color)) {
                        if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK
                                || id == Constants.pieceIDs.WHITE_KINGS_ROOK
                                || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                    }
                }
            }

            // down check
            if (d < 8 && down) {
                if ((initialBoard.board[x][d] > beginTeamRange && initialBoard.board[x][d] < endTeamRange)
                        || initialBoard.board[x][d] == teamRook) {
                    down = false;
                } else if (initialBoard.board[x][d] != Constants.pieceIDs.EMPTY_CELL) {
                    // if there is an enemy piece at this location.
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;
                    // moves this piece to there.
                    newBoard.board[x][d] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;

                    // sets the string for the previous move
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                            + Integer.toString(y) + "." + Integer.toString(x) + Integer.toString(d));
                    // if the move doesn't place the king in check, it adds it to the list.
                    if (isNotUnderCheck(newBoard, color)) {
                        if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK
                                || id == Constants.pieceIDs.WHITE_KINGS_ROOK
                                || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                        down = false;
                    }
                } else {
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;

                    // moves piece there
                    newBoard.board[x][d] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                            + Integer.toString(y) + "." + Integer.toString(x) + Integer.toString(d));
                    if (isNotUnderCheck(newBoard, color)) {
                        if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK
                                || id == Constants.pieceIDs.WHITE_KINGS_ROOK
                                || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                    }
                }
            }

            // left check
            if (l > -1 && left) {
                // if there is a teammate piece at this location.
                if ((initialBoard.board[l][y] > beginTeamRange && initialBoard.board[l][y] < endTeamRange)
                        || initialBoard.board[l][y] == teamRook) {
                    left = false;
                } else if (initialBoard.board[l][y] != Constants.pieceIDs.EMPTY_CELL) {
                    // if there is an enemy piece at this location.
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;
                    // moves this piece to there.
                    newBoard.board[l][y] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;

                    // sets the string for the previous move
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                            + Integer.toString(y) + "." + Integer.toString(l) + Integer.toString(y));
                    // if the move doesn't place the king in check, it adds it to the list.
                    if (isNotUnderCheck(newBoard, color)) {
                        if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK
                                || id == Constants.pieceIDs.WHITE_KINGS_ROOK
                                || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                        left = false;
                    }
                } else {
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;

                    // moves piece there
                    newBoard.board[l][y] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                            + Integer.toString(y) + "." + Integer.toString(l) + Integer.toString(y));
                    if (isNotUnderCheck(newBoard, color)) {
                        if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK
                                || id == Constants.pieceIDs.WHITE_KINGS_ROOK
                                || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                    }
                }
            }

            // right check
            if (r < 8 && right) {
                // if there is a teammate piece at this location.
                if ((initialBoard.board[r][y] > beginTeamRange && initialBoard.board[r][y] < endTeamRange)
                        || initialBoard.board[r][y] == teamRook) {
                    right = false;
                } else if (initialBoard.board[r][y] != Constants.pieceIDs.EMPTY_CELL) {
                    // if there is an enemy piece at this location.
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;
                    // moves this piece to there.
                    newBoard.board[r][y] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;

                    // sets the string for the previous move
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                            + Integer.toString(y) + "." + Integer.toString(r) + Integer.toString(y));
                    // if the move doesn't place the king in check, it adds it to the list.
                    if (isNotUnderCheck(newBoard, color)) {
                        if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK
                                || id == Constants.pieceIDs.WHITE_KINGS_ROOK
                                || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                        right = false;
                    }
                } else {
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;

                    // moves piece there
                    newBoard.board[r][y] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                            + Integer.toString(y) + "." + Integer.toString(r) + Integer.toString(y));
                    if (isNotUnderCheck(newBoard, color)) {
                        if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK
                                || id == Constants.pieceIDs.WHITE_KINGS_ROOK
                                || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                    }
                }
            }

            if (d > 7) {
                down = false;
            }
            if (r > 7) {
                right = false;
            }
            if (l < 0) {
                left = false;
            }
            if (u < 0) {
                up = false;
            }
            u--;
            l--;
            r++;
            d++;
        }

        return boards;
    }

    /**
     * Method responsible for generating moves for bishops and queens, focuses on
     * diagonal moves.
     * 
     * @param boards       An ArrayList of BoardInfo objects to add the generated
     *                     boards to.
     * @param initialBoard The original board to generate from.
     * @param x            The x location of the piece.
     * @param y            The y location of the piece.
     * @param color        The color of the piece, true if the piece is black, false
     *                     if not.
     * @param id           The id of the piece.
     * @return An ArrayList of BoardInfo objects that contain the newly generated
     *         boards.
     */
    private ArrayList<BoardInfo> movesBishop(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {

        byte teamBeginRange;
        byte teamEndRange;
        byte promotedRook;

        BoardInfo newBoard;

        if (color) {
            teamBeginRange = -1;
            teamEndRange = 16;
            promotedRook = Constants.pieceIDs.BLACK_PROMOTED_ROOK;
        } else {
            teamBeginRange = 15;
            teamEndRange = 32;
            promotedRook = Constants.pieceIDs.WHITE_PROMOTED_ROOK;
        }

        byte u = (byte) (y - 1);
        byte d = (byte) (y + 1);
        byte l = (byte) (x - 1);
        byte r = (byte) (x + 1);

        boolean downRight = true, downLeft = true, upRight = true, upLeft = true;

        while (downRight || downLeft || upRight || upLeft) {
            // upright moves
            if (u > -1 && r < 8 && upRight) {
                // if the piece is on this team
                if ((initialBoard.board[r][u] > teamBeginRange && initialBoard.board[r][u] < teamEndRange)
                        || initialBoard.board[r][u] == promotedRook) {
                    upRight = false;
                } else if (initialBoard.board[r][u] != Constants.pieceIDs.EMPTY_CELL) {
                    // if the piece is an enemy piece
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;
                    newBoard.board[r][u] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;

                    // sets the string for the previous move
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                            + Integer.toString(y) + "." + Integer.toString(r) + Integer.toString(u));
                    // if the move doesn't place the king in check, it adds it to the list.
                    if (isNotUnderCheck(newBoard, color)) {
                        boards.add(newBoard);
                        upRight = false;
                    }
                } else {
                    // piece occupying square is an empty cell
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;
                    newBoard.board[r][u] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;

                    // sets the string for the previous move
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                            + Integer.toString(y) + "." + Integer.toString(r) + Integer.toString(u));
                    // if the move doesn't place the king in check, it adds it to the list.
                    if (isNotUnderCheck(newBoard, color)) {
                        boards.add(newBoard);
                    }
                }
            }

            // upleft moves
            if (u > -1 && l > -1 && upLeft) {
                // if the piece is on this team
                if ((initialBoard.board[l][u] > teamBeginRange && initialBoard.board[l][u] < teamEndRange)
                        || initialBoard.board[l][u] == promotedRook) {
                    upLeft = false;
                } else if (initialBoard.board[l][u] != Constants.pieceIDs.EMPTY_CELL) {
                    // if the piece is an enemy piece
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;
                    newBoard.board[l][u] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;

                    // sets the string for the previous move
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                            + Integer.toString(y) + "." + Integer.toString(l) + Integer.toString(u));
                    // if the move doesn't place the king in check, it adds it to the list.
                    if (isNotUnderCheck(newBoard, color)) {
                        boards.add(newBoard);
                        upLeft = false;
                    }
                } else {
                    // piece occupying square is an empty cell
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;
                    newBoard.board[l][u] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;

                    // sets the string for the previous move
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                            + Integer.toString(y) + "." + Integer.toString(l) + Integer.toString(u));
                    // if the move doesn't place the king in check, it adds it to the list.
                    if (isNotUnderCheck(newBoard, color)) {
                        boards.add(newBoard);
                    }
                }
            }

            // downRight moves
            if (d < 8 && r < 8 && downRight) {
                // if the piece is on this team
                if ((initialBoard.board[r][d] > teamBeginRange && initialBoard.board[r][d] < teamEndRange)
                        || initialBoard.board[r][d] == promotedRook) {
                    downRight = false;
                } else if (initialBoard.board[r][d] != Constants.pieceIDs.EMPTY_CELL) {
                    // if the piece is an enemy piece
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;
                    newBoard.board[r][d] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;

                    // sets the string for the previous move
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                            + Integer.toString(y) + "." + Integer.toString(r) + Integer.toString(d));
                    // if the move doesn't place the king in check, it adds it to the list.
                    if (isNotUnderCheck(newBoard, color)) {
                        boards.add(newBoard);
                        downRight = false;
                    }
                } else {
                    // piece occupying square is an empty cell
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;
                    newBoard.board[r][d] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;

                    // sets the string for the previous move
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                            + Integer.toString(y) + "." + Integer.toString(r) + Integer.toString(d));
                    // if the move doesn't place the king in check, it adds it to the list.
                    if (isNotUnderCheck(newBoard, color)) {
                        boards.add(newBoard);
                    }
                }
            }

            // downLeft moves
            if (l > -1 && d < 8 && downLeft) {
                // if the piece is on this team
                if ((initialBoard.board[l][d] > teamBeginRange && initialBoard.board[l][d] < teamEndRange)
                        || initialBoard.board[l][d] == promotedRook) {
                    downLeft = false;
                } else if (initialBoard.board[l][d] != Constants.pieceIDs.EMPTY_CELL) {
                    // if the piece is an enemy piece
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;
                    newBoard.board[l][d] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;

                    // sets the string for the previous move
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                            + Integer.toString(y) + "." + Integer.toString(l) + Integer.toString(d));
                    // if the move doesn't place the king in check, it adds it to the list.
                    if (isNotUnderCheck(newBoard, color)) {
                        boards.add(newBoard);
                        downLeft = false;
                    }
                } else {
                    // piece occupying square is an empty cell
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;
                    newBoard.board[l][d] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;

                    // sets the string for the previous move
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                            + Integer.toString(y) + "." + Integer.toString(l) + Integer.toString(d));
                    // if the move doesn't place the king in check, it adds it to the list.
                    if (isNotUnderCheck(newBoard, color)) {
                        boards.add(newBoard);
                    }
                }
            }
            if (l < 0) {
                downLeft = false;
                upLeft = false;
            }
            if (u < 0) {
                upLeft = false;
                upRight = false;
            }
            if (d > 7) {
                downLeft = false;
                downRight = false;
            }
            if (r > 7) {
                upRight = false;
                downRight = false;
            }
            r++;
            l--;
            u--;
            d++;
        }

        return boards;
    }

    private ArrayList<BoardInfo> movesPawn(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {

        byte pawnDirection;
        byte enemyBeginRange;
        byte enemyEndRange;
        byte pawnFinalRank;

        // pawn ranges are for enemy pawns
        byte startPawnRange;
        byte endPawnRange;

        byte enemyPromotedRook;
        byte[] promotions = new byte[4];
        BoardInfo newBoard;
        if (color) {
            pawnDirection = 1;
            enemyBeginRange = 15;
            enemyEndRange = 32;
            enemyPromotedRook = Constants.pieceIDs.WHITE_PROMOTED_ROOK;
            promotions[0] = Constants.pieceIDs.BLACK_QUEEN;
            promotions[1] = Constants.pieceIDs.BLACK_PROMOTED_ROOK;
            promotions[2] = Constants.pieceIDs.BLACK_QUEENS_BISHOP;
            promotions[3] = Constants.pieceIDs.BLACK_QUEENS_KNIGHT;
            startPawnRange = Constants.pieceIDs.BEGIN_WHITE_PAWNS;
            endPawnRange = Constants.pieceIDs.END_WHITE_PAWNS;
            pawnFinalRank = 7;

        } else {
            pawnDirection = -1;
            startPawnRange = Constants.pieceIDs.BEGIN_BLACK_PAWNS;
            endPawnRange = Constants.pieceIDs.END_BLACK_PAWNS;
            enemyBeginRange = -1;
            enemyEndRange = 16;
            enemyPromotedRook = Constants.pieceIDs.BLACK_PROMOTED_ROOK;
            promotions[0] = Constants.pieceIDs.WHITE_QUEEN;
            promotions[1] = Constants.pieceIDs.WHITE_PROMOTED_ROOK;
            promotions[2] = Constants.pieceIDs.WHITE_QUEENS_BISHOP;
            promotions[3] = Constants.pieceIDs.WHITE_QUEENS_KNIGHT;
            pawnFinalRank = 0;
        }

        // if the y is in the board
        if (y + pawnDirection * 1 > -1 && y + pawnDirection * 1 < 8) {
            if (x - 1 > -1) {
                // if it can attack left
                if ((initialBoard.board[x - 1][y + pawnDirection * 1] > enemyBeginRange
                        && initialBoard.board[x - 1][y + pawnDirection * 1] < enemyEndRange)
                        || initialBoard.board[x - 1][y + pawnDirection * 1] == enemyPromotedRook) {
                    // if piece in the square is an enemy piece.

                    // if this move is a promotion move
                    if ((y + pawnDirection * 1) == pawnFinalRank) {
                        for (byte promotion : promotions) {
                            newBoard = initialBoard.copy();
                            newBoard.moveCount++;
                            newBoard.board[x - 1][y + pawnDirection * 1] = promotion;
                            newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                            if (isNotUnderCheck(newBoard, color)) {
                                boards.add(newBoard);
                                newBoard.setPreviousMove(Constants.moveTypes.PROMOTION + id + "f" + Integer.toString(x)
                                        + Integer.toString(y) + "." + Integer.toString(x - 1)
                                        + Integer.toString(y + pawnDirection * 1) + "." + promotion);
                            }
                        }
                    } else {
                        newBoard = initialBoard.copy();
                        newBoard.moveCount++;
                        newBoard.board[x - 1][y + pawnDirection * 1] = id;
                        newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                        if (isNotUnderCheck(newBoard, color)) {
                            boards.add(newBoard);
                            newBoard.setPreviousMove(
                                    Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x) + Integer.toString(y)
                                            + "." + Integer.toString(x - 1) + Integer.toString(y + pawnDirection * 1));
                        }
                    }
                }

                // passant left
                if (initialBoard.board[x - 1][y] > startPawnRange && initialBoard.board[x - 1][y] < endPawnRange
                        && initialBoard.passant[getPassantIndex(initialBoard.board[x - 1][y])] == initialBoard.moveCount
                                - 1) {
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;
                    // clears the postion of the pawn attacking and the piece that it attacks
                    newBoard.board[x - 1][y] = Constants.pieceIDs.EMPTY_CELL;
                    newBoard.board[x - 1][y + 1 * pawnDirection] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if (isNotUnderCheck(newBoard, color)) {
                        boards.add(newBoard);
                        newBoard.setPreviousMove(
                                Constants.moveTypes.PASSANT_LEFT + id + "f" + Integer.toString(x) + Integer.toString(y)
                                        + "." + Integer.toString(x - 1) + Integer.toString(y + 1 * pawnDirection));
                    }

                }
            }

            if (x + 1 < 8) {
                // if it can attack right
                if ((initialBoard.board[x + 1][y + pawnDirection * 1] > enemyBeginRange
                        && initialBoard.board[x + 1][y + pawnDirection * 1] < enemyEndRange)
                        || initialBoard.board[x + 1][y + pawnDirection * 1] == enemyPromotedRook) {

                    // if piece in the square is an enemy piece.

                    // if the move is a promotion move
                    if ((y + pawnDirection * 1) == pawnFinalRank) {
                        for (byte promotion : promotions) {
                            newBoard = initialBoard.copy();
                            newBoard.moveCount++;
                            newBoard.board[x + 1][y + pawnDirection * 1] = promotion;
                            newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                            if (isNotUnderCheck(newBoard, color)) {
                                boards.add(newBoard);
                                newBoard.setPreviousMove(Constants.moveTypes.PROMOTION + id + "f" + Integer.toString(x)
                                        + Integer.toString(y) + "." + Integer.toString(x + 1)
                                        + Integer.toString(y + pawnDirection * 1) + "." + promotion);
                            }
                        }
                    } else {
                        newBoard = initialBoard.copy();
                        newBoard.moveCount++;
                        newBoard.board[x + 1][y + pawnDirection * 1] = id;
                        newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                        if (isNotUnderCheck(newBoard, color)) {
                            boards.add(newBoard);
                            newBoard.setPreviousMove(
                                    Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x) + Integer.toString(y)
                                            + "." + Integer.toString(x + 1) + Integer.toString(y + pawnDirection * 1));
                        }
                    }

                }

                // passant right
                if (initialBoard.board[x + 1][y] > startPawnRange && initialBoard.board[x + 1][y] < endPawnRange
                        && initialBoard.passant[getPassantIndex(initialBoard.board[x + 1][y])] == initialBoard.moveCount
                                - 1) {
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;
                    // clears the postion of the pawn attacking and the piece that it attacks
                    newBoard.board[x + 1][y] = Constants.pieceIDs.EMPTY_CELL;
                    newBoard.board[x + 1][y + 1 * pawnDirection] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if (isNotUnderCheck(newBoard, color)) {
                        boards.add(newBoard);
                        newBoard.setPreviousMove(
                                Constants.moveTypes.PASSANT_RIGHT + id + "f" + Integer.toString(x) + Integer.toString(y)
                                        + "." + Integer.toString(x + 1) + Integer.toString(y + 1 * pawnDirection));
                    }

                }

            }

            if (initialBoard.board[x][y + pawnDirection * 1] == Constants.pieceIDs.EMPTY_CELL) {
                // it can move forward

                // pawn promotion
                if (y + pawnDirection * 1 == pawnFinalRank) {
                    for (byte promotion : promotions) {
                        newBoard = initialBoard.copy();
                        newBoard.moveCount++;
                        newBoard.board[x][y + pawnDirection * 1] = promotion;
                        newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                        if (isNotUnderCheck(newBoard, color)) {
                            boards.add(newBoard);
                            newBoard.setPreviousMove(Constants.moveTypes.PROMOTION + id + "f" + Integer.toString(x)
                                    + Integer.toString(y) + "." + Integer.toString(x)
                                    + Integer.toString(y + pawnDirection * 1) + "." + promotion);
                        }
                    }
                } else {
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;
                    newBoard.board[x][y + pawnDirection * 1] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if (isNotUnderCheck(newBoard, color)) {
                        boards.add(newBoard);
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                                + Integer.toString(y) + "." + Integer.toString(x)
                                + Integer.toString(y + pawnDirection * 1));
                    }
                }
            }
        }

        // double forward move.
        if (((y == 6 && !color) || (y == 1 && color))
                && initialBoard.board[x][y + 1 * pawnDirection] == Constants.pieceIDs.EMPTY_CELL
                && initialBoard.board[x][y + 2 * pawnDirection] == Constants.pieceIDs.EMPTY_CELL) {

            newBoard = initialBoard.copy();
            newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
            newBoard.board[x][y + 2 * pawnDirection] = id;
            newBoard.passant[getPassantIndex(id)] = newBoard.moveCount;
            newBoard.moveCount++;
            if (isNotUnderCheck(newBoard, color)) {
                boards.add(newBoard);
                newBoard.setPreviousMove(
                        Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x) + Integer.toString(y) + "."
                                + Integer.toString(x) + Integer.toString(y + 2 * pawnDirection));
            }
        }

        // passant attack
        return boards;
    }

    /**
     * Method responsible for generating the moves from a king.
     * 
     * @param boards       The ArrayList of BoardInfo objects to add to.
     * @param initialBoard The initial board to generate from.
     * @param x            The x location of the king.
     * @param y            The y location of the king.
     * @param color        The color of the king, true if the king is black, false
     *                     if not.
     * @param id           The id of the king.
     * @return An ArrayList of BoardInfo objects containing the newly generated
     *         moves.
     */
    private ArrayList<BoardInfo> movesKing(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {

        BoardInfo newBoard;
        int[][] kingPossibleMoves = {
                { (x - 1), y },
                { (x + 1), y },
                { (x - 1), (y + 1) },
                { (x + 1), (y + 1) },
                { (x - 1), (y - 1) },
                { (x + 1), (y - 1) },
                { x, (y + 1) },
                { x, (y - 1) }

        };
        byte queensRook;
        byte kingsRook;
        byte teamPromotedRook;
        byte beginTeamRange;
        byte endTeamRange;

        if (color) {
            queensRook = Constants.pieceIDs.BLACK_QUEENS_ROOK;
            kingsRook = Constants.pieceIDs.BLACK_KINGS_ROOK;
            teamPromotedRook = Constants.pieceIDs.BLACK_PROMOTED_ROOK;
            beginTeamRange = -1;
            endTeamRange = 16;
        } else {
            queensRook = Constants.pieceIDs.WHITE_QUEENS_ROOK;
            kingsRook = Constants.pieceIDs.WHITE_KINGS_ROOK;
            teamPromotedRook = Constants.pieceIDs.WHITE_PROMOTED_ROOK;
            beginTeamRange = 15;
            endTeamRange = 32;
        }

        for (int[] move : kingPossibleMoves) {
            if (move[0] > -1 && move[0] < 8 && move[1] > -1 && move[1] < 8) {
                // if the move is inside the board
                if (!((initialBoard.board[move[0]][move[1]] > beginTeamRange
                        && initialBoard.board[move[0]][move[1]] < endTeamRange)
                        || initialBoard.board[move[0]][move[1]] == teamPromotedRook)) {
                    // if the piece is not a black piece
                    newBoard = initialBoard.copy();
                    newBoard.moveCount++;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    newBoard.setKingPos(color, (byte) move[0], (byte) move[1]);
                    if (isNotUnderCheck(newBoard, color)) {
                        boards.add(newBoard);
                        newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x)
                                + Integer.toString(y) + "." + Integer.toString(move[0]) + Integer.toString(move[1]));
                    }
                }
            }
        }

        // castling code
        // castling left
        if (!initialBoard.hasMoved[getHasMovedIndex(id)]
                && !initialBoard.hasMoved[getHasMovedIndex(queensRook)]
                && initialBoard.board[x - 4][y] == queensRook
                && initialBoard.board[x - 1][y] == Constants.pieceIDs.EMPTY_CELL
                && initialBoard.board[x - 2][y] == Constants.pieceIDs.EMPTY_CELL
                && initialBoard.board[x - 3][y] == Constants.pieceIDs.EMPTY_CELL) {
            // if no pieces are in the way and the king and castle have not moved

            newBoard = initialBoard.copy();
            if (isNotUnderCheck(newBoard, color)) {
                // if the king isn't currently under check
                newBoard.setKingPos(color, (byte) (x - 1), (byte) y);
                newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                if (isNotUnderCheck(newBoard, color)) {
                    // if the king doesn't pass through check
                    newBoard.setKingPos(color, (byte) (x - 2), (byte) y);
                    newBoard.board[x - 1][y] = Constants.pieceIDs.EMPTY_CELL;
                    if (isNotUnderCheck(newBoard, color)) {
                        // moves the castle to its new position, then clears its old position
                        newBoard.board[x - 1][y] = newBoard.board[0][y];
                        newBoard.board[0][y] = Constants.pieceIDs.EMPTY_CELL;
                        if (isNotUnderCheck(newBoard, color)) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                            newBoard.hasMoved[getHasMovedIndex(queensRook)] = true;
                            boards.add(newBoard);
                            newBoard.setPreviousMove(Constants.moveTypes.CASTLE_LEFT + id + ".");
                        }
                    }
                }
            }

        }

        // castle right
        if (!initialBoard.hasMoved[getHasMovedIndex(id)]
                && !initialBoard.hasMoved[getHasMovedIndex(kingsRook)]
                && initialBoard.board[x + 3][y] == kingsRook
                && initialBoard.board[x + 1][y] == Constants.pieceIDs.EMPTY_CELL
                && initialBoard.board[x + 2][y] == Constants.pieceIDs.EMPTY_CELL) {
            // if no pieces are in the way and the king and castle have not moved

            newBoard = initialBoard.copy();
            if (isNotUnderCheck(newBoard, color)) {
                // if the king isn't currently under check
                newBoard.setKingPos(color, (byte) (x + 1), (byte) y);
                newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                if (isNotUnderCheck(newBoard, color)) {
                    // if the king doesn't pass through check
                    newBoard.setKingPos(color, (byte) (x + 2), (byte) y);
                    newBoard.board[x - 1][y] = Constants.pieceIDs.EMPTY_CELL;
                    if (isNotUnderCheck(newBoard, color)) {
                        // moves the castle to its new position, then clears its old position
                        newBoard.board[x + 1][y] = newBoard.board[7][y];
                        newBoard.board[0][y] = Constants.pieceIDs.EMPTY_CELL;
                        if (isNotUnderCheck(newBoard, color)) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                            newBoard.hasMoved[getHasMovedIndex(kingsRook)] = true;
                            boards.add(newBoard);
                            newBoard.setPreviousMove(Constants.moveTypes.CASTLE_RIGHT + id + ".");
                        }
                    }
                }
            }

        }

        return boards;
    }

    /**
     * Method for generating the boards that come with knight movement.
     * 
     * @param boards       ArrayList of BoardInfo objects that the method will add
     *                     the newly generated boards to.
     * @param initialBoard The initial board to generate from.
     * @param x            X location of the knight.
     * @param y            Y location of the knight.
     * @param color        Indicates the color of the knight, true if black, false
     *                     if not.
     * @param id           ID of the knight.
     * @return An ArrayList of BoardInfo objects containing any newly generated
     *         boards.
     */
    private ArrayList<BoardInfo> movesKnight(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        int[][] possibleMoves = {
                { (x + 1), (y + 2) },
                { (x + 1), (y - 2) },
                { (x - 1), (y + 2) },
                { (x - 1), (y - 2) },
                { (x + 2), (y + 1) },
                { (x - 2), (y + 1) },
                { (x + 2), (y - 1) },
                { (x - 2), (y - 1) }
        };

        if (color) {
            // for black
            for (int[] move : possibleMoves) {
                if (move[0] > -1 && move[0] < 8 && move[1] > -1 && move[1] < 8) {
                    if (!((initialBoard.board[move[0]][move[1]] > -1 && initialBoard.board[move[0]][move[1]] < 16)
                            || initialBoard.board[move[0]][move[1]] == Constants.pieceIDs.BLACK_PROMOTED_ROOK)) {
                        // if the piece is not a black piece
                        BoardInfo newBoard = initialBoard.copy();
                        newBoard.moveCount++;
                        newBoard.board[move[0]][move[1]] = id;
                        newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                        if (isNotUnderCheck(newBoard, color)) {
                            boards.add(newBoard);
                            newBoard.setPreviousMove(
                                    Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x) + Integer.toString(y)
                                            + "." + Integer.toString(move[0]) + Integer.toString(move[1]));
                        }
                    }
                }
            }
        } else {
            // for white
            for (int[] move : possibleMoves) {
                if (move[0] > -1 && move[0] < 8 && move[1] > -1 && move[1] < 8) {
                    if (!((initialBoard.board[move[0]][move[1]] > 15 && initialBoard.board[move[0]][move[1]] < 32)
                            || initialBoard.board[move[0]][move[1]] == Constants.pieceIDs.WHITE_PROMOTED_ROOK)) {
                        // if the piece is not a white piece
                        BoardInfo newBoard = initialBoard.copy();
                        newBoard.moveCount++;
                        newBoard.board[move[0]][move[1]] = id;
                        newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                        if (isNotUnderCheck(newBoard, color)) {
                            boards.add(newBoard);
                            newBoard.setPreviousMove(
                                    Constants.moveTypes.REGULAR + id + "f" + Integer.toString(x) + Integer.toString(y)
                                            + "." + Integer.toString(move[0]) + Integer.toString(move[1]));
                        }
                    }
                }
            }
        }
        return boards;
    }

    /**
     * Method for getting the index for a pawn's passant value. This is the index
     * for the int[] passant array attribute in any BoardInfo object.
     * 
     * @param id The id of the pawn.
     * @return The indexe where the passant value is stored at.
     */
    private int getPassantIndex(byte id) {
        // if the pawn is black, the index will be from 0-7, if the pawn is white the
        // index is from 8-15

        // TODO if you change the constatns these need to be changed. 
        byte index = (id / Constants.pieceIDs.COLOR_DIVISOR == Constants.pieceIDs.BLACK) ? (byte) (id - 8) : (byte) (id - 24 + 8);
        // System.out.println("Pawn index for id: " + id + ", is " + index);
        return index;
    }

    /**
     * Method for getting the index for a castles hasMoved variable. This is the
     * index for the boolean[] hasMoved array in any BoardInfo object.
     * 
     * @param id The id of the castle.
     * @return The index where the hasMoved for that castle is stored at.
     */
    private byte getHasMovedIndex(byte id) {
        switch (id) {
            case Constants.pieceIDs.BLACK_KINGS_ROOK:
                return 0;
            case Constants.pieceIDs.BLACK_QUEENS_ROOK:
                return 1;
            case Constants.pieceIDs.WHITE_KINGS_ROOK:
                return 2;
            case Constants.pieceIDs.WHITE_QUEENS_ROOK:
                return 3;
            case Constants.pieceIDs.BLACK_KING:
                return 4;
            case Constants.pieceIDs.WHITE_KING:
                return 5;
            default:
                System.out.println("Wrong id in getHasMovedIndex() of Bot class");
                return -1;

        }

    }

    /**
     * Class for BoardInteractions.
     * 
     * @author Selim Abdelwahab
     * @version 1.0
     */
    public class BoardInteractions {
        private String rawAIMoves;
        private Piece piece;
        private byte[] move;

        private boolean moveMade = false;

        public BoardInteractions parseAiMove(String move) {
            System.out.println("Move: " + move);
            rawAIMoves = move;
            moveMade = true;

            return this;
        }

        public Piece getMovedPiece() {
            return piece;
        }

        public byte[] getMove() {
            return move;
        }
    }

}
