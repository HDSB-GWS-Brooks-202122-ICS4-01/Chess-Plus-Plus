
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

/**
 * JavaFX Ap */

public class App extends Application {

    private static Scene scene;
    private static Image SPRITESHEET = new Image(App.class.getResource("assets\\chess_spritesheet.png").toString());

    public static int MOVE_COUNT = 1;
    public static Piece[] GAME_PIECES;

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

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    public static Image getSpritesheet(){
        return SPRITESHEET;
    }

}