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
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        try {
            App.setRoot("startScreen");
        } catch (Exception e) {}

    }
    
}
