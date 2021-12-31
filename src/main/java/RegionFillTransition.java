import javafx.util.Duration;

import javafx.animation.Transition;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * Class for making fade transitions with button background.
 * Note: In order to prevent flickering with the background, there must be no
 * CSS applied to this element.
 * 
 * @author Akil Pathiranage
 * @version 1.0
 */
public class RegionFillTransition extends Transition {

    Color start;
    Color end;
    Region region;

    public RegionFillTransition(Region region, Color start, Color end, Duration duration) {
        this.start = start;
        this.end = end;
        this.region = region;
        setCycleDuration(duration);

    }

    @Override
    public void interpolate(double frac) {
        BackgroundFill fill = new BackgroundFill((start.interpolate(end, frac)), null, null);

        region.setBackground(new Background(fill));

    }

}
