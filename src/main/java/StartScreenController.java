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
    public void initialize() {
        System.out.println(playButton.getStyle());
        playButton.setVisible(false);
        settingsButton.setVisible(false);
        playButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        settingsButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

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
            settingsButton.setVisible(true);
            settingsButton.setOpacity(0);
            FadeTransition ft = new FadeTransition(Duration.millis(600), playButton);
            FadeTransition ftt = new FadeTransition(Duration.millis(600), settingsButton);
            ft.setToValue(1);
            ftt.setToValue(1);
            ft.play();
            ftt.play();

        });
        tt.play();
        mainPane.setOnMouseEntered(null);

    }

    @FXML
    public void switchToGame() throws IOException {
        FadeTransition ft = new FadeTransition(Duration.millis(600), playButton);
        FadeTransition ftt = new FadeTransition(Duration.millis(600), settingsButton);
        FadeTransition fttt = new FadeTransition(Duration.millis(600), title);
        ft.setToValue(0);
        ftt.setToValue(0);
        fttt.setToValue(0);
        fttt.play();
        ft.setOnFinished(e -> {
            try {
                App.setRoot("game");
            } catch (Exception exception) {}
        });
        ft.play();
        ftt.play();

        //App.setRoot("game");
    }

}
