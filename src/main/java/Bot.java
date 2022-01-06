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

        BoardInfo[] boards = generateBoards(boardInfo, true);
        if(boards.length ==0){
            return Constants.moveTypes.NO_MOVES;
        } else {
            return boards[0].previousMove;
        }
        // return minimax(depth, true, boardInfo).previousMove;
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
        if (depth == 0) {
            // end case for recursions, at this point the board has generated the
            return boardInfo;
        }

        // If it is trying to maximize, the initial eval is set to the lowest so that
        // any eval that comes after is higher
        // If it is trying to minimize, the intial eval is set to the highest so that
        // any eval that comes after is lower
        int lastEval;
        if (max) {
            lastEval = -1000;
        } else {
            lastEval = 1000;
        }
        int thisEval;
        BoardInfo lastChild = null;

        // Generates boards that come after this board.
        BoardInfo[] children = generateBoards(boardInfo, max);

        BoardInfo boardToEvaluate;

        for (BoardInfo child : children) {
            // Sorts through the children to find the highest or lowest one, returns the
            // highest/lowest.
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
    private BoardInfo[] generateBoards(BoardInfo boardInfo, boolean color) {
        //TODO Akil finish this
        //TODO Fix diagonal move generation
        ArrayList<BoardInfo> generatedBoards = new ArrayList<BoardInfo>();
        System.out.println("Color to generate for: " + ((color)? "black" : "white"));
        
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                byte id = boardInfo.board[x][y];
                if((id/16==Constants.pieceIDs.BLACK) == color){
                    //if the piece is  the color it is generating for.
        
                    if(id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.WHITE_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK || id == Constants.pieceIDs.WHITE_QUEENS_ROOK){
                        generatedBoards = movesRook(generatedBoards, boardInfo, x, y, color, id);
                        System.out.println("Boards generated for rook" + id + "moving: " + generatedBoards.size());
                        for(int i = 0; i < generatedBoards.size(); i++){
                            System.out.println(generatedBoards.get(i).previousMove);
                        }
                    }

                } else {

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
            beginPawnRange = 23;
            endPawnRange = 32;
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
            beginPawnRange = 7;
            endPawnRange = 16;
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
                    System.out.println("check from left");
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
                    System.out.println("check from right");
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
                    System.out.println("check from down");
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
                    System.out.println("check from up");
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
                    System.out.println("check from upRight");
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
                    System.out.println("check from upLeft");
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
                    System.out.println("check from downRight");
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
                    System.out.println("check from downleft");
                    return false;
                } else if (boardPositions[l][d] != Constants.pieceIDs.EMPTY_CELL) {
                    downLeft = false;
                }
            }
            if(d > 7){
                down = false;
                downRight = false;
                downLeft = false;
            }
            if(r > 7){
                right = false;
                downRight = false;
                upRight = false;
            }
            if(l < 0){
                downLeft = false;
                upLeft = false;
                left= false;
            }
            if(u < 0){
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
                            System.out.println("check from knight");
                    return false;
                }
            }
        }

        // pawnCheck
        // enemy pawn to the right
        if (x + 1 < 8 && (y + pawnDirection > -1 && y + pawnDirection < 8)) {
            if (boardPositions[x + 1][y + pawnDirection] > beginPawnRange
                    && boardPositions[x + 1][y + pawnDirection] < endPawnRange) {
                System.out.println("check from pawn");
                return false;
            }
        }
        // enemy pawn to the left
        if (x - 1 > -1 && (y + pawnDirection > -1 && y + pawnDirection < 8)) {
            if (boardPositions[x - 1][y + pawnDirection] > beginPawnRange
                    && boardPositions[x - 1][y + pawnDirection] < endPawnRange) {
                System.out.println("check from pawn");
                return false;
            }
        }

        // king check
        if (Math.abs(boardInfo.blackKingX - boardInfo.whiteKingX) < 2
                && Math.abs(boardInfo.blackKingY - boardInfo.whiteKingY) < 2) {
            System.out.println("check from king");
            System.out.println("Color: " + color);
            System.out.println("BlackKing coords: " + boardInfo.blackKingX + "," + boardInfo.blackKingY);
            System.out.println("WhiteKing coords: " + boardInfo.whiteKingX + "," + boardInfo.whiteKingY);
            return false;
        }

        System.out.println("No piece in check");
        return true;
    }

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
                    //adds the deadpieces to the boardInfo object.
                    //moves this piece to there.
                    newBoard.deadPieces.add(newBoard.board[x][u]);
                    newBoard.board[x][u] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;

                    //sets the string for the previous move
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + u);
                    //if the move doesn't place the king in check, it adds it to the list. 
                    if(isNotUnderCheck(newBoard, color)){
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
            
                    //moves piece there
                    newBoard.board[x][u] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + u);
                    if(isNotUnderCheck(newBoard, color)){
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
                    //adds the deadpieces to the boardInfo object.
                    //moves this piece to there.
                    newBoard.deadPieces.add(newBoard.board[x][d]);
                    newBoard.board[x][d] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;

                    //sets the string for the previous move
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + d);
                    //if the move doesn't place the king in check, it adds it to the list. 
                    if(isNotUnderCheck(newBoard, color)){
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
            
                    //moves piece there
                    newBoard.board[x][d] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + d);
                    if(isNotUnderCheck(newBoard, color)){
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
                    //adds the deadpieces to the boardInfo object.
                    //moves this piece to there.
                    newBoard.deadPieces.add(newBoard.board[l][y]);
                    newBoard.board[l][y] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;

                    //sets the string for the previous move
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + l + y);
                    //if the move doesn't place the king in check, it adds it to the list. 
                    if(isNotUnderCheck(newBoard, color)){
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
            
                    //moves piece there
                    newBoard.board[l][y] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + l + y);
                    if(isNotUnderCheck(newBoard, color)){
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
                    //adds the deadpieces to the boardInfo object.
                    //moves this piece to there.
                    newBoard.deadPieces.add(newBoard.board[r][y]);
                    newBoard.board[r][y] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;

                    //sets the string for the previous move
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + r + y);
                    //if the move doesn't place the king in check, it adds it to the list. 
                    if(isNotUnderCheck(newBoard, color)){
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
            
                    //moves piece there
                    newBoard.board[r][y] = id;
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + r + y);
                    if(isNotUnderCheck(newBoard, color)){
                        if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK
                                || id == Constants.pieceIDs.WHITE_KINGS_ROOK
                                || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
                            newBoard.hasMoved[getHasMovedIndex(id)] = true;
                        }
                        boards.add(newBoard);
                    }
                }
            }


            if(d > 7){
                down = false;
            }
            if(r > 7){
                right = false;
            }
            if(l < 0){
                left= false;
            }
            if(u < 0){
                up = false;
            }
            u--;
            l--;
            r++;
            d++;
        }

        return boards;
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
                    if ((initialBoard.board[i][j] > -1 && initialBoard.board[i][j] < 16)
                            || initialBoard.board[i][j] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
                        break;
                    } else {
                        BoardInfo newBoard = initialBoard.copy();
                        newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                        if (initialBoard.board[i][j] != Constants.pieceIDs.EMPTY_CELL) {
                            newBoard.deadPieces.add(newBoard.board[i][j]);
                            newBoard.board[i][j] = id;
                            if (isNotUnderCheck(newBoard, color)) {
                                newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                                boards.add(newBoard);
                            }
                            break;
                        }
                        newBoard.board[i][j] = id;
                        if (isNotUnderCheck(newBoard, color)) {
                            newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + i + j);
                            boards.add(newBoard);
                        }
                    }
                }
            }

        } else {
            for (int i = x - 1; i > -1; i--) {
                for (int j = y - 1; j > -1; j--) {
                    if ((initialBoard.board[i][j] > 15 && initialBoard.board[i][j] < 32)
                            || initialBoard.board[i][j] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
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
                    if ((initialBoard.board[i][j] > -1 && initialBoard.board[i][j] < 16)
                            || initialBoard.board[i][j] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
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
                    if ((initialBoard.board[i][j] > 15 && initialBoard.board[i][j] < 32)
                            || initialBoard.board[i][j] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
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
                    if ((initialBoard.board[i][j] > -1 && initialBoard.board[i][j] < 16)
                            || initialBoard.board[i][j] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
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
                    if ((initialBoard.board[i][j] > 15 && initialBoard.board[i][j] < 32)
                            || initialBoard.board[i][j] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
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
                    if ((initialBoard.board[i][j] > -1 && initialBoard.board[i][j] < 16)
                            || initialBoard.board[i][j] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
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
                    if ((initialBoard.board[i][j] > 15 && initialBoard.board[i][j] < 32)
                            || initialBoard.board[i][j] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
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
                if ((initialBoard.board[i][y] > -1 && initialBoard.board[i][y] < 16)
                        || initialBoard.board[i][y] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
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
                if ((initialBoard.board[i][y] > 15 && initialBoard.board[i][y] < 32)
                        || initialBoard.board[i][y] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
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
                if ((initialBoard.board[i][y] > -1 && initialBoard.board[i][y] < 16)
                        || initialBoard.board[i][y] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
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
                if ((initialBoard.board[i][y] > 15 && initialBoard.board[i][y] < 32)
                        || initialBoard.board[i][y] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
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
                if ((initialBoard.board[x][i] > -1 && initialBoard.board[x][i] < 16)
                        || initialBoard.board[i][y] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
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
                if ((initialBoard.board[x][i] > 15 && initialBoard.board[x][i] < 32)
                        || initialBoard.board[i][y] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
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
                if ((initialBoard.board[x][i] > -1 && initialBoard.board[x][i] < 16)
                        || initialBoard.board[i][y] == Constants.pieceIDs.BLACK_PROMOTED_ROOK) {
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
                if ((initialBoard.board[x][i] > 15 && initialBoard.board[x][i] < 32)
                        || initialBoard.board[i][y] == Constants.pieceIDs.WHITE_PROMOTED_ROOK) {
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
     * Method for getting the index for a pawn's passant value. This is the index
     * for the int[] passant array attribute in any BoardInfo object.
     * 
     * @param id The id of the pawn.
     * @return The indexe where the passant value is stored at.
     */
    private byte getPassantIndex(byte id) {
        // if the pawn is black, the index will be from 0-7, if the pawn is white the
        // index is from 8-15
        byte index = (id / 16 == Constants.pieceIDs.BLACK) ? (byte) (id - 8) : (byte) (id - 24 + 8);
        System.out.println("Pawn index for id: " + id + ", is " + index);
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
