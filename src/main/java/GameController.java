import java.io.IOException;
import java.time.Duration;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class GameController {
   @FXML
   StackPane sp_root;

   @FXML
   GridPane gp_board, gp_blackDeadCells, gp_whiteDeadCells;

   @FXML
   Label lbl_bTimer, lbl_wTimer;

   private Board board;

   @FXML
   public void initialize() {
      board = new Board(this, new GridPane[] { gp_board, gp_blackDeadCells, gp_whiteDeadCells });
   }

   public Label getTimeReference(byte color) {
      if (color == Constants.pieceIDs.BLACK)
         return lbl_bTimer;
      else
         return lbl_wTimer;
   }

   public void displayWhitePawnPromotion() throws IOException {
      Parent nextScene = App.loadFXML("white-pawn-promotion");
      sp_root.getChildren().add(nextScene);
      nextScene.translateYProperty().set(sp_root.getScene().getHeight());

      // Create a timeline
      Timeline timeline = new Timeline(
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(nextScene.translateYProperty(), 0, Interpolator.EASE_BOTH)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(nextScene.translateYProperty(), -sp_root.getHeight(), Interpolator.EASE_BOTH)));

      // Play ani
      timeline.play();

      // timeline.setOnFinished((e) -> {
      // // Remove containers
      // sp_root.getChildren().remove(hb_container);
      // sp_root.getChildren().remove(nextScene);

      // int width = (int) Math.round(grid.getWidth() / slider_nor.getValue());
      // int size = (int) Math.round(slider_nor.getValue());

      // App.setWidth(width);

      // App.setArray(App.generateArray(size, grid.getHeight()));
      // App.setAlgorithm(name);

      // // Sneakily set the scen ;)
      // try {
      // App.setRoot("sort");
      // } catch (IOException err) {
      // err.printStackTrace();
      // }
      // });

      timeline.play();
   }
}