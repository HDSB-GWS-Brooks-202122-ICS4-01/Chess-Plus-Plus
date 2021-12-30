import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Class for the controller for the start screen. 
 * 
 * @author Akil Pathiranage
 * @version 1.0
 */
public class StartScreenController {
    @FXML
    Pane mainPane;
    @FXML
    Button playButton;
    @FXML
    Button settingsButton;
    @FXML
    Label title;
    @FXML
    Button aiButton;
    @FXML
    Button resumeButton;

    

    @FXML
    public void initialize() {

        playButton.setVisible(false);
        settingsButton.setVisible(false);
        resumeButton.setVisible(false);
        aiButton.setVisible(false);

        playButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        settingsButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        aiButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        resumeButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

        title.setVisible(false);
        title.setLayoutY(-100);

        ButtonFadeTransition playHoverEffect = new ButtonFadeTransition(playButton, Color.web("#621B00"),
                Color.web("#C75000"), Duration.millis(200));
        ButtonFadeTransition playExitEffect = new ButtonFadeTransition(playButton, Color.web("#C75000"),
                Color.web("#621B00"), Duration.millis(200));
        ButtonFadeTransition settingsHoverEffect = new ButtonFadeTransition(settingsButton, Color.web("#621B00"),
                Color.web("#C75000"), Duration.millis(200));
        ButtonFadeTransition settingsExitEffect = new ButtonFadeTransition(settingsButton, Color.web("#C75000"),
                Color.web("#621B00"), Duration.millis(200));
        ButtonFadeTransition aiHoverEffect = new ButtonFadeTransition(aiButton, Color.web("#621B00"),
                Color.web("#C75000"), Duration.millis(200));
        ButtonFadeTransition aiExitEffect = new ButtonFadeTransition(aiButton, Color.web("#C75000"),
                Color.web("#621B00"), Duration.millis(200));
        ButtonFadeTransition resumeHoverEffect = new ButtonFadeTransition(resumeButton, Color.web("#621B00"),
                Color.web("#C75000"), Duration.millis(200));
        ButtonFadeTransition resumeExitEffect = new ButtonFadeTransition(resumeButton, Color.web("#C75000"),
                Color.web("#621B00"), Duration.millis(200));

        playButton.setOnMouseEntered(e -> {
            playHoverEffect.play();
        });
        playButton.setOnMouseExited(e -> {
            playExitEffect.play();
        });

        settingsButton.setOnMouseEntered(e -> {
            settingsHoverEffect.play();
        });
        settingsButton.setOnMouseExited(e -> {
            settingsExitEffect.play();
        });
        aiButton.setOnMouseEntered(e -> {
            aiHoverEffect.play();
        });
        aiButton.setOnMouseExited(e -> {
            aiExitEffect.play();
        });
        resumeButton.setOnMouseEntered(e -> {
            resumeHoverEffect.play();
        });
        resumeButton.setOnMouseExited(e -> {
            resumeExitEffect.play();
        });

        mainPane.setOnMouseEntered(this::animateStartUp);
    }

    public void animateStartUp(MouseEvent event) {

        title.setVisible(true);
        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), title);
        tt.setFromY(title.getLayoutY());
        tt.setToY(300);
        tt.setOnFinished(e -> {
            playButton.setVisible(true);
            playButton.setOpacity(0);
            FadeTransition playFade = new FadeTransition(Duration.millis(600), playButton);
            playFade.setToValue(1);
            playFade.play();

            settingsButton.setVisible(true);
            settingsButton.setOpacity(0);
            FadeTransition settingsFade = new FadeTransition(Duration.millis(600), settingsButton);
            settingsFade.setToValue(1);
            settingsFade.play();


            aiButton.setVisible(true);
            aiButton.setOpacity(0);
            FadeTransition aiFade = new FadeTransition(Duration.millis(600), aiButton);
            aiFade.setToValue(1);
            aiFade.play();

            resumeButton.setVisible(true);
            resumeButton.setOpacity(0);
            FadeTransition resumeFade = new FadeTransition(Duration.millis(600), resumeButton);
            resumeFade.setToValue(1);
            resumeFade.play();
        });
        tt.play();
        mainPane.setOnMouseEntered(null);

    }

    @FXML
    public void switchToGame() {
        transition("game");
    }

    @FXML
    public void switchToSettings(){
        transition("settings");
    }

    @FXML
    public void switchToAi() {

    }

    @FXML
    public void switchToResume() {

    }

    private void transition(String inp){
        FadeTransition playFade = new FadeTransition(Duration.millis(600), playButton);
        playFade.setToValue(0);
        playFade.play();

        FadeTransition settingsFade = new FadeTransition(Duration.millis(600), settingsButton);
        settingsFade.setToValue(0);
        settingsFade.play();

        FadeTransition aiFade = new FadeTransition(Duration.millis(600), aiButton);
        aiFade.setToValue(0);
        aiFade.play();

        FadeTransition resumeFade = new FadeTransition(Duration.millis(600), resumeButton);
        resumeFade.setToValue(0);
        resumeFade.play();

        FadeTransition fttt = new FadeTransition(Duration.millis(600), title);
        fttt.setToValue(0);
        fttt.play();
        fttt.setOnFinished(e -> {
            try {
                App.setRoot(inp);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

}
