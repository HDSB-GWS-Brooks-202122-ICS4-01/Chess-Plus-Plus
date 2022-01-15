import java.io.IOException;
import java.util.Properties;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controller of the game scene, also initializes the Board class.
 * 
 * @author Selim Abdelwahab
 * @version 1.0
 */
public class GameController {
   Properties config = App.getConfig();

   @FXML
   StackPane sp_root, sp_container;

   @FXML
   GridPane gp_board, gp_blackDeadCells, gp_whiteDeadCells;

   @FXML
   Pane pn_dev;

   @FXML
   Label lbl_bTimer, lbl_wTimer, lbl_hbTimer, lbl_hwTimer;

   @FXML
   VBox vb_gameover;

   private Board board = null;
   private OnlineBoard oBoard = null;

   @FXML
   /**
    * Acts as the constructor and initializes the scene
    * @throws FirebaseAuthException General firebase exception
    * @throws InterruptedException  Will occur when the thread is interrupted
    */
   public void initialize() throws FirebaseAuthException, InterruptedException {
      // Set boards
      if (App.getGameMode() != Constants.boardData.MODE_ONLINE)
         board = new Board(this, new GridPane[] { gp_board, gp_blackDeadCells, gp_whiteDeadCells }, App.getGameMode());
      else
         oBoard = new OnlineBoard(this, new GridPane[] { gp_board, gp_blackDeadCells, gp_whiteDeadCells });

      // Check if the user is signed in
      if (config.getProperty("signedIn").equalsIgnoreCase("t")) {
         UserRecord userRecord = FirebaseAuth.getInstance().getUser(config.getProperty("UID"));

         boolean userIsDev = false;

         // Check email against developers' emails
         for (String dev : Constants.Online.DEV_EMAILS) {
            if (userRecord.getEmail().equalsIgnoreCase(dev)) {
               userIsDev = true;
               break;
            }
         }

         // Display invisible pane
         if (userIsDev) {
            App.getStage().setHeight(1050);
            pn_dev.setVisible(true);
         }
      }
   }

   /**
    * This method will return the lable reference to a timer
    * @param color   The color corresponding to the timer
    * @return  Label object
    */
   public Label getTimeReference(byte color) {
      if (color == Constants.pieceIDs.BLACK)
         return lbl_bTimer;
      else
         return lbl_wTimer;
   }

   /**
    * This method will return the lable reference to an invisible timer
    * @param color   The color corresponding to the timer
    * @return  Label object
    */
   public Label getHiddenTimeReference(byte color) {
      if (color == Constants.pieceIDs.BLACK)
         return lbl_hbTimer;
      else
         return lbl_hwTimer;
   }

