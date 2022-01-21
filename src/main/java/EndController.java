import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.StorageClient;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Controller of the end scene.
 * 
 * @author Selim Abdelwahab
 * @version 1.0
 */
public class EndController {
   Properties config = App.getConfig();

   @FXML
   StackPane sp_root;

   @FXML
   HBox hb_container;

   @FXML
   SplitPane splt_container;

   @FXML
   Label lbl_winnerMsg, lbl_output;

   @FXML
   Button btn_home, btn_downloadTranscript;

   Button[] submitButtons;

   private boolean savedMatchTranscript = false;

   @FXML
   Label lbl_b_rt, lbl_b_tm, lbl_b_pk, lbl_w_rt, lbl_w_pk, lbl_w_tm;

   @FXML
   /**
    * This method acts as the constructor and will initialize the scene.
    */
   public void initialize() {
      lbl_winnerMsg.setText(App.getWinMsg());

      submitButtons = new Button[] { btn_home, btn_downloadTranscript };

      for (Button btn : submitButtons) {
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


               RegionFillTransition bft = new RegionFillTransition(btn, Color.web("#AE4619"), Color.web("#FC4700"),
                     Duration.millis(100));

               ParallelTransition pt = new ParallelTransition(st, bft);
               pt.play();

               btn.toBack();
            }
         });
      }

      Map<String, Integer>[] stats = App.getMatchStats();

      // Set labels
      if (stats != null) {
         lbl_w_rt.setText(PlayerTimer
               .getStringFormat(Long.parseLong(stats[Constants.pieceIDs.WHITE].get("remaining_time").toString())));
         lbl_w_tm.setText(stats[Constants.pieceIDs.WHITE].get("total_moves").toString());
         lbl_w_pk.setText(stats[Constants.pieceIDs.WHITE].get("pieces_killed").toString());

         lbl_b_rt.setText(PlayerTimer
               .getStringFormat(Long.parseLong(stats[Constants.pieceIDs.BLACK].get("remaining_time").toString())));
         lbl_b_tm.setText(stats[Constants.pieceIDs.BLACK].get("total_moves").toString());
         lbl_b_pk.setText(stats[Constants.pieceIDs.BLACK].get("pieces_killed").toString());
      }

      try {
         if (App.getGameMode() == Constants.boardData.MODE_ONLINE) {
            String UID = config.getProperty("UID");

            // User is signed in
            if (!UID.equals("null")) {
               // Get user record
               UserRecord userRecord = FirebaseAuth.getInstance().getUser(UID);

               com.google.cloud.storage.Bucket bucket = StorageClient.getInstance().bucket();

               // Get user's stats
               try (FileOutputStream fos = new FileOutputStream(Constants.Online.PATH_TO_STATS)) {
                  fos.write(bucket.get("profiles/" + userRecord.getUid() + "/stats.txt").getContent());
                  fos.close();
               }

               Scanner statsReader = new Scanner(new FileReader(Constants.Online.PATH_TO_STATS));

               boolean won = App.getWinner() == App.getOnlineColor();
               int wins = 0;
               int losses = 0;
               int score = 0;

               // Read the data
               while (statsReader.hasNextLine()) {
                  String line = statsReader.nextLine();

                  if (line.contains("wins")) {
                     wins = Integer.parseInt(line.replace("wins=", ""));
                  } else if (line.contains("losses")) {
                     losses = Integer.parseInt(line.replace("losses=", ""));
                  } else if (line.contains("score")) {
                     score = Integer.parseInt(line.replace("score=", ""));
                  }
               }

               // Add wins or losses and update score
               if (won) {
                  wins++;
                  score += 100;
               } else {
                  losses++;

                  if (score - 50 > 0)
                     score -= 50;
               }

               String statsPush = "wins=" + wins + "\nlosses=" + losses + "\nplayTime=0\nscore=" + score;

               System.out.println(statsPush);

               FileWriter writer = new FileWriter(new File(Constants.Online.PATH_TO_STATS));
               // write data
               writer.write(statsPush);
               writer.close();

               // Push data to server
               bucket.create("profiles/" + UID + "/stats.txt",
                     java.nio.file.Files.readAllBytes(Paths.get(Constants.Online.PATH_TO_STATS)));
            }
         }
      } catch (Exception e) { // Most likely the user was deleted
         config.setProperty(Constants.Online.CONFIG_SIGNED_IN, "f");
         config.setProperty(Constants.Online.CONFIG_UID, "null");

         try {
            goToHome();
         } catch (IOException ee) {

         }
         App.saveConfig(config);
      }

   }

   @FXML
   /**
    * This method transitions the scene to home
    * 
    * @throws IOException Will throw an error if the fxml file is not found.
    */
   private void goToHome() throws IOException {
      Parent nextScene = App.loadFXML("startScreen");

      sp_root.getChildren().add(nextScene);
      nextScene.translateYProperty().set(sp_root.getScene().getHeight());

      Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1),
            new KeyValue(nextScene.translateYProperty(), 0, Interpolator.EASE_BOTH)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(hb_container.translateYProperty(), -sp_root.getScene().getHeight(),
                        Interpolator.EASE_BOTH)));

      // Play ani
      timeline.play();

      timeline.setOnFinished(f1 -> {
         sp_root.getChildren().remove(nextScene);

         App.setRoot(nextScene);
      });
   }

   @FXML
   /**
    * This method will save the match transcript as a txt file to a folder.
    */
   private void downloadGameTranscript() {
      if (savedMatchTranscript) {
         lbl_output.setText("Game transcript already saved.");
         return;
      }

      // Create new file
      try {
         File folder = new File("src\\main\\resources\\data\\transcripts\\");

         // Generate name for new trancript
         String name = "GAME_" + folder.listFiles().length + "_TRANSCRIPT.txt";

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
