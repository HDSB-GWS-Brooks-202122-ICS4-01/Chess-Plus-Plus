import java.util.ArrayList;

public class Bot {
    public final BoardInteractions BI = new BoardInteractions();
    final int depth;
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
                depth = 3;
                break;
            case "medium":
                depth = 5;
                break;
            case "hard":
                depth = 7;
                break;
            default:
                depth = 3;
                break;
        }
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
        // converting deadpieces arraylist into an arraylist of bytes.
        ArrayList<Byte> deadPieces = new ArrayList<Byte>();
        for (int i = 0; i < deadPiecesObjects.size(); i++) {
            deadPieces.add(deadPiecesObjects.get(i).getId());
        }

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

        BoardInfo boardInfo = new BoardInfo(boardPositions, passant, hasMoved, deadPieces);
        boardInfo.setKingPos(true, App.GAME_PIECES[Constants.pieceIDs.BLACK_KING].gridX,
                App.GAME_PIECES[Constants.pieceIDs.BLACK_KING].gridY);
        boardInfo.setKingPos(false, App.GAME_PIECES[Constants.pieceIDs.WHITE_KING].gridX,
                App.GAME_PIECES[Constants.pieceIDs.WHITE_KING].gridY);

        return minimax(depth, true, boardInfo).previousMove;
    }

    /**
     * Minimax algorithm used for the A.I.
     * 
     * @param depth The depth to continue the recursion to. 
     * @param max Variable for determining whether it is maximizing, or minimizing, true if maximizing, false if minimizing,
     * @param boardInfo The board to generate from. 
     * @return The best board. 
     */
    private BoardInfo minimax(int depth, boolean max, BoardInfo boardInfo) {
        if (depth == 0) {
            // end case for recursions, at this point the board has generated the
            return boardInfo;
        }

        //If it is trying to maximize, the initial eval is set to the lowest so that any eval that comes after is higher
        //If it is trying to minimize, the intial eval is set to the highest so that any eval that comes after is lower
        int lastEval;
        if(max){
            lastEval = -1000;
        } else {
            lastEval = 1000;
        }
        int thisEval;
        BoardInfo lastChild = null;
        
        //Generates boards that come after this board.
        BoardInfo[] children = generateBoards(boardInfo);


        BoardInfo boardToEvaluate;

        for (BoardInfo child : children) {
            //Sorts through the children to find the highest or lowest one, returns the highest/lowest.
            boardToEvaluate = minimax(depth - 1, !max, child);
            thisEval = boardToEvaluate.evaluate();
            if ((max && thisEval > lastEval) || (!max && thisEval < lastEval)) {
                lastEval = thisEval;
                lastChild = boardToEvaluate;
            }

        }


        return lastChild;

    }

    /**
     * Method for generating all the possible boards/outcomes from this board.
     * 
     * @param boardInfo The original board to generate from.
     * @return An array of BoardInfo objects, representing all the possible moves
     *         generated from the original board.
     */
    private BoardInfo[] generateBoards(BoardInfo boardInfo) {
        ArrayList<BoardInfo> generatedBoards = new ArrayList<BoardInfo>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                byte id = boardInfo.board[x][y];
                if (id == Constants.pieceIDs.EMPTY_CELL) {
                    continue;
                } else if (id == Constants.pieceIDs.BLACK_KINGS_ROOK
                        || id == Constants.pieceIDs.BLACK_QUEENS_ROOK
                        || id == Constants.pieceIDs.BLACK_PROMOTED_ROOK
                        || id == Constants.pieceIDs.WHITE_QUEENS_ROOK
                        || id == Constants.pieceIDs.WHITE_KINGS_ROOK
                        || id == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {

                    generatedBoards = movesLeft(generatedBoards, boardInfo, x, y,
                            (id / 16 == Constants.pieceIDs.BLACK), id);
                    generatedBoards = movesRight(generatedBoards, boardInfo, x, y,
                            (id / 16 == Constants.pieceIDs.BLACK), id);
                    generatedBoards = movesUp(generatedBoards, boardInfo, x, y,
                            (id / 16 == Constants.pieceIDs.BLACK), id);
                    generatedBoards = movesDown(generatedBoards, boardInfo, x, y,
                            (id / 16 == Constants.pieceIDs.BLACK), id);
                    System.out.println("Moves generated after looking at black castle: " + generatedBoards.size());

                } else if (id == Constants.pieceIDs.BLACK_KINGS_KNIGHT
                        || id == Constants.pieceIDs.BLACK_QUEENS_KNIGHT
                        || id == Constants.pieceIDs.WHITE_KINGS_KNIGHT
                        || id == Constants.pieceIDs.WHITE_QUEENS_KNIGHT) {

                } else if (id == Constants.pieceIDs.BLACK_QUEEN
                        || id == Constants.pieceIDs.WHITE_QUEEN) {
                    generatedBoards = movesLeft(generatedBoards, boardInfo, x, y,
                            (id / 16 == Constants.pieceIDs.BLACK), id);
                    generatedBoards = movesRight(generatedBoards, boardInfo, x, y,
                            (id / 16 == Constants.pieceIDs.BLACK), id);
                    generatedBoards = movesUp(generatedBoards, boardInfo, x, y,
                            (id / 16 == Constants.pieceIDs.BLACK), id);
                    generatedBoards = movesDown(generatedBoards, boardInfo, x, y,
                            (id / 16 == Constants.pieceIDs.BLACK), id);
                    generatedBoards = movesUpRight(generatedBoards, boardInfo, x, y,
                            (id / 16 == Constants.pieceIDs.BLACK), id);
                    generatedBoards = movesUpLeft(generatedBoards, boardInfo, x, y,
                            (id / 16 == Constants.pieceIDs.BLACK), id);
                    generatedBoards = movesDownRight(generatedBoards, boardInfo, x, y,
                            (id / 16 == Constants.pieceIDs.BLACK), id);
                    generatedBoards = movesDownLeft(generatedBoards, boardInfo, x, y,
                            (id / 16 == Constants.pieceIDs.BLACK), id);
                    System.out.println("Moves generated after looking at queen: " + generatedBoards.size());

                } else if (id == Constants.pieceIDs.BLACK_KINGS_BISHOP
                        || id == Constants.pieceIDs.BLACK_QUEENS_BISHOP
                        || id == Constants.pieceIDs.WHITE_KINGS_BISHOP
                        || id == Constants.pieceIDs.WHITE_QUEENS_BISHOP) {

                    generatedBoards = movesUpRight(generatedBoards, boardInfo, x, y,
                            (id / 16 == Constants.pieceIDs.BLACK), id);
                    generatedBoards = movesUpLeft(generatedBoards, boardInfo, x, y,
                            (id / 16 == Constants.pieceIDs.BLACK), id);
                    generatedBoards = movesDownRight(generatedBoards, boardInfo, x, y,
                            (id / 16 == Constants.pieceIDs.BLACK), id);
                    generatedBoards = movesDownLeft(generatedBoards, boardInfo, x, y,
                            (id / 16 == Constants.pieceIDs.BLACK), id);
                    System.out.println("Moves generated after looking at bishop: " + generatedBoards.size());

                } else if (id == Constants.pieceIDs.BLACK_KING || id == Constants.pieceIDs.WHITE_KING) {

                } else if ((id > 7 && id < 16) || (id > 23 && id < 32)) {

                }

            }

        }

        return (BoardInfo[]) generatedBoards.toArray();
    }

    /**
     * Method for determining if the king is under check.
     * 
     * @param boardInfo BoardInfo object containing the board data. 
     * @param color Color of the piece, true means black, false means white. 
     * 
     * @return true if the king isn't under check, false if it is.
     */
    boolean isNotUnderCheck(BoardInfo boardInfo, boolean color) {
        // if the color it is checking is black.
        if (color) {
            int x = boardInfo.blackKingX;
            int y = boardInfo.blackKingY;
            byte[][] boardPositions = boardInfo.board;
            boolean up = true, down = true, left = true, right = true, upRight = true, downRight =true, upLeft = true, downLeft = true;
            byte[][] knightPositions = {
                {(byte) (x+1), (byte) (y+2)},
                {(byte) (x+1), (byte) (y-2)},
                {(byte) (x-1), (byte) (y+2)},
                {(byte) (x-1), (byte) (y-2)},
                {(byte) (x-2), (byte) (y+1)},
                {(byte) (x+2), (byte) (y+1)},
                {(byte) (x-2), (byte) (y-1)},
                {(byte) (x+2), (byte) (y-1)},
            };

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
                if (l > -1  && left) {
                    if (boardPositions[l][y] == Constants.pieceIDs.WHITE_QUEEN
                            || boardPositions[l][y] == Constants.pieceIDs.WHITE_KINGS_ROOK
                            || boardPositions[l][y] == Constants.pieceIDs.WHITE_QUEENS_ROOK
                            || boardPositions[l][y] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
                        return false;
                    } else if (boardPositions[l][y] != Constants.pieceIDs.EMPTY_CELL) {
                        left = false;
                    }
                }

                // right check
                if (r < 8 && right) {
                    if (boardPositions[r][y] == Constants.pieceIDs.WHITE_QUEEN
                            || boardPositions[r][y] == Constants.pieceIDs.WHITE_KINGS_ROOK
                            || boardPositions[r][y] == Constants.pieceIDs.WHITE_QUEENS_ROOK
                            || boardPositions[r][y] == Constants.pieceIDs.WHITE_PROMOTED_ROOK){
                        return false;
                    } else if (boardPositions[r][y] != Constants.pieceIDs.EMPTY_CELL) {
                        right = false;
                    }
                }

                // down check
                if (d < 8 && down) {
                    if (boardPositions[x][d] == Constants.pieceIDs.WHITE_QUEEN
                            || boardPositions[x][d] == Constants.pieceIDs.WHITE_KINGS_ROOK
                            || boardPositions[x][d] == Constants.pieceIDs.WHITE_QUEENS_ROOK
                            || boardPositions[x][d] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
                        return false;
                    } else if (boardPositions[x][d] != Constants.pieceIDs.EMPTY_CELL) {
                        down = false;
                    }
                }

                // up check
                if (u > -1 && up) {
                    if (boardPositions[x][u] == Constants.pieceIDs.WHITE_QUEEN
                            || boardPositions[x][u] == Constants.pieceIDs.WHITE_KINGS_ROOK
                            || boardPositions[x][u] == Constants.pieceIDs.WHITE_QUEENS_ROOK
                            || boardPositions[x][u] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
                        return false;
                    } else if (boardPositions[x][u] != Constants.pieceIDs.EMPTY_CELL) {
                        up = false;
                    }
                }

                // upright check
                if (r < 8 && u > -1 && upRight) {
                    if (boardPositions[r][u] == Constants.pieceIDs.WHITE_QUEEN
                            || boardPositions[r][u] == Constants.pieceIDs.WHITE_QUEENS_BISHOP
                            || boardPositions[r][u] == Constants.pieceIDs.WHITE_KINGS_BISHOP) {
                        return false;
                    } else if (boardPositions[r][u] != Constants.pieceIDs.EMPTY_CELL){
                        upRight = false;
                    }
                }

                //upLeft
                if (l > -1 && u > -1 && upLeft) {
                    if (boardPositions[l][u] == Constants.pieceIDs.WHITE_QUEEN
                            || boardPositions[l][u] == Constants.pieceIDs.WHITE_QUEENS_BISHOP
                            || boardPositions[l][u] == Constants.pieceIDs.WHITE_KINGS_BISHOP) {
                        return false;
                    } else if (boardPositions[l][u] != Constants.pieceIDs.EMPTY_CELL){
                        upLeft = false;
                    }
                }

                //downRight
                if (r < 8 && d < 8 && downRight) {
                    if (boardPositions[r][d] == Constants.pieceIDs.WHITE_QUEEN
                            || boardPositions[r][d] == Constants.pieceIDs.WHITE_QUEENS_BISHOP
                            || boardPositions[r][d] == Constants.pieceIDs.WHITE_KINGS_BISHOP) {
                        return false;
                    } else if (boardPositions[r][d] != Constants.pieceIDs.EMPTY_CELL){
                        downRight = false;
                    }
                }

                //downLeft
                if (l >-1  && d < 8 && downLeft) {
                    if (boardPositions[l][d] == Constants.pieceIDs.WHITE_QUEEN
                            || boardPositions[l][d] == Constants.pieceIDs.WHITE_QUEENS_BISHOP
                            || boardPositions[l][d] == Constants.pieceIDs.WHITE_KINGS_BISHOP) {
                        return false;
                    } else if (boardPositions[l][d] != Constants.pieceIDs.EMPTY_CELL){
                        downLeft = false;
                    }
                }
                d++;
                l--;
                r++;
                u--;
            }
            
        } else {
            int x = boardInfo.whiteKingX;
            int y = boardInfo.whiteKingY;
            byte[][] boardPositions = boardInfo.board;
            boolean up = true, down = true, left = true, right = true, upRight = true, downRight =true, upLeft = true, downLeft = true;

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
                if (l > -1  && left) {
                    if (boardPositions[l][y] == Constants.pieceIDs.BLACK_QUEEN
                            || boardPositions[l][y] == Constants.pieceIDs.BLACK_KINGS_ROOK
                            || boardPositions[l][y] == Constants.pieceIDs.BLACK_QUEENS_ROOK
                            || boardPositions[l][y] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                        return false;
                    } else if (boardPositions[l][y] != Constants.pieceIDs.EMPTY_CELL) {
                        left = false;
                    }
                }

                // right check
                if (r < 8 && right) {
                    if (boardPositions[r][y] == Constants.pieceIDs.BLACK_QUEEN
                            || boardPositions[r][y] == Constants.pieceIDs.BLACK_KINGS_ROOK
                            || boardPositions[r][y] == Constants.pieceIDs.BLACK_QUEENS_ROOK
                            || boardPositions[r][y] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                        return false;
                    } else if (boardPositions[r][y] != Constants.pieceIDs.EMPTY_CELL) {
                        right = false;
                    }
                }

                // down check
                if (d < 8 && down) {
                    if (boardPositions[x][d] == Constants.pieceIDs.BLACK_QUEEN
                            || boardPositions[x][d] == Constants.pieceIDs.BLACK_KINGS_ROOK
                            || boardPositions[x][d] == Constants.pieceIDs.BLACK_QUEENS_ROOK
                            || boardPositions[x][d] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                        return false;
                    } else if (boardPositions[x][d] != Constants.pieceIDs.EMPTY_CELL) {
                        down = false;
                    }
                }

                // up check
                if (u > -1 && up) {
                    if (boardPositions[x][u] == Constants.pieceIDs.BLACK_QUEEN
                            || boardPositions[x][u] == Constants.pieceIDs.BLACK_KINGS_ROOK
                            || boardPositions[x][u] == Constants.pieceIDs.BLACK_QUEENS_ROOK
                            || boardPositions[x][u] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                        return false;
                    } else if (boardPositions[x][u] != Constants.pieceIDs.EMPTY_CELL) {
                        up = false;
                    }
                }

                // upright check
                if (r < 8 && u > -1 && upRight) {
                    if (boardPositions[r][u] == Constants.pieceIDs.BLACK_QUEEN
                            || boardPositions[r][u] == Constants.pieceIDs.BLACK_QUEENS_BISHOP
                            || boardPositions[r][u] == Constants.pieceIDs.BLACK_KINGS_BISHOP) {
                        return false;
                    } else if (boardPositions[r][u] != Constants.pieceIDs.EMPTY_CELL){
                        upRight = false;
                    }
                }

                //upLeft
                if (l > -1 && u > -1 && upLeft) {
                    if (boardPositions[l][u] == Constants.pieceIDs.BLACK_QUEEN
                            || boardPositions[l][u] == Constants.pieceIDs.BLACK_QUEENS_BISHOP
                            || boardPositions[l][u] == Constants.pieceIDs.BLACK_KINGS_BISHOP) {
                        return false;
                    } else if (boardPositions[l][u] != Constants.pieceIDs.EMPTY_CELL){
                        upLeft = false;
                    }
                }

                //downRight
                if (r < 8 && d < 8 && downRight) {
                    if (boardPositions[r][d] == Constants.pieceIDs.BLACK_QUEEN
                            || boardPositions[r][d] == Constants.pieceIDs.BLACK_QUEENS_BISHOP
                            || boardPositions[r][d] == Constants.pieceIDs.BLACK_KINGS_BISHOP) {
                        return false;
                    } else if (boardPositions[r][d] != Constants.pieceIDs.EMPTY_CELL){
                        downRight = false;
                    }
                }

                //downLeft
                if (l >-1  && d < 8 && downLeft) {
                    if (boardPositions[l][d] == Constants.pieceIDs.BLACK_QUEEN
                            || boardPositions[l][d] == Constants.pieceIDs.BLACK_QUEENS_BISHOP
                            || boardPositions[l][d] == Constants.pieceIDs.BLACK_KINGS_BISHOP) {
                        return false;
                    } else if (boardPositions[l][d] != Constants.pieceIDs.EMPTY_CELL){
                        downLeft = false;
                    }
                }
                d++;
                l--;
                r++;
                u--;
            }

        }
        return true;
    }

    private ArrayList<BoardInfo> movesPawn(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        return boards;
    }

    private ArrayList<BoardInfo> movesKing(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        // if the color of the king is black
        if (color) {

        } else {

        }

        return boards;
    }

    private ArrayList<BoardInfo> movesKnight(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        return boards;
    }

    /**
     * Method for generating boards when a piece is moving to diagonally down to the
     * left. It generates a new board when a piece is moving to a new position,
     * the newly generated board contains no references to the original board or any
     * fields inside of it. It also updates the deadPieces
     * field in the newBoard object when needed and records what move led to that
     * board.
     * 
     * @param boards       ArrayList of boards that it will add the newly generated
     *                     boards to.
     * @param initialBoard The initial board it is generating moves from.
     * @param x            Pieces location in the x axis on the board.
     * @param y            Pieces location in the y axis on the board.
     * @param color        Color of the piece.
     * @param id           Id of the piece.
     * @return An ArrayList of BoardInfo objects that contain the newly generated
     *         moves in this direction.
     */
    private ArrayList<BoardInfo> movesDownLeft(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        if (color) {
            for (int i = x - 1; i > -1; i--) {
                for (int j = y - 1; j > -1; j--) {
                    if ((initialBoard.board[i][j] > -1 && initialBoard.board[i][j] < 16 )|| initialBoard.board[i][j] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                        break;
                    } else {
                        BoardInfo newBoard = initialBoard.copy();
                        newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                        if (initialBoard.board[i][j] != Constants.pieceIDs.EMPTY_CELL) {
                            newBoard.deadPieces.add(newBoard.board[i][j]);
                            newBoard.board[i][j] = id;
                            newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                            boards.add(newBoard);
                            break;
                        }
                        newBoard.board[i][j] = id;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                        boards.add(newBoard);
                    }
                }
            }

        } else {
            for (int i = x - 1; i > -1; i--) {
                for (int j = y - 1; j > -1; j--) {
                    if ((initialBoard.board[i][j] > 15 && initialBoard.board[i][j] < 32) || initialBoard.board[i][j] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
                        break;
                    } else {
                        BoardInfo newBoard = initialBoard.copy();
                        newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                        if (initialBoard.board[i][j] != Constants.pieceIDs.EMPTY_CELL) {
                            newBoard.deadPieces.add(newBoard.board[i][j]);
                            newBoard.board[i][j] = id;
                            newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                            boards.add(newBoard);
                            break;
                        }
                        newBoard.board[i][j] = id;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                        boards.add(newBoard);

                    }
                }
            }

        }
        return boards;
    }

    /**
     * Method for generating boards when a piece is moving to diagonally down to the
     * right. It generates a new board when a piece is moving to a new position,
     * the newly generated board contains no references to the original board or any
     * fields inside of it. It also updates the deadPieces
     * field in the newBoard object when needed and records what move led to that
     * board.
     * 
     * @param boards       ArrayList of boards that it will add the newly generated
     *                     boards to.
     * @param initialBoard The initial board it is generating moves from.
     * @param x            Pieces location in the x axis on the board.
     * @param y            Pieces location in the y axis on the board.
     * @param color        Color of the piece.
     * @param id           Id of the piece.
     * @return An ArrayList of BoardInfo objects that contain the newly generated
     *         moves in this direction.
     */
    private ArrayList<BoardInfo> movesDownRight(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        if (color) {
            for (int i = x + 1; i < 8; i++) {
                for (int j = y + 1; j < 8; j++) {
                    if ((initialBoard.board[i][j] > -1 && initialBoard.board[i][j] < 16)|| initialBoard.board[i][j] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                        break;
                    } else {
                        BoardInfo newBoard = initialBoard.copy();
                        newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                        if (initialBoard.board[i][j] != Constants.pieceIDs.EMPTY_CELL) {
                            newBoard.deadPieces.add(newBoard.board[i][j]);
                            newBoard.board[i][j] = id;
                            newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                            boards.add(newBoard);
                            break;
                        }
                        newBoard.board[i][j] = id;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                        boards.add(newBoard);
                    }
                }
            }

        } else {
            for (int i = x + 1; i < 8; i++) {
                for (int j = y + 1; j < 8; j++) {
                    if ((initialBoard.board[i][j] > 15 && initialBoard.board[i][j] < 32) || initialBoard.board[i][j] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
                        break;
                    } else {
                        BoardInfo newBoard = initialBoard.copy();
                        newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                        if (initialBoard.board[i][j] != Constants.pieceIDs.EMPTY_CELL) {
                            newBoard.deadPieces.add(newBoard.board[i][j]);
                            newBoard.board[i][j] = id;
                            newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                            boards.add(newBoard);
                            break;
                        }
                        newBoard.board[i][j] = id;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                        boards.add(newBoard);

                    }
                }
            }

        }
        return boards;
    }

    /**
     * Method for generating boards when a piece is moving to diagonally up to the
     * left. It generates a new board when a piece is moving to a new position,
     * the newly generated board contains no references to the original board or any
     * fields inside of it. It also updates the deadPieces
     * field in the newBoard object when needed and records what move led to that
     * board.
     * 
     * @param boards       ArrayList of boards that it will add the newly generated
     *                     boards to.
     * @param initialBoard The initial board it is generating moves from.
     * @param x            Pieces location in the x axis on the board.
     * @param y            Pieces location in the y axis on the board.
     * @param color        Color of the piece.
     * @param id           Id of the piece.
     * @return An ArrayList of BoardInfo objects that contain the newly generated
     *         moves in this direction.
     */
    private ArrayList<BoardInfo> movesUpLeft(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        if (color) {
            for (int i = x - 1; i > -1; i--) {
                for (int j = y - 1; j > -1; j--) {
                    if ((initialBoard.board[i][j] > -1 && initialBoard.board[i][j] < 16) || initialBoard.board[i][j] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                        break;
                    } else {
                        BoardInfo newBoard = initialBoard.copy();
                        newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                        if (initialBoard.board[i][j] != Constants.pieceIDs.EMPTY_CELL) {
                            newBoard.deadPieces.add(newBoard.board[i][j]);
                            newBoard.board[i][j] = id;
                            newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                            boards.add(newBoard);
                            break;
                        }
                        newBoard.board[i][j] = id;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                        boards.add(newBoard);
                    }
                }
            }

        } else {
            for (int i = x - 1; i > -1; i--) {
                for (int j = y - 1; j > -1; j--) {
                    if ((initialBoard.board[i][j] > 15 && initialBoard.board[i][j] < 32)|| initialBoard.board[i][j] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
                        break;
                    } else {
                        BoardInfo newBoard = initialBoard.copy();
                        newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                        if (initialBoard.board[i][j] != Constants.pieceIDs.EMPTY_CELL) {
                            newBoard.deadPieces.add(newBoard.board[i][j]);
                            newBoard.board[i][j] = id;
                            newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                            boards.add(newBoard);
                            break;
                        }
                        newBoard.board[i][j] = id;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                        boards.add(newBoard);

                    }
                }
            }

        }
        return boards;
    }

    /**
     * Method for generating boards when a piece is moving to diagonally up to the
     * right. It generates a new board when a piece is moving to a new position,
     * the newly generated board contains no references to the original board or any
     * fields inside of it. It also updates the deadPieces
     * field in the newBoard object when needed and records what move led to that
     * board.
     * 
     * @param boards       ArrayList of boards that it will add the newly generated
     *                     boards to.
     * @param initialBoard The initial board it is generating moves from.
     * @param x            Pieces location in the x axis on the board.
     * @param y            Pieces location in the y axis on the board.
     * @param color        Color of the piece.
     * @param id           Id of the piece.
     * @return An ArrayList of BoardInfo objects that contain the newly generated
     *         moves in this direction.
     */
    private ArrayList<BoardInfo> movesUpRight(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        if (color) {
            for (int i = x + 1; i < 8; i++) {
                for (int j = y - 1; j > -1; j--) {
                    if ((initialBoard.board[i][j] > -1 && initialBoard.board[i][j] < 16) || initialBoard.board[i][j] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                        break;
                    } else {
                        BoardInfo newBoard = initialBoard.copy();
                        newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                        if (initialBoard.board[i][j] != Constants.pieceIDs.EMPTY_CELL) {
                            newBoard.deadPieces.add(newBoard.board[i][j]);
                            newBoard.board[i][j] = id;
                            newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                            boards.add(newBoard);
                            break;
                        }
                        newBoard.board[i][j] = id;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                        boards.add(newBoard);
                    }
                }
            }

        } else {
            for (int i = x + 1; i < 8; i++) {
                for (int j = y - 1; j > -1; j--) {
                    if ((initialBoard.board[i][j] > 15 && initialBoard.board[i][j] < 32)|| initialBoard.board[i][j] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
                        break;
                    } else {
                        BoardInfo newBoard = initialBoard.copy();
                        newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                        if (initialBoard.board[i][j] != Constants.pieceIDs.EMPTY_CELL) {
                            newBoard.deadPieces.add(newBoard.board[i][j]);
                            newBoard.board[i][j] = id;
                            newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                            boards.add(newBoard);
                            break;
                        }
                        newBoard.board[i][j] = id;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                        boards.add(newBoard);

                    }
                }
            }

        }
        return boards;
    }

    /**
     * Method for generating boards when a piece is moving to the left. It generates
     * a new board when a piece is moving to a new position,
     * the newly generated board contains no references to the original board or any
     * fields inside of it. It also updates the deadPieces
     * field in the newBoard object when needed and records what move led to that
     * board.
     * 
     * @param boards       ArrayList of boards that it will add the newly generated
     *                     boards to.
     * @param initialBoard The initial board it is generating moves from.
     * @param x            Pieces location in the x axis on the board.
     * @param y            Pieces location in the y axis on the board.
     * @param color        Color of the piece.
     * @param id           Id of the piece.
     * @return An ArrayList of BoardInfo objects that contain the newly generated
     *         moves in this direction.
     */
    private ArrayList<BoardInfo> movesLeft(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        // if the color of the piece is black
        if (color) {
            for (int i = x - 1; i > -1; i--) {
                if ((initialBoard.board[i][y] > -1 && initialBoard.board[i][y] < 16) || initialBoard.board[i][y] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                    break;
                } else {
                    BoardInfo newBoard = initialBoard.copy();
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if (initialBoard.board[i][y] != Constants.pieceIDs.EMPTY_CELL) {
                        newBoard.deadPieces.add(newBoard.board[i][y]);
                        newBoard.board[i][y] = id;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + y);
                        if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                        break;
                    }
                    newBoard.board[i][y] = id;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + y);
                    if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK) {
                        newBoard.hasMoved[getHasMovedIndex(id)] = true;
                    }
                    boards.add(newBoard);
                }
            }
        } else {
            for (int i = x - 1; i > -1; i--) {
                if ((initialBoard.board[i][y] > 15 && initialBoard.board[i][y] < 32) || initialBoard.board[i][y] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
                    break;
                } else {
                    BoardInfo newBoard = initialBoard.copy();
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if (initialBoard.board[i][y] != Constants.pieceIDs.EMPTY_CELL) {
                        newBoard.deadPieces.add(newBoard.board[i][y]);
                        newBoard.board[i][y] = id;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + y);
                        if (id == Constants.pieceIDs.WHITE_KINGS_ROOK || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                        break;
                    }
                    newBoard.board[i][y] = id;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + y);
                    boards.add(newBoard);
                }
            }

        }

        return boards;
    }

    /**
     * Method for generating boards when a piece is moving to the right. It
     * generates a new board when a piece is moving to a new position,
     * the newly generated board contains no references to the original board or any
     * fields inside of it. It also updates the deadPieces
     * field in the newBoard object when needed and records what move led to that
     * board.
     * 
     * @param boards       ArrayList of boards that it will add the newly generated
     *                     boards to.
     * @param initialBoard The initial board it is generating moves from.
     * @param x            Pieces location in the x axis on the board.
     * @param y            Pieces location in the y axis on the board.
     * @param color        Color of the piece.
     * @param id           Id of the piece.
     * @return An ArrayList of BoardInfo objects that contain the newly generated
     *         moves in this direction.
     */
    private ArrayList<BoardInfo> movesRight(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        // if the color of the piece is black
        if (color) {
            for (int i = x + 1; i < 8; i++) {
                if ((initialBoard.board[i][y] > -1 && initialBoard.board[i][y] < 16)|| initialBoard.board[i][y] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                    break;
                } else {
                    BoardInfo newBoard = initialBoard.copy();
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if (initialBoard.board[i][y] != Constants.pieceIDs.EMPTY_CELL) {
                        newBoard.deadPieces.add(newBoard.board[i][y]);
                        newBoard.board[i][y] = id;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + y);
                        if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                        break;
                    }
                    newBoard.board[i][y] = id;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + y);
                    if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK) {
                        newBoard.hasMoved[getHasMovedIndex(id)] = true;
                    }
                    boards.add(newBoard);
                }
            }
        } else {
            for (int i = x + 1; i < 8; i++) {
                if ((initialBoard.board[i][y] > 15 && initialBoard.board[i][y] < 32)|| initialBoard.board[i][y] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
                    break;
                } else {
                    BoardInfo newBoard = initialBoard.copy();
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if (initialBoard.board[i][y] != Constants.pieceIDs.EMPTY_CELL) {
                        newBoard.deadPieces.add(newBoard.board[i][y]);
                        newBoard.board[i][y] = id;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + y);
                        if (id == Constants.pieceIDs.WHITE_KINGS_ROOK || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                        break;
                    }
                    newBoard.board[i][y] = id;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + y);
                    if (id == Constants.pieceIDs.WHITE_KINGS_ROOK || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
                        newBoard.hasMoved[getHasMovedIndex(id)] = true;
                    }
                    boards.add(newBoard);
                }
            }
        }

        return boards;
    }

    /**
     * Method for generating boards when a piece is moving up. It generates a new
     * board when a piece is moving to a new position,
     * the newly generated board contains no references to the original board or any
     * fields inside of it. It also updates the deadPieces
     * field in the newBoard object when needed and records what move led to that
     * board.
     * 
     * @param boards       ArrayList of boards that it will add the newly generated
     *                     boards to.
     * @param initialBoard The initial board it is generating moves from.
     * @param x            Pieces location in the x axis on the board.
     * @param y            Pieces location in the y axis on the board.
     * @param color        Color of the piece.
     * @param id           Id of the piece.
     * @return An ArrayList of BoardInfo objects that contain the newly generated
     *         moves in this direction.
     */
    private ArrayList<BoardInfo> movesUp(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        // if the color of the piece is black
        if (color) {
            for (int i = y - 1; i > -1; i--) {
                if ((initialBoard.board[x][i] > -1 && initialBoard.board[x][i] < 16) || initialBoard.board[i][y] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                    break;
                } else {
                    BoardInfo newBoard = initialBoard.copy();
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if (initialBoard.board[x][i] != Constants.pieceIDs.EMPTY_CELL) {
                        newBoard.deadPieces.add(newBoard.board[x][i]);
                        newBoard.board[x][i] = id;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + i);
                        if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                        break;
                    }
                    newBoard.board[x][i] = id;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + i);
                    if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK) {
                        newBoard.hasMoved[getHasMovedIndex(id)] = true;
                    }
                    boards.add(newBoard);
                }
            }
        } else {
            for (int i = y - 1; i > -1; i--) {
                if ((initialBoard.board[x][i] > 15 && initialBoard.board[x][i] < 32) || initialBoard.board[i][y] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
                    break;
                } else {
                    BoardInfo newBoard = initialBoard.copy();
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if (initialBoard.board[x][i] != Constants.pieceIDs.EMPTY_CELL) {
                        newBoard.deadPieces.add(newBoard.board[x][i]);
                        newBoard.board[x][i] = id;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + i);
                        if (id == Constants.pieceIDs.WHITE_KINGS_ROOK || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                        break;
                    }
                    newBoard.board[x][i] = id;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + i);
                    if (id == Constants.pieceIDs.WHITE_KINGS_ROOK || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
                        newBoard.hasMoved[getHasMovedIndex(id)] = true;
                    }
                    boards.add(newBoard);
                }
            }

        }

        return boards;
    }

    /**
     * Method for generating boards when a piece is moving down. It generates a new
     * board when a piece is moving to a new position,
     * the newly generated board contains no references to the original board or any
     * fields inside of it. It also updates the deadPieces
     * field in the newBoard object when needed and records what move led to that
     * board.
     * 
     * @param boards       ArrayList of boards that it will add the newly generated
     *                     boards to.
     * @param initialBoard The initial board it is generating moves from.
     * @param x            Pieces location in the x axis on the board.
     * @param y            Pieces location in the y axis on the board.
     * @param color        Color of the piece.
     * @param id           Id of the piece.
     * @return An ArrayList of BoardInfo objects that contain the newly generated
     *         moves in this direction.
     */
    private ArrayList<BoardInfo> movesDown(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        // if the color of the piece is black
        if (color) {
            for (int i = y + 1; i < 8; i++) {
                if ((initialBoard.board[x][i] > -1 && initialBoard.board[x][i] < 16) || initialBoard.board[i][y] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                    break;
                } else {
                    BoardInfo newBoard = initialBoard.copy();
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if (initialBoard.board[x][i] != Constants.pieceIDs.EMPTY_CELL) {
                        newBoard.deadPieces.add(newBoard.board[x][i]);
                        newBoard.board[x][i] = id;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + i);
                        if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                        break;
                    }
                    newBoard.board[x][i] = id;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + i);
                    if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK) {
                        newBoard.hasMoved[getHasMovedIndex(id)] = true;
                    }
                    boards.add(newBoard);
                }
            }
        } else {
            for (int i = y + 1; i < 8; i++) {
                if ((initialBoard.board[x][i] > 15 && initialBoard.board[x][i] < 32)|| initialBoard.board[i][y] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
                    break;
                } else {
                    BoardInfo newBoard = initialBoard.copy();
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if (initialBoard.board[x][i] != Constants.pieceIDs.EMPTY_CELL) {
                        newBoard.deadPieces.add(newBoard.board[x][i]);
                        newBoard.board[x][i] = id;
                        newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + i);
                        if (id == Constants.pieceIDs.WHITE_KINGS_ROOK || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                        break;
                    }
                    newBoard.board[x][i] = id;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + i);
                    if (id == Constants.pieceIDs.WHITE_KINGS_ROOK || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
                        newBoard.hasMoved[getHasMovedIndex(id)] = true;
                    }
                    boards.add(newBoard);
                }
            }

        }

        return boards;
    }

    /**
     * Method for getting the index for a pawn's passant value. This is the index for the int[] passant array attribute in any BoardInfo object. 
     * 
     * @param id The id of the pawn. 
     * @return The indexe where the passant value is stored at. 
     */
    private byte getPassantIndex(byte id) {
        //if the pawn is black, the index will be from 0-7, if the pawn is white the index is from 8-15
        byte index = (id / 16 == Constants.pieceIDs.BLACK) ? (byte) (id - 8) : (byte) (id-24+ 8);
        System.out.println("Pawn index for id: " + id + ", is " + index);
        return index;
    }

    /**
     * Method for getting the index for a castles hasMoved variable. This is the index for the boolean[] hasMoved array in any BoardInfo object. 
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

    public class BoardInteractions {
        private String rawAIMoves;
        private Piece piece;
        private byte[] move;

        private boolean moveMade = false;

        public BoardInteractions parseAiMove(String move) {
            System.out.println(move);
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
