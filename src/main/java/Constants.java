public class Constants {

    public static final class pieceIDs {

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

        public static final int[][] DEFAULT_GAME_SETUP = {
                {BLACK_QUEENS_ROOK, BLACK_PAWN_ZERO, -1, -1, -1, -1, WHITE_PAWN_ZERO, WHITE_QUEENS_ROOK}, 
                {BLACK_QUEENS_HORSE, BLACK_PAWN_ONE, -1, -1, -1, -1, WHITE_PAWN_ONE, WHITE_QUEENS_HORSE}, 
                {BLACK_QUEENS_BISHOP, BLACK_PAWN_TWO, -1, -1, -1, -1, WHITE_PAWN_TWO, WHITE_QUEENS_BISHOP}, 
                {BLACK_QUEEN, BLACK_PAWN_THREE, -1, -1, -1, -1, WHITE_PAWN_THREE, WHITE_QUEEN}, 
                {BLACK_KING, BLACK_PAWN_FOUR, -1, -1, -1, -1, WHITE_PAWN_FOUR, WHITE_KING}, 
                {BLACK_KINGS_BISHOP, BLACK_PAWN_FIVE, -1, -1, -1, -1, WHITE_PAWN_FIVE, WHITE_KINGS_BISHOP}, 
                {BLACK_KINGS_HORSE, BLACK_PAWN_SIX, -1, -1, -1, -1, WHITE_PAWN_SIX, WHITE_KINGS_HORSE}, 
                {BLACK_KINGS_ROOK, BLACK_PAWN_SEVEN, -1, -1, -1, -1, WHITE_PAWN_SEVEN, WHITE_KINGS_ROOK}
        };
    }
}
