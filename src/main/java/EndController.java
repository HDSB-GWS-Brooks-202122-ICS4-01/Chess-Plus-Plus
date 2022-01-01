import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.print.DocFlavor.STRING;

import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

public class EndController {
   @FXML
   StackPane sp_root;

   @FXML
   HBox hb_container;

   @FXML
   SplitPane splt_container;

   @FXML
   Label lbl_output;

   @FXML
   Button btn_replay, btn_matchmake, btn_home, btn_downloadTranscript;

   Button[] SUBMIT_BUTTONS;

   private boolean savedMatchTranscript = false;

   @FXML
   Label lbl_b_rt, lbl_b_tm, lbl_b_pk, lbl_w_rt, lbl_w_pk, lbl_w_tm;

   @FXML
   public void initialize() {
      SUBMIT_BUTTONS = new Button[] { btn_replay, btn_matchmake, btn_home, btn_downloadTranscript };

      for (Button btn : SUBMIT_BUTTONS) {
         String defaultStyle = btn.getStyle();
         btn.setBackground(new Background(new BackgroundFill(Color.web("#FC4700"), null, null)));
         btn.setOnMouseEntered(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {
               ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);

               st.setByX(1);
               st.setByY(1);

               st.setToX(1.2);
               st.setToY(1.2);

               RegionFillTransition bft = new RegionFillTransition(btn, Color.web("#FC4700"), Color.web("#AE4619"),
                     Duration.millis(100));

               ParallelTransition pt = new ParallelTransition(st, bft);
               pt.play();

               btn.toFront();
            }
         });

         btn.setOnMouseExited(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent me) {

               ScaleTransition st = new ScaleTransition(Duration.millis(100), btn);

               st.setByX(1.2);
               st.setByY(1.2);

               st.setToX(1);
               st.setToY(1);

               Timeline ft = new Timeline(new KeyFrame(Duration.millis(100),
                     new KeyValue(btn.styleProperty(), defaultStyle)));

                     RegionFillTransition bft = new RegionFillTransition(btn, Color.web("#AE4619"), Color.web("#FC4700"),
                     Duration.millis(100));

               ParallelTransition pt = new ParallelTransition(st, bft);
               pt.play();

               btn.toBack();
            }
         });
      }

      Map[] stats = App.getMatchStats();
      Scene scene = btn_home.getScene();

      if (stats != null) {
         lbl_w_rt.setText(PlayerTimer.getStringFormat(Long.parseLong(stats[Constants.pieceIDs.WHITE].get("remaining_time").toString())));
         lbl_w_tm.setText(stats[Constants.pieceIDs.WHITE].get("total_moves").toString());
         lbl_w_pk.setText(stats[Constants.pieceIDs.WHITE].get("pieces_killed").toString());
         
         lbl_b_rt.setText(PlayerTimer.getStringFormat(Long.parseLong(stats[Constants.pieceIDs.BLACK].get("remaining_time").toString())));
         lbl_b_tm.setText(stats[Constants.pieceIDs.BLACK].get("total_moves").toString());
         lbl_b_pk.setText(stats[Constants.pieceIDs.BLACK].get("pieces_killed").toString());
      }
   }

   @FXML
   private void replayMatch() throws IOException {
      Parent gameScene = App.loadFXML("game");

      sp_root.getChildren().add(gameScene);
      gameScene.translateYProperty().set(-sp_root.getScene().getHeight());

      Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1),
            new KeyValue(gameScene.translateYProperty(), 0)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(hb_container.translateYProperty(), sp_root.getScene().getHeight())));

      // Play ani
      timeline.play();

      timeline.setOnFinished(f1 -> {
         sp_root.getChildren().remove(gameScene);

         try {
            App.setRoot("game");
         } catch (IOException e) {
         }
      });
   }

   @FXML
   private void goToHome() throws IOException {
      Parent gameScene = App.loadFXML("startScreen");

      sp_root.getChildren().add(gameScene);
      gameScene.translateYProperty().set(sp_root.getScene().getHeight());

      Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1),
            new KeyValue(gameScene.translateYProperty(), 0)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(hb_container.translateYProperty(), -sp_root.getScene().getHeight())));

      // Play ani
      timeline.play();

      timeline.setOnFinished(f1 -> {
         sp_root.getChildren().remove(gameScene);

         try {
            App.setRoot("game");
         } catch (IOException e) {
         }
      });
   }

   @FXML
   private void downloadGameTranscript() {
      if (savedMatchTranscript) {
         lbl_output.setText("Game transcript already saved.");
         return;
      }

      // Create new file
      try {
         File folder = new File("src\\main\\resources\\data\\transcripts\\");

         String name = "GAME_" + folder.listFiles().length + "_TRANSCRIPT.txt";

         // File newFile = new File("src\\main\\resources\\data\\" +
         // tf_storyName.getText() + ".txt");
         File newFile = new File("src\\main\\resources\\data\\transcripts\\" + name);

         if (newFile.createNewFile()) { // File created
            lbl_output.setText("Game transcript has been saved!");
            savedMatchTranscript = true;
         } else { // File found and will be overwritten
            lbl_output.setText("An error occured while attempting to download.");
         }

         // Write to file
         try {
            FileWriter wr = new FileWriter(newFile.getPath());
            wr.write(App.getTranscript());
            wr.close();
         } catch (IOException e) {
            lbl_output.setText("An error has occurd while attempting to create story. " + e.getMessage());
         }
      } catch (IOException e) {
         lbl_output.setText("An error has occurd while attempting to create story. " + e.getMessage());
      }
   }
}
