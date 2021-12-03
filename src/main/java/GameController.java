import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class GameController {
   @FXML
   HBox hb_root;
   @FXML
   GridPane gp_board;

   private final Board BOARD = new Board(this, gp_board);

   @FXML
   public void initialize() {
      System.out.println(true);
   }
}