package app.controllers;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Properties;

import app.App;
import app.util.RegionFillTransition;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class for the setting controller for the settings scene. 
 * 
 * @author Akil Pathiranage
 * @version 1.0
 */
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

    /**
     * FXML method for intializing the Settings scene. 
     * 
     */
    @FXML
    public void initialize() {

        //This code begins by trying to load the config file. 
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


        Region[] elements = {gameTimeButton, mainMenuButton, diffButton};

        //This for loop sets up each element on the screen
        //It makes them completely transparent to prepare for the intro transition.
        //It also makes sure the backgrounds of each element are transparent. 

        //Then it adds the hover effect for each button and creates the intro transition
        //for each button and plays it.
        for(Region element: elements){
            element.setOpacity(0);
            element.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

            RegionFillTransition hoverEffect = new RegionFillTransition(element, Color.TRANSPARENT,
                Color.web("#C75000"), Duration.millis(200));
            RegionFillTransition exitEffect = new RegionFillTransition(element, Color.web("#C75000"),
                Color.TRANSPARENT, Duration.millis(200));

            element.setOnMouseEntered(e -> {
                exitEffect.stop();
                hoverEffect.play();
            });
        
            element.setOnMouseExited(e -> {
                hoverEffect.stop();
                exitEffect.play();
            });

            //diffButton.setDisable(true);
            diffButton.setTooltip(new Tooltip("Feature coming soon!"));

            FadeTransition introTransition = new FadeTransition(Duration.millis(1000), element);
            introTransition.setFromValue(0);
            introTransition.setToValue(1);
            introTransition.play();

        }

        RegionFillTransition paneTransition = new RegionFillTransition(mainPane, Color.web("#621B00"), Color.web("#2F1000"), Duration.millis(1500));
        paneTransition.play();

    }


    /**
     * FXML method for switching to the intro screen. 
     */
    @FXML
    public void switchToIntro() {
        try {
            FileWriter writer = new FileWriter("src\\main\\resources\\data\\config.properties");
            config.store(writer, "Config saved");
            writer.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }


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

    /**
     * Method for setting the difficulty to easy, called from the action event handler for
     * the easy menuItem.
     */
    @FXML
    public void onEasy() {
        config.setProperty("aidiff", "easy");
    }

    /**
     * Method for setting the difficulty to medium, called from the action event handler for
     * the medium menuItem.
     */
    @FXML
    public void onMedium() {
        config.setProperty("aidiff", "medium");

    }

    /**
     * Method for setting the difficulty to hard, called from the action event handler for
     * the hard menuItem.
     */
    @FXML
    public void onHard() {
        config.setProperty("aidiff", "hard");
    }

    /**
     * Method for setting the time limit to ten minutes per player, called from the action event handler for
     * the 10:00 menuItem.
     */
    @FXML
    public void onTen() {
        config.setProperty("gametime", "10");
    }

    /**
     * Method for setting the time limit to ten minutes per player, called from the action event handler for
     * the 30:00 menuItem.
     */
    @FXML
    public void onThirty() {
        config.setProperty("gametime", "30");
    }

    /**
     * Method for setting the time limit to ten minutes per player, called from the action event handler for
     * the 3:00 menuItem.
     */
    @FXML
    public void onThree() {
        config.setProperty("gametime", "3");

    }

}
