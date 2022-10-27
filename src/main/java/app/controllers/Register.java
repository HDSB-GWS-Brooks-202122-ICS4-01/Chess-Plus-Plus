package app.controllers;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Properties;

import com.google.cloud.storage.Bucket;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.StorageClient;

import app.App;
import app.util.Constants;
import app.util.RegionFillTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Controller of the Register scene.
 * 
 * @author Selim Abdelwahab
 * @version 1.0
 */
public class Register {
   Properties config = App.getConfig();

   @FXML
   StackPane sp_root;

   @FXML
   Pane pn_main;

   @FXML
   Button btn_signIn, btn_register, btn_home;

   @FXML
   Label lbl_output, lbl_dnCounter;

   @FXML
   TextField tf_email, tf_displayName;

   @FXML
   PasswordField pf_password, pf_passwordConfirm;

   Timeline lbl_outputClearAni;

   @FXML
   /**
    * This method acts as the constructor and initializes the scene
    */
   public void initialize() {
      for (Button element : new Button[] { btn_signIn, btn_register, btn_home }) {
         element.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
         RegionFillTransition hoverEffect;
         RegionFillTransition exitEffect;

         if (element.getText().equalsIgnoreCase("register")) {
            hoverEffect = new RegionFillTransition(element, Color.TRANSPARENT,
                  Color.web("#00C681"), Duration.millis(200));

            exitEffect = new RegionFillTransition(element, Color.web("#00C681"),
                  Color.TRANSPARENT, Duration.millis(200));
         } else if (element.getText().equalsIgnoreCase("sign in")) {
            hoverEffect = new RegionFillTransition(element, Color.TRANSPARENT,
                  Color.web("#0077C6"), Duration.millis(200));

            exitEffect = new RegionFillTransition(element, Color.web("#0077C6"),
                  Color.TRANSPARENT, Duration.millis(200));
         } else {
            hoverEffect = new RegionFillTransition(element, Color.TRANSPARENT,
                  Color.web("#EC7063"), Duration.millis(200));

            exitEffect = new RegionFillTransition(element, Color.web("#EC7063"),
                  Color.TRANSPARENT, Duration.millis(200));
         }

         element.setOnMouseEntered(e -> {
            exitEffect.stop();
            hoverEffect.play();
         });
         element.setOnMouseExited(e -> {
            hoverEffect.stop();
            exitEffect.play();
         });
      }

      /**
       * This animation will clear the text out put after 4 seconds have passed.
       */
      lbl_outputClearAni = new Timeline(new KeyFrame(Duration.seconds(4), e -> {
         lbl_output.setText("");
      }));

      tf_displayName.textProperty().addListener(new ChangeListener<String>() {
         @Override
         public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            int val = newValue.length();

            lbl_dnCounter.setText(val + "/15");

            if (val >= 15) {
               tf_displayName.setText(tf_displayName.getText().substring(0, 15));
               lbl_dnCounter.setTextFill(Color.RED);
            } else {
               lbl_dnCounter.setTextFill(Color.BLACK);
            }

         }
      });
   }

   /**
    * This method will run a few checks when the register button is pressed, and
    * then attempt to create an account.
    * 
    * @throws NullPointerException         Will throw an error if there is an
    *                                      invalid
    *                                      entry.
    * @throws FirebaseAuthException        Will throw an error if an email is
    *                                      taken, or
    *                                      the domain name is incorrect, etc.
    * @throws FileNotFoundException        Will throw an error if the file isn't
    *                                      found
    * @throws UnsupportedEncodingException Will theow an error if the encoding is
    *                                      not supported
    */
   @FXML
   private void createAccount()
         throws NullPointerException, FirebaseAuthException, FileNotFoundException, UnsupportedEncodingException {
      boolean error = false;

      // Check the display name is not blank
      if (tf_displayName.getText().isBlank()) {
         lbl_output.setText("The display name can not be left empty!");

         error = true;
      }

      // Check if password is less than 6
      else if (pf_password.getText().length() < 6) {
         lbl_output.setText("Password must be greater than six characters.");

         error = true;
      }

      // Make sure there are no whitespaces in the password
      else if (pf_password.getText().contains(" ")) {
         lbl_output.setText("Password may not contain white characters (spaces).");

         error = true;
      }

      // Check both passwords are identical
      else if (!pf_password.getText().equals(pf_passwordConfirm.getText())) {
         lbl_output.setText("Passwords do not match.");

         error = true;
      }

      // declare userRecord
      UserRecord userRecord = null;
      try {
         // Create new account
         com.google.firebase.auth.UserRecord.CreateRequest request = new com.google.firebase.auth.UserRecord.CreateRequest()
               .setEmail(tf_email.getText())
               .setEmailVerified(false)
               .setPassword(pf_password.getText())
               .setDisplayName(tf_displayName.getText())
               // .setPhotoUrl(Constants.Online.PATH_TO_DEFAULT_AVATAR)
               .setPhotoUrl("https://" + pf_password.getText() + ".com")
               .setDisabled(false);

         userRecord = FirebaseAuth.getInstance().createUser(request);

         // Create new storage
         Bucket bucket = StorageClient.getInstance().bucket();
         // transcripts
         bucket.create("profiles/" + userRecord.getUid() + "/transcripts/holder.txt",
               "This is your transcripts file path".getBytes(StandardCharsets.UTF_8));

         // stats
         bucket.create("profiles/" + userRecord.getUid() + "/stats.txt",
               java.nio.file.Files.readAllBytes(Paths.get(Constants.Online.PATH_TO_DEFAULT_STATS)));

         // profile picture
         bucket.create("profiles/" + userRecord.getUid() + "/avatar.jpg",
               java.nio.file.Files.readAllBytes(Paths.get(Constants.Online.PATH_TO_DEFAULT_AVATAR)));
      } catch (Exception e) {
         error = true;
         lbl_output.setText(e.getMessage());
      }

      if (error) {
         lbl_outputClearAni.playFromStart();
         return;
      } else if (userRecord != null) {
         lbl_output.setText("User successfully created! Returning to home screen now.");

         config.setProperty("signedIn", "t");
         config.setProperty("UID", userRecord.getUid());

         App.saveConfig(config);

         Timeline toHome = new Timeline(new KeyFrame(Duration.seconds(2), f -> {
            try {
               switchToHome();
            } catch (IOException e) {
               lbl_output.setText("Unable to automatically return to home screen, please do so manually.");
            }
         }));

         toHome.play();
      }
   }

   /**
    * This method when called, will transition to the start screen.
    * 
    * @throws IOException Will throw an exception if the fxml file is not found.
    */
    @FXML
   private void switchToHome() throws IOException {
      Parent signInScene = App.loadFXML("signIn");
      Parent homeScene = App.loadFXML("startScreen");

      sp_root.getChildren().addAll(homeScene, signInScene);
      signInScene.translateXProperty().set(-sp_root.getScene().getWindow().getWidth());
      homeScene.translateXProperty().set(-2 * sp_root.getScene().getWindow().getWidth());

      Timeline timeline = new Timeline(
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(homeScene.translateXProperty(), 0, Interpolator.EASE_BOTH)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(signInScene.translateXProperty(), sp_root.getScene().getWindow().getWidth(),
                        Interpolator.EASE_BOTH)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(pn_main.translateXProperty(), 2 * sp_root.getScene().getWindow().getWidth(),
                        Interpolator.EASE_BOTH)));

      // Play ani
      timeline.play();

      timeline.setOnFinished(f1 -> {
         sp_root.getChildren().remove(signInScene);
         sp_root.getChildren().remove(homeScene);

         App.setRoot(homeScene);
      });
   }

   @FXML
   /**
    * This method will transition to the sign in screen
    * 
    * @throws IOException Will throw an IOException if the signIn scene could not
    *                     be located
    */
   private void switchToSignIn() throws IOException {
      Parent signInScene = App.loadFXML("signIn");

      sp_root.getChildren().add(signInScene);
      signInScene.translateXProperty().set(-sp_root.getScene().getWidth());

      Timeline timeline = new Timeline(
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(signInScene.translateXProperty(), 0, Interpolator.EASE_BOTH)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(pn_main.translateXProperty(), sp_root.getScene().getWidth(), Interpolator.EASE_BOTH)));

      // Play ani
      timeline.play();

      timeline.setOnFinished(f1 -> {
         sp_root.getChildren().remove(signInScene);

         try {
            App.setRoot("signIn");
         } catch (IOException e) {
         }
      });
   }
}
