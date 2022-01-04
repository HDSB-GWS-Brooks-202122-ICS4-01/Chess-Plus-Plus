import java.util.ArrayList;

public class Bot {
    final int depth;


    public Bot(String diff){
        switch(diff){
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
    }

    public byte[] getMove(byte[][] board, ArrayList<Piece> deadPiecesObjects){
        //converting deadpieces arraylist into an arraylist of bytes. 
        ArrayList<Byte> deadPieces = new ArrayList<Byte>();
        for(int i = 0; i < deadPiecesObjects.size(); i++){
            deadPieces.add(deadPiecesObjects.get(i).getId());
        }

        //creating array containing hasmoved data
        boolean[] hasMoved = new boolean[6];
        hasMoved[0] = App.GAME_PIECES[Constants.pieceIDs.BLACK_KINGS_ROOK].hasMoved;
        hasMoved[1] = App.GAME_PIECES[Constants.pieceIDs.BLACK_QUEENS_ROOK].hasMoved;
        hasMoved[2] = App.GAME_PIECES[Constants.pieceIDs.WHITE_KINGS_ROOK].hasMoved;
        hasMoved[3] = App.GAME_PIECES[Constants.pieceIDs.WHITE_QUEENS_ROOK].hasMoved;
        hasMoved[4] = App.GAME_PIECES[Constants.pieceIDs.BLACK_KING].hasMoved;
        hasMoved[5] = App.GAME_PIECES[Constants.pieceIDs.WHITE_KING].hasMoved;

        //creating array containing passant data
        int[] passant = new int[16];
        for(int i = 0; i < 8; i++){
            //data for black pawns
            passant[i] = ((Pawn) App.GAME_PIECES[i+8]).passant;
        }
        for(int i = 8; i < 16; i++){
            //data for white pawns
            passant[i] = ((Pawn) App.GAME_PIECES[i+16]).passant;
        }


        BoardInfo boardInfo = new BoardInfo(board, passant, hasMoved);


        BoardInfo chosenMove = minimax(depth, true, boardInfo);
        
        return null;
    }


    private int evaluate(BoardInfo boardInfo){
        return 0;
    }

    private BoardInfo minimax(int depth, boolean max, BoardInfo boardInfo){
        if(depth == 0){
            //end case for recursions, at this point the board has generated the 
            return boardInfo;
        }

        int lastEval = -1000;
        int thisEval;
        BoardInfo lastChild = null;
        BoardInfo[] children = generateBoards(boardInfo);
        BoardInfo boardToEvaluate;


        for(BoardInfo child : children){
            boardToEvaluate = minimax(depth-1, !max, child);
            thisEval = evaluate(boardToEvaluate);
            if((max && thisEval > lastEval) || (!max &&thisEval < lastEval)){
                lastEval = thisEval;
                lastChild = boardToEvaluate;
            }

        }
        return lastChild;

    }

    private BoardInfo[] generateBoards(BoardInfo boardInfo){
        return null;
    }

}

