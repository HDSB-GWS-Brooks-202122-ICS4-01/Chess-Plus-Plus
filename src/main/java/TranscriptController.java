import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

/**
 * Controller for the transcript screen.
 * 
 * @version 1.0
 * @author Akil Pathiranage
 */
public class TranscriptController {

    @FXML
    Pane pane;

    @FXML
    ScrollPane scrollP;

    @FXML
    VBox transcriptContainer;

    @FXML
    Button mainMenuButton;

    Button select;
    File transcriptDir;

    @FXML
    Button deleteButton;

    @FXML
    Button playButton;

    @FXML
    Label title;

    /**
     * FXML Initialize method for the transcript controller.
     */
    @FXML
    public void initialize() {

        Region[] elements = { mainMenuButton, deleteButton, playButton };

        // creates hover and exit effects for the constant buttons.
        for (Region element : elements) {
            element.setBackground(new Background(new BackgroundFill(Color.web("#621B00"), null, null)));
            element.setOpacity(0);
            RegionFillTransition hover = new RegionFillTransition(element, Color.web("#621B00"), Color.web("#C75000"),
                    Duration.millis(250));
            RegionFillTransition exit = new RegionFillTransition(element, Color.web("#C75000"), Color.web("#621B00"),
                    Duration.millis(250));

            element.setOnMouseEntered(e -> {
                exit.stop();
                hover.play();
            });

            element.setOnMouseExited(e -> {
                hover.stop();
                exit.play();
            });
        }

        deleteButton.setDisable(true);
        playButton.setDisable(true);
        scrollP.setFitToWidth(true);

        // Gets the directory
        File[] stories;
        transcriptDir = new File("src\\main\\resources\\data\\transcripts");
        System.out.println(transcriptDir.getAbsolutePath());

        // method of sorting through files was found from here
        // https://howtodoinjava.com/java/io/java-filefilter-example/

        // Takes in files with .txt extension.
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".txt");
            }
        };

        // creates an array of file objects containing all the txt files in the
        // transcripts directory.
        stories = transcriptDir.listFiles(filter);

        for (File file : stories) {
            Button choice = new Button();
            choice.setText(file.getName().substring(0, file.getName().length() - 4));

            // Solution for removing default styleclass of buttons.
            // https://stackoverflow.com/questions/30759310/how-to-reset-back-to-default-css-after-adding-style
            choice.getStyleClass().clear();

            // styling of the buttons.
            choice.setPrefWidth(750);
            choice.setPrefHeight(100);
            choice.setBackground(new Background(new BackgroundFill(Color.web("#621B00"), null, null)));
            choice.setFont(Font.font("System", FontWeight.BOLD, 36));
            choice.setStyle("-fx-alignment: CENTER");
            choice.setTextAlignment(TextAlignment.CENTER);
            choice.setTextFill(Color.WHITE);

            // hover and exit effects for buttons.
            RegionFillTransition hoverEffect = new RegionFillTransition(choice, Color.web("#621B00"),
                    Color.web("#C75000"), Duration.millis(200));
            RegionFillTransition exitEffect = new RegionFillTransition(choice, Color.web("#C75000"),
                    Color.web("#621B00"), Duration.millis(200));

            choice.setOnMouseEntered(e -> {
                if (!choice.equals(select)) {
                    exitEffect.stop();
                    hoverEffect.play();
                }
            });

            choice.setOnMouseExited(e -> {
                if (!choice.equals(select)) {
                    hoverEffect.stop();
                    exitEffect.play();
                }
            });

            choice.setOnAction(e -> {
                hoverEffect.stop();
                exitEffect.stop();

                // if there was a button selected before, reset it.
                if (select != null) {
                    select.setBackground(new Background(new BackgroundFill(Color.web("#621B00"), null, null)));
                }

                // set the select variable to this button, changes its background to red
                select = choice;
                select.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));

                // make sure the deletebutton and playbutton are set to enabled.
                deleteButton.setDisable(false);
                playButton.setDisable(false);

            });

            transcriptContainer.getChildren().add(choice);
            // choice.setBackground(new Background(new BackgroundFill(Color.web("#FC4700"),
            // null, null)));
        }

        // sets the vbox background to transparent.
        transcriptContainer.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        scrollP.setOpacity(0);
        pane.setBackground(new Background(new BackgroundFill(Color.web("#621B00"), null, null)));

        mainMenuButton.setOnAction(e -> {
            transition("startScreen");
        });

        playButton.setOnAction(e -> {
            App.setTranscriptPath(Constants.boardData.TRANSCRIPT_DIR_PATH + select.getText() + ".txt");
            App.setTranscript("");
            App.setGameMode(Constants.boardData.MODE_RESUME_GAME);
            transition("game");
        });

        deleteButton.setOnAction(e -> {
            // File to delete.
            File fileToDel = new File(Constants.boardData.TRANSCRIPT_DIR_PATH + select.getText() + ".txt");
            // If it successfully deletes the file.
            if (fileToDel.delete()) {
                transcriptContainer.getChildren().remove(select);
                deleteButton.setDisable(true);
                playButton.setDisable(true);
                select = null;
            } else {
                System.out.println("Error in deleting file, TranscriptController.java");
            }

        });

        title.setOpacity(0);

        // Into transitions for the transcript screen.
        RegionFillTransition fillT = new RegionFillTransition(pane, Color.web("#621B00"), Color.web("#2F1000"),
                Duration.millis(1000));
        fillT.setOnFinished(e -> {
            Region[] introElements = { mainMenuButton, playButton, deleteButton, scrollP, title };
            for (Region element : introElements) {
                FadeTransition ft = new FadeTransition(Duration.millis(1000), element);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.play();
            }

        });

        fillT.play();
    }

    /**
     * Method for transitioning to the next fxml scene.
     * 
     * @param scene FXML file name without the file extension.
     */
    public void transition(String scene) {
        FadeTransition[] transitions = {
                new FadeTransition(Duration.millis(600), deleteButton),
                new FadeTransition(Duration.millis(600), mainMenuButton),
                new FadeTransition(Duration.millis(600), scrollP),
                new FadeTransition(Duration.millis(600), playButton),
                new FadeTransition(Duration.millis(600), title) };

        for (FadeTransition transition : transitions) {
            transition.setToValue(0);
            transition.play();
        }

        transitions[transitions.length - 1].setOnFinished(e -> {
            try {
                App.setRoot(scene);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });

    }
}
