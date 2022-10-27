package app.controllers;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.StorageClient;

import app.App;
import app.util.Constants;
import app.util.RegionFillTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Profile controller, displays user data
 * 
 * @author Selim Abdelwahab
 * @version 1.0
 */
public class Profile {
   Properties config = App.getConfig();

   @FXML
   StackPane sp_root;

   @FXML
   Pane pn_main;

   @FXML
   Button btn_mainMenu;

   @FXML
   Label lbl_displayName, lbl_wins, lbl_losses, lbl_score, lbl_output;

   @FXML
   ImageView imgv_avatar;

   @FXML
   /**
    * This method initializes the scene
    */
   public void initialize() {
      btn_mainMenu.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

      RegionFillTransition hoverEffect = new RegionFillTransition(btn_mainMenu, Color.TRANSPARENT,
            Color.web("#0077C6"), Duration.millis(200));

      RegionFillTransition exitEffect = new RegionFillTransition(btn_mainMenu, Color.web("#0077C6"),
            Color.TRANSPARENT, Duration.millis(200));

      btn_mainMenu.setOnMouseEntered(e -> {
         exitEffect.stop();
         hoverEffect.play();
      });
      btn_mainMenu.setOnMouseExited(e -> {
         hoverEffect.stop();
         exitEffect.play();
      });

      try {
         String UID = config.getProperty("UID");

         UserRecord userRecord = FirebaseAuth.getInstance().getUser(UID);

         com.google.cloud.storage.Bucket bucket = StorageClient.getInstance().bucket();

         Image avatar = new Image(
               new ByteArrayInputStream(bucket.get("profiles/" + userRecord.getUid() + "/avatar.jpg").getContent()));

         imgv_avatar.setImage(avatar);
         imgv_avatar.setFitWidth(150);
         imgv_avatar.setFitHeight(150);
         Rectangle clip = new Rectangle(imgv_avatar.getFitWidth(), imgv_avatar.getFitHeight());
         clip.setArcWidth(avatar.getWidth());
         clip.setArcHeight(avatar.getHeight());

         imgv_avatar.setClip(clip);

         lbl_displayName.setText(userRecord.getDisplayName());

         // Write the byte data to a file
         try (FileOutputStream fos = new FileOutputStream(Constants.Online.PATH_TO_STATS)) {

            fos.write(bucket.get("profiles/" + userRecord.getUid() + "/stats.txt").getContent());
            fos.close();
         }

         Scanner statsReader = new Scanner(new FileReader(Constants.Online.PATH_TO_STATS));

         // Read the data
         while (statsReader.hasNextLine()) {
            String line = statsReader.nextLine();

            if (line.contains("wins")) {
               lbl_wins.setText(line.replace("wins=", ""));
            } else if (line.contains("losses")) {
               lbl_losses.setText(line.replace("losses=", ""));
            } else if (line.contains("score")) {
               lbl_score.setText(line.replace("score=", ""));
            }
         }
      } catch (Exception e) { // Most likely the user was deleted
         config.setProperty(Constants.Online.CONFIG_SIGNED_IN, "f");
         config.setProperty(Constants.Online.CONFIG_UID, "null");

         try {
            switchToStartScreen();
         } catch (IOException ee) {

         }
         App.saveConfig(config);
      }

   }

   @FXML
   /**
    * This method will return to the home screen.
    * @throws IOException This method will throw an IOException if the startScreen couldn't be located.
    */
   private void switchToStartScreen() throws IOException {
      Parent startScreen = App.loadFXML("startScreen");

      sp_root.getChildren().add(startScreen);
      startScreen.translateXProperty().set(sp_root.getScene().getWidth());

      Timeline timeline = new Timeline(
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(startScreen.translateXProperty(), 0, Interpolator.EASE_BOTH)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(pn_main.translateXProperty(), -sp_root.getScene().getWidth(), Interpolator.EASE_BOTH)));

      // Play ani
      timeline.play();

      timeline.setOnFinished(f1 -> {
         sp_root.getChildren().remove(startScreen);
         App.setRoot(startScreen);
      });
   }
}
