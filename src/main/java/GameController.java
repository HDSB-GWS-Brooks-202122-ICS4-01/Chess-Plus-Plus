import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class GameController {
   @FXML
   HBox hb_root;
   @FXML
   GridPane gp_board, gp_blackDeadCells, gp_whiteDeadCells;

   @FXML
   Label lbl_bTimer, lbl_wTimer;

   private Board board;

   @FXML
   public void initialize() {
      System.out.println(true);

      board = new Board(this, new GridPane[]{gp_board, gp_blackDeadCells, gp_whiteDeadCells});
   }

   public Label getTimeReference(byte color) {
      if (color == Constants.pieceIDs.BLACK) 
         return lbl_bTimer;
      else
         return lbl_wTimer;
   }
}