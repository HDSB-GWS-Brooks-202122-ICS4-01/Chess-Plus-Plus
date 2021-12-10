import java.util.Timer;
import java.util.concurrent.TimeUnit;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class PlayerTimer {
   Timeline tracker;
   Timer timer = new Timer();
   long timeSince = -1;
   long duration = -1;
   boolean isDone = false;
   final Label lbl_timer;

   public PlayerTimer(Label reference, int d, boolean isRunning) {
      lbl_timer = reference;

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
   }

   public void setTime(long millis) {
      duration = millis;

      lbl_timer.setText(getStringFormat());
   }

   private void updateTime() {
      if (timeSince != -1) {
         this.duration -= (System.currentTimeMillis() - timeSince);

         if (this.duration <= 0) {
            isDone = true;
            this.duration = 0;
         }

         lbl_timer.setText(getStringFormat());
      }

      timeSince = System.currentTimeMillis();
   }

   private String getStringFormat() {
      long minutes = (duration / 1000) / 60;
      int seconds = (int)((duration / 1000) % 60);

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

   public void playTimer() {
      lbl_timer.setTextFill(Color.LIGHTGREEN);

      tracker.play();
   }

   public void pauseTimer() {
      lbl_timer.setTextFill(Color.WHITE);

      timeSince = -1;
      tracker.pause();
   }
}
