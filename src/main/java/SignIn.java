import java.io.IOException;
import java.io.ObjectInputFilter.Config;
import java.util.Properties;

import com.google.cloud.storage.Acl.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserMetadata;
import com.google.firebase.auth.UserRecord;

import javafx.animation.Interpolatable;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * SignIn class, stores the users login information.
 * 
 * @author Selim Abdelwahab
 * @version 1.0
 */
public class SignIn {
   Properties config = App.getConfig();

   @FXML
   StackPane sp_root;

   @FXML
   Pane pn_main;

   @FXML
   Button btn_signIn, btn_register;

   @FXML
   Label lbl_output;

   @FXML
   TextField tf_email;

   @FXML
   PasswordField pf_password;

   Timeline lbl_outputClearAni;

   @FXML
   public void initialize() {
      for (Button element : new Button[] { btn_signIn, btn_register }) {
         element.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
         RegionFillTransition hoverEffect;
         RegionFillTransition exitEffect;

         if (element.getText().equalsIgnoreCase("register")) {
            hoverEffect = new RegionFillTransition(element, Color.TRANSPARENT,
                  Color.web("#0077C6"), Duration.millis(200));

            exitEffect = new RegionFillTransition(element, Color.web("#0077C6"),
                  Color.TRANSPARENT, Duration.millis(200));
         } else {
            hoverEffect = new RegionFillTransition(element, Color.TRANSPARENT,
                  Color.web("#00C681"), Duration.millis(200));

            exitEffect = new RegionFillTransition(element, Color.web("#00C681"),
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
   }

   @FXML
   private void switchToRegister() throws IOException {
      Parent registerScene = App.loadFXML("register");

      sp_root.getChildren().add(registerScene);
      registerScene.translateXProperty().set(sp_root.getScene().getWidth());

      Timeline timeline = new Timeline(
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(registerScene.translateXProperty(), 0, Interpolator.EASE_BOTH)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(pn_main.translateXProperty(), -sp_root.getScene().getWidth(), Interpolator.EASE_BOTH)));

      // Play ani
      timeline.play();

      timeline.setOnFinished(f1 -> {
         sp_root.getChildren().remove(registerScene);

         try {
            App.setRoot("register");
         } catch (IOException e) {
         }
      });
   }

   @FXML
   public void signIn() throws FirebaseAuthException {
      boolean error = false;
      String msg = "Success! You have been signed in, and will be redirected to the main menu.";

      String email = tf_email.getText();

      try {
         UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);

         // One might say this is not secure, I say it's innovation. Literally no other option with Firebase Admin SDK.
         String password = userRecord.getPhotoUrl();

         password = password.replace("https://", "");
         password = password.replace(".com", "");

         if (password.equals(pf_password.getText())) {
            config.setProperty("UID", userRecord.getUid());
            config.setProperty("signedIn", "t");

            App.saveConfig(config);
         } else {
            error = true;
            msg = "Email or password are incorrect.";
         }
      } catch (FirebaseAuthException fae) {
         msg = fae.getMessage();
         error = true;
      }

      lbl_output.setText(msg);

      if (!error) {
         Timeline toHome = new Timeline(new KeyFrame(Duration.seconds(2), f -> {
            try {
               switchToHome();
            } catch (IOException e) {
               lbl_output.setText("Unable to automatically return to home screen, please do so manually.");

            }
         }));

         toHome.play();
      }

      lbl_outputClearAni.playFromStart();
   }

   /**
    * This method when called, will transition to the start screen.
    * 
    * @throws IOException Will throw an exception if the fxml file is not found.
    */
   private void switchToHome() throws IOException {
      Parent homeScene = App.loadFXML("startScreen");

      sp_root.getChildren().add(homeScene);
      homeScene.translateXProperty().set(-sp_root.getScene().getWidth());

      Timeline timeline = new Timeline(
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(homeScene.translateXProperty(), 0, Interpolator.EASE_BOTH)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(pn_main.translateXProperty(), sp_root.getScene().getWidth(), Interpolator.EASE_BOTH)));

      // Play ani
      timeline.play();

      timeline.setOnFinished(f1 -> {
         sp_root.getChildren().remove(homeScene);

         try {
            App.setRoot("startScreen");
         } catch (IOException e) {
         }
      });
   }
}
