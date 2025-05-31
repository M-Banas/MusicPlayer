package player;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class MusicPlayer {
    private MediaPlayer mediaPlayer;

    public void load(Playlist play,int i) {
        Media media = new Media(play.songs.get(i).url); 
        mediaPlayer = new MediaPlayer(media);
    }

        public void play() {
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
        }

        public void pause() {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        }

        public void stop() {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        }
    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            Duration currentTime = mediaPlayer.getCurrentTime();
            return (int) currentTime.toMillis();
        }
        return 0;
    }

    public int getDuration() {
        if (mediaPlayer != null) {
            Duration total = mediaPlayer.getTotalDuration();
            return total.isUnknown() ? 0 : (int) total.toMillis();
        }
        return 0;
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
        }
        return false;
    }

    public void seekTo(int millis) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.millis(millis));
        }
    }
    public void setRate(double rate) {
        if (mediaPlayer != null) {
            // Ustawienie prędkości odtwarzania **po** załadowaniu medi (gdy MediaPlayer jest READY)
            mediaPlayer.setOnReady(() -> {
                mediaPlayer.setRate(rate);
            });
        }
    }
    
}
