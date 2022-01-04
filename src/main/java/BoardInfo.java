import java.util.Arrays;

public class BoardInfo {
    byte[][] board;
    int[] passant;
    boolean[] hasMoved;

    public BoardInfo(byte[][] board, int[] passant, boolean[] hasMoved){

        this.board = board;
        this.passant = passant;
        this.hasMoved = hasMoved;
    }

    public BoardInfo copy(){

        return new BoardInfo(Arrays.copyOf(board, board.length), Arrays.copyOf(passant, passant.length), Arrays.copyOf(hasMoved, hasMoved.length));

    }
    
}
