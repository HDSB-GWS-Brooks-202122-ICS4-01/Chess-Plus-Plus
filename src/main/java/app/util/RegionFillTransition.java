package app.util;
import javafx.util.Duration;
import javafx.animation.Transition;
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

    /**
     * Constructor for RegionFillTransition object. 
     * @param region Region object to apply the fill transition to.
     * @param start The color to begin from. 
     * @param end The color to end from. 
     * @param duration The duration of the animation. 
     */
    public RegionFillTransition(Region region, Color start, Color end, Duration duration) {
        this.start = start;
        this.end = end;
        this.region = region;
        setCycleDuration(duration);

    }

    /**
     *{@inheritDoc}
     */
    @Override
    public void interpolate(double frac) {
        BackgroundFill fill = new BackgroundFill((start.interpolate(end, frac)), null, null);

        region.setBackground(new Background(fill));

    }

}
