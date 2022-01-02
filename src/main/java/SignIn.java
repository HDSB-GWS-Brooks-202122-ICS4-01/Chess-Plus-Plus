import java.io.IOException;

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

   }

   @FXML
   private void switchToRegister() throws IOException {
      Parent registerScene = App.loadFXML("register");

      sp_root.getChildren().add(registerScene);
      registerScene.translateXProperty().set(sp_root.getScene().getWidth());

      Timeline timeline = new Timeline(
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(registerScene.translateXProperty(), 0)),
            new KeyFrame(javafx.util.Duration.seconds(1),
                  new KeyValue(pn_main.translateXProperty(), -sp_root.getScene().getWidth())));

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
}
