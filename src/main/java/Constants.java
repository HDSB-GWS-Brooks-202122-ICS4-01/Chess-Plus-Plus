public class Constants {

    public static final class pieceIDs {
        public static final byte EMPTY_CELL = -1;
        
        public static final byte BLACK = 0;
        public static final byte WHITE = 1;


        public static final byte BLACK_KING = 0;
        public static final byte BLACK_QUEEN = 1;
        public static final byte BLACK_KINGS_BISHOP = 2;
        public static final byte BLACK_QUEENS_BISHOP = 3;
        public static final byte BLACK_KINGS_HORSE = 4;
        public static final byte BLACK_QUEENS_HORSE = 5;
        public static final byte BLACK_KINGS_ROOK = 6;
        public static final byte BLACK_QUEENS_ROOK = 7;
        public static final byte BLACK_PAWN_ZERO = 8;
        public static final byte BLACK_PAWN_ONE = 9;
        public static final byte BLACK_PAWN_TWO = 10;
        public static final byte BLACK_PAWN_THREE = 11;
        public static final byte BLACK_PAWN_FOUR = 12;
        public static final byte BLACK_PAWN_FIVE = 13;
        public static final byte BLACK_PAWN_SIX = 14;
        public static final byte BLACK_PAWN_SEVEN = 15;

        public static final byte WHITE_KING = 16;
        public static final byte WHITE_QUEEN = 17;
        public static final byte WHITE_KINGS_BISHOP = 18;
        public static final byte WHITE_QUEENS_BISHOP = 19;
        public static final byte WHITE_KINGS_HORSE = 20;
        public static final byte WHITE_QUEENS_HORSE = 21;
        public static final byte WHITE_KINGS_ROOK = 22;
        public static final byte WHITE_QUEENS_ROOK = 23;
        public static final byte WHITE_PAWN_ZERO = 24;
        public static final byte WHITE_PAWN_ONE = 25;
        public static final byte WHITE_PAWN_TWO = 26;
        public static final byte WHITE_PAWN_THREE = 27;
        public static final byte WHITE_PAWN_FOUR = 28;
        public static final byte WHITE_PAWN_FIVE = 29;
        public static final byte WHITE_PAWN_SIX = 30;
        public static final byte WHITE_PAWN_SEVEN = 31;
    }

    public static final class boardData {
        public static final byte[][] DEFAULT_GAME_SETUP = {
            {pieceIDs.BLACK_QUEENS_ROOK, pieceIDs.BLACK_PAWN_ZERO, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.WHITE_PAWN_ZERO, pieceIDs.WHITE_QUEENS_ROOK}, 
            {pieceIDs.BLACK_QUEENS_HORSE, pieceIDs.BLACK_PAWN_ONE, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.WHITE_PAWN_ONE, pieceIDs.WHITE_QUEENS_HORSE}, 
            {pieceIDs.BLACK_QUEENS_BISHOP, pieceIDs.BLACK_PAWN_TWO, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.WHITE_PAWN_TWO, pieceIDs.WHITE_QUEENS_BISHOP}, 
            {pieceIDs.BLACK_QUEEN, pieceIDs.BLACK_PAWN_THREE, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.WHITE_PAWN_THREE, pieceIDs.WHITE_QUEEN}, 
            {pieceIDs.BLACK_KING, pieceIDs.BLACK_PAWN_FOUR, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.WHITE_PAWN_FOUR, pieceIDs.WHITE_KING}, 
            {pieceIDs.BLACK_KINGS_BISHOP, pieceIDs.BLACK_PAWN_FIVE, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.WHITE_PAWN_FIVE, pieceIDs.WHITE_KINGS_BISHOP}, 
            {pieceIDs.BLACK_KINGS_HORSE, pieceIDs.BLACK_PAWN_SIX, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.WHITE_PAWN_SIX, pieceIDs.WHITE_KINGS_HORSE}, 
            {pieceIDs.BLACK_KINGS_ROOK, pieceIDs.BLACK_PAWN_SEVEN, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.EMPTY_CELL, pieceIDs.WHITE_PAWN_SEVEN, pieceIDs.WHITE_KINGS_ROOK}
        };
        

        //first index represents the id, second index has the position
        public static final byte[][] INITIAL_POSITIONS= {
            //Black positions
            {3,7},
            {4,7},
            {2,7},
            {5,7},
            {1,7},
            {6,7},
            {0,7},
            {7,7},
            {0,6},
            {1,6},
            {2,6},
            {3,6},
            {4,6},
            {5,6},
            {6,6},
            {7,6},

            //White positions
            {3,0},
            {4,0},
            {2,0},
            {5,0},
            {1,0},
            {6,0},
            {0,0},
            {7,0},
            {0,1},
            {1,1},
            {2,1},
            {3,1},
            {4,1},
            {5,1},
            {6,1},
            {7,1},
        };
    }

    public static final class SpriteSheetDimensions{
        public static final double PIECE_WIDTH = 200;
        public static final double PIECE_HEIGHT = 200;

        public static final double PIECE_FIT_WIDTH = 80;
        public static final double PIECE_FIT_HEIGHT = 80;


        public static final double WHITE_PIECE_Y = 0;


        // public static final double BLACK_PIECE_Y = 800;
        public static final double BLACK_PIECE_Y = 200;

    

        // public static final double KING_X = 0;
        // public static final double QUEEN_X = 800;
        // public static final double BISHOP_X = 1600;
        // public static final double KNIGHT_X = 2400;
        // public static final double ROOK_X = 3200;
        // public static final double PAWN_X=  4000;

        public static final double KING_X = 0;
        public static final double QUEEN_X = 200;
        public static final double BISHOP_X = 400;
        public static final double KNIGHT_X = 600;
        public static final double ROOK_X = 800;
        public static final double PAWN_X=  1000;
    } 
}
