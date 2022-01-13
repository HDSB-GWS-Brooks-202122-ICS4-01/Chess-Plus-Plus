import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputFilter.Config;
import java.util.Properties;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.StorageClient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Class for the controller for the start screen.
 * 
 * @author Akil Pathiranage
 * @version 1.0
 */
public class StartScreenController {
    Properties config = App.getConfig();

    @FXML
    StackPane sp_root;

    @FXML
    Pane mainPane, pn_notSignedIn, pn_signedIn;
    @FXML
    Button playButton;
    @FXML
    Button settingsButton;
    @FXML
    Label title, lbl_welcome;
    @FXML
    Button aiButton;
    @FXML
    Button resumeButton;
    @FXML
    Button btn_signIn, btn_register, btn_signOut, btn_playOnline;

    @FXML
    ImageView imgv_avatar;

    /**
     * Method for intializing the start screen scene.
     */
    @FXML
    public void initialize() throws IOException, FirebaseAuthException {

        Region[] elements = { playButton, resumeButton, settingsButton, aiButton, btn_signIn, btn_register,
                btn_signOut, btn_playOnline };

        for (Region element : elements) {
            element.setOpacity(0);
            element.setDisable(true);
            element.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

            RegionFillTransition hoverEffect = new RegionFillTransition(element, Color.TRANSPARENT,
                    Color.web("#C75000"), Duration.millis(200));
            RegionFillTransition exitEffect = new RegionFillTransition(element, Color.web("#C75000"),
                    Color.TRANSPARENT, Duration.millis(200));

            element.setOnMouseEntered(e -> {
                exitEffect.stop();
                hoverEffect.play();
            });
            element.setOnMouseExited(e -> {
                hoverEffect.stop();
                exitEffect.play();
            });

        }

        title.setVisible(false);
        title.setLayoutY(-100);

        mainPane.setOnMouseEntered(this::animateStartUp);

        // Check if the user is signed in
        if (config.getProperty(Constants.Online.CONFIG_SIGNED_IN).equals("t")) { // Signed in
            try {
                pn_signedIn.setVisible(true);
                pn_notSignedIn.setVisible(false);

                String UID = config.getProperty("UID");

                UserRecord userRecord = FirebaseAuth.getInstance().getUser(UID);

                lbl_welcome.setText("Welcome, " + userRecord.getDisplayName().toUpperCase() + "!");

                com.google.cloud.storage.Bucket bucket = StorageClient.getInstance().bucket();

                Image avatar = new Image(new ByteArrayInputStream(
                        bucket.get("profiles/" + userRecord.getUid() + "/avatar.jpg").getContent()));

                imgv_avatar.setImage(avatar);
                imgv_avatar.setFitWidth(150);
                imgv_avatar.setFitHeight(150);
                Rectangle clip = new Rectangle(imgv_avatar.getFitWidth(), imgv_avatar.getFitHeight());
                clip.setArcWidth(avatar.getWidth());
                clip.setArcHeight(avatar.getHeight());

                imgv_avatar.setClip(clip);
            } catch (Exception e) { // Most likely the user was deleted
                pn_signedIn.setVisible(false);
                pn_notSignedIn.setVisible(true);

                config.setProperty(Constants.Online.CONFIG_SIGNED_IN, "f");
                config.setProperty(Constants.Online.CONFIG_UID, "null");

                App.saveConfig(config);
            }

        } else {
            pn_notSignedIn.setVisible(true);
            pn_signedIn.setVisible(false);
        }
    }