   /**
    * This method will display the pawn promotion for the white player.
    * 
    * @param piece Piece that needs to be promoted
    * @throws IOException May throw an exception if the fxml is not found.
    */
   public void displayPawnPromotion(Piece piece, byte type) throws IOException {
      Parent nextScene = App.loadFXML((type == Constants.pieceIDs.WHITE) ? "white-pawn-end" : "black-pawn-end");
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

                  // Promote the pawn
                  if (App.getGameMode() == Constants.boardData.MODE_ONLINE)
                     oBoard.promotePawn(piece, type, false);
                  else
                     board.promotePawn(piece, type, false);

                  Timeline timeline = new Timeline(
                        new KeyFrame(javafx.util.Duration.seconds(1),
                              new KeyValue(nextScene.translateYProperty(), sp_root.getHeight(),
                                    Interpolator.EASE_BOTH)),
                        new KeyFrame(javafx.util.Duration.seconds(1),
                              new KeyValue(nextScene.translateYProperty(), sp_root.getHeight(),
                                    Interpolator.EASE_BOTH)));

                  // Play animations
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

   /**
    * This method will transition to the end screen.
    * 
    * @param piece Piece that needs to be promoted
    * @throws IOException May throw an exception if the fxml is not found.
    */
   public void gameOver() throws IOException {
      App.getStage().setHeight(App.getStage().getMinHeight());

      vb_gameover.translateYProperty().set(sp_root.getScene().getHeight());
      vb_gameover.setVisible(true);

      Parent endScene = App.loadFXML("end");

      sp_root.getChildren().add(endScene);
      endScene.translateYProperty().set(sp_root.getScene().getHeight());

      Timeline[] timelines = new Timeline[] {
            new Timeline(
                  new KeyFrame(javafx.util.Duration.seconds(1),
                        new KeyValue(vb_gameover.translateYProperty(), 0, Interpolator.EASE_BOTH))),
            new Timeline(
                  new KeyFrame(javafx.util.Duration.seconds(1),
                        new KeyValue(endScene.translateYProperty(), 0)),
                  new KeyFrame(javafx.util.Duration.seconds(1),
                        new KeyValue(sp_container.translateYProperty(), -sp_root.getScene().getHeight()))),
      };

      // Play ani
      timelines[0].play();
      timelines[0].setOnFinished(f0 -> {
         timelines[1].setDelay(javafx.util.Duration.seconds(3));
         timelines[1].play();

      });

      timelines[1].setOnFinished(f1 -> {
         sp_root.getChildren().remove(endScene);
         App.setRoot(endScene);
      });
   }

   /**
    * This method will be call the dev request method with the aiMove argument.
    */
   @FXML
   private void devGetAiMoves() {
      board.devRequest(Constants.Dev.GET_AI_MOVES);
   }

   @FXML
   /**
    * This method will pause the game --> Only allowed in offline games.
    * 
    * @throws IOException Will throw an exception if the pausedGame fxml file can't
    *                     be located.
    */
   private void pauseGame() throws IOException {
      board.pauseGame();

      Parent nextScene = App.loadFXML("pausedGame");
      sp_root.getChildren().add(nextScene);
      nextScene.scaleXProperty().set(0);
      nextScene.scaleYProperty().set(0);
      nextScene.opacityProperty().set(0);
      nextScene.applyCss();

      // Create a timeline
      Timeline inAni = new Timeline(
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(nextScene.scaleXProperty(), 1, Interpolator.EASE_BOTH)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(nextScene.scaleYProperty(), 1, Interpolator.EASE_BOTH)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(nextScene.opacityProperty(), 1, Interpolator.EASE_BOTH)));

      Timeline outAni = new Timeline(
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(nextScene.scaleXProperty(), 0, Interpolator.EASE_BOTH)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(nextScene.scaleYProperty(), 0, Interpolator.EASE_BOTH)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(nextScene.opacityProperty(), 0, Interpolator.EASE_BOTH)));

      inAni.play();
      inAni.setOnFinished(inAniF -> {
         Button btn_resumeGame = (Button) nextScene.lookup("#btn_resumeGame");

         btn_resumeGame.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
               outAni.play();
               outAni.setOnFinished(outAniF -> {
                  board.resumeGame();
                  sp_root.getChildren().remove(nextScene);
               });
            }
         });

         Button btn_saveAndQuit = (Button) nextScene.lookup("#btn_saveAndQuit");

         btn_saveAndQuit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
               try {
                  board.saveGame();
               } catch (IOException e) 
               {
                  e.printStackTrace();
               }
            }
         });

         Button btn_home = (Button) nextScene.lookup("#btn_home");

         btn_home.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
               try {
                  transitionToHome();
               } catch (IOException e) {
                  e.printStackTrace();
               }
            }
         });
      });
   }

   /**
    * This method will play an animation as it transitions to home.
    * 
    * @throws IOException Will throw an exception if the startScreen can't be
    *                     located.
    */
   public void transitionToHome() throws IOException {
      App.getStage().setHeight(App.getStage().getMinHeight());

      Parent homeScene = App.loadFXML("startScreen");

      sp_root.getChildren().add(homeScene);
      homeScene.translateXProperty().set(sp_root.getScene().getWidth());
      homeScene.translateYProperty().set(sp_root.getScene().getHeight());

      Timeline timeline = new Timeline(
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(homeScene.translateXProperty(), 0, Interpolator.EASE_BOTH)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(homeScene.translateYProperty(), 0, Interpolator.EASE_BOTH)));
      // Play ani
      timeline.play();

      timeline.setOnFinished(f1 -> {
         sp_root.getChildren().remove(homeScene);

         App.setRoot(homeScene);
      });
   }
}