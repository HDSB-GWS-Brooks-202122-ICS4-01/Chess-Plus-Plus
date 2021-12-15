import java.io.IOException;

import javafx.animation.FillTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class StartScreenController {
    @FXML
    VBox mainVBox = new VBox();

    @FXML Button playButton;

    @FXML
    public void intialize(){
    }
    

    @FXML
    public void switchToGame() throws IOException{
        App.setRoot("game");
    }


    @FXML
    public void animateEnterPlayBttn(){

    }
}
