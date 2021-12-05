import java.util.ArrayList;
import java.util.function.Function;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Board {
   private final GameController GAME;
   private final GridPane gp_CHESS_BOARD;
   private final byte[][] GRID = Constants.boardData.DEFAULT_GAME_SETUP;

   private final StackPane[][] CELLS = new StackPane[8][8];

   private final ArrayList<Piece> LIVE_PIECES = new ArrayList<Piece>();
   private final ArrayList<Piece> DEAD_PIECES = new ArrayList<Piece>();

   private StackPane sp_selected;

   private byte turn = 1;

   public Board(GameController game, GridPane chessBoard) {
      GAME = game;
      gp_CHESS_BOARD = chessBoard;

      for (javafx.scene.Node node : gp_CHESS_BOARD.getChildren()) {
         StackPane sp = (StackPane) node;

         int x = (GridPane.getColumnIndex(node) != null) ? GridPane.getColumnIndex(node) : 0;
         int y = (GridPane.getRowIndex(node) != null) ? GridPane.getRowIndex(node) : 0;

         // System.out.println("(" + x + ", " + y + ")");

         // sp.setOnMouseEntered((sp) -> this::highlightMouseCellHover);
         sp.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
               // if (this.check)
               if (getAllowInteract(sp, x, y))
                  highlightMouseCellHover(true, sp);
            }
         });

         sp.setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
               highlightMouseCellHover(false, sp);
            }
         });

         sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
               if (sp_selected != null)
                  sp_selected.getStyleClass().remove("cell-selected");

               if (getAllowInteract(sp, x, y)) {
                  sp.getStyleClass().add("cell-selected");
                  sp_selected = sp;
               } else
                  sp_selected = null;
            }
         });

         CELLS[x][y] = sp;
      }

      for (int x = 0; x < GRID.length; x++) {
         for (int y = 0; y < GRID.length; y++) {
            byte id = GRID[x][y];

            if (id == Constants.pieceIDs.EMPTY_CELL)
               continue;

            Piece piece = null;

            if (id == Constants.pieceIDs.BLACK_KING || id == Constants.pieceIDs.WHITE_KING) {
               piece = new King(id);
            } else if (id == Constants.pieceIDs.BLACK_QUEEN || id == Constants.pieceIDs.WHITE_QUEEN) {
               piece = new Queen(id);
            } else if (id == Constants.pieceIDs.BLACK_KINGS_BISHOP || id == Constants.pieceIDs.BLACK_QUEENS_BISHOP
                  || id == Constants.pieceIDs.WHITE_KINGS_BISHOP || id == Constants.pieceIDs.WHITE_QUEENS_BISHOP) {
               piece = new Bishop(id);
            } else if (id == Constants.pieceIDs.BLACK_KINGS_HORSE || id == Constants.pieceIDs.BLACK_QUEENS_HORSE
                  || id == Constants.pieceIDs.WHITE_KINGS_HORSE || id == Constants.pieceIDs.WHITE_QUEENS_HORSE) {
               piece = new Knight(id);
            } else if (id == Constants.pieceIDs.BLACK_KINGS_ROOK || id == Constants.pieceIDs.BLACK_QUEENS_ROOK
                  || id == Constants.pieceIDs.WHITE_KINGS_ROOK || id == Constants.pieceIDs.WHITE_QUEENS_ROOK) {
               piece = new Rook(id);
            } else {
               piece = new Pawn(id);
            }

            piece.setGridPos(x, y);
            LIVE_PIECES.add(piece);
            CELLS[x][y].getChildren().add(piece.getSprite());
         }
      }
   }

   /**
    * This method will check to see if the player is able to interact with a
    * certain grid element.
    * 
    * @param target Target stackpane
    * @param x      X coordinates of the grid cell
    * @param y      Y coordinates of the grid cell
    * @return true / false
    */
   private boolean getAllowInteract(StackPane target, int x, int y) {
      for (Node node : target.getChildren()) {
         if (node instanceof ImageView) {
            if (GRID[x][y] != Constants.pieceIDs.EMPTY_CELL) {
               for (Piece livePiece : LIVE_PIECES) {
                  if (livePiece.getGridX() == x && livePiece.getGridY() == y) {
                     if (livePiece.getColor() == turn)
                        return true;
                     else
                        return false;
                  }
               }
            }

            break;
         }

      }

      return false;
   }

   private Piece getPieceOnGrid(int x, int y) {
      for (Piece piece : LIVE_PIECES) {
         if (piece.getGridX() == x && piece.getGridY() == y)
            return piece;
      }

      return null;
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
      if (turn == 0) {

         turn = 1;
      } else {
         turn = 0;
      }
   }

   private void highlightMouseCellHover(boolean entered, StackPane target) {
      if (entered) {
         target.getStyleClass().add("cell-hover");
      } else {
         target.getStyleClass().remove("cell-hover");
      }
   }

}
