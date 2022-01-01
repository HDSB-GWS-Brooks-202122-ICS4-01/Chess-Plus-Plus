
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * JavaFX Ap
 */

public class App extends Application {

    private static Scene scene;
    private static Image SPRITESHEET = new Image(App.class.getResource("assets\\chess_spritesheet.png").toString());

    private static byte winner = -1;

    public static int MOVE_COUNT = 1;
    public static Piece[] GAME_PIECES;
    private static String matchTranscript;

    private static Map[] matchStats = null;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("game"));
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    static void setRoot(Parent parent) {
        scene.setRoot(parent);
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void setWinner(byte w) {
        winner = w;
    }

    public static byte getWinner() {
        return winner;
    }

    public static String getWinnerColor() {
        if (winner == Constants.pieceIDs.BLACK)
            return "Black";
        else
            return "White";
    }

    public static void main(String[] args) {
        launch();
    }

    public static Image getSpritesheet() {
        return SPRITESHEET;
    }

    public static String getTranscript() {
        return matchTranscript;
    }

    public static void setMatchStats(Map[] ms) {
        matchStats = ms;
    }

    public static Map[] getMatchStats() {
        return matchStats;
    }

    public static void setTranscript(String mt) {
        matchTranscript = mt;
    }

    public static Scene getScene() {
        return scene;
    }

}