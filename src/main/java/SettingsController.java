import javafx.fxml.FXML;
import java.util.Properties;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
public class SettingsController {

    Properties config;

    @FXML
    public void initialize(){

        try {
            FileReader reader = new FileReader("src\\main\\resources\\data\\config.properties");
            config = new Properties();
            
            try {
                config.load(reader);
                reader.close();
                System.out.println("GameTime: " + config.getProperty("gametime"));
                System.out.println("aidiff: " + config.getProperty("aidiff"));

            } catch (Exception e) {
                System.out.println("Couldn't read from config file");
                e.printStackTrace();
            } 

        } catch (FileNotFoundException fileNotFoundException) {

            System.out.println("Config file not found! Recreating new file");


            config = new Properties();
            config.setProperty("gametime", "10");
            config.setProperty("aidiff", "easy");
            try {
                FileWriter writer = new FileWriter("src\\main\\resources\\data\\config.properties");
    
                config.store(writer, "Config created");
                writer.close();
                
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }



        
    }

    @FXML
    public void switchToIntro(){
        try {
            FileWriter writer = new FileWriter("src\\main\\resources\\data\\config.properties");
            config.store(writer, "Config saved");
            writer.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        try {
            App.setRoot("startScreen");
        } catch (Exception e) {}

    }

    @FXML
    public void onEasy(){

    }

    @FXML
    public void onMedium(){

    }

    @FXML
    public void onHard(){

    }

    @FXML
    public void onTen(){
        config.setProperty("gametime", "10");
    }

    @FXML
    public void onThirty(){
        System.out.println("chnag");
        config.setProperty("gametime", "30");
    }

    @FXML
    public void onThree(){
        config.setProperty("gametime", "3");

    }

    @FXML
    public void onInfinite(){

    }
    
}
