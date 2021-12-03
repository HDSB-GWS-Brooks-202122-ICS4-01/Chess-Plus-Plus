public class Constants {

    public static final class pieceIDs {
        public static final int EMPTY_CELL = -1;

        public static final int BLACK_KING = 0;
        public static final int BLACK_QUEEN = 1;
        public static final int BLACK_KINGS_BISHOP = 2;
        public static final int BLACK_QUEENS_BISHOP = 3;
        public static final int BLACK_KINGS_HORSE = 4;
        public static final int BLACK_QUEENS_HORSE = 5;
        public static final int BLACK_KINGS_ROOK = 6;
        public static final int BLACK_QUEENS_ROOK = 7;
        public static final int BLACK_PAWN_ZERO = 8;
        public static final int BLACK_PAWN_ONE = 9;
        public static final int BLACK_PAWN_TWO = 10;
        public static final int BLACK_PAWN_THREE = 11;
        public static final int BLACK_PAWN_FOUR = 12;
        public static final int BLACK_PAWN_FIVE = 13;
        public static final int BLACK_PAWN_SIX = 14;
        public static final int BLACK_PAWN_SEVEN = 15;

        public static final int WHITE_KING = 16;
        public static final int WHITE_QUEEN = 17;
        public static final int WHITE_KINGS_BISHOP = 18;
        public static final int WHITE_QUEENS_BISHOP = 19;
        public static final int WHITE_KINGS_HORSE = 20;
        public static final int WHITE_QUEENS_HORSE = 21;
        public static final int WHITE_KINGS_ROOK = 22;
        public static final int WHITE_QUEENS_ROOK = 23;
        public static final int WHITE_PAWN_ZERO = 24;
        public static final int WHITE_PAWN_ONE = 25;
        public static final int WHITE_PAWN_TWO = 26;
        public static final int WHITE_PAWN_THREE = 27;
        public static final int WHITE_PAWN_FOUR = 28;
        public static final int WHITE_PAWN_FIVE = 29;
        public static final int WHITE_PAWN_SIX = 30;
        public static final int WHITE_PAWN_SEVEN = 31;
    }

    public static final class boardData {
        public static final int[][] DEFAULT_GAME_SETUP = {
            {pieceIDs.BLACK_QUEENS_ROOK, pieceIDs.BLACK_PAWN_ZERO, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.WHITE_PAWN_ZERO, pieceIDs.WHITE_QUEENS_ROOK}, 
            {pieceIDs.BLACK_QUEENS_HORSE, pieceIDs.BLACK_PAWN_ONE, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.WHITE_PAWN_ONE, pieceIDs.WHITE_QUEENS_HORSE}, 
            {pieceIDs.BLACK_QUEENS_BISHOP, pieceIDs.BLACK_PAWN_TWO, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.WHITE_PAWN_TWO, pieceIDs.WHITE_QUEENS_BISHOP}, 
            {pieceIDs.BLACK_QUEEN, pieceIDs.BLACK_PAWN_THREE, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.WHITE_PAWN_THREE, pieceIDs.WHITE_QUEEN}, 
            {pieceIDs.BLACK_KING, pieceIDs.BLACK_PAWN_FOUR, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.WHITE_PAWN_FOUR, pieceIDs.WHITE_KING}, 
            {pieceIDs.BLACK_KINGS_BISHOP, pieceIDs.BLACK_PAWN_FIVE, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.WHITE_PAWN_FIVE, pieceIDs.WHITE_KINGS_BISHOP}, 
            {pieceIDs.BLACK_KINGS_HORSE, pieceIDs.BLACK_PAWN_SIX, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.WHITE_PAWN_SIX, pieceIDs.WHITE_KINGS_HORSE}, 
            {pieceIDs.BLACK_KINGS_ROOK, pieceIDs.BLACK_PAWN_SEVEN, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.WHITE_PAWN_SEVEN, pieceIDs.WHITE_KINGS_ROOK}
    };
    }
}
