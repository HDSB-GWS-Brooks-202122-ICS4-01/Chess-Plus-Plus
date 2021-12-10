import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class EndController {
   @FXML
   Button btn_replay, btn_matchmake, btn_home, btn_downloadTranscript;

   final Button[] SUBMIT_BUTTONS = new Button[]{btn_replay, btn_matchmake, btn_home, btn_downloadTranscript};
}
