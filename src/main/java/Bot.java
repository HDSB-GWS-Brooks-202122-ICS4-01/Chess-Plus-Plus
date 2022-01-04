import java.util.ArrayList;

import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.common.eventbus.DeadEvent;

import javafx.animation.FadeTransition;

public class Bot {
    final int depth;
    final boolean black;

    /**
     * Constructor for the bot. 
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

        return minimax(depth, true, boardInfo).previousMove;
    }

    /**
     * Method for evaluating the value of the board. Evaluating a board means to apply a value for it,
     * this is used in the minimax algorithm.
     * @param boardInfo The board to evaluate.
     * @return an Integer represnting the score for this board. 
     */
    private int evaluate(BoardInfo boardInfo) {
        return 0;
    }

    /**
     * Minimax algorithm used for the A.I. 
     * @param depth
     * @param max
     * @param boardInfo
     * @return
     */
    private BoardInfo minimax(int depth, boolean max, BoardInfo boardInfo) {
        if (depth == 0) {
            // end case for recursions, at this point the board has generated the
            return boardInfo;
        }

        int lastEval = -1000;
        int thisEval;
        BoardInfo lastChild = null;
        BoardInfo[] children = generateBoards(boardInfo);
        BoardInfo boardToEvaluate;

        for (BoardInfo child : children) {
            boardToEvaluate = minimax(depth - 1, !max, child);
            thisEval = evaluate(boardToEvaluate);
            if ((max && thisEval > lastEval) || (!max && thisEval < lastEval)) {
                lastEval = thisEval;
                lastChild = boardToEvaluate;
            }

        }
        return lastChild;

    }

