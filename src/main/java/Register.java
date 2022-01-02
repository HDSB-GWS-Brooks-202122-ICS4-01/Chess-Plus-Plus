import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.rmi.server.UID;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.WriteChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Bucket.BlobTargetOption;
import com.google.common.io.Files;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.OidcProviderConfig.UpdateRequest;
import com.google.firebase.cloud.StorageClient;

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
import javafx.scene.image.Image;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Register {
   Properties config = App.getConfig();

   @FXML
   StackPane sp_root;

   @FXML
   Pane pn_main;

   @FXML
   Button btn_signIn, btn_register;

   @FXML
   Label lbl_output, lbl_dnCounter;

   @FXML
   TextField tf_email, tf_displayName;

   @FXML
   PasswordField pf_password, pf_passwordConfirm;

   Timeline lbl_outputClearAni;

   @FXML
   public void initialize() {
      for (Button element : new Button[] { btn_signIn, btn_register }) {
         element.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
         RegionFillTransition hoverEffect;
         RegionFillTransition exitEffect;

         if (element.getText().equalsIgnoreCase("register")) {
            hoverEffect = new RegionFillTransition(element, Color.TRANSPARENT,
                  Color.web("#00C681"), Duration.millis(200));

            exitEffect = new RegionFillTransition(element, Color.web("#00C681"),
                  Color.TRANSPARENT, Duration.millis(200));
         } else {
            hoverEffect = new RegionFillTransition(element, Color.TRANSPARENT,
                  Color.web("#0077C6"), Duration.millis(200));

            exitEffect = new RegionFillTransition(element, Color.web("#0077C6"),
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
    * @throws FileNotFoundException
    * @throws UnsupportedEncodingException
    */
   @FXML
   private void createAccount()
         throws NullPointerException, FirebaseAuthException, FileNotFoundException, UnsupportedEncodingException {
      boolean error = false;

      if (tf_displayName.getText().isBlank()) {
         lbl_output.setText("The display name can not be left empty!");

         error = true;
      }

      else if (pf_password.getText().length() < 6) {
         lbl_output.setText("Password must be greater than six characters.");

         error = true;
      }

      else if (pf_password.getText().contains(" ")) {
         lbl_output.setText("Password may not contain white characters (spaces).");

         error = true;
      }

      else if (!pf_password.getText().equals(pf_passwordConfirm.getText())) {
         lbl_output.setText("Passwords do not match.");

         error = true;
      }

      UserRecord userRecord = null;
      try {
         com.google.firebase.auth.UserRecord.CreateRequest request = new com.google.firebase.auth.UserRecord.CreateRequest()
               .setEmail(tf_email.getText())
               .setEmailVerified(false)
               .setPassword(pf_password.getText())
               .setDisplayName(tf_displayName.getText())
               // .setPhotoUrl(Constants.Online.PATH_TO_DEFAULT_AVATAR)
               .setDisabled(false);

         userRecord = FirebaseAuth.getInstance().createUser(request);

         Bucket bucket = StorageClient.getInstance().bucket();
         bucket.create("profiles/" + userRecord.getUid() + "/transcripts/holder.txt",
               "This is your transcripts file path".getBytes(StandardCharsets.UTF_8));

         bucket.create("profiles/" + userRecord.getUid() + "/stats.txt",
               java.nio.file.Files.readAllBytes(Paths.get(Constants.Online.PATH_TO_DEFAULT_STATS)));

         bucket.create("profiles/" + userRecord.getUid() + "/avatar.jpg",
               java.nio.file.Files.readAllBytes(Paths.get(Constants.Online.PATH_TO_DEFAULT_AVATAR)));

         System.out.println("Successfully created new user: " + userRecord.getUid());

      } catch (Exception e) {
         e.printStackTrace();
         error = true;
         lbl_output.setText(e.getMessage());
         System.out.println(e.getMessage());
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

   private void switchToHome() throws IOException {
      Parent signInScene = App.loadFXML("signIn");
      Parent homeScene = App.loadFXML("startScreen");

      sp_root.getChildren().addAll(homeScene, signInScene);
      signInScene.translateXProperty().set(-sp_root.getScene().getWindow().getWidth());
      homeScene.translateXProperty().set(-2 * sp_root.getScene().getWindow().getWidth());

      Timeline timeline = new Timeline(
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(homeScene.translateXProperty(), 0)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(signInScene.translateXProperty(), sp_root.getScene().getWindow().getWidth())),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(pn_main.translateXProperty(), 2 * sp_root.getScene().getWindow().getWidth())));

      // Play ani
      timeline.play();

      timeline.setOnFinished(f1 -> {
         sp_root.getChildren().remove(signInScene);
         sp_root.getChildren().remove(homeScene);

         try {
            App.setRoot("startScreen");
         } catch (IOException e) {
         }
      });
   }

   @FXML
   private void switchToSignIn() throws IOException {
      Parent signInScene = App.loadFXML("signIn");

      sp_root.getChildren().add(signInScene);
      signInScene.translateXProperty().set(-sp_root.getScene().getWidth());

      Timeline timeline = new Timeline(
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(signInScene.translateXProperty(), 0)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(pn_main.translateXProperty(), sp_root.getScene().getWidth())));

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
