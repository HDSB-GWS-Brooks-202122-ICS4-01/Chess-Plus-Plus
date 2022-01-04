
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl.User;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

/**
 * JavaFX Ap
 */

public class App extends Application {
    private static Properties config = App.getConfig();

    private static Scene scene;
    private static Image SPRITESHEET = new Image(App.class.getResource("assets\\chess_spritesheet.png").toString());

    private static byte winner = -1;

    public static int MOVE_COUNT = 1;
    public static Piece[] GAME_PIECES;
    private static String matchTranscript;

    private static Map[] matchStats = null;

    private static String aiDiff = config.getProperty("aidiff");
    private static byte gameMode;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("startScreen"));
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    static Properties getConfig() {
        Properties config = null;

        try {
            FileReader reader = new FileReader("src\\main\\resources\\data\\config.properties");
            config = new Properties();

            try {
                config.load(reader);
                reader.close();
            } catch (Exception e) {
                System.out.println("Couldn't read from config file");
                e.printStackTrace();
            }

        } catch (FileNotFoundException fileNotFoundException) {

            System.out.println("Config file not found! Recreating new file");

            config = new Properties();
            config.setProperty("gametime", "10");
            config.setProperty("aidiff", "easy");
            config.setProperty("signedIn", "f");
            config.setProperty("UID", "null");
            try {
                FileWriter writer = new FileWriter("src\\main\\resources\\data\\config.properties");

                config.store(writer, "Config created");
                writer.close();

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        return config;
    }

    static void saveConfig(Properties config) {
        try {
            FileWriter writer = new FileWriter("src\\main\\resources\\data\\config.properties");
            config.store(writer, "Config saved");
            writer.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
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

    public static void main(String[] args) throws IOException {
        FileInputStream serviceAccount = new FileInputStream(Constants.Online.PATH_TO_JSON_PK);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://chess-app-bb905-default-rtdb.firebaseio.com")
                .setStorageBucket("chess-app-bb905.appspot.com")
                .build();

        FirebaseApp.initializeApp(options);

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

    public static String getDiff() {
        return App.aiDiff;
    }

    public static void setGameMode(byte gm) {
        gameMode = gm;
    }

    public static byte getGameMode() {
        return gameMode;
    }
}