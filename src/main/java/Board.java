import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class Board {
   final GameController GAME;
   final GridPane gp_CHESS_BOARD;
   final byte[][] GRID = Constants.boardData.DEFAULT_GAME_SETUP;

   final StackPane[][] CELLS = new StackPane[8][8];

   final ArrayList<Piece> LIVE_PIECES = new ArrayList<Piece>();
   final ArrayList<Piece> DEAD_PIECES = new ArrayList<Piece>();

   private int turn = 1;

   public Board(GameController game, GridPane chessBoard) {
      GAME = game;
      gp_CHESS_BOARD = chessBoard;

      for (javafx.scene.Node node : gp_CHESS_BOARD.getChildren()) {
         StackPane sp = (StackPane) node;

         int x = (GridPane.getColumnIndex(node) != null) ? GridPane.getColumnIndex(node) : 0;
         int y = (GridPane.getRowIndex(node) != null) ? GridPane.getRowIndex(node) : 0;
         
         // System.out.println("(" + x + ", " + y + ")");

         CELLS[x][y] = sp;
      }

      for (int x = 0; x < GRID.length; x++) {
         for (int y = 0; y < GRID.length; y++) {
            CELLS[x][y].getChildren().add(new Label("(" + x + ", " + y + ")"));
            byte id = GRID[x][y];

            if (id == Constants.pieceIDs.EMPTY_CELL)
               continue;

            System.out.println("(" + x + ", " + y + ")");
            // System.out.println(id);

            Piece piece = null;

            if (id == Constants.pieceIDs.BLACK_KING || id == Constants.pieceIDs.WHITE_KING) {
               piece = new King(id);
            } else if (id == Constants.pieceIDs.BLACK_QUEEN || id == Constants.pieceIDs.WHITE_QUEEN) {
               piece = new Queen(id);
            } else if (id == Constants.pieceIDs.BLACK_KINGS_BISHOP || id == Constants.pieceIDs.BLACK_QUEENS_BISHOP || id == Constants.pieceIDs.WHITE_KINGS_BISHOP || id == Constants.pieceIDs.WHITE_QUEENS_BISHOP) {
               piece = new Bishop(id);
            } else if (id == Constants.pieceIDs.BLACK_KINGS_HORSE || id == Constants.pieceIDs.BLACK_QUEENS_HORSE || id == Constants.pieceIDs.WHITE_KINGS_HORSE || id == Constants.pieceIDs.WHITE_QUEENS_HORSE) {
               piece = new Knight(id);
            } else if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK || id == Constants.pieceIDs.WHITE_KINGS_ROOK || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
               piece = new Rook(id);
            } else{
               piece = new Pawn(id);
            }

            LIVE_PIECES.add(piece);
            CELLS[x][y].getChildren().add(piece.getSprite());

            System.out.println(CELLS[x][y].getChildren().toString());


            // switch (id) {
            // case Constants.pieceIDs.BLACK_KING:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.BLACK_QUEEN:
            // piece = new Queen((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.BLACK_KINGS_BISHOP:
            // piece = new Bishop((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.BLACK_QUEENS_BISHOP:
            // piece = new Bishop((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.BLACK_KINGS_HORSE:
            // piece = new Knight((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.BLACK_QUEENS_HORSE:
            // piece = new Knight((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.BLACK_KINGS_ROOK:
            // piece = new Rook((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.BLACK_QUEENS_ROOK:
            // piece = new Rook((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.BLACK_PAWN_ZERO:
            // piece = new Pawn((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.BLACK_PAWN_ONE:
            // piece = new Pawn((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.BLACK_PAWN_TWO:
            // piece = new Pawn((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.BLACK_PAWN_THREE:
            // piece = new Pawn((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.BLACK_PAWN_FOUR:
            // piece = new Pawn((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.BLACK_PAWN_FIVE:
            // piece = new Pawn((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.BLACK_PAWN_SIX:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.BLACK_PAWN_SEVEN:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.WHITE_KING:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.WHITE_QUEEN:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.WHITE_KINGS_BISHOP:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.WHITE_QUEENS_BISHOP:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.WHITE_KINGS_HORSE:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.WHITE_QUEENS_HORSE:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.WHITE_KINGS_ROOK:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.WHITE_QUEENS_ROOK:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.WHITE_PAWN_ZERO:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.WHITE_PAWN_ONE:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.WHITE_PAWN_TWO:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.WHITE_PAWN_THREE:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.WHITE_PAWN_FOUR:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.WHITE_PAWN_FIVE:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.WHITE_PAWN_SIX:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // case Constants.pieceIDs.WHITE_PAWN_SEVEN:
            // piece = new King((byte) Constants.pieceIDs.BLACK_KING);
            // break;
            // }
         }
      }

      System.out.println(GRID[3][7]);
   }

   /**
    * This method will return the grid fxml object
    * 
    * @return GridPane object
    */
   public GridPane getBoard() {
      return gp_CHESS_BOARD;
   }

   /**
    * This method will which player's turn it is.
    * 
    * @return int
    */
   public int getTurn() {
      return turn;
   }

   /**
    * This method will return an array list containing all the live pieces
    * 
    * @return ArrayList containing pieces
    */
   public ArrayList<Piece> getLivePieces() {
      return LIVE_PIECES;
   }

   /**
    * This method will return an array list containing all the dead pieces
    * 
    * @return ArrayList containing pieces
    */
   public ArrayList<Piece> getDeadPieces() {
      return DEAD_PIECES;
   }

   /**
    * This method will see if the attempted move is possible
    * 
    * @param id       int value corresponding to the id of the game piece
    * @param location int array containing the x and y coordinates of the game
    *                 piece
    * @return boolean, true or false.
    */
   public boolean tryMove(int id, int[] location) {

      return false;
   }

   /**
    * This method will cycle to the next player's turn
    */
   public void nextMove() {
      if (turn == 1) {

         turn = 2;
      }
   }

}
