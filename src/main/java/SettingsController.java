import javafx.fxml.FXML;

public class SettingsController {

    @FXML
    public void initialize(){
        
    }

    @FXML
    public void switchToIntro(){
        try {
            App.setRoot("startScreen");
        } catch (Exception e) {}

    }
    
}
