import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Properties;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsController {

    Properties config;

    @FXML
    Pane mainPane;

    @FXML
    MenuButton gameTimeButton;

    @FXML
    MenuButton diffButton;

    @FXML
    Button mainMenuButton;

    @FXML
    public void initialize() {
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

        gameTimeButton.setOpacity(0);
        mainMenuButton.setOpacity(0);
        diffButton.setOpacity(0);
        gameTimeButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        diffButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        mainMenuButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

        RegionFillTransition gameTimeButtonHover = new RegionFillTransition(gameTimeButton, Color.web("#2F1000"),
                Color.web("#C75000"), Duration.millis(200));
        RegionFillTransition gameTimeButtonExit = new RegionFillTransition(gameTimeButton, Color.web("#C75000"),
                Color.web("#2F1000"), Duration.millis(200));
        gameTimeButton.setOnMouseEntered(e -> {
            gameTimeButtonExit.stop();
            gameTimeButtonHover.play();
        });

        gameTimeButton.setOnMouseExited(e -> {
            gameTimeButtonHover.stop();
            gameTimeButtonExit.play();
        });

        RegionFillTransition diffButtonHover = new RegionFillTransition(diffButton, Color.web("#2F1000"),
                Color.web("#C75000"), Duration.millis(200));
        RegionFillTransition diffButtonExit = new RegionFillTransition(diffButton, Color.web("#C75000"),
                Color.web("#2F1000"), Duration.millis(200));
        diffButton.setOnMouseEntered(e -> {
            diffButtonExit.stop();
            diffButtonHover.play();
        });

        diffButton.setOnMouseExited(e -> {
            diffButtonHover.stop();
            diffButtonExit.play();
        });

        RegionFillTransition mainButtonHover = new RegionFillTransition(mainMenuButton, Color.web("#2F1000"),
                Color.web("#C75000"), Duration.millis(200));
        RegionFillTransition mainButtonExit = new RegionFillTransition(mainMenuButton, Color.web("#C75000"),
                Color.web("#2F1000"), Duration.millis(200));
        mainMenuButton.setOnMouseEntered(e -> {
            mainButtonExit.stop();
            mainButtonHover.play();
        });

        mainMenuButton.setOnMouseExited(e -> {
            mainButtonHover.stop();
            mainButtonExit.play();
        });

        FadeTransition[] introTransitions = {
                new FadeTransition(Duration.millis(1000), mainMenuButton),
                new FadeTransition(Duration.millis(1000), diffButton),
                new FadeTransition(Duration.millis(1000), gameTimeButton) };
        for (FadeTransition transition : introTransitions){
            transition.setFromValue(0);
            transition.setToValue(1);
            transition.play();
        }

        RegionFillTransition paneTransition = new RegionFillTransition(mainPane, Color.web("#621B00"), Color.web("#2F1000"), Duration.millis(1500));
        paneTransition.play();

    }

    @FXML
    public void switchToIntro() {
        try {
            FileWriter writer = new FileWriter("src\\main\\resources\\data\\config.properties");
            config.store(writer, "Config saved");
            writer.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        gameTimeButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        diffButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        mainMenuButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));


        FadeTransition[] outroTransitions = {
            new FadeTransition(Duration.millis(500), mainMenuButton),
            new FadeTransition(Duration.millis(500), diffButton),
            new FadeTransition(Duration.millis(500), gameTimeButton)};
        for (FadeTransition transition : outroTransitions){
            transition.setFromValue(1);
            transition.setToValue(0);
            transition.play();
        }

        RegionFillTransition paneTransition = new RegionFillTransition(mainPane, Color.web("#2F1000"), Color.web("#621B00"), Duration.millis(500));
        paneTransition.setOnFinished(e -> {
            try {
                App.setRoot("startScreen");
            } catch (Exception except) {
                except.printStackTrace();
            }
        });
        paneTransition.play();

    }

    @FXML
    public void onEasy() {
        config.setProperty("aidiff", "easy");
    }

    @FXML
    public void onMedium() {
        config.setProperty("aidiff", "medium");

    }

    @FXML
    public void onHard() {
        config.setProperty("aidiff", "hard");
    }

    @FXML
    public void onTen() {
        config.setProperty("gametime", "10");
    }

    @FXML
    public void onThirty() {
        System.out.println("chnag");
        config.setProperty("gametime", "30");
    }

    @FXML
    public void onThree() {
        config.setProperty("gametime", "3");

    }

    @FXML
    public void onInfinite() {

    }

}
