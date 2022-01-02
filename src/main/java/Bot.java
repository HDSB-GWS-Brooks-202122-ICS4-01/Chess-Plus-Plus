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

    public byte[] getMove(byte[][] board){

        int[] pieceData = new int[32];

        return null;
    }


    private int evaluate(byte[][] board){
        return 0;
    }


    private byte[] minimax(int depth, boolean max, byte[][] board, int[] pieceData){
        return null;

    }
}
