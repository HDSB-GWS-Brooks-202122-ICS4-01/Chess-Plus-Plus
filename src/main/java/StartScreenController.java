import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class StartScreenController {

    @FXML Pane mainPane;
    @FXML Button playButton;
    @FXML Button settingsButton;
    @FXML Label title;

    @FXML
    public void initialize(){
        playButton.setVisible(false);
        settingsButton.setVisible(false);
        title.setVisible(false);
        title.setLayoutY(-100);




        Transition buttonHoverEffect = new Transition() {
            {
                setCycleDuration(Duration.millis(200));
            }

            @Override
            public void interpolate(double frac){
                BackgroundFill fill = new BackgroundFill((Color.web("#621B00").interpolate(Color.web("#C75000"), frac)), null, null);

                playButton.setBackground(new Background(fill));

            }
            
        };


        Transition buttonExitEffect = new Transition(){
            {setCycleDuration(Duration.millis(200));}

            @Override
            protected void interpolate(double frac) {
                BackgroundFill fill = new BackgroundFill((Color.web("#C75000").interpolate(Color.web("#621B00"), frac)), null, null);

                playButton.setBackground(new Background(fill));
            }

        };
        playButton.setOnMouseEntered(e -> {
            buttonHoverEffect.play();
        });

        playButton.setOnMouseExited(e -> {
            buttonExitEffect.play();
        });

        mainPane.setOnMouseEntered(this::animateStartUp);
    }
    

    public void animateStartUp(MouseEvent event){
        
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

        } );
        tt.play();
        mainPane.setOnMouseEntered(null);

    }

    @FXML
    public void switchToGame() throws IOException{
        App.setRoot("game");
    }

}