    /**
     * Method for generating all the possible boards/outcomes from this board. 
     * @param boardInfo The original board to generate from.
     * @return An array of BoardInfo objects, representing all the possible moves generated from the original board.
     */
    private BoardInfo[] generateBoards(BoardInfo boardInfo) {
        ArrayList<BoardInfo> generatedBoards = new ArrayList<BoardInfo>();
        if (black) {
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    if (boardInfo.board[x][y] == Constants.pieceIDs.EMPTY_CELL) {

                    } else if (boardInfo.board[x][y] == Constants.pieceIDs.BLACK_KINGS_ROOK
                            || boardInfo.board[x][y] == Constants.pieceIDs.BLACK_QUEENS_ROOK) {
                        generatedBoards = movesLeft(generatedBoards, boardInfo, x, y,
                                (boardInfo.board[x][y] % 16 == Constants.pieceIDs.BLACK), boardInfo.board[x][y]);
                        generatedBoards = movesRight(generatedBoards, boardInfo, x, y,
                                (boardInfo.board[x][y] % 16 == Constants.pieceIDs.BLACK), boardInfo.board[x][y]);
                        generatedBoards = movesUp(generatedBoards, boardInfo, x, y,
                                (boardInfo.board[x][y] % 16 == Constants.pieceIDs.BLACK), boardInfo.board[x][y]);
                        generatedBoards = movesDown(generatedBoards, boardInfo, x, y,
                                (boardInfo.board[x][y] % 16 == Constants.pieceIDs.BLACK), boardInfo.board[x][y]);
                            System.out.println("Moves generated after looking at black castle: " + generatedBoards.size());
                        

                    } else if (boardInfo.board[x][y] == Constants.pieceIDs.BLACK_KINGS_KNIGHT
                            || boardInfo.board[x][y] == Constants.pieceIDs.BLACK_QUEENS_KNIGHT) {

                    } else if (boardInfo.board[x][y] == Constants.pieceIDs.BLACK_QUEEN) {

                    } else if (boardInfo.board[x][y] == Constants.pieceIDs.BLACK_KINGS_BISHOP
                            || boardInfo.board[x][y] == Constants.pieceIDs.BLACK_QUEENS_BISHOP) {

                    } else if (boardInfo.board[x][y] == Constants.pieceIDs.BLACK_KINGS_ROOK
                            || boardInfo.board[x][y] == Constants.pieceIDs.BLACK_QUEENS_ROOK) {

                    } else if (boardInfo.board[x][y] == Constants.pieceIDs.BLACK_KING) {

                    } else if (boardInfo.board[x][y] > 7 && boardInfo.board[x][y] < 16) {

                    }

                }

            }

        } else {

        }

        return (BoardInfo[]) generatedBoards.toArray();
    }

    /**
     * Method for generating boards when a piece is moving to the left. It generates a new board when a piece is moving to a new position,
     * the newly generated board contains no references to the original board or any fields inside of it. It also updates the deadPieces 
     * field in the newBoard object when needed and records what move led to that board. 
     * @param boards ArrayList of boards that it will add the newly generated boards to. 
     * @param initialBoard The initial board it is generating moves from. 
     * @param x Pieces location in the x axis on the board.
     * @param y Pieces location in the y axis on the board. 
     * @param color Color of the piece. 
     * @param id Id of the piece. 
     * @return An ArrayList of BoardInfo objects that contain the newly generated moves in this direction.
     */
    private ArrayList<BoardInfo> movesLeft(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        // if the color of the piece is black
        if (color) {
            for (int i = x - 1; i > -1; i--) {
                if (initialBoard.board[i][y] > -1 && initialBoard.board[i][y] < 16) {
                    break;
                } else {
                    BoardInfo newBoard = initialBoard.copy();
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if(initialBoard.board[i][y] != Constants.pieceIDs.EMPTY_CELL){
                        newBoard.deadPieces.add(newBoard.board[i][y]);
                    }
                    newBoard.board[i][y] = id;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + y);
                    boards.add(newBoard);
                    break;
                }
            }
        } else {
            for (int i = x - 1; i > -1; i--) {
                if (initialBoard.board[i][y] > 15 && initialBoard.board[i][y] < 32) {
                    break;
                } else {
                    BoardInfo newBoard = initialBoard.copy();
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if(initialBoard.board[i][y] != Constants.pieceIDs.EMPTY_CELL){
                        newBoard.deadPieces.add(newBoard.board[i][y]);
                    }
                    newBoard.board[i][y] = id;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + y);
                    boards.add(newBoard);
                    break;
                }
            }

        }

        return boards;
    }



    /**
     * Method for generating boards when a piece is moving to the right. It generates a new board when a piece is moving to a new position,
     * the newly generated board contains no references to the original board or any fields inside of it. It also updates the deadPieces 
     * field in the newBoard object when needed and records what move led to that board. 
     * @param boards ArrayList of boards that it will add the newly generated boards to. 
     * @param initialBoard The initial board it is generating moves from. 
     * @param x Pieces location in the x axis on the board.
     * @param y Pieces location in the y axis on the board. 
     * @param color Color of the piece. 
     * @param id Id of the piece. 
     * @return An ArrayList of BoardInfo objects that contain the newly generated moves in this direction.
     */
    private ArrayList<BoardInfo> movesRight(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        // if the color of the piece is black
        if (color) {
            for (int i = x + 1; i < 8; i++) {
                if (initialBoard.board[i][y] > -1 && initialBoard.board[i][y] < 16) {
                    break;
                } else {
                    BoardInfo newBoard = initialBoard.copy();
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if(initialBoard.board[i][y] != Constants.pieceIDs.EMPTY_CELL){
                        newBoard.deadPieces.add(newBoard.board[i][y]);
                    }
                    newBoard.board[i][y] = id;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + y);
                    boards.add(newBoard);
                    break;
                }
            }
        } else {
            for (int i = x + 1; i < 8; i++) {
                if (initialBoard.board[i][y] > 15 && initialBoard.board[i][y] < 32) {
                    break;
                } else {
                    BoardInfo newBoard = initialBoard.copy();
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if(initialBoard.board[i][y] != Constants.pieceIDs.EMPTY_CELL){
                        newBoard.deadPieces.add(newBoard.board[i][y]);
                    }
                    newBoard.board[i][y] = id;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + y);
                    boards.add(newBoard);
                    break;
                }
            }
        }

        return boards;
    }


    /**
     * Method for generating boards when a piece is moving up. It generates a new board when a piece is moving to a new position,
     * the newly generated board contains no references to the original board or any fields inside of it. It also updates the deadPieces 
     * field in the newBoard object when needed and records what move led to that board. 
     * @param boards ArrayList of boards that it will add the newly generated boards to. 
     * @param initialBoard The initial board it is generating moves from. 
     * @param x Pieces location in the x axis on the board.
     * @param y Pieces location in the y axis on the board. 
     * @param color Color of the piece. 
     * @param id Id of the piece. 
     * @return An ArrayList of BoardInfo objects that contain the newly generated moves in this direction.
     */
    private ArrayList<BoardInfo> movesUp(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        // if the color of the piece is black
        if (color) {
            for (int i = y - 1; i > -1; i--) {
                if (initialBoard.board[x][i] > -1 && initialBoard.board[x][i] < 16) {
                    break;
                } else {
                    BoardInfo newBoard = initialBoard.copy();
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if(initialBoard.board[x][i] != Constants.pieceIDs.EMPTY_CELL){
                        newBoard.deadPieces.add(newBoard.board[x][i]);
                    }
                    newBoard.board[x][i] = id;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + i);
                    boards.add(newBoard);
                    break;
                }
            }
        } else {
            for (int i = y - 1; i > -1; i--) {
                if (initialBoard.board[x][i] > 15 && initialBoard.board[x][i] < 32) {
                    break;
                } else {
                    BoardInfo newBoard = initialBoard.copy();
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if(initialBoard.board[x][i] != Constants.pieceIDs.EMPTY_CELL){
                        newBoard.deadPieces.add(newBoard.board[x][i]);
                    }
                    newBoard.board[x][i] = id;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + i);
                    boards.add(newBoard);
                    break;
                }
            }

        }

        return boards;
    }


    /**
     * Method for generating boards when a piece is moving down. It generates a new board when a piece is moving to a new position,
     * the newly generated board contains no references to the original board or any fields inside of it. It also updates the deadPieces 
     * field in the newBoard object when needed and records what move led to that board. 
     * @param boards ArrayList of boards that it will add the newly generated boards to. 
     * @param initialBoard The initial board it is generating moves from. 
     * @param x Pieces location in the x axis on the board.
     * @param y Pieces location in the y axis on the board. 
     * @param color Color of the piece. 
     * @param id Id of the piece. 
     * @return An ArrayList of BoardInfo objects that contain the newly generated moves in this direction.
     */
    private ArrayList<BoardInfo> movesDown(ArrayList<BoardInfo> boards, BoardInfo initialBoard, int x, int y,
            boolean color, byte id) {
        // if the color of the piece is black
        if (color) {
            for (int i = y + 1; i < 8; i++) {
                if (initialBoard.board[x][i] > -1 && initialBoard.board[x][i] < 16) {
                    break;
                } else {
                    BoardInfo newBoard = initialBoard.copy();
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if(initialBoard.board[x][i] != Constants.pieceIDs.EMPTY_CELL){
                        newBoard.deadPieces.add(newBoard.board[x][i]);
                    }
                    newBoard.board[x][i] = id;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + i);
                    boards.add(newBoard);
                    break;
                }
            }
        } else {
            for (int i = y - 1; i > -1; i++) {
                if (initialBoard.board[x][i] > 15 && initialBoard.board[x][i] < 32) {
                    break;
                } else {
                    BoardInfo newBoard = initialBoard.copy();
                    newBoard.board[x][y] = Constants.pieceIDs.EMPTY_CELL;
                    if(initialBoard.board[x][i] != Constants.pieceIDs.EMPTY_CELL){
                        newBoard.deadPieces.add(newBoard.board[x][i]);
                    }
                    newBoard.board[x][i] = id;
                    newBoard.setPreviousMove(Constants.moveTypes.REGULAR + id + "." + x + i);
                    boards.add(newBoard);
                    break;
                }
            }

        }

        return boards;
    }


}
