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
import javafx.scene.layout.Region;
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
    

    

    /**
     * Method for intializing the start screen scene. 
     */
    @FXML
    public void initialize() {

        Region[] elements = {playButton, resumeButton, settingsButton, aiButton};

        for(Region element : elements){
            element.setOpacity(0);
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
    }

    /**
     * Method for animating the startup of the application. This is called once per scene change to the start screen.
     * 
     * @param event MouseEvent object that resulted in the event. 
     */
    public void animateStartUp(MouseEvent event) {
        title.setVisible(true);
        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), title);
        tt.setFromY(title.getLayoutY());
        tt.setToY(300);

        tt.setOnFinished(e -> {
            Region[] elements = {playButton, settingsButton, resumeButton, aiButton};

            for(Region element : elements){
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
        transition("game");
    }

    /**
     * Method for switching the scene to the settings.
     */
    @FXML
    public void switchToSettings(){
        transition("settings");
    }

    /**
     * Method for switching the scene to the game against a computer. 
     */
    @FXML
    public void switchToAi() {

    }

    /**
     * Method for switching the scene to resume the last played game.
     */
    @FXML
    public void switchToResume() {

    }

    /**
     * Method for transitioning between scenes. Contains all the code for teh fade transitions, once the transitions are done it
     * tries to switch the scene. 
     * @param inp The name of the fxml file without the extension. 
     */
    private void transition(String inp){
        FadeTransition[] transitions = {
            new FadeTransition(Duration.millis(600), playButton), 
            new FadeTransition(Duration.millis(600), settingsButton),
            new FadeTransition(Duration.millis(600), aiButton),
            new FadeTransition(Duration.millis(600), resumeButton),
            new FadeTransition(Duration.millis(600), title)};
        
        for(FadeTransition transition : transitions){
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

}