    /**
     * Method for animating the startup of the application. This is called once per
     * scene change to the start screen.
     * 
     * @param event MouseEvent object that resulted in the event.
     */
    public void animateStartUp(MouseEvent event) {
        title.setVisible(true);
        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), title);
        tt.setFromY(title.getLayoutY());
        tt.setToY(300);

        tt.setOnFinished(e -> {
            Region[] elements = { playButton, settingsButton, resumeButton, aiButton, btn_signIn, btn_register,
                    btn_signOut, btn_playOnline };

            for (Region element : elements) {
                element.setDisable(false);
                FadeTransition fade = new FadeTransition(Duration.millis(600), element);
                fade.setToValue(1);
                fade.play();
            }
        });
        tt.play();
        mainPane.setOnMouseEntered(null);

    }

    /**
     * Method for switching the scene to the game, pass and play.
     */
    @FXML
    public void switchToGame() {
        App.setGameMode(Constants.boardData.MODE_PASS_N_PLAY);
        transition("game");
    }

    /**
     * Method for switching the scene to the settings.
     */
    @FXML
    public void switchToSettings() {
        transition("settings");
    }

    /**
     * Method for switching the scene to the game against a computer.
     */
    @FXML
    public void switchToAi() {
        App.setGameMode(Constants.boardData.MODE_AI);
        transition("game");
    }

    /**
     * Method for switching the scene to resume the last played game.
     */
    @FXML
    public void switchToResume() {
        /**App.setGameMode(Constants.boardData.MODE_RESUME_GAME);
        transition("game");*/
        transition("transcriptScreen");
    }

    @FXML
    public void matchmake() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("servers");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean foundServer = false;

                if (dataSnapshot.getChildrenCount() == 1) {
                    DatabaseReference newRef = ref.push();

                    DatabaseReference user1 = newRef.child("USER " + config.getProperty("UID"));
                    user1.child("turn").setValueAsync("true");
                    user1.child("timer").setValueAsync(600000);
                    user1.child("color").setValueAsync(Constants.pieceIDs.WHITE);

                    newRef.child("matchBegun").setValueAsync(false);
                    newRef.child("move").setValueAsync("");

                    newRef.addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                            String key = snapshot.getKey();

                            if (key.contains("USER") && !key.contains(config.getProperty("UID"))) {
                                App.setServerReference(newRef);
                                App.setGameMode(Constants.boardData.MODE_ONLINE);
                                transition("game");

                                newRef.child("matchBegun").setValueAsync(true);
                            }
                        }

                        @Override
                        public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {

                        }

                    });
                }

                for (DataSnapshot server : dataSnapshot.getChildren()) {
                    boolean matchBegun = (Boolean) server.child("matchBegun").getValue();

                    if (matchBegun)
                        continue;

                    DatabaseReference user2 = server.getRef().child("USER " + config.getProperty("UID"));

                    user2.child("turn").setValueAsync("false");
                    user2.child("timer").setValueAsync(600000);
                    user2.child("color").setValueAsync(Constants.pieceIDs.BLACK);

                    App.setServerReference(server.getRef());
                    App.setGameMode(Constants.boardData.MODE_ONLINE);
                    transition("game");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    /**
     * Method for transitioning between scenes. Contains all the code for teh fade
     * transitions, once the transitions are done it
     * tries to switch the scene.
     * 
     * @param inp The name of the fxml file without the extension.
     */
    private void transition(String inp) {
        FadeTransition[] transitions = {
                new FadeTransition(Duration.millis(600), playButton),
                new FadeTransition(Duration.millis(600), settingsButton),
                new FadeTransition(Duration.millis(600), aiButton),
                new FadeTransition(Duration.millis(600), resumeButton),
                new FadeTransition(Duration.millis(600), title) };

        for (FadeTransition transition : transitions) {
            transition.setToValue(0);
            transition.play();
        }

        transitions[4].setOnFinished(e -> {
            try {
                App.setRoot(inp);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    @FXML
    private void switchToSignIn() throws IOException {
        Parent signInScene = App.loadFXML("signIn");

        sp_root.getChildren().add(signInScene);
        signInScene.translateXProperty().set(sp_root.getScene().getWidth());

        Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1),
                new KeyValue(signInScene.translateXProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(javafx.util.Duration.seconds(1),
                        new KeyValue(mainPane.translateXProperty(), -sp_root.getScene().getWidth(),
                                Interpolator.EASE_BOTH)));

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

    @FXML
    private void switchToRegister() throws IOException {
        Parent signInScene = App.loadFXML("signIn"),
                registerScene = App.loadFXML("register");

        sp_root.getChildren().addAll(signInScene, registerScene);
        signInScene.translateXProperty().set(sp_root.getScene().getWidth());
        registerScene.translateXProperty().set(sp_root.getScene().getWidth() * 2);

        Timeline timeline = new Timeline(
                new KeyFrame(javafx.util.Duration.seconds(1),
                        new KeyValue(registerScene.translateXProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(javafx.util.Duration.seconds(1),
                        new KeyValue(signInScene.translateXProperty(), -sp_root.getScene().getWidth(),
                                Interpolator.EASE_BOTH)),
                new KeyFrame(javafx.util.Duration.seconds(1),
                        new KeyValue(mainPane.translateXProperty(), -2 * sp_root.getScene().getWidth(),
                                Interpolator.EASE_BOTH)));

        // Play ani
        timeline.play();

        timeline.setOnFinished(f1 -> {
            sp_root.getChildren().remove(signInScene);

            try {
                App.setRoot("register");
            } catch (IOException e) {
            }
        });
    }

    @FXML
    private void signOut() throws IOException {
        config.setProperty("UID", "null");
        config.setProperty(Constants.Online.CONFIG_SIGNED_IN, "f");

        App.saveConfig(config);
        App.setRoot("startScreen");
    }

    @FXML
    private void switchToProfile() throws IOException {
        Parent profileScene = App.loadFXML("profile");

        sp_root.getChildren().add(profileScene);
        profileScene.translateXProperty().set(-sp_root.getScene().getWidth());

        Timeline timeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1),
                new KeyValue(profileScene.translateXProperty(), 0, Interpolator.EASE_BOTH)),
                new KeyFrame(javafx.util.Duration.seconds(1),
                        new KeyValue(mainPane.translateXProperty(), sp_root.getScene().getWidth(),
                                Interpolator.EASE_BOTH)));

        // Play ani
        timeline.play();

        timeline.setOnFinished(f1 -> {
            sp_root.getChildren().remove(profileScene);

            try {
                App.setRoot("profile");
            } catch (IOException e) {
            }
        });
    }
}