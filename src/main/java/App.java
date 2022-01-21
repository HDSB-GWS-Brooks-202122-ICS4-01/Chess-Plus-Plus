
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
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
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;

/**
 * JavaFX Ap
 */

public class App extends Application {
    private static Properties config = App.getConfig();

    private static Scene scene;
    private static Stage stage;

    private static Image SPRITESHEET = new Image(App.class.getResource("assets\\chess_spritesheet.png").toString());

    private static byte winner = -1;

    public static int MOVE_COUNT = 1;
    public static Piece[] GAME_PIECES;
    private static String matchTranscript;
    private static String transcriptPath;

    private static Map<String, Integer>[] matchStats = null;

    private static String aiDiff = config.getProperty("aidiff");
    private static byte gameMode;

    private static DatabaseReference serverReference;
    private static String opponentRefId;
    private static byte onlineColor;

    private static String winMsg;

    @Override
    /**
     * This method is called from the entry point method and starts up the window.
     * 
     * @param st reference to the Stage
     * @throws IOException will throw an IOException if the startScreen can't be
     *                     located.
     */
    public void start(Stage st) throws IOException {
        scene = new Scene(loadFXML("startScreen"));
        stage = st;

        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                System.exit(0);
            }
        });
    }

    /**
     * This method will load the config file and return it
     * 
     * @return Properties object containing configuration data
     */
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

    /**
     * This method will save the configuration data to the configuration file
     * location.
     * 
     * @param config The Properties object containing the configuration data.
     */
    static void saveConfig(Properties config) {
        try {
            FileWriter writer = new FileWriter("src\\main\\resources\\data\\config.properties");
            config.store(writer, "Config saved");
            writer.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    /**
     * This method will set the root to a Parent object
     * 
     * @param parent Parent object
     */
    static void setRoot(Parent parent) {
        scene.setRoot(parent);
    }

    /**
     * This method will call the method that loads and FXML file and then sets the
     * root to the fxml data
     * 
     * @param fxml String name of the fxml file
     * @throws IOException Will throw and IOException if the file name could not be
     *                     resolved
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * This method will load an FXML file from string name
     * 
     * @param fxml String object containing the name of the file
     * @return Loaded object hierarchy
     * @throws IOException Will throw and IOException if the file name could not be
     *                     resolved
     */
    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * This method will set the game winner
     * @param w byte value representing the winner
     */
    public static void setWinner(byte w) {
        winner = w;
    }

    /**
     * This method will return the value of the winner
     * @return  byte value representing the winner
     */
    public static byte getWinner() {
        return winner;
    }

    /**
     * This method will get the winning color
     * @return String value of the winner
     */
    public static String getWinnerColor() {
        if (winner == Constants.pieceIDs.BLACK)
            return "Black";
        else
            return "White";
    }

    /**
     * Entry point of the program
     * @param args  Array of string containing starting arguments
     * @throws IOException  Will throw and IOException if the JSON firebase data file could not be located.
     */
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

    /**
     * This method will return the match transcript
     * @return  String value containing match transcript
     */
    public static String getTranscript() {
        return matchTranscript;
    }

    /**
     * This method will set the match stats
     * @param ms    HashMap containing the match stats
     */
    public static void setMatchStats(Map<String, Integer>[] ms) {
        matchStats = ms;
    }

    /**
     * This method will return the match stats
     * @return  a hashmap containing the stats
     */
    public static Map<String, Integer>[] getMatchStats() {
        return matchStats;
    }

    /**
     * Sets the match transcript
     * @param mt    String value containing the match transcript
     */
    public static void setTranscript(String mt) {
        matchTranscript = mt;
    }

    /**
     * This method will return the scene
     * @return  Scene object
     */
    public static Scene getScene() {
        return scene;
    }

    /**
     * This method will return the AI difficulty
     * @return String object
     */
    public static String getDiff() {
        return App.aiDiff;
    }

    /**
     * This method will set the game mode
     * @param gm    byte value corresponding to the game mode
     */
    public static void setGameMode(byte gm) {
        gameMode = gm;
    }

    /**
     * This method will return the game mode
     * @return  byte value corresponding to game mode
     */
    public static byte getGameMode() {
        return gameMode;
    }

    /**
     * This method will get the Stage object
     * @return  Stage object
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * This method deep copies any two dimensional byte array.
     * 
     * @param original Original 2-d byte array to copy.
     * @return A new 2-d byte array containing all the same elements with no
     *         references to the original.
     */
    public static byte[][] deepCopy(byte[][] original) {
        byte[][] newArray = new byte[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            newArray[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return newArray;
    }

    /**
     * This method sets the server reference
     * @param serverRef DatabaseReference object
     */
    public static void setServerReference(DatabaseReference serverRef) {
        serverReference = serverRef;
    }

    /**
     * This method will get the server reference
     * @return  DatabaseReference onject
     */
    public static DatabaseReference getServerReference() {
        return serverReference;
    }

    /**
     * This method will set the match transcript file path
     * @param newPath   String object
     */
    public static void setTranscriptPath(String newPath) {
        transcriptPath = newPath;
    }

    /**
     * This method will get the transcript file path
     * @return  String object
     */
    public static String getTranscriptPath() {
        return transcriptPath;
    }

    /**
     * This method will set the opponent reference id
     * @param key   String object
     */
    public static void setOpponentRefId(String key) {
        opponentRefId = key;
    }

    /**
     * This method will get the opponent reference id
     * @return String object
     */
    public static String getOpponentRefId() {
        return opponentRefId;
    } 

    /**
     * This method sets the user's online color attribute
     * @param c byte value
     */
    public static void setOnlineColor(byte c) {
        onlineColor = c;
    }

    /**
     * This method gets the user's online color attribute
     * @return  byte value
     */
    public static byte getOnlineColor() {
        return onlineColor;
    }

    /**
     * This method will set the winning message
     * @param value String object
     */
    public static void setWinMsg(String value) {
        winMsg = value;
    }

    /**
     * This method return the winning message
     * @return  winning message
     */
    public static String getWinMsg() {
        return winMsg;
    }
}