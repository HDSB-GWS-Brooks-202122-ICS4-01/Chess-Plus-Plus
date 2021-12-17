import javafx.util.Duration;

import javafx.animation.Transition;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

/**
 * Class for making fade transitions with button background.
 * Note: In order to prevent flickering with the background, there must be no
 * CSS applied to this element.
 */
public class ButtonFadeTransition extends Transition {

    Color start;
    Color end;
    Button button;

    public ButtonFadeTransition(Button button, Color start, Color end, Duration duration) {
        this.start = start;
        this.end = end;
        this.button = button;
        setCycleDuration(duration);

    }

    @Override
    public void interpolate(double frac) {
        BackgroundFill fill = new BackgroundFill((start.interpolate(end, frac)), null, null);

        button.setBackground(new Background(fill));

    }

}
