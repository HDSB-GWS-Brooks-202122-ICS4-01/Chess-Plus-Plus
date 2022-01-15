import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * This class handles the player's timer.
 * 
 * @author Selim Abdelwahab
 * @version 1.0
 */
public class PlayerTimer {
   private Timeline tracker;

   private long timeSince = -1;
   private long duration = -1;

   private final Label lbl_timer;
   private final Label lbl_hiddenTimer;

   private boolean isOnline = false;
   private OnlineBoard oBoard = null;

   /**
    * This is the constructor for the PlayerTimer
    * 
    * @param reference Label object
    * @param d         long value for the duration
    * @param isRunning boolean value for if the timer is running
    * @param isOnline  boolean value for if the timer is being used in an online
    *                  game
    * @param oBoard    OnlineBoard object
    */
   public PlayerTimer(Label reference, Label hiddenReference, int d, boolean isRunning, boolean isOnline,
         OnlineBoard oBoard) {
      lbl_timer = reference;
      lbl_hiddenTimer = hiddenReference;

      this.duration = d;

      tracker = new Timeline(new KeyFrame(Duration.millis(1), new EventHandler<ActionEvent>() {
         public void handle(ActionEvent ae) {
            updateTime();
         }
      }));

      tracker.setCycleCount(Timeline.INDEFINITE);

      if (isRunning) {
         tracker.play();
         reference.setTextFill(Color.LIGHTGREEN);
      } else
         tracker.pause();

      this.isOnline = isOnline;
      this.oBoard = oBoard;
   }

   /**
    * This method takes in a long value and will set the (remaining) duration to
    * the value.
    * 
    * @param millis New value in ms
    */
   public void setTime(long millis) {
      duration = millis;

      lbl_timer.setText(getStringFormat(duration));
   }

   /**
    * This method will update the timer.
    */
   private void updateTime() {
      if (timeSince != -1) {
         this.duration -= (System.currentTimeMillis() - timeSince);

         if (this.duration <= 0) {
            this.duration = 0;
         }

         lbl_timer.setText(getStringFormat(duration));
         lbl_hiddenTimer.setText(Long.toString(duration));
      }

      timeSince = System.currentTimeMillis();

      if (isOnline) {
         oBoard.updateTimeToServer(duration);
      }
   }

   /**
    * This method will return a string in the format of min:seconds
    * 
    * @param duration long value of the remaining duration
    * @return String object
    */
   public static String getStringFormat(long duration) {
      long minutes = (duration / 1000) / 60;
      int seconds = (int) ((duration / 1000) % 60);

      String minutesText = "";
      String secondsText = "";

      if (minutes < 10)
         minutesText = "0" + minutes;
      else if (minutes <= 0)
         minutesText = "00";
      else
         minutesText = Long.toString(minutes);

      if (seconds < 10)
         secondsText = "0" + seconds;
      else if (seconds <= 0)
         secondsText = "00";
      else
         secondsText = Integer.toString(seconds);

      return minutesText + ":" + secondsText;
   }

   /**
    * This method will run the timer
    */
   public void playTimer() {
      lbl_timer.setTextFill(Color.LIGHTGREEN);

      tracker.play();
   }

   /**
    * This method will pause the timer
    */
   public void pauseTimer() {
      lbl_timer.setTextFill(Color.WHITE);

      timeSince = -1;
      tracker.pause();
   }

   /**
    * This method returns the remaining time
    * 
    * @return long value
    */
   public long getTimeMillis() {
      return duration;
   }
}
