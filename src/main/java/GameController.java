import java.io.IOException;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
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

   /**
    * This method will display the pawn promotion for the white player.
    * 
    * @param piece Piece that needs to be promoted
    * @throws IOException May throw an exception if the fxml is not found.
    */
   public void displayWhitePawnPromotion(Piece piece) throws IOException {
      Parent nextScene = App.loadFXML("white-pawn-end");
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
      timeline.setOnFinished(f -> {
         GridPane gp = (GridPane) nextScene.getScene().lookup("#gp_wPieces");

         System.out.println(gp);

         for (Node n : gp.getChildren()) {
            n.setOnMouseEntered(new EventHandler<MouseEvent>() {
               public void handle(MouseEvent me) {
                  n.getStyleClass().add("cell-hover");
               }
            });

            n.setOnMouseExited(new EventHandler<MouseEvent>() {
               public void handle(MouseEvent me) {
                  n.getStyleClass().remove("cell-hover");
               }
            });

            n.setOnMouseClicked(new EventHandler<MouseEvent>() {
               public void handle(MouseEvent me) {
                  String type = ((StackPane) n).getId();

                  board.promotePawn(piece, type);

                  Timeline timeline = new Timeline(
                        new KeyFrame(javafx.util.Duration.seconds(1),
                              new KeyValue(nextScene.translateYProperty(), sp_root.getHeight(), Interpolator.EASE_BOTH)),
                        new KeyFrame(javafx.util.Duration.seconds(1),
                              new KeyValue(nextScene.translateYProperty(), sp_root.getHeight(),
                                    Interpolator.EASE_BOTH)));

                  timeline.play();
                  timeline.setOnFinished((r) -> {
                     sp_root.getChildren().remove(nextScene);
                  });
               }
            });
         }
      });

      timeline.play();
   }
}