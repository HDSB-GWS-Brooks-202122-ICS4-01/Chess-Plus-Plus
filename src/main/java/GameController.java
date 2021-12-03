import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class GameController {
   @FXML
   HBox hb_root;
   @FXML
   GridPane gp_board;

   private Board board;

   @FXML
   public void initialize() {
      System.out.println(true);

      board = new Board(this, gp_board);
   }
}