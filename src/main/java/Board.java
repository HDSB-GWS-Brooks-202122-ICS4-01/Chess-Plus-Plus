import java.util.ArrayList;

import javafx.scene.layout.GridPane;

public class Board {
   final GameController GAME;
   final GridPane gp_CHESS_BOARD;
   final int[][] GRID = Constants.boardData.DEFAULT_GAME_SETUP;

   final ArrayList<Piece> LIVE_PIECES = new ArrayList<Piece>();
   final ArrayList<Piece> DEAD_PIECES = new ArrayList<Piece>();

   private int turn = 1;

   public Board(GameController game, GridPane chessBoard) {
      GAME = game;
      gp_CHESS_BOARD = chessBoard;

      System.out.println(GRID[3][7]);
   }

   /**
    * This method will return the grid fxml object
    * @return  GridPane object
    */
   public GridPane getBoard() {
      return gp_CHESS_BOARD;
   }

   /**
    * This method will which player's turn it is.
    * @return  int
    */
   public int getTurn() {
      return turn;
   }

   /**
    * This method will return an array list containing all the live pieces
    * @return  ArrayList containing pieces
    */
   public ArrayList<Piece> getLivePieces() {
      return LIVE_PIECES;
   }

   /**
    * This method will return an array list containing all the dead pieces
    * @return  ArrayList containing pieces
    */
   public ArrayList<Piece> getDeadPieces() {
      return DEAD_PIECES;
   }

   /**
    * This method will see if the attempted move is possible
    * @param id         int value corresponding to the id of the game piece
    * @param location   int array containing the x and y coordinates of the game piece
    * @return           boolean, true or false.
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
